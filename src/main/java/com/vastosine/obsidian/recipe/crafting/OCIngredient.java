package com.vastosine.obsidian.recipe.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
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

    public OCIngredient(HolderSet<Item> values) {
        this(values, 1);
    }

    private static Holder.Reference<Item> toHolder(ItemLike itemLike) {
        return itemLike.asItem().builtInRegistryHolder();
    }

    public static OCIngredient of(ItemLike itemLike) {
        return of(1, itemLike);
    }

    public static OCIngredient of(final ItemLike... items) {
        return of(1, Arrays.stream(items));
    }

    public static OCIngredient of(final Stream<? extends ItemLike> stream) {
        return of(1, stream);
    }

    public static OCIngredient of(final HolderSet<Item> tag) {
        return of(1, tag);
    }

    public static OCIngredient of(int count, ItemLike itemLike) {
        return new OCIngredient(HolderSet.direct(toHolder(itemLike)), count);
    }

    public static OCIngredient of(int count, final ItemLike... items) {
        return of(count, Arrays.stream(items));
    }

    public static OCIngredient of(int count, final Stream<? extends ItemLike> stream) {
        return new OCIngredient(HolderSet.direct(stream.map(e -> toHolder(e.asItem())).toList()), count);
    }

    public static OCIngredient of(int count, final HolderSet<Item> tag) {
        return new OCIngredient(tag, count);
    }

    @Override
    public boolean test(ItemStack stack) {
        return toVanilla().test(stack) && stack.count() >= count;
    }

    public boolean testWithoutCount(ItemStack stack) {
        return toVanilla().test(stack);
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
