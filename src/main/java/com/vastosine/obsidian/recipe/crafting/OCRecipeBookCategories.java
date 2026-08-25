package com.vastosine.obsidian.recipe.crafting;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeBookCategory;

public class OCRecipeBookCategories {
    public static final RecipeBookCategory ALLOYING = register("alloying");

    private static RecipeBookCategory register(final String id) {
        return Registry.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY, ObsidianCraft.id(id), new RecipeBookCategory());
    }

    public static void onInitialize() {
        ObsidianCraft.onInitializeInfo("Recipe Book Categories");
    }
}
