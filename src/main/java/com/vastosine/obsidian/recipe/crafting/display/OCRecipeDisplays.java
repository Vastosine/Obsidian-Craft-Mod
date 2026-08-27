package com.vastosine.obsidian.recipe.crafting.display;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

public class OCRecipeDisplays {
    private static void register(final String name, RecipeDisplay.Type<?> type) {
        Registry.register(BuiltInRegistries.RECIPE_DISPLAY, ObsidianCraft.id(name), type);
    }

    public static void onInitialize() {
        ObsidianCraft.onInitializeInfo("Recipe Displays");
        register("alloying", NeedFuelRecipeDisplay.TYPE);
    }
}
