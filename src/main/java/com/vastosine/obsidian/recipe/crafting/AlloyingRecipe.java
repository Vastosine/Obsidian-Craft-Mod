package com.vastosine.obsidian.recipe.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;

import com.vastosine.obsidian.block.OCBlocks;
import com.vastosine.obsidian.recipe.crafting.display.AlloyingRecipeDisplay;
import com.vastosine.obsidian.recipe.crafting.display.IngredientWithCountSlotDisplay;
import com.vastosine.obsidian.utils.RecipeMatches;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

public class AlloyingRecipe implements Recipe<AlloyingInput> {
    public static final MapCodec<AlloyingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                            IngredientWithCount.CODEC.listOf().fieldOf("ingredients").forGetter(o -> o.ingredients),
                            ItemStackTemplate.CODEC.fieldOf("results").forGetter(o -> o.result),
                            Codec.INT.optionalFieldOf("cookingTime", 200).forGetter(o -> o.cookingTime),
                            Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(o -> o.experience)
                    )
                    .apply(i, AlloyingRecipe::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, AlloyingRecipe> STREAM_CODEC = StreamCodec.composite(
            IngredientWithCount.STREAM_CODEC.apply(ByteBufCodecs.list()), o -> o.ingredients,
            ItemStackTemplate.STREAM_CODEC, o -> o.result,
            ByteBufCodecs.INT, o -> o.cookingTime,
            ByteBufCodecs.FLOAT, o -> o.experience,
            AlloyingRecipe::new
    );
    public static final RecipeSerializer<AlloyingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final List<IngredientWithCount> ingredients;
    private final ItemStackTemplate result;
    private final int cookingTime;
    private final float experience;
    private PlacementInfo placementInfo;

    public AlloyingRecipe(
            List<IngredientWithCount> ingredients,
            ItemStackTemplate result,
            int cookingTime,
            float experience) {
        this.ingredients = ingredients;
        this.result = result;
        this.cookingTime = cookingTime;
        this.experience = experience;
    }

    public int cookingTime() {
        return cookingTime;
    }

    public float experience() {
        return experience;
    }

    // input will be consumed
    @Override
    public boolean matches(AlloyingInput input, Level level) {
        return get_match(input) != null;
    }

    private int[] get_match(AlloyingInput input) {
        return RecipeMatches.getMatches(ingredients, input);
    }

    public void consume(AlloyingInput input) {
        int[] match = get_match(input);
        for (int i = 0; i < input.size() && i < match.length; i++) {
            ItemStack itemStack = input.getItem(i);
            itemStack.setCount(itemStack.count() - match[i]);
        }
    }

    @Override
    public ItemStack assemble(AlloyingInput input) {
        return this.result.create();
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<? extends Recipe<AlloyingInput>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<AlloyingInput>> getType() {
        return OCRecipeTypes.ALLOYING;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (placementInfo == null) {
            List<Ingredient> ingredientList = new ArrayList<>();
            ingredients.forEach(p -> ingredientList.add(p.ingredient()));
            placementInfo = PlacementInfo.create(ingredientList);
        }
        return placementInfo;
    }

    @Override
    public List<RecipeDisplay> display() {
        List<SlotDisplay> ingredientsDisplay = new ArrayList<>();
        ingredients.forEach(p -> ingredientsDisplay.add(new IngredientWithCountSlotDisplay(p)));
        List<SlotDisplay> resultDisplay = new ArrayList<>();
        resultDisplay.add(new SlotDisplay.ItemStackSlotDisplay(result));
        return List.of(
                new AlloyingRecipeDisplay(
                        ingredientsDisplay,
                        SlotDisplay.AnyFuel.INSTANCE,
                        resultDisplay,
                        new SlotDisplay.ItemSlotDisplay(OCBlocks.ALLOY_SMELTER.asItem()),
                        cookingTime,
                        experience
                )
        );
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return OCRecipeBookCategories.ALLOYING;
    }
}
