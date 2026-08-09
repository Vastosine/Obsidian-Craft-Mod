package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.block.OCBlocks;
import com.vastosine.obsidian.item.OCItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class OCEnUsProvider extends FabricLanguageProvider {
    public OCEnUsProvider(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(packOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        // Items
        translationBuilder.add(OCItems.OBSIDIAN_INGOT, "Obsidian Ingot");
        translationBuilder.add(OCItems.OBSIDIAN_SWORD, "Obsidian Sword");
        translationBuilder.add(OCItems.OBSIDIAN_PICKAXE, "Obsidian Pickaxe");
        translationBuilder.add(OCItems.OBSIDIAN_SHOVEL, "Obsidian Shovel");
        translationBuilder.add(OCItems.OBSIDIAN_HOE, "Obsidian Hoe");
        translationBuilder.add(OCItems.OBSIDIAN_AXE, "Obsidian Axe");

        // Blocks
        translationBuilder.add(OCBlocks.OBSIDIAN_BLOCK, "Obsidian Block");

        // itemGroups
        translationBuilder.add("itemGroup.obsidian_craft", "Obsidian Craft");
    }
}
