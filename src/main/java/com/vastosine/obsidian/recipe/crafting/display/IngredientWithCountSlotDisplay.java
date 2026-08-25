package com.vastosine.obsidian.recipe.crafting.display;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vastosine.obsidian.recipe.crafting.IngredientWithCount;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.stream.Stream;

public record IngredientWithCountSlotDisplay(IngredientWithCount ingredient) implements SlotDisplay {
    public static final MapCodec<IngredientWithCountSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(IngredientWithCount.CODEC.fieldOf("item").forGetter(IngredientWithCountSlotDisplay::ingredient)).apply(i, IngredientWithCountSlotDisplay::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, IngredientWithCountSlotDisplay> STREAM_CODEC = StreamCodec.composite(
            IngredientWithCount.STREAM_CODEC, IngredientWithCountSlotDisplay::ingredient, IngredientWithCountSlotDisplay::new
    );
    public static final SlotDisplay.Type<IngredientWithCountSlotDisplay> TYPE = new SlotDisplay.Type<>(MAP_CODEC, STREAM_CODEC);


    @Override
    public <T> Stream<T> resolve(ContextMap context, DisplayContentsFactory<T> factory) {
//        return factory instanceof DisplayContentsFactory.ForStacks<T> stacks ? Stream.of(stacks.forStack(this.stack.create())) : Stream.empty();
        return factory instanceof DisplayContentsFactory.ForStacks<T> ? ingredient.display().resolve(context, factory) : Stream.empty();
    }

    @Override
    public Type<? extends SlotDisplay> type() {
        return TYPE;
    }
}
