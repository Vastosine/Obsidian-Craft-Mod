package com.vastosine.obsidian;

import com.vastosine.obsidian.block.ModBlocks;
import com.vastosine.obsidian.block.entity.ModBlockEntities;
import com.vastosine.obsidian.item.ModCreativeModeTabs;
import com.vastosine.obsidian.item.ModItems;
import com.vastosine.obsidian.menu.ModMenuTypes;
import com.vastosine.obsidian.recipe.AlloyRecipeDisplay;
import com.vastosine.obsidian.recipe.ModRecipeBookCategories;
import com.vastosine.obsidian.recipe.ModRecipeSerializers;
import com.vastosine.obsidian.recipe.ModRecipeTypes;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObsidianCraft implements ModInitializer {
	public static final String MOD_ID = "obsidian";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		// The order matters: ModBlocks must exist before the block entity type,
		// and the recipe book category before any recipe codec is used
		// (the alloy recipe codec serializes its category from the registry).
		ModBlocks.init();
		ModItems.init();
		ModRecipeBookCategories.init();
		ModRecipeTypes.init();
		ModRecipeSerializers.init();
		ModBlockEntities.init();
		ModMenuTypes.init();
		AlloyRecipeDisplay.register();
		ModCreativeModeTabs.init();

		LOGGER.info("Hello Fabric world!");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
