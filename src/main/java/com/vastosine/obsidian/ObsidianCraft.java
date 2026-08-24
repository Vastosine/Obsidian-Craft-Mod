package com.vastosine.obsidian;

import com.vastosine.obsidian.block.OCBlocks;
import com.vastosine.obsidian.block.entity.OCBlockEntityTypes;
import com.vastosine.obsidian.inventory.menu.OCMenuTypes;
import com.vastosine.obsidian.item.OCCreativeModeTabs;
import com.vastosine.obsidian.item.OCItems;
import com.vastosine.obsidian.item.crafting.OCRecipeBookCategories;
import com.vastosine.obsidian.item.crafting.OCRecipeSerializers;
import com.vastosine.obsidian.item.crafting.OCRecipeTypes;
import com.vastosine.obsidian.stats.OCStats;
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

        LOGGER.info("Hello Fabric world!");
        OCItems.onInitialize();
        OCBlocks.onInitialize();
        OCCreativeModeTabs.onInitialize();
        OCBlockEntityTypes.onInitialize();
        OCStats.onInitialize();
        OCMenuTypes.onInitialize();
        OCRecipeSerializers.onInitialize();
        OCRecipeTypes.onInitialize();
        OCRecipeBookCategories.onInitialize();
    }

	public static final String INFO = "Registering {} for {}";

	public static void onInitializeInfo(final String type) {
		ObsidianCraft.LOGGER.info(INFO, type, MOD_ID);
	}

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
