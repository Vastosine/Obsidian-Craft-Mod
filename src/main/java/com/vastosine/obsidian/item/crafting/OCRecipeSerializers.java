package com.vastosine.obsidian.item.crafting;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class OCRecipeSerializers {
    private static void register(final String name, RecipeSerializer<?> serializer) {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ObsidianCraft.id(name), serializer);
    }

    public static void onInitialize() {
        ObsidianCraft.onInitializeInfo("Recipe Serializers");
        register("alloying", AlloyingRecipe.SERIALIZER);
    }
}
