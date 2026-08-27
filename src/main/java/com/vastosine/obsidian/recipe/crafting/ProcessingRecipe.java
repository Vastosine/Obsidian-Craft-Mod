package com.vastosine.obsidian.recipe.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public abstract class ProcessingRecipe implements Recipe<ProcessingInput> {
    public static final int DEFAULT_COST = 200;

    protected final CommonInfo commonInfo;
    private final List<OCIngredient> ingredients;
    private final List<ItemStackTemplate> results;
    private final int cost;
    private final int speed;
    private final float experience;
    private PlacementInfo placementInfo;

    public ProcessingRecipe(CommonInfo commonInfo, List<OCIngredient> ingredients, List<ItemStackTemplate> results, int cost, int speed, float experience) {
        this.commonInfo = commonInfo;
        this.ingredients = ingredients;
        this.results = results;
        this.cost = cost;
        this.speed = speed;
        this.experience = experience;
    }

    @FunctionalInterface
    public interface Factory<T extends ProcessingRecipe> {
        T create(
                CommonInfo commonInfo,
                List<OCIngredient> ingredients,
                List<ItemStackTemplate> results,
                int cost,
                int speed,
                float experience
        );
    }

    public static <T extends ProcessingRecipe> @NonNull MapCodec<T> getMapCodec(Factory<T> factory, UnaryOperator<String> operator, int defaultCost) {
        return RecordCodecBuilder.mapCodec(
                i -> i.group(
                                CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
                                OCIngredient.CODEC.listOf().fieldOf(operator.apply("ingredients")).forGetter(ProcessingRecipe::ingredients),
                                Codec.withAlternative(
                                        ItemStackTemplate.CODEC.listOf(), ItemStackTemplate.CODEC, List::of
                                ).fieldOf(operator.apply("results")).forGetter(ProcessingRecipe::results),
                                Codec.INT.optionalFieldOf(operator.apply("cost"), defaultCost).forGetter(ProcessingRecipe::cost),
                                Codec.INT.optionalFieldOf(operator.apply("speed"), 1).forGetter(ProcessingRecipe::speed),
                                Codec.FLOAT.optionalFieldOf(operator.apply("experience"), 0.0F).forGetter(ProcessingRecipe::experience)
                        )
                        .apply(i, factory::create)
        );
    }

    public static <T extends ProcessingRecipe> @NonNull MapCodec<T> getMapCodec(Factory<T> factory, UnaryOperator<String> operator) {
        return getMapCodec(factory, operator, DEFAULT_COST);
    }

    public static <T extends ProcessingRecipe> @NonNull MapCodec<T> getMapCodec(Factory<T> factory) {
        return getMapCodec(factory, s -> s);
    }

    public static <T extends ProcessingRecipe> @NonNull StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec(Factory<T> factory) {
        return StreamCodec.composite(
                CommonInfo.STREAM_CODEC, o -> o.commonInfo,
                OCIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), ProcessingRecipe::ingredients,
                ItemStackTemplate.STREAM_CODEC.apply(ByteBufCodecs.list()), ProcessingRecipe::results,
                ByteBufCodecs.INT, ProcessingRecipe::cost,
                ByteBufCodecs.INT, ProcessingRecipe::speed,
                ByteBufCodecs.FLOAT, ProcessingRecipe::experience,
                factory::create
        );
    }

    public List<OCIngredient> ingredients() {
        return ingredients;
    }

    public List<ItemStackTemplate> results() {
        return results;
    }

    public int cost() {
        return cost;
    }

    public int speed() {
        return speed;
    }

    public float experience() {
        return this.experience;
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

    // input will be consumed
    @Override
    public boolean matches(ProcessingInput input, Level level) {
        return get_match(input) != null;
    }

    private int[] get_match(ProcessingInput input) {
        return RecipeMatches.getMatches(ingredients, input);
    }

    public void consume(ProcessingInput input) {
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
    public ItemStack assemble(ProcessingInput input) {
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
        // TODO
        List<SlotDisplay> ingredientsDisplay = ingredients.stream().map(OCIngredientSlotDisplay::new).collect(Collectors.toUnmodifiableList());
        List<SlotDisplay> resultDisplay = results.stream().map(SlotDisplay.ItemStackSlotDisplay::new).collect(Collectors.toUnmodifiableList());
        return List.of(
                new AlloyingRecipeDisplay(
                        ingredientsDisplay,
                        SlotDisplay.AnyFuel.INSTANCE,
                        resultDisplay,
                        new SlotDisplay.ItemSlotDisplay(OCBlocks.ALLOY_SMELTER.asItem()),
                        cost,
                        experience
                )
        );
    }
}
