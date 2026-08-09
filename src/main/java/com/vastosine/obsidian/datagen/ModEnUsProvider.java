package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.block.ModBlocks;
import com.vastosine.obsidian.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModEnUsProvider extends FabricLanguageProvider {
    public ModEnUsProvider(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(packOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        // Items
        translationBuilder.add(ModItems.OBSIDIAN_INGOT, "Obsidian Ingot");

        // Blocks
        translationBuilder.add(ModBlocks.OBSIDIAN_BLOCK, "Obsidian Block");

        // itemGroups
        translationBuilder.add("itemGroup.obsidian_craft", "Obsidian Craft");
    }
}
