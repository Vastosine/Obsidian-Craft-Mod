package com.vastosine.obsidian.recipe;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * The alloy recipe type. Registered under obsidian:alloy_furnace so it never
 * collides with the vanilla minecraft:smelting types (RecipeType.register()
 * would hard-code the minecraft namespace, hence the manual Registry.register).
 */
public final class ModRecipeTypes {
	public static final RecipeType<AlloyFurnaceRecipe> ALLOY_FURNACE = Registry.register(
		BuiltInRegistries.RECIPE_TYPE,
		ObsidianCraft.id("alloy_furnace"),
		new RecipeType<AlloyFurnaceRecipe>() {
			@Override
			public String toString() {
				return "alloy_furnace";
			}
		}
	);

	private ModRecipeTypes() {
	}

	public static void init() {
		// Registration happens in the static field above
	}
}
