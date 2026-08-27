package com.vastosine.obsidian.recipe.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import org.jspecify.annotations.NonNull;

import java.util.List;

public abstract class NeedFuelRecipe extends ProcessingRecipe {
    public NeedFuelRecipe(CommonInfo commonInfo, List<OCIngredient> ingredients, List<ItemStackTemplate> results, int cost, int speed, float experience) {
        super(commonInfo, ingredients, results, cost, speed, experience);
    }

    public NeedFuelRecipe(CommonInfo commonInfo, List<OCIngredient> ingredients, List<ItemStackTemplate> results, int cost, float experience) {
        super(commonInfo, ingredients, results, cost, 1, experience);
    }

    @FunctionalInterface
    public interface Factory<T extends ProcessingRecipe> {
        T create(
                CommonInfo commonInfo,
                List<OCIngredient> ingredients,
                List<ItemStackTemplate> results,
                int cost,
                float experience
        );
    }

    public static <T extends ProcessingRecipe> ProcessingRecipe.Factory<T> factoryTransformer(Factory<T> factory) {
        return (CommonInfo commonInfo, List<OCIngredient> ingredients, List<ItemStackTemplate> results, int cost, int speed, float experience) ->
                factory.create(commonInfo, ingredients, results, cost, experience);
    }


    public static <T extends ProcessingRecipe> @NonNull MapCodec<T> getMapCodec(Factory<T> factory) {
        return getMapCodec(factoryTransformer(factory), s -> s.equals("cost") ? "cookingTime" : s);
    }

    public static <T extends ProcessingRecipe> @NonNull StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec(Factory<T> factory) {
        return getStreamCodec(factoryTransformer(factory));
    }

    public int cookingTime() {
        return cost();
    }
}
