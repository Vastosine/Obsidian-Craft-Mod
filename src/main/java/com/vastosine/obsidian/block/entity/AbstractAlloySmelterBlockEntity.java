package com.vastosine.obsidian.block.entity;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.providers.number.ResolvableNumber;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.Nullable;

import java.util.*;

public abstract class AbstractAlloySmelterBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, StackedContentsCompatible, RecipeCraftingHolder {
    protected final int SLOT_INPUT_COUNT = 2;
    protected final int SLOT_FUEL_COUNT = 1;
    protected final int SLOT_RESULT_COUNT = 1;

    protected final int[] SLOT_INPUT = get_sequence(0, SLOT_INPUT_COUNT);
    protected final int[] SLOT_FUEL = get_sequence(SLOT_INPUT_COUNT, SLOT_FUEL_COUNT);
    protected final int[] SLOT_RESULT = get_sequence(SLOT_INPUT_COUNT + SLOT_FUEL_COUNT, SLOT_RESULT_COUNT);

    private final int[] SLOTS_FOR_UP = SLOT_INPUT;
    private final int[] SLOTS_FOR_FRONT = SLOT_FUEL;
    private final int[] SLOTS_FOR_BACK = SLOT_FUEL;
    private final int[] SLOTS_FOR_LEFT = new int[]{0};
    private final int[] SLOTS_FOR_RIGHT = new int[]{1};
    private final int[] SLOTS_FOR_DOWN = ArrayUtils.addAll(SLOT_RESULT, SLOT_FUEL);

