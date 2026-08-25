package com.vastosine.obsidian.recipe.crafting;

import com.mojang.serialization.MapCodec;
import com.vastosine.obsidian.ObsidianCraft;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public class OCCustomIngredientSerializers {
    public static final CustomIngredientSerializer<OCIngredient> OC_INGREDIENT = new CustomIngredientSerializer<>() {
        @Override
        public Identifier getIdentifier() {
            return ObsidianCraft.id("oc_ingredient");
        }

        @Override
        public MapCodec getCodec() {
            return OCIngredient.MAP_CODEC;
        }

        @Override
        public StreamCodec getStreamCodec() {
            return OCIngredient.STREAM_CODEC;
        }
    };

    public static void onInitialize() {
        ObsidianCraft.onInitializeInfo("Custom Ingredient Serializers");
        CustomIngredientSerializer.register(OC_INGREDIENT);
    }
}
