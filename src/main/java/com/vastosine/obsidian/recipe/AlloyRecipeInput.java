package com.vastosine.obsidian.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Recipe input for the Alloy Furnace: three ordered ingredient slots.
 * Matching against this input is order-insensitive (see AlloyFurnaceRecipe.allocate),
 * so the slots are just three material containers.
 */
public record AlloyRecipeInput(ItemStack first, ItemStack second, ItemStack third) implements RecipeInput {
	@Override
	public ItemStack getItem(final int index) {
		return switch (index) {
			case 0 -> this.first;
			case 1 -> this.second;
			case 2 -> this.third;
			default -> throw new IllegalArgumentException("No item for index " + index);
		};
	}

	@Override
	public int size() {
		return 3;
	}
}
