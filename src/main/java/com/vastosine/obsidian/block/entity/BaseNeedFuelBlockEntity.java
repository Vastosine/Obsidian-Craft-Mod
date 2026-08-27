package com.vastosine.obsidian.block.entity;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.vastosine.obsidian.recipe.crafting.AlloyingRecipe;
import com.vastosine.obsidian.recipe.crafting.NeedFuelRecipe;
import com.vastosine.obsidian.recipe.crafting.ProcessingInput;
import com.vastosine.obsidian.utils.OCUtils;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public abstract class BaseNeedFuelBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, StackedContentsCompatible, RecipeCraftingHolder {
    private final int slotInputCount;
    private final int slotFuelCount;
    private final int slotResultCount;

    public static final int DATA_LIT_TIME = 0;
    public static final int DATA_LIT_DURATION = 1;
    public static final int DATA_COOKING_PROGRESS = 2;
    public static final int DATA_COOKING_TOTAL_TIME = 3;
    public static final int NUM_DATA_VALUES = 4;
    public static final int BURN_COOL_SPEED = 2;
    public static final Codec<Map<ResourceKey<Recipe<?>>, Integer>> RECIPES_USED_CODEC = Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);
    public static final Codec<Map<ResourceKey<Recipe<?>>, Integer>> RECIPES_USING_CODEC = Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);
    public static final Codec<List<ItemStack>> RECIPE_RESULTS = Codec.list(ItemStack.CODEC);
    public static final float DEFAULT_SPEED_MULTIPLIER = 1.0F;
    protected NonNullList<ItemStack> items;
    protected List<ItemStack> inputs, fuels, results;
    protected int litTimeRemaining;
    protected int litTotalTime;
    protected int cookingTimer;
    protected int cookingTotalTime;
    protected float speedMultiplier = DEFAULT_SPEED_MULTIPLIER;
    protected final float fuelConsumingSpeed;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(final int dataId) {
            return switch (dataId) {
                case DATA_LIT_TIME -> BaseNeedFuelBlockEntity.this.litTimeRemaining;
                case DATA_LIT_DURATION -> BaseNeedFuelBlockEntity.this.litTotalTime;
                case DATA_COOKING_PROGRESS -> BaseNeedFuelBlockEntity.this.cookingTimer;
                case DATA_COOKING_TOTAL_TIME -> BaseNeedFuelBlockEntity.this.cookingTotalTime;
                default -> 0;
            };
        }

        @Override
        public void set(final int dataId, final int value) {
            switch (dataId) {
                case DATA_LIT_TIME -> BaseNeedFuelBlockEntity.this.litTimeRemaining = value;
                case DATA_LIT_DURATION -> BaseNeedFuelBlockEntity.this.litTotalTime = value;
                case DATA_COOKING_PROGRESS -> BaseNeedFuelBlockEntity.this.cookingTimer = value;
                case DATA_COOKING_TOTAL_TIME -> BaseNeedFuelBlockEntity.this.cookingTotalTime = value;
            }
        }

        @Override
        public int getCount() {
            return NUM_DATA_VALUES;
        }
    };
    protected final List<ItemStack> recipeResults = new ArrayList<>();
    protected final Reference2IntOpenHashMap<ResourceKey<Recipe<?>>> recipesUsed = new Reference2IntOpenHashMap<>();
    protected final Reference2IntOpenHashMap<ResourceKey<Recipe<?>>> recipesUsing = new Reference2IntOpenHashMap<>();

    private final Direction face;

    protected BaseNeedFuelBlockEntity(
            BlockEntityType<?> type, BlockPos worldPosition,
            BlockState blockState, Direction face,
            float fuelConsumingSpeed,
            int slotInputCount, int slotFuelCount, int slotResultCount
    ) {
        super(type, worldPosition, blockState);
        this.face = face;
        this.slotInputCount = slotInputCount;
        this.slotFuelCount = slotFuelCount;
        this.slotResultCount = slotResultCount;
        this.fuelConsumingSpeed = fuelConsumingSpeed;
        this.items = NonNullList.withSize(slotInputCount + slotFuelCount + slotResultCount, ItemStack.EMPTY);
        initializeItems();
    }

    private void initializeItems() {
        inputs = items.subList(0, slotInputCount);
        fuels = items.subList(slotInputCount, slotInputCount + slotFuelCount);
        results = items.subList(slotInputCount + slotFuelCount, slotInputCount + slotFuelCount + slotResultCount);
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        initializeItems();
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

    public void tick(final ServerLevel level, final BlockPos pos, BlockState state) {
        boolean changed = false;
        boolean isLit;
        boolean wasLit;
        if (litTimeRemaining > 0) {
            wasLit = true;
            litTimeRemaining--;
            isLit = litTimeRemaining > 0;
        } else {
            wasLit = false;
            isLit = false;
        }

        ItemStack fuel = fuels.stream().filter(i -> getBurnDuration(level, i) > 0).findFirst().orElse(null);
        boolean hasFuel = fuel != null;
        boolean isCooking = !recipeResults.isEmpty();
        if (!isCooking) {
            if (isLit || hasFuel) {
                isCooking = loadRecipe(level);
            }
        }
        if (isCooking) {
            if (!isLit && hasFuel) {
                litTotalTime = litTimeRemaining = (int) (getBurnDuration(level, fuel) / fuelConsumingSpeed);
                speedMultiplier = getSpeedMultiplier(level, fuel);
                if (cookingTotalTime > 0 && cookingTimer < cookingTotalTime) {
                    float completionRatio = (float) cookingTimer / cookingTotalTime;
                    cookingTotalTime = (int) (cookingTotalTime / (speedMultiplier > 0 ? speedMultiplier : 1.0));
                    cookingTimer = (int) Math.ceil(completionRatio * cookingTotalTime);
                }
                if (litTotalTime > 0) {
                    isLit = true;
                    changed = true;
                    consumeFuel(fuel);
                }
            }
            if (isLit) {
                cookingTimer++;
                if (cookingTimer >= cookingTotalTime) {
                    cookingTimer -= cookingTotalTime;
                    for (ResourceKey<Recipe<?>> recipe : recipesUsing.keySet()) {
                        recipesUsed.addTo(recipe, recipesUsing.getInt(recipe));
                    }
                    changed = true;
                    burn();
                }
            } else if (cookingTimer > 0) {
                burnCool();
            }
        }
        if (wasLit != isLit) {
            changed = true;
            state = state.setValue(BlockStateProperties.LIT, isLit);
            level.setBlockAndUpdate(pos, state);
        }
        if (changed) {
            setChanged(level, pos, state);
        }
    }

    protected <R extends NeedFuelRecipe> boolean loadNeedFuelRecipe(ServerLevel level, RecipeType<R> type) {
        ProcessingInput alloyingInput = new ProcessingInput(inputs);
        List<RecipeHolder<R>> recipes = level
                .recipeAccess()
                .getAllMatches(type, alloyingInput, level)
                .toList();
        RecipeHolder<R> recipe = recipes.stream().filter(
                i -> canCraft(i.value().getResults(slotResultCount))
        ).max(Comparator.comparingInt(p -> p.value().ingredientSize())).orElse(null);
        if (recipe == null) return false;
        cookingTotalTime = getNeedFuelTotalTime(recipe);
        recipeResults.addAll(recipe.value().getResults(slotResultCount));
        recipesUsing.clear();
        recipesUsing.addTo(recipe.id(), 1);
        recipe.value().consume(alloyingInput);
        return true;
    }

    protected int getNeedFuelTotalTime(final RecipeHolder<? extends NeedFuelRecipe> recipe) {
        return Mth.ceil(recipe.value().cookingTime() / (speedMultiplier > 0.0F ? speedMultiplier : 1.0F));
    }

    protected void burnCool() {
        cookingTimer = Mth.clamp(cookingTimer - BURN_COOL_SPEED, 0, cookingTotalTime);
    }

    protected abstract boolean loadRecipe(ServerLevel level);

    protected void consumeFuel(final ItemStack fuel) {
        Item fuelItem = fuel.getItem();
        fuel.shrink(1);
        ItemStackTemplate remainder = fuelItem.getCraftingRemainder();
        if (remainder != null) {
            for (int slot = 0; slot < slotFuelCount; slot++) {
                ItemStack itemStack = fuels.get(slot);
                if (itemStack.isEmpty()) {
                    fuels.set(slot, remainder.create());
                    return;
                }
            }
        }
    }

    public void burn() {
        applyResult(getMaxStackSize(), recipeResults, results);
        recipeResults.clear();
    }

    protected int getBurnDuration(final ServerLevel level, final ItemStack fuelItem) {
        return ResolvableNumber.getIntFromItem(fuelItem, DataComponents.COOKING_FUEL, CookingFuel::burnTime, this.getLootContext(level), 0);
    }

    protected float getSpeedMultiplier(final ServerLevel level, final ItemStack fuelItem) {
        return ResolvableNumber.getFloatFromItem(fuelItem, DataComponents.COOKING_FUEL, CookingFuel::speedMultiplier, this.getLootContext(level), 1.0F);
    }

    @Override
    public int[] getSlotsForFace(final Direction direction) {
        int x = directionToInt(direction);
        int y = directionToInt(face);
        if (x == -1)
            return OCUtils.getSequence(slotInputCount + slotFuelCount + slotResultCount - 1, slotFuelCount + slotResultCount, -1);
        if (x == -2) return OCUtils.getSequence(0, slotInputCount);
        int z = (x - y + 4) % 4;
        if (z == 0 && slotInputCount >= 3) return new int[]{2};
        if (z % 2 == 0) return OCUtils.getSequence(slotInputCount, slotFuelCount);
        if (z == 1) return new int[]{0};
        if (z == 3) return new int[]{1};
        return ArrayUtils.EMPTY_INT_ARRAY;
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
        return direction != Direction.DOWN || !isFuelSlot(slot) || itemStack.is(Items.WATER_BUCKET) || itemStack.is(Items.BUCKET);
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
        this.items.set(slot, itemStack);
        itemStack.limitSize(this.getMaxStackSize(itemStack));
    }

    public boolean isInputSlot(int slot) {
        return OCUtils.isInRange(slot, slotInputCount);
    }

    public boolean isFuelSlot(int slot) {
        return OCUtils.isInRange(slot - slotInputCount, slotFuelCount);
    }

    public boolean isResultSlot(int slot) {
        return OCUtils.isInRange(slot - slotInputCount + slotFuelCount, slotResultCount);
    }

    @Override
    public boolean canPlaceItem(final int slot, final ItemStack itemStack) {
        return isInputSlot(slot) || isFuelSlot(slot) &&
                (itemStack.has(DataComponents.COOKING_FUEL) || itemStack.is(Items.BUCKET) && !items.get(slot).is(Items.BUCKET));
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

    protected boolean canCraft(final int maxStackSize, final ItemStack result) {
        for (ItemStack resultItemStack : results) {
            if (resultItemStack.isEmpty()) {
                return true;
            }

            if (!ItemStack.isSameItemSameComponents(resultItemStack, result)) {
                continue;
            }

            int resultCount = resultItemStack.getCount() + result.count();
            int maxResultCount = Math.min(maxStackSize, result.getMaxStackSize());
            if (resultCount <= maxResultCount) {
                return true;
            }
        }
        return false;
    }

    protected boolean canCraft(final ItemStack result) {
        return canCraft(getMaxStackSize(), result);
    }

    protected boolean canCraft(final int maxStackSize, final List<ItemStack> results) {
        return applyResult(maxStackSize, results, new ArrayList<>(this.results.stream().map(ItemStack::copy).toList()));
    }

    private static boolean applyResult(final int maxStackSize, final List<ItemStack> results, List<ItemStack> itemStacks) {
        for (ItemStack result : results) {
            if (result.isEmpty()) continue;
            int count = result.count();
            for (int slot = 0; slot < itemStacks.size(); slot++) {
                if (count <= 0) break;
                if  (itemStacks.get(slot).isEmpty()) {
                    itemStacks.set(slot, result.copyWithCount(1));
                    count--;
                    if (count <= 0) break;
                }
                ItemStack itemStack = itemStacks.get(slot);
                if (!ItemStack.isSameItemSameComponents(itemStack, result)) {
                    continue;
                }
                int maxResultCount = Math.min(maxStackSize, result.getMaxStackSize());
                int x = Math.min(maxResultCount - itemStack.count(), count);
                itemStack.grow(x);
                count -= x;
            }
            if (count > 0) return false;
        }
        return true;
    }

    protected boolean canCraft(final List<ItemStack> results) {
        return canCraft(getMaxStackSize(), results);
    }
}
