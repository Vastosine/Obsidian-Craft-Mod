package com.vastosine.obsidian.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;

/**
 * Recipe book display for an alloy recipe. A custom type is required because
 * FurnaceRecipeDisplay has a single ingredient slot while an alloy recipe can
 * consume up to three. Registered in BuiltInRegistries.RECIPE_DISPLAY so the
 * codec dispatch (RecipeDisplay.CODEC/STREAM_CODEC) can round-trip it.
 */
public record AlloyRecipeDisplay(
	List<SlotDisplay> ingredients,
	SlotDisplay fuel,
	SlotDisplay result,
	SlotDisplay craftingStation,
	int duration,
	float experience
) implements RecipeDisplay {
	public static final MapCodec<AlloyRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
		i -> i.group(
			SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(AlloyRecipeDisplay::ingredients),
			SlotDisplay.CODEC.fieldOf("fuel").forGetter(AlloyRecipeDisplay::fuel),
			SlotDisplay.CODEC.fieldOf("result").forGetter(AlloyRecipeDisplay::result),
			SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(AlloyRecipeDisplay::craftingStation),
			Codec.INT.fieldOf("duration").forGetter(AlloyRecipeDisplay::duration),
			Codec.FLOAT.fieldOf("experience").forGetter(AlloyRecipeDisplay::experience)
		).apply(i, AlloyRecipeDisplay::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, AlloyRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
		SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()),
		AlloyRecipeDisplay::ingredients,
		SlotDisplay.STREAM_CODEC,
		AlloyRecipeDisplay::fuel,
		SlotDisplay.STREAM_CODEC,
		AlloyRecipeDisplay::result,
		SlotDisplay.STREAM_CODEC,
		AlloyRecipeDisplay::craftingStation,
		ByteBufCodecs.VAR_INT,
		AlloyRecipeDisplay::duration,
		ByteBufCodecs.FLOAT,
		AlloyRecipeDisplay::experience,
		AlloyRecipeDisplay::new
	);

	public static final RecipeDisplay.Type<AlloyRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

	@Override
	public RecipeDisplay.Type<AlloyRecipeDisplay> type() {
		return TYPE;
	}

	/**
	 * Idempotent registration. onInitialize runs before the datagen hook (which fires
	 * after the registries have been frozen), and the datagen init chain re-runs this
	 * method, so the second call must be a no-op instead of throwing "already frozen".
	 */
	public static void register() {
		if (!BuiltInRegistries.RECIPE_DISPLAY.containsKey(ObsidianCraft.id("alloy_furnace"))) {
			Registry.register(BuiltInRegistries.RECIPE_DISPLAY, ObsidianCraft.id("alloy_furnace"), TYPE);
		}
	}
}
