package com.vastosine.obsidian.block.entity;

import com.vastosine.obsidian.recipe.crafting.OCRecipeInput;
import com.vastosine.obsidian.recipe.crafting.AlloyingRecipe;
import com.vastosine.obsidian.recipe.crafting.OCRecipeTypes;
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
        return loadAlloyingRecipe(level) || loadSmeltingRecipe(level);
    }

    private boolean loadAlloyingRecipe(ServerLevel level) {
        OCRecipeInput alloyingInput = new OCRecipeInput(inputs);
        List<RecipeHolder<AlloyingRecipe>> recipes = level
                .recipeAccess()
                .getAllMatches(OCRecipeTypes.ALLOYING, alloyingInput, level)
                .toList();
        RecipeHolder<AlloyingRecipe> recipe = recipes.stream().filter(
                i -> canCraft(i.value().getResults(slotResultCount))
        ).max(Comparator.comparingInt(p -> p.value().ingredientSize())).orElse(null);
        if (recipe == null) return false;
        cookingTotalTime = getTotalAlloyTime(recipe);
        recipeResults.addAll(recipe.value().getResults(slotResultCount));
        recipesUsing.clear();
        recipesUsing.addTo(recipe.id(), 1);
        recipe.value().consume(alloyingInput);
        return true;
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

    protected int getTotalAlloyTime(final RecipeHolder<AlloyingRecipe> recipe) {
        return Mth.ceil(recipe.value().cookingTime() / alloyingSpeed / (speedMultiplier > 0.0F ? speedMultiplier : 1.0F));
    }
}
