package com.vastosine.obsidian.recipe.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vastosine.obsidian.block.OCBlocks;
import com.vastosine.obsidian.recipe.crafting.display.AlloyingRecipeDisplay;
import com.vastosine.obsidian.recipe.crafting.display.OCIngredientSlotDisplay;
import com.vastosine.obsidian.utils.RecipeMatches;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

public class AlloyingRecipe implements Recipe<OCRecipeInput> {
    public static final Codec<List<ItemStackTemplate>> RESULT_CODEC = Codec.withAlternative(
            ItemStackTemplate.CODEC.listOf(), ItemStackTemplate.CODEC, List::of
    );

    public static final MapCodec<AlloyingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                            OCIngredient.CODEC.listOf().fieldOf("ingredients").forGetter(o -> o.ingredients),
                            RESULT_CODEC.fieldOf("results").forGetter(o -> o.results),
                            Codec.INT.optionalFieldOf("cookingTime", 200).forGetter(o -> o.cookingTime),
                            Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(o -> o.experience)
                    )
                    .apply(i, AlloyingRecipe::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, AlloyingRecipe> STREAM_CODEC = StreamCodec.composite(
            OCIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), o -> o.ingredients,
            ItemStackTemplate.STREAM_CODEC.apply(ByteBufCodecs.list()), o -> o.results,
            ByteBufCodecs.INT, o -> o.cookingTime,
            ByteBufCodecs.FLOAT, o -> o.experience,
            AlloyingRecipe::new
    );
    public static final RecipeSerializer<AlloyingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final List<OCIngredient> ingredients;
    private final List<ItemStackTemplate> results;
    private final int cookingTime;
    private final float experience;
    private PlacementInfo placementInfo;

    public AlloyingRecipe(
            List<OCIngredient> ingredients,
            ItemStackTemplate result,
            int cookingTime,
            float experience) {
        this(ingredients, List.of(result), cookingTime, experience);
    }

    public AlloyingRecipe(
            List<OCIngredient> ingredients,
            List<ItemStackTemplate> result,
            int cookingTime,
            float experience) {
        this.ingredients = ingredients;
        this.results = result;
        this.cookingTime = cookingTime;
        this.experience = experience;
    }

    private int count_sum = 0;
    public int ingredientSize() {
        if (count_sum > 0) return count_sum;
        int count = 0;
        for (OCIngredient ingredient : ingredients) {
            count += ingredient.count();
        }
        return count_sum = count;
    }

    public int cookingTime() {
        return cookingTime;
    }

    public float experience() {
        return experience;
    }

    // input will be consumed
    @Override
    public boolean matches(OCRecipeInput input, Level level) {
        return get_match(input) != null;
    }

    private int[] get_match(OCRecipeInput input) {
        return RecipeMatches.getMatches(ingredients, input);
    }

    public void consume(OCRecipeInput input) {
        int[] match = get_match(input);
        for (int i = 0; i < input.size() && i < match.length; i++) {
            ItemStack itemStack = input.getItem(i);
            itemStack.setCount(itemStack.count() - match[i]);
        }
    }

    public List<ItemStack> getResults() {
        return this.results.stream().map(ItemStackTemplate::create).toList();
    }

    public List<ItemStack> getResults(int count) {
        return this.results.stream().limit(count).map(ItemStackTemplate::create).toList();
    }

    @Override
    public ItemStack assemble(OCRecipeInput input) {
        return this.results.getFirst().create();
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
    public RecipeSerializer<? extends Recipe<OCRecipeInput>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<OCRecipeInput>> getType() {
        return OCRecipeTypes.ALLOYING;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (placementInfo == null) {
            List<Ingredient> ingredientList = new ArrayList<>();
            ingredients.forEach(p -> ingredientList.add(Ingredient.of(p.values())));
            placementInfo = PlacementInfo.create(ingredientList);
        }
        return placementInfo;
    }

    @Override
    public List<RecipeDisplay> display() {
        List<SlotDisplay> ingredientsDisplay = ingredients.stream().map(OCIngredientSlotDisplay::new).collect(Collectors.toUnmodifiableList());
        List<SlotDisplay> resultDisplay = results.stream().map(SlotDisplay.ItemStackSlotDisplay::new).collect(Collectors.toUnmodifiableList());
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
