package com.vastosine.obsidian.recipe.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public record OCRecipeInput(List<ItemStack> inputs) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return inputs.get(index);
    }

    @Override
    public int size() {
        return inputs.size();
    }

    public List<ItemStack> getItemStack() {
        return inputs;
    }
}
