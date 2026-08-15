package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.block.OCBlocks;
import com.vastosine.obsidian.datagen.custom.OCLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

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
                "tooltip.obsidiancraft.obsidian_pickaxe", "Press [§aShift§r] to learn more.",
                "tooltip.obsidiancraft.obsidian_pickaxe.shift.1", "Mine blocks like §5Obsidian§r to repair it!",
                "tooltip.obsidiancraft.obsidian_pickaxe.shift.2", "More efficiency when mining blocks like §5Obsidian§r!",

                // Blocks
                OCBlocks.OBSIDIAN_BLOCK, "Obsidian Block",

                // itemGroups
                "itemGroup.obsidian_craft", "Obsidian Craft"
        );

        // Advancement
        addAdvancement(
                translationBuilder,
                "root", "Obsidian Craft", "Welcome to Obsidian Craft Mod!",
                "smelt_obsidian", "Harder", "Smelt an Obsidian Ingot",
                "obsidian_pickaxe", "Obsidian Miner", "Craft an Obsidian Pickaxe",
                "obsidian_armor", "More Fire Protection", "Protect yourself with an piece of Obsidian Armor"
        );
    }
}
