package com.vastosine.obsidian.recipe.crafting;

import com.mojang.serialization.MapCodec;

import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;

public class AlloyingRecipe extends NeedFuelRecipe {
    public AlloyingRecipe(CommonInfo commonInfo, List<OCIngredient> ingredients, List<ItemStackTemplate> results, int cost, float experience) {
        super(commonInfo, ingredients, results, cost, experience);
    }

    public static final MapCodec<AlloyingRecipe> MAP_CODEC = getMapCodec(AlloyingRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, AlloyingRecipe> STREAM_CODEC = getStreamCodec(AlloyingRecipe::new);
    public static final RecipeSerializer<AlloyingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public RecipeSerializer<? extends Recipe<ProcessingInput>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<ProcessingInput>> getType() {
        return OCRecipeTypes.ALLOYING;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return OCRecipeBookCategories.ALLOYING;
    }
}
