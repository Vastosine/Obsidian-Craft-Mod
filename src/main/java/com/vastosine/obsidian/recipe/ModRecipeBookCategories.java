package com.vastosine.obsidian.recipe;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeBookCategory;

/**
 * Recipe book categories are registries; alloy recipes get their own category so the
 * recipe book groups them into a dedicated tab (the vanilla cooking categories are
 * hard-coded for the vanilla machines). The category must be registered before any
 * recipe JSON is decoded — the recipe codec serializes it via
 * BuiltInRegistries.RECIPE_BOOK_CATEGORY.byNameCodec(), which datagen exercises too.
 */
public final class ModRecipeBookCategories {
	public static final RecipeBookCategory ALLOY = Registry.register(
		BuiltInRegistries.RECIPE_BOOK_CATEGORY,
		ObsidianCraft.id("alloy"),
		new RecipeBookCategory()
	);

	private ModRecipeBookCategories() {
	}

	public static void init() {
		// Registration happens in the static field above
	}
}
