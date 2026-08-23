package com.vastosine.obsidian.block.entity;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.vastosine.obsidian.item.crafting.AlloyingInput;
import com.vastosine.obsidian.item.crafting.AlloyingRecipe;
import com.vastosine.obsidian.item.crafting.OCRecipeTypes;
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
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.providers.number.ResolvableNumber;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.Nullable;

import java.util.*;

public abstract class AbstractAlloySmelterBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, StackedContentsCompatible, RecipeCraftingHolder {
    protected final int slotInputCount;
    protected final int slotFuelCount;
    protected final int slotResultCount;

    protected final int[] slotInput;
    protected final int[] slotFuel;
    protected final int[] slotResult;

    private final int[] slotsForUp;
    private final int[] slotsForFront;
    private final int[] slotsForBack;
    private final int[] slotsForLeft;
    private final int[] slotsForRight;
    private final int[] slotsForDown;

    public static final int DATA_LIT_TIME = 0;
    public static final int DATA_LIT_DURATION = 1;
    public static final int DATA_COOKING_PROGRESS = 2;
    public static final int DATA_COOKING_TOTAL_TIME = 3;
    public static final int NUM_DATA_VALUES = 4;
    public static final int BURN_TIME_STANDARD = 200;
    public static final int BURN_COOL_SPEED = 2;
    private static final Codec<Map<ResourceKey<Recipe<?>>, Integer>> RECIPES_USED_CODEC = Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);
    private static final Codec<Map<ResourceKey<Recipe<?>>, Integer>> RECIPES_USING_CODEC = Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);
    private static final Codec<List<ItemStack>> RECIPE_RESULTS = Codec.list(ItemStack.CODEC);
    private static final float DEFAULT_SPEED_MULTIPLIER = 1.0F;
    protected NonNullList<ItemStack> items;
    private int litTimeRemaining;
    private int litTotalTime;
    private int cookingTimer;
    private int cookingTotalTime;
    private float speedMultiplier = 1.0F;

    private final float smeltingSpeed = 2.5F;
    private final float alloyingSpeed = 1.0F;
    private final float fuelConsumingSpeed = 2.0F;

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
                case DATA_LIT_TIME -> AbstractAlloySmelterBlockEntity.this.litTimeRemaining;
                case DATA_LIT_DURATION -> AbstractAlloySmelterBlockEntity.this.litTotalTime;
                case DATA_COOKING_PROGRESS -> AbstractAlloySmelterBlockEntity.this.cookingTimer;
                case DATA_COOKING_TOTAL_TIME -> AbstractAlloySmelterBlockEntity.this.cookingTotalTime;
                default -> 0;
            };
        }

        @Override
        public void set(final int dataId, final int value) {
            switch (dataId) {
                case DATA_LIT_TIME -> AbstractAlloySmelterBlockEntity.this.litTimeRemaining = value;
                case DATA_LIT_DURATION -> AbstractAlloySmelterBlockEntity.this.litTotalTime = value;
                case DATA_COOKING_PROGRESS -> AbstractAlloySmelterBlockEntity.this.cookingTimer = value;
                case DATA_COOKING_TOTAL_TIME -> AbstractAlloySmelterBlockEntity.this.cookingTotalTime = value;
            }
        }

        @Override
        public int getCount() {
            return NUM_DATA_VALUES;
        }
    };
    private final Reference2IntOpenHashMap<ResourceKey<Recipe<?>>> recipesUsed = new Reference2IntOpenHashMap<>();
    private final Reference2IntOpenHashMap<ResourceKey<Recipe<?>>> recipesUsing = new Reference2IntOpenHashMap<>();
    private final List<ItemStack> recipeResults = new ArrayList<>();
    private final RecipeManager.CachedCheck<SingleRecipeInput, SmeltingRecipe> smeltingQuickCheck = RecipeManager.createCheck(RecipeType.SMELTING);
    private final RecipeManager.CachedCheck<AlloyingInput, AlloyingRecipe> alloyingQuickCheck = RecipeManager.createCheck(OCRecipeTypes.ALLOYING);

    private final Direction face;
    private int[][] intToDirection;

    protected AbstractAlloySmelterBlockEntity(
            BlockEntityType<?> type, BlockPos worldPosition,
            BlockState blockState,
            Direction face,
            int slotInputCount
    ) {
        this(type, worldPosition, blockState, face, slotInputCount, 1, 1);
    }

    protected AbstractAlloySmelterBlockEntity(
            BlockEntityType<?> type, BlockPos worldPosition,
            BlockState blockState,
            Direction face,
            int slotInputCount,
            int slotResultCount
    ) {
        this(type, worldPosition, blockState, face, slotInputCount, 1, slotResultCount);
    }

    protected AbstractAlloySmelterBlockEntity(
            BlockEntityType<?> type, BlockPos worldPosition,
            BlockState blockState,
            Direction face
    ) {
        this(type, worldPosition, blockState, face, 2, 1, 1);
    }

    protected AbstractAlloySmelterBlockEntity(
            BlockEntityType<?> type, BlockPos worldPosition,
            BlockState blockState,
            Direction face,
            int slotInputCount, int slotFuelCount, int slotResultCount
    ) {
        super(type, worldPosition, blockState);
        this.face = face;
        this.slotInputCount = slotInputCount;
        this.slotFuelCount = slotFuelCount;
        this.slotResultCount = slotResultCount;
        this.slotInput = get_sequence(0, slotInputCount);
        this.slotFuel = get_sequence(slotInputCount, slotFuelCount);
        this.slotResult = get_sequence(slotInputCount + slotFuelCount, slotResultCount);
        this.slotsForUp = slotInput;
        this.slotsForFront = slotFuel;
        this.slotsForBack = slotFuel;
        this.slotsForLeft = new int[]{0};
        this.slotsForRight = new int[]{1};
        this.slotsForDown = ArrayUtils.addAll(slotResult, slotFuel);
        this.items = NonNullList.withSize(slotInputCount + slotFuelCount + slotResultCount, ItemStack.EMPTY);
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
        this.speedMultiplier = input.getFloatOr("speed_multiplier", DEFAULT_SPEED_MULTIPLIER);
        this.recipesUsed.clear();
        this.recipesUsed.putAll(input.read("RecipesUsed", RECIPES_USED_CODEC).orElse(Map.of()));
        this.recipesUsing.clear();
        this.recipesUsing.putAll(input.read("RecipesUsing", RECIPES_USING_CODEC).orElse(Map.of()));
        this.recipeResults.clear();
        this.recipeResults.addAll(input.read("CookingRecipeResults", RECIPE_RESULTS).orElse(new ArrayList<>()));
        this.intToDirection = new int[][]{slotsForFront, slotsForLeft, slotsForBack, slotsForRight};
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
        output.store("RecipesUsing", RECIPES_USING_CODEC, this.recipesUsing);
        output.store("CookingRecipeResults", RECIPE_RESULTS, this.recipeResults);
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
        for (int slot : entity.slotFuel) {
            fuel = entity.items.get(slot);
            if (entity.getBurnDuration(level, fuel) > 0) break;
        }
        ArrayList<ItemStack> ingredients = new ArrayList<>();
        for (int slot : entity.slotInput) {
            ItemStack itemStack = entity.items.get(slot);
            if (!itemStack.isEmpty()) ingredients.add(itemStack);
        }
        boolean isCooking = !entity.recipeResults.isEmpty();
        boolean hasFuel = fuel != null && !fuel.isEmpty();
        if (!isCooking) {
            if (isLit || hasFuel) {
                // TODO Alloy Smelt Recipes
                AlloyingInput alloyingInput = new AlloyingInput(ingredients);
                RecipeHolder<AlloyingRecipe> alloyingRecipe = entity.alloyingQuickCheck.getRecipeFor(alloyingInput, level).orElse(null);
                ItemStack alloyingResult;
                if (alloyingRecipe != null && canBurn(entity, entity.getMaxStackSize(), alloyingResult = alloyingRecipe.value().assemble(alloyingInput))) {
                    entity.cookingTotalTime = getTotalAlloyTime(alloyingRecipe, entity);
                    entity.recipeResults.add(alloyingResult);
                    entity.recipesUsing.clear();
                    entity.recipesUsing.addTo(alloyingRecipe.id(), 1);
                    alloyingRecipe.value().consume(alloyingInput);
                } else {
                    for (ItemStack ingredient : ingredients) {
                        SingleRecipeInput input = new SingleRecipeInput(ingredient);
                        RecipeHolder<SmeltingRecipe> recipe = entity.smeltingQuickCheck.getRecipeFor(input, level).orElse(null);
                        if (recipe == null) continue;
                        ItemStack result = recipe.value().assemble(input);
                        if (!canBurn(entity, entity.getMaxStackSize(), result)) continue;
                        entity.cookingTotalTime = getTotalSmeltTime(recipe, entity);
                        entity.recipeResults.add(result);
                        ingredient.shrink(1);
                        entity.recipesUsing.clear();
                        entity.recipesUsing.addTo(recipe.id(), 1);
                        break;
                    }
                }
            }
        }
        isCooking = !entity.recipeResults.isEmpty();
        if (isCooking) {
            if (!isLit && hasFuel) {
                entity.litTotalTime = entity.litTimeRemaining = (int) (entity.getBurnDuration(level, fuel) / entity.fuelConsumingSpeed);
                entity.speedMultiplier = entity.getSpeedMultiplier(level, fuel);
                if (entity.cookingTotalTime > 0 && entity.cookingTimer < entity.cookingTotalTime) {
                    float completionRatio = (float) entity.cookingTimer / entity.cookingTotalTime;
                    entity.cookingTotalTime = (int) (entity.cookingTotalTime / (entity.speedMultiplier > 0 ? entity.speedMultiplier : 1.0));
                    entity.cookingTimer = (int) Math.ceil(completionRatio * entity.cookingTotalTime);
                }
                if (entity.litTotalTime > 0) {
                    isLit = true;
                    changed = true;
                    consumeFuel(entity, fuel);
                }
            }
            if (isLit) {
                entity.cookingTimer++;
                if (entity.cookingTimer >= entity.cookingTotalTime) {
                    entity.cookingTimer -= entity.cookingTotalTime;
                    for (ResourceKey<Recipe<?>> recipe : entity.recipesUsing.keySet()) {
                        entity.recipesUsed.addTo(recipe, entity.recipesUsing.getInt(recipe));
                    }
                    changed = true;
                    burn(entity);
                }
            } else if (entity.cookingTimer > 0) {
                entity.cookingTimer = Mth.clamp(entity.cookingTimer - BURN_COOL_SPEED, 0, entity.cookingTotalTime);
            }
        }
        if (changed) {
            setChanged(level, pos, state);
        }
        if (wasLit != isLit) {
            changed = true;
            state = state.setValue(BlockStateProperties.LIT, isLit);
            level.setBlockAndUpdate(pos, state);
        }
    }

    private static void consumeFuel(final AbstractAlloySmelterBlockEntity entity, final ItemStack fuel) {
        Item fuelItem = fuel.getItem();
        fuel.shrink(1);
        ItemStackTemplate remainder = fuelItem.getCraftingRemainder();
//        entity.items.set(entity.SLOT_FUEL[0], remainder != null ? remainder.create() : ItemStack.EMPTY);
        if (remainder != null) {
            for (int slot : entity.slotFuel) {
                if (entity.items.get(slot).isEmpty()) {
                    entity.items.set(slot, remainder.create());
                    return;
                }
            }
        }
    }

    private static boolean canBurn(final AbstractAlloySmelterBlockEntity entity, final int maxStackSize, final ItemStack burnResult) {
        for (int slot : entity.slotResult) {
            ItemStack resultItemStack = entity.items.get(slot);
            if (resultItemStack.isEmpty()) {
                return true;
            }

            if (!ItemStack.isSameItemSameComponents(resultItemStack, burnResult)) {
                continue;
            }

            int resultCount = resultItemStack.getCount() + burnResult.count();
            int maxResultCount = Math.min(maxStackSize, burnResult.getMaxStackSize());
            if (resultCount <= maxResultCount) {
                return true;
            }
        }
        return false;
    }

    private static void burn(final AbstractAlloySmelterBlockEntity entity) {
        final NonNullList<ItemStack> items = entity.items;
        final List<ItemStack> results = entity.recipeResults;
        for (ItemStack result : results) {
            for (int slot = 0; slot < entity.slotResultCount; slot++) {
                int resultSlot = slot + entity.slotInputCount + entity.slotFuelCount;
                ItemStack resultItemStack = items.get(resultSlot);
                if (resultItemStack.isEmpty()) {
                    items.set(resultSlot, result.copy());
                } else {
                    if (resultItemStack.getItem() != result.getItem()) continue;
                    resultItemStack.grow(result.getCount());
                }
                break;
            }
        }
        entity.recipeResults.clear();
    }

    protected int getBurnDuration(final ServerLevel level, final ItemStack fuelItem) {
        return ResolvableNumber.getIntFromItem(fuelItem, DataComponents.COOKING_FUEL, CookingFuel::burnTime, this.getLootContext(level), 0);
    }

    protected float getSpeedMultiplier(final ServerLevel level, final ItemStack fuelItem) {
        return ResolvableNumber.getFloatFromItem(fuelItem, DataComponents.COOKING_FUEL, CookingFuel::speedMultiplier, this.getLootContext(level), 1.0F);
    }

    private static int getTotalSmeltTime(final RecipeHolder<SmeltingRecipe> recipe, final AbstractAlloySmelterBlockEntity entity) {
        return Mth.ceil(recipe.value().cookingTime() / entity.smeltingSpeed / (entity.speedMultiplier > 0.0F ? entity.speedMultiplier : 1.0F));
    }

    private static int getTotalAlloyTime(final RecipeHolder<AlloyingRecipe> recipe, final AbstractAlloySmelterBlockEntity entity) {
        return Mth.ceil(recipe.value().cookingTime() / entity.alloyingSpeed / (entity.speedMultiplier > 0.0F ? entity.speedMultiplier : 1.0F));
    }

    @Override
    public int[] getSlotsForFace(final Direction direction) {
        int x = directionToInt(direction);
        int y = directionToInt(face);
        if (x == -1) return slotsForDown;
        if (x == -2) return slotsForUp;
        int z = x - y;
        return intToDirection[(z + 4) % 4];
    }

    private int directionToInt(Direction direction) {
        return switch (direction) {
            case EAST -> 0;
            case SOUTH -> 1;
            case WEST -> 2;
            case NORTH -> 3;
            case DOWN -> -1;
            case UP -> -2;
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(final int slot, final ItemStack itemStack, final @Nullable Direction direction) {
        return this.canPlaceItem(slot, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(final int slot, final ItemStack itemStack, final Direction direction) {
//        return direction != Direction.DOWN || slot != 1 || itemStack.is(Items.WATER_BUCKET) || itemStack.is(Items.BUCKET);
        return direction != Direction.DOWN || Arrays.stream(slotFuel).noneMatch(x -> x == slot) || itemStack.is(Items.WATER_BUCKET) || itemStack.is(Items.BUCKET);
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
    }

    @Override
    public boolean canPlaceItem(final int slot, final ItemStack itemStack) {
        if (Arrays.stream(slotResult).anyMatch(x -> x == slot)) {
            return false;
        }

        if (!Arrays.stream(slotFuel).anyMatch(x -> x == slot)) {
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
                recipesToAward.add(recipe);
                if (recipe.value() instanceof AbstractCookingRecipe cookingRecipe) {
                    createExperience(level, position, entry.getIntValue(), cookingRecipe.experience());
                } else if (recipe.value() instanceof AlloyingRecipe alloyingRecipe) {
                    createExperience(level, position, entry.getIntValue(), alloyingRecipe.experience());
                }
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
