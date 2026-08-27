package com.vastosine.obsidian.block.entity;

import com.vastosine.obsidian.recipe.crafting.NeedFuelRecipe;
import com.vastosine.obsidian.recipe.crafting.OCRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractAlloySmelterBlockEntity extends BaseNeedFuelBlockEntity {
    private final float smeltingSpeed;
    private final float alloyingSpeed;

    protected final RecipeManager.CachedCheck<SingleRecipeInput, SmeltingRecipe> smeltingQuickCheck = RecipeManager.createCheck(RecipeType.SMELTING);

    protected AbstractAlloySmelterBlockEntity(
            BlockEntityType<?> type, BlockPos worldPosition,
            BlockState blockState, Direction face,
            float smeltingSpeed, float alloyingSpeed, float fuelConsumingSpeed,
            int slotInputCount, int slotFuelCount, int slotResultCount
    ) {
        super(
                type, worldPosition, blockState, face,
                fuelConsumingSpeed, slotInputCount, slotFuelCount, slotResultCount
        );
        this.smeltingSpeed = smeltingSpeed;
        this.alloyingSpeed = alloyingSpeed;
    }

    @Override
    protected boolean loadRecipe(ServerLevel level) {
        return loadNeedFuelRecipe(level, OCRecipeTypes.ALLOYING) || loadSmeltingRecipe(level);
    }

    private boolean loadSmeltingRecipe(ServerLevel level) {
        for (ItemStack ingredient : inputs) {
            SingleRecipeInput input = new SingleRecipeInput(ingredient);
            RecipeHolder<SmeltingRecipe> recipe = smeltingQuickCheck.getRecipeFor(input, level).orElse(null);
            if (recipe == null) continue;
            ItemStack result = recipe.value().assemble(input);
            if (!canCraft(result)) continue;
            cookingTotalTime = getTotalSmeltTime(recipe);
            recipeResults.add(result);
            ingredient.shrink(1);
            recipesUsing.clear();
            recipesUsing.addTo(recipe.id(), 1);
            return true;
        }
        return false;
    }

    protected int getTotalSmeltTime(final RecipeHolder<SmeltingRecipe> recipe) {
        return Mth.ceil(recipe.value().cookingTime() / smeltingSpeed / (speedMultiplier > 0.0F ? speedMultiplier : 1.0F));
    }

    @Override
    protected int getNeedFuelTotalTime(RecipeHolder<? extends NeedFuelRecipe> recipe) {
        return Mth.ceil(recipe.value().cookingTime() / alloyingSpeed / (speedMultiplier > 0.0F ? speedMultiplier : 1.0F));
    }

    @Override
    public void burn() {
        if (recipeResults.stream().anyMatch(i -> i.is(Items.SPONGE))) {
            int emptyIndex = -1, bucketIndex = -1, singleBucketIndex = -1;
            for (int index = 0;  index < fuels.size(); index++) {
                ItemStack itemStack = fuels.get(index);
                if (itemStack.isEmpty() && emptyIndex == -1) {
                    emptyIndex = index;
                } else if (itemStack.is(Items.BUCKET)) {
                    if (itemStack.count() == 1 && singleBucketIndex == -1) {
                        singleBucketIndex = index;
                    }
                    if (bucketIndex == -1) {
                        bucketIndex = index;
                    }
                }
            }
            if (emptyIndex == -1 && singleBucketIndex != -1) {
                fuels.set(singleBucketIndex, new ItemStack(Items.WATER_BUCKET));
            } else if (emptyIndex != -1 && bucketIndex != -1) {
                fuels.get(bucketIndex).shrink(1);
                if (fuels.get(bucketIndex).isEmpty()) {
                    emptyIndex = Math.min(emptyIndex, bucketIndex);
                }
                fuels.set(emptyIndex, new ItemStack(Items.WATER_BUCKET));
            }
        }
        super.burn();
    }
}
