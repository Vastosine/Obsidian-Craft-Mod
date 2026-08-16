package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.datagen.custom.OCLanguageProvider;
import com.vastosine.obsidian.item.OCCreativeModeTabs;
import com.vastosine.obsidian.stats.OCStats;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

import static com.vastosine.obsidian.block.OCBlocks.*;
import static com.vastosine.obsidian.item.OCItems.*;

public class OCEnUsProvider extends OCLanguageProvider {
    public OCEnUsProvider(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(packOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        add(translationBuilder,
                // Items
                OBSIDIAN_INGOT, "Obsidian Ingot",
                OBSIDIAN_SWORD, "Obsidian Sword",
                OBSIDIAN_PICKAXE, "Obsidian Pickaxe",
                OBSIDIAN_SHOVEL, "Obsidian Shovel",
                OBSIDIAN_HOE, "Obsidian Hoe",
                OBSIDIAN_AXE, "Obsidian Axe",
                OBSIDIAN_HELMET, "Obsidian Helmet",
                OBSIDIAN_CHESTPLATE, "Obsidian Chestplate",
                OBSIDIAN_LEGGINGS, "Obsidian Leggings",
                OBSIDIAN_BOOTS, "Obsidian Boots",

                // Tooltips
                "tooltip.obsidian.obsidian_pickaxe", "Press [§aShift§r] to learn more.",
                "tooltip.obsidian.obsidian_pickaxe.shift.1", "Mine blocks like §5Obsidian§r to repair it!",
                "tooltip.obsidian.obsidian_pickaxe.shift.2", "More efficiency when mining blocks like §5Obsidian§r!",

                // Blocks
                OBSIDIAN_BLOCK, "Obsidian Block",
                OBSIDIAN_FURNACE, "Obsidian Furnace",

                // Block Entities
                "container.obsidian.obsidian_furnace", "Obsidian Furnace",
                OCStats.INTERACT_WITH_OBSIDIAN_FURNACE, "Interact With Obsidian Furnace"
        );

        // itemGroups
        translationBuilder.add(OCCreativeModeTabs.OBSIDIAN_CRAFT_KEY, "Obsidian Craft");

        // Advancement
        addAdvancement(
                translationBuilder,
                "root", "Obsidian Craft", "Welcome to Obsidian Craft Mod!",
                "smelt_obsidian", "Harder", "Smelt an Obsidian Ingot",
                "obsidian_pickaxe", "Obsidian Miner", "Craft an Obsidian Pickaxe",
                "obsidian_armor", "More Fire Protection", "Protect yourself with an piece of Obsidian Armor",
                "obsidian_furnace", "Faster Smelting", "Craft an Obsidian Furnace"
        );
    }
}
