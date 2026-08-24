package com.vastosine.obsidian.item.crafting.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;

public record AlloyingRecipeDisplay(List<SlotDisplay> ingredients, SlotDisplay fuel, List<SlotDisplay> results, SlotDisplay craftingStation, int duration, float experience)
        implements RecipeDisplay {
    public static final MapCodec<AlloyingRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                            SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(AlloyingRecipeDisplay::ingredients),
                            SlotDisplay.CODEC.fieldOf("fuel").forGetter(AlloyingRecipeDisplay::fuel),
                            SlotDisplay.CODEC.listOf().fieldOf("results").forGetter(AlloyingRecipeDisplay::results),
                            SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(AlloyingRecipeDisplay::craftingStation),
                            Codec.INT.fieldOf("duration").forGetter(AlloyingRecipeDisplay::duration),
                            Codec.FLOAT.fieldOf("experience").forGetter(AlloyingRecipeDisplay::experience)
                    )
                    .apply(i, AlloyingRecipeDisplay::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, AlloyingRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
            SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()), AlloyingRecipeDisplay::ingredients,
            SlotDisplay.STREAM_CODEC, AlloyingRecipeDisplay::fuel,
            SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()), AlloyingRecipeDisplay::results,
            SlotDisplay.STREAM_CODEC, AlloyingRecipeDisplay::craftingStation,
            ByteBufCodecs.VAR_INT, AlloyingRecipeDisplay::duration,
            ByteBufCodecs.FLOAT, AlloyingRecipeDisplay::experience,
            AlloyingRecipeDisplay::new
    );
    public static final RecipeDisplay.Type<AlloyingRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public RecipeDisplay.Type<AlloyingRecipeDisplay> type() {
        return TYPE;
    }

    @Override
    public SlotDisplay result() {
        return results.getFirst();
    }

    @Override
    public boolean isEnabled(final FeatureFlagSet enabledFeatures) {
        for (SlotDisplay ingredient : ingredients) {
            if (!ingredient.isEnabled(enabledFeatures)) return false;
        }
        for (SlotDisplay result : results) {
            if (!result.isEnabled(enabledFeatures)) return false;
        }
        return this.fuel().isEnabled(enabledFeatures) && RecipeDisplay.super.isEnabled(enabledFeatures);
    }
}
