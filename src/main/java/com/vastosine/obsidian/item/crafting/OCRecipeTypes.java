package com.vastosine.obsidian.item.crafting;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public class OCRecipeTypes {
    public static final RecipeType<AlloyingRecipe> ALLOYING = register("alloying");

    private static <T extends Recipe<?>> RecipeType<T> register(final String name) {
        return Registry.register(BuiltInRegistries.RECIPE_TYPE, ObsidianCraft.id(name), new RecipeType<T>() {
            @Override
            public String toString() {
                return name;
            }
        });
    }

    public static void onInitialize() {
        ObsidianCraft.onInitializeInfo("Recipe Types");
    }
}
