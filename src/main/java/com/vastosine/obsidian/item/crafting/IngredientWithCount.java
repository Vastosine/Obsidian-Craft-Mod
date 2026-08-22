package com.vastosine.obsidian.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;

public record IngredientWithCount(Ingredient ingredient, int count) {
    public static final MapCodec<IngredientWithCount> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    Ingredient.CODEC.fieldOf("id").forGetter(IngredientWithCount::ingredient),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(IngredientWithCount::count)
            ).apply(i, IngredientWithCount::new)
    );
    public static final Codec<IngredientWithCount> CODEC = Codec.withAlternative(MAP_CODEC.codec(), Ingredient.CODEC, IngredientWithCount::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, IngredientWithCount> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, IngredientWithCount::ingredient,
            ByteBufCodecs.INT, IngredientWithCount::count,
            IngredientWithCount::new
    );

    IngredientWithCount(Ingredient ingredient) {
        this(ingredient, 1);
    }
}
