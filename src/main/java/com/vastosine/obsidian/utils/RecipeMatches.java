package com.vastosine.obsidian.utils;

import com.vastosine.obsidian.recipe.crafting.OCIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public class RecipeMatches {

    public static <I extends RecipeInput> int[] getMatches(final List<OCIngredient> ingredients, final I inputs) {
        int n = ingredients.size();
        int m = inputs.size();
        BigraphMatch graph = new BigraphMatch(n, m);
        for (int i = 0; i < n; i++) {
            Ingredient ingredient = ingredients.get(i).toVanilla();
            graph.setCount(false, i, ingredients.get(i).count());
            for (int j = 0; j < m; j++) {
                ItemStack input = inputs.getItem(j);
                if (ingredient.test(input)) {
                    graph.addEdge(i, j);
                }
            }
        }
        for (int i = 0; i < m; i++) {
            graph.setCount(true, i, inputs.getItem(i).count());
        }
        return graph.run();
    }
}
