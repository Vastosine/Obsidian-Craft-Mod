package com.vastosine.obsidian.recipe.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vastosine.obsidian.registry.OCCustomIngredientSerializers;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.HolderSetCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.stream.Stream;

public record OCIngredient(HolderSet<Item> values, int count) implements CustomIngredient {
    public static final Codec<HolderSet<Item>> NON_AIR_HOLDER_SET_CODEC = ExtraCodecs.nonEmptyHolderSet(HolderSetCodec.create(Registries.ITEM, Item.CODEC, false));

    public static final MapCodec<OCIngredient> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    NON_AIR_HOLDER_SET_CODEC.fieldOf("value").forGetter(OCIngredient::values),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(OCIngredient::count)
            ).apply(i, OCIngredient::new)
    );

    public static final Codec<OCIngredient> CODEC = Codec.withAlternative(MAP_CODEC.codec(), NON_AIR_HOLDER_SET_CODEC.xmap(OCIngredient::new, OCIngredient::values));

    public static final StreamCodec<RegistryFriendlyByteBuf, OCIngredient> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderSet(Registries.ITEM), OCIngredient::values,
            ByteBufCodecs.INT, OCIngredient::count,
            OCIngredient::new
    );

    private OCIngredient(HolderSet<Item> values) {
        this(values, 1);
    }

    @Override
    public boolean test(ItemStack stack) {
        return false;
    }

    @Override
    public Stream<Holder<Item>> items() {
        return this.values.stream();
    }

    @Override
    public boolean requiresTesting() {
        return false;
    }

    @Override
    public CustomIngredientSerializer<?> getSerializer() {
        return OCCustomIngredientSerializers.OC_INGREDIENT;
    }

    @Override
    public Ingredient toVanilla() {
        return Ingredient.of(values);
    }
}
