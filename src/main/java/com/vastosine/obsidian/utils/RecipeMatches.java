package com.vastosine.obsidian.utils;

import com.vastosine.obsidian.recipe.crafting.OCIngredient;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.Arrays;
import java.util.List;

public class RecipeMatches {

    public static <I extends RecipeInput> int[] getMatches(final List<OCIngredient> ingredients, I inputs) {
        return new BigraphMatch(
                ingredients.stream().mapToInt(OCIngredient::count).toArray(),
                Arrays.stream(OCUtils.getSequence(inputs.size())).map(i -> inputs.getItem(i).count()).toArray(),
                (u, v) -> ingredients.get(u).testWithoutCount(inputs.getItem(v))
        ).run();
    }
}
