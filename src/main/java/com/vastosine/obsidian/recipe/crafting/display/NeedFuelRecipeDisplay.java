package com.vastosine.obsidian.recipe.crafting.display;

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

public record NeedFuelRecipeDisplay(List<SlotDisplay> ingredients, SlotDisplay fuel, List<SlotDisplay> results, SlotDisplay craftingStation, int duration, float experience)
        implements RecipeDisplay {
    public static final MapCodec<NeedFuelRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                            SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(NeedFuelRecipeDisplay::ingredients),
                            SlotDisplay.CODEC.fieldOf("fuel").forGetter(NeedFuelRecipeDisplay::fuel),
                            SlotDisplay.CODEC.listOf().fieldOf("results").forGetter(NeedFuelRecipeDisplay::results),
                            SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(NeedFuelRecipeDisplay::craftingStation),
                            Codec.INT.fieldOf("duration").forGetter(NeedFuelRecipeDisplay::duration),
                            Codec.FLOAT.fieldOf("experience").forGetter(NeedFuelRecipeDisplay::experience)
                    )
                    .apply(i, NeedFuelRecipeDisplay::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, NeedFuelRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
            SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()), NeedFuelRecipeDisplay::ingredients,
            SlotDisplay.STREAM_CODEC, NeedFuelRecipeDisplay::fuel,
            SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()), NeedFuelRecipeDisplay::results,
            SlotDisplay.STREAM_CODEC, NeedFuelRecipeDisplay::craftingStation,
            ByteBufCodecs.VAR_INT, NeedFuelRecipeDisplay::duration,
            ByteBufCodecs.FLOAT, NeedFuelRecipeDisplay::experience,
            NeedFuelRecipeDisplay::new
    );
    public static final RecipeDisplay.Type<NeedFuelRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public RecipeDisplay.Type<NeedFuelRecipeDisplay> type() {
        return TYPE;
    }

    @Override
    public SlotDisplay result() {
        return results.getFirst();
    }

    public List<SlotDisplay> ingredients() {
        return ingredients;
    }

    @Override
    public boolean isEnabled(final FeatureFlagSet enabledFeatures) {
        return ingredients.stream().allMatch(i -> i.isEnabled(enabledFeatures)) &&
                results.stream().allMatch(i -> i.isEnabled(enabledFeatures)) &&
                this.fuel().isEnabled(enabledFeatures) && RecipeDisplay.super.isEnabled(enabledFeatures);
    }
}
