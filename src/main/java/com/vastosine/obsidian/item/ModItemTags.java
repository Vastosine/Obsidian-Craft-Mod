package com.vastosine.obsidian.item;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * Item tags used by the Alloy Furnace. Keeping the material lists in tags means a new
 * gold or copper source is a datagen tag edit, not a recipe edit.
 */
public final class ModItemTags {
	/** Gold sources for alloying (ingot, ores, raw gold). */
	public static final TagKey<Item> GOLD_MATERIALS = create("gold_materials");
	/** Copper sources for alloying (ingot, ores, raw copper). */
	public static final TagKey<Item> COPPER_MATERIALS = create("copper_materials");
	/** Netherite sources for alloying (netherite scrap, ancient debris). */
	public static final TagKey<Item> DEBRIS_MATERIALS = create("debris_materials");
	/** Empty tag: makes the overridden vanilla netherite_ingot recipe impossible to place. */
	public static final TagKey<Item> RECIPE_REMOVED = create("recipe_removed");

	private ModItemTags() {
	}

	private static TagKey<Item> create(String path) {
		return TagKey.create(Registries.ITEM, ObsidianCraft.id(path));
	}
}
