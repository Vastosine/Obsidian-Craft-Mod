package com.vastosine.obsidian.block.entity;

import com.vastosine.obsidian.recipe.crafting.AlloyingInput;
import com.vastosine.obsidian.recipe.crafting.AlloyingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public abstract class AbstractAlloySmelterBlockEntity extends BaseConsumesFuelBlockEntity {
    private final float smeltingSpeed;
    private final float alloyingSpeed;

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
        AlloyingInput alloyingInput = new AlloyingInput(inputs);
        RecipeHolder<AlloyingRecipe> alloyingRecipe = alloyingQuickCheck.getRecipeFor(alloyingInput, level).orElse(null);
        ItemStack alloyingResult;
        if (alloyingRecipe != null && canBurn(getMaxStackSize(), alloyingResult = alloyingRecipe.value().assemble(alloyingInput))) {
            cookingTotalTime = getTotalAlloyTime(alloyingRecipe);
            recipeResults.add(alloyingResult);
            recipesUsing.clear();
            recipesUsing.addTo(alloyingRecipe.id(), 1);
            alloyingRecipe.value().consume(alloyingInput);
            return true;
        }
        for (ItemStack ingredient : inputs) {
            SingleRecipeInput input = new SingleRecipeInput(ingredient);
            RecipeHolder<SmeltingRecipe> recipe = smeltingQuickCheck.getRecipeFor(input, level).orElse(null);
            if (recipe == null) continue;
            ItemStack result = recipe.value().assemble(input);
            if (!canBurn(getMaxStackSize(), result)) continue;
            cookingTotalTime = getTotalSmeltTime(recipe);
            recipeResults.add(result);
            ingredient.shrink(1);
            recipesUsing.clear();
            recipesUsing.addTo(recipe.id(), 1);
            return true;
        }

        return false;
    }

    @Override
    protected int getTotalSmeltTime(final RecipeHolder<SmeltingRecipe> recipe) {
        return Mth.ceil(recipe.value().cookingTime() / smeltingSpeed / (speedMultiplier > 0.0F ? speedMultiplier : 1.0F));
    }

    @Override
    protected int getTotalAlloyTime(final RecipeHolder<AlloyingRecipe> recipe) {
        return Mth.ceil(recipe.value().cookingTime() / alloyingSpeed / (speedMultiplier > 0.0F ? speedMultiplier : 1.0F));
    }

    private boolean canBurn(final int maxStackSize, final ItemStack burnResult) {
        for (ItemStack resultItemStack : results) {
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
}
