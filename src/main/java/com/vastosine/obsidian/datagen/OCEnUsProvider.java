package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.datagen.custom.OCLanguageProvider;
import com.vastosine.obsidian.item.OCCreativeModeTabs;
import com.vastosine.obsidian.stats.OCStats;
import com.vastosine.obsidian.tags.OCBlockTags;
import com.vastosine.obsidian.tags.OCItemTags;
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
                "tooltip.obsidian.obsidian_pickaxe.shift.0", "Mine blocks like §5Obsidian§r to repair it!",
                "tooltip.obsidian.obsidian_pickaxe.shift.1", "More efficiency when mining blocks like §5Obsidian§r!",

                // Item Tags
                OCItemTags.OBSIDIAN_ARMOR_MATERIALS, "Obsidian Armor Materials",
                OCItemTags.OBSIDIAN_ARMORS, "Obsidian Armor",
                OCItemTags.OBSIDIAN_TOOL_MATERIALS, "Obsidian Tool Materials",

                // Blocks
                OBSIDIAN_BLOCK, "Obsidian Block",
                OBSIDIAN_FURNACE, "Obsidian Furnace",
                ALLOY_SMELTER, "Alloy Smelter",

                // Block Entities
                "container.obsidian.obsidian_furnace", "Obsidian Furnace",
                "container.obsidian.alloy_smelter", "Alloy Smelter",

                OCStats.INTERACT_WITH_OBSIDIAN_FURNACE.toLanguageKey("stat"), "Interactions With Obsidian Furnace",
                OCStats.INTERACT_WITH_ALLOY_SMELTER.toLanguageKey("stat"), "Interactions With Alloy Smelter",

                // Block Tags
                OCBlockTags.INCORRECT_FOR_OBSIDIAN_TOOL, "Incorrect for Obsidian Tool",
                OCBlockTags.OBSIDIAN_BLOCK, "Obsidian Block"
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
                "obsidian_furnace", "Faster Smelting", "Craft an Obsidian Furnace",
                "alloy_smelter", "Fusing!", "Craft an Alloy Smelter"
        );
    }
}