    public static final int DATA_LIT_DURATION = 1;
    public static final int DATA_COOKING_PROGRESS = 2;
    public static final int DATA_COOKING_TOTAL_TIME = 3;
    public static final int NUM_DATA_VALUES = 4;
    public static final int BURN_TIME_STANDARD = 200;
    public static final int BURN_COOL_SPEED = 2;
    private static final Codec<Map<ResourceKey<Recipe<?>>, Integer>> RECIPES_USED_CODEC = Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);
    private static final int DEFAULT_COOKING_TIMER = 0;
    private static final int DEFAULT_COOKING_TOTAL_TIME = 0;
    private static final int DEFAULT_LIT_TIME_REMAINING = 0;
    private static final int DEFAULT_LIT_TOTAL_TIME = 0;
    private static final float DEFAULT_SPEED_MULTIPLIER = 1.0F;
    protected NonNullList<ItemStack> items = NonNullList.withSize(SLOT_INPUT_COUNT + SLOT_FUEL_COUNT + SLOT_RESULT_COUNT, ItemStack.EMPTY);
    private int litTimeRemaining;
    private int litTotalTime;
    private int cookingTimer;
    private int cookingTotalTime;
    private float speedMultiplier = 1.0F;

    public static int[] get_sequence(int start, int len) {
        int[] ans = new int[len];
        for (int i = 0; i < len; i++) {
            ans[i] = i + start;
        }
        return ans;
    }

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(final int dataId) {
            return switch (dataId) {
                case 0 -> AbstractAlloySmelterBlockEntity.this.litTimeRemaining;
                case 1 -> AbstractAlloySmelterBlockEntity.this.litTotalTime;
                case 2 -> AbstractAlloySmelterBlockEntity.this.cookingTimer;
                case 3 -> AbstractAlloySmelterBlockEntity.this.cookingTotalTime;
                default -> 0;
            };
        }

        @Override
        public void set(final int dataId, final int value) {
            switch (dataId) {
                case 0 -> AbstractAlloySmelterBlockEntity.this.litTimeRemaining = value;
                case 1 -> AbstractAlloySmelterBlockEntity.this.litTotalTime = value;
                case 2 -> AbstractAlloySmelterBlockEntity.this.cookingTimer = value;
                case 3 -> AbstractAlloySmelterBlockEntity.this.cookingTotalTime = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };
    private final Reference2IntOpenHashMap<ResourceKey<Recipe<?>>> recipesUsed = new Reference2IntOpenHashMap<>();
    private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickCheck;

    private final Direction FACE;

    @SafeVarargs
    protected AbstractAlloySmelterBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState, Direction face, final RecipeType<? extends AbstractCookingRecipe>... recipeTypes) {
        super(type, worldPosition, blockState);
        this.quickCheck = RecipeManager.createCheck(recipeTypes[0]);
        FACE = face;
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.cookingTimer = input.getIntOr("cooking_time_spent", 0);
        this.cookingTotalTime = input.getIntOr("cooking_total_time", 0);
        this.litTimeRemaining = input.getIntOr("lit_time_remaining", 0);
        this.litTotalTime = input.getIntOr("lit_total_time", 0);
        this.speedMultiplier = input.getFloatOr("speed_multiplier", 1.0F);
        this.recipesUsed.clear();
        this.recipesUsed.putAll(input.read("RecipesUsed", RECIPES_USED_CODEC).orElse(Map.of()));
    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("cooking_time_spent", this.cookingTimer);
        output.putInt("cooking_total_time", this.cookingTotalTime);
        output.putInt("lit_time_remaining", this.litTimeRemaining);
        output.putInt("lit_total_time", this.litTotalTime);
        output.putFloat("speed_multiplier", this.speedMultiplier);
        ContainerHelper.saveAllItems(output, this.items);
        output.store("RecipesUsed", RECIPES_USED_CODEC, this.recipesUsed);
    }

    public static void serverTick(final ServerLevel level, final BlockPos pos, BlockState state, final AbstractAlloySmelterBlockEntity entity) {
        boolean changed = false;
        boolean isLit;
        boolean wasLit;
        if (entity.litTimeRemaining > 0) {
            wasLit = true;
            entity.litTimeRemaining--;
            isLit = entity.litTimeRemaining > 0;
        } else {
            wasLit = false;
            isLit = false;
        }

        ItemStack fuel = null;
        for (int slot : entity.SLOT_FUEL) {
            fuel = entity.items.get(slot);
            if (entity.getBurnDuration(level, fuel) > 0) break;
        }
        ArrayList<ItemStack> ingredients = new ArrayList<>();
        for (int slot : entity.SLOT_INPUT) {
            ItemStack itemStack = entity.items.get(slot);
            if (!itemStack.isEmpty()) ingredients.add(itemStack);
        }
        boolean hasIngredient = !ingredients.isEmpty();
        boolean hasFuel = fuel != null && !fuel.isEmpty();
        if (isLit || hasFuel && hasIngredient) {
            if (hasIngredient) {
                SingleRecipeInput input = new SingleRecipeInput(ingredients.getFirst());
                RecipeHolder<? extends AbstractCookingRecipe> recipe = entity.quickCheck.getRecipeFor(input, level).orElse(null);
                for (ItemStack ingredient : ingredients) {
                    input = new SingleRecipeInput(ingredient);
                    recipe = entity.quickCheck.getRecipeFor(input, level).orElse(null);
                    if (recipe == null) continue;

                    int maxStackSize = entity.getMaxStackSize();
                    ItemStack burnResult = recipe.value().assemble(input);
                    if (!burnResult.isEmpty() && canBurn(entity, maxStackSize, burnResult)) {
                        if (!isLit) {
                            int newLitTime = entity.getBurnDuration(level, fuel);
                            float newSpeedMultiplier = entity.getSpeedMultiplier(level, fuel);
                            entity.litTimeRemaining = newLitTime;
                            entity.litTotalTime = newLitTime;
                            entity.speedMultiplier = newSpeedMultiplier;
                            if (entity.cookingTotalTime > 0 && entity.cookingTimer < entity.cookingTotalTime) {
                                float completionRatio = (float)entity.cookingTimer / entity.cookingTotalTime;
                                entity.cookingTotalTime = getTotalCookTime(recipe, entity);
                                entity.cookingTimer = (int)Math.ceil(completionRatio * entity.cookingTotalTime);
                            }

                            if (newLitTime > 0) {
                                consumeFuel(entity, fuel);
                                isLit = true;
                                changed = true;
                            }
                        }

                        if (isLit) {
                            entity.cookingTimer++;
                            if (entity.cookingTimer >= entity.cookingTotalTime) {
                                entity.cookingTimer = 0;
                                entity.cookingTotalTime = getTotalCookTime(recipe, entity);
//                                burn(entity, ingredient, burnResult);
                                entity.setRecipeUsed(recipe);
                                changed = true;
                            }
                        } else {
                            entity.cookingTimer = 0;
                        }
                    } else {
                        entity.cookingTimer = 0;
                    }
                    break;
                }
            } else {
                entity.cookingTimer = 0;
            }
        } else if (entity.cookingTimer > 0) {
            entity.cookingTimer = Mth.clamp(entity.cookingTimer - 2, 0, entity.cookingTotalTime);
        }

        if (wasLit != isLit) {
            changed = true;
            state = state.setValue(AbstractFurnaceBlock.LIT, isLit);
            level.setBlockAndUpdate(pos, state);
        }

        if (changed) {
            setChanged(level, pos, state);
        }
    }

    private static void consumeFuel(final AbstractAlloySmelterBlockEntity entity, final ItemStack fuel) {
        Item fuelItem = fuel.getItem();
        fuel.shrink(1);
        ItemStackTemplate remainder = fuelItem.getCraftingRemainder();
//        entity.items.set(entity.SLOT_FUEL[0], remainder != null ? remainder.create() : ItemStack.EMPTY);
        if (remainder != null) {
            for (int slot : entity.SLOT_FUEL) {
                if (entity.items.get(slot).isEmpty()) {
                    entity.items.set(slot, remainder.create());
                    return;
                }
            }
        }
    }

    private static boolean canBurn(final AbstractAlloySmelterBlockEntity entity, final int maxStackSize, final ItemStack burnResult) {
        ItemStack resultItemStack = entity.items.get(3);
        if (resultItemStack.isEmpty()) {
            return true;
        }

        if (!ItemStack.isSameItemSameComponents(resultItemStack, burnResult)) {
            return false;
        }

        int resultCount = resultItemStack.getCount() + burnResult.count();
        int maxResultCount = Math.min(maxStackSize, burnResult.getMaxStackSize());
        return resultCount <= maxResultCount;
    }

    private static void burn(final AbstractAlloySmelterBlockEntity entity, final ItemStack inputItemStack, final ItemStack result, int fuel) {
        final NonNullList<ItemStack> items = entity.items;
        ItemStack resultItemStack = items.get(2);
        if (resultItemStack.isEmpty()) {
            items.set(2, result.copy());
        } else {
            resultItemStack.grow(result.getCount());
        }

        if (inputItemStack.is(Items.WET_SPONGE) && !items.get(1).isEmpty() && items.get(1).is(Items.BUCKET)) {
            items.set(1, new ItemStack(Items.WATER_BUCKET));
        }

        inputItemStack.shrink(1);
    }

    protected int getBurnDuration(final ServerLevel level, final ItemStack fuelItem) {
        return ResolvableNumber.getIntFromItem(fuelItem, DataComponents.COOKING_FUEL, CookingFuel::burnTime, this.getLootContext(level), 0);
    }

    protected float getSpeedMultiplier(final ServerLevel level, final ItemStack fuelItem) {
        return ResolvableNumber.getFloatFromItem(fuelItem, DataComponents.COOKING_FUEL, CookingFuel::speedMultiplier, this.getLootContext(level), 1.0F);
    }

    private static int getTotalCookTime(final RecipeHolder<? extends AbstractCookingRecipe> recipe, final AbstractAlloySmelterBlockEntity entity) {
        int cookingTotalTime = recipe.value().cookingTime();
        return entity.speedMultiplier > 0.0F ? (int)Math.ceil(cookingTotalTime / entity.speedMultiplier) : cookingTotalTime;
    }

    private static int getTotalCookTime(final ServerLevel level, final AbstractAlloySmelterBlockEntity entity) {
        SingleRecipeInput input = new SingleRecipeInput(entity.getItem(0));
        return entity.quickCheck
                .getRecipeFor(input, level)
                .map(recipeHolder -> getTotalCookTime((RecipeHolder<? extends AbstractCookingRecipe>)recipeHolder, entity))
                .orElse(200);
    }

    public final int[][] intToDirection = {SLOTS_FOR_FRONT, SLOTS_FOR_LEFT, SLOTS_FOR_BACK, SLOTS_FOR_RIGHT};

    @Override
    public int[] getSlotsForFace(final Direction direction) {
        int x = directionToInt(direction);
        int y = directionToInt(FACE);
        if (x == -1) return SLOTS_FOR_DOWN;
        if (x == -2) return SLOTS_FOR_UP;
        int z = x - y;
        return intToDirection[(z + 4) % 4];
    }

    private int directionToInt(Direction direction) {
        return switch (direction) {
            case EAST -> 0;
            case SOUTH -> 1;
            case WEST -> 2;
            case NORTH -> 3;
            case DOWN-> -1;
            case UP -> -2;
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(final int slot, final ItemStack itemStack, final @Nullable Direction direction) {
        return this.canPlaceItem(slot, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(final int slot, final ItemStack itemStack, final Direction direction) {
        return direction != Direction.DOWN || Arrays.stream(SLOT_FUEL).anyMatch(x -> x == slot) || itemStack.is(Items.WATER_BUCKET) || itemStack.is(Items.BUCKET);
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(final NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public void setItem(final int slot, final ItemStack itemStack) {
        ItemStack oldStack = this.items.get(slot);
        boolean same = !itemStack.isEmpty() && ItemStack.isSameItemSameComponents(oldStack, itemStack);
        this.items.set(slot, itemStack);
        itemStack.limitSize(this.getMaxStackSize(itemStack));
        if (Arrays.stream(SLOT_INPUT).anyMatch(x -> x == slot) && !same && this.level instanceof ServerLevel serverLevel) {
            this.cookingTotalTime = getTotalCookTime(serverLevel, this);
            this.cookingTimer = 0;
            this.setChanged();
        }
    }

    @Override
    public boolean canPlaceItem(final int slot, final ItemStack itemStack) {
        if (Arrays.stream(SLOT_RESULT).anyMatch(x -> x == slot)) {
            return false;
        }

        if (!Arrays.stream(SLOT_FUEL).anyMatch(x -> x == slot)) {
            return true;
        }

        ItemStack fuelSlot = this.items.get(1);
        return itemStack.has(DataComponents.COOKING_FUEL) || itemStack.is(Items.BUCKET) && !fuelSlot.is(Items.BUCKET);
    }

    @Override
    public void setRecipeUsed(final @Nullable RecipeHolder<?> recipeUsed) {
        if (recipeUsed != null) {
            ResourceKey<Recipe<?>> id = recipeUsed.id();
            this.recipesUsed.addTo(id, 1);
        }
    }

    @Override
    public @Nullable RecipeHolder<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void awardUsedRecipes(final Player player, final List<ItemStack> itemStacks) {
    }

    public void awardUsedRecipesAndPopExperience(final ServerPlayer player) {
        List<RecipeHolder<?>> recipesToAward = this.getRecipesToAwardAndPopExperience(player.level(), player.position());
        player.awardRecipes(recipesToAward);

        for (RecipeHolder<?> recipe : recipesToAward) {
            player.triggerRecipeCrafted(recipe, this.items);
        }

        this.recipesUsed.clear();
    }

    public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(final ServerLevel level, final Vec3 position) {
        List<RecipeHolder<?>> recipesToAward = Lists.newArrayList();

        for (Reference2IntMap.Entry<ResourceKey<Recipe<?>>> entry : this.recipesUsed.reference2IntEntrySet()) {
            level.recipeAccess().byKey(entry.getKey()).ifPresent(recipe -> {
                recipesToAward.add((RecipeHolder<?>)recipe);
                createExperience(level, position, entry.getIntValue(), ((AbstractCookingRecipe)recipe.value()).experience());
            });
        }

        return recipesToAward;
    }

    private static void createExperience(final ServerLevel level, final Vec3 position, final int amount, final float value) {
        int xpReward = Mth.floor(amount * value);
        float xpFraction = Mth.frac(amount * value);
        if (xpFraction != 0.0F && level.getRandom().nextFloat() < xpFraction) {
            xpReward++;
        }

        ExperienceOrb.award(level, position, xpReward);
    }

    @Override
    public void fillStackedContents(final StackedItemContents contents) {
        for (ItemStack itemStack : this.items) {
            contents.accountStack(itemStack);
        }
    }

    @Override
    public void preRemoveSideEffects(final BlockPos pos, final BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (this.level instanceof ServerLevel serverLevel) {
            this.getRecipesToAwardAndPopExperience(serverLevel, Vec3.atCenterOf(pos));
        }
    }
}
