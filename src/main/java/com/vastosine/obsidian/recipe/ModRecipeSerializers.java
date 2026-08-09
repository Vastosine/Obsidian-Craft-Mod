package com.vastosine.obsidian.recipe;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * The alloy recipe serializer (26.3 serializers are records of a map codec and a
 * stream codec, see AlloyFurnaceRecipe.MAP_CODEC / STREAM_CODEC).
 */
public final class ModRecipeSerializers {
	public static final RecipeSerializer<AlloyFurnaceRecipe> ALLOY_FURNACE = Registry.register(
		BuiltInRegistries.RECIPE_SERIALIZER,
		ObsidianCraft.id("alloy_furnace"),
		new RecipeSerializer<>(AlloyFurnaceRecipe.MAP_CODEC, AlloyFurnaceRecipe.STREAM_CODEC)
	);

	private ModRecipeSerializers() {
	}

	public static void init() {
		// Registration happens in the static field above
	}
}
