package com.vastosine.obsidian.recipe.crafting.display;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vastosine.obsidian.recipe.crafting.OCIngredient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.stream.Stream;

public record OCIngredientSlotDisplay(OCIngredient ingredient) implements SlotDisplay {
    public static final MapCodec<OCIngredientSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(OCIngredient.MAP_CODEC.fieldOf("item").forGetter(OCIngredientSlotDisplay::ingredient)).apply(i, OCIngredientSlotDisplay::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, OCIngredientSlotDisplay> STREAM_CODEC = StreamCodec.composite(
            OCIngredient.STREAM_CODEC, OCIngredientSlotDisplay::ingredient, OCIngredientSlotDisplay::new
    );
    public static final SlotDisplay.Type<OCIngredientSlotDisplay> TYPE = new SlotDisplay.Type<>(MAP_CODEC, STREAM_CODEC);


    @Override
    public <T> Stream<T> resolve(ContextMap context, DisplayContentsFactory<T> factory) {
//        return factory instanceof DisplayContentsFactory.ForStacks<T> stacks ? Stream.of(stacks.forStack(this.stack.create())) : Stream.empty();
        return factory instanceof DisplayContentsFactory.ForStacks<T> stacks ? ingredient.values().stream().map(p -> stacks.forStack(new ItemStack(p, ingredient.count()))) : Stream.empty();
    }

    @Override
    public Type<? extends SlotDisplay> type() {
        return TYPE;
    }
}
