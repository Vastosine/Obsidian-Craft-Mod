package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.block.ModBlocks;
import com.vastosine.obsidian.item.ModCreativeModeTabs;
import com.vastosine.obsidian.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModEnglishLanguageProvider extends FabricLanguageProvider {
	public ModEnglishLanguageProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(output, "en_us", registryLookup);
	}

	@Override
	public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
		translationBuilder.add(ModBlocks.OBSIDIAN_BLOCK, "Obsidian Block");
		translationBuilder.add(ModItems.OBSIDIAN_INGOT, "Obsidian Ingot");
		translationBuilder.add(ModItems.OBSIDIAN_APPLE, "Obsidian Apple");
		translationBuilder.add(ModCreativeModeTabs.OBSIDIAN, "Obsidian Craft");

		translationBuilder.add(ModItems.OBSIDIAN_PICKAXE, "Obsidian Pickaxe");
		translationBuilder.add(ModItems.OBSIDIAN_AXE, "Obsidian Axe");
		translationBuilder.add(ModItems.OBSIDIAN_SHOVEL, "Obsidian Shovel");
		translationBuilder.add(ModItems.OBSIDIAN_HOE, "Obsidian Hoe");
		translationBuilder.add(ModItems.OBSIDIAN_SWORD, "Obsidian Sword");
		translationBuilder.add(ModItems.OBSIDIAN_HELMET, "Obsidian Helmet");
		translationBuilder.add(ModItems.OBSIDIAN_CHESTPLATE, "Obsidian Chestplate");
		translationBuilder.add(ModItems.OBSIDIAN_LEGGINGS, "Obsidian Leggings");
		translationBuilder.add(ModItems.OBSIDIAN_BOOTS, "Obsidian Boots");
		translationBuilder.add("item.obsidian.obsidian_pickaxe.tooltip", "+150% mining speed on obsidian");
		translationBuilder.add("item.obsidian.obsidian_pickaxe.repair", "Mining obsidian restores 2 durability");
		translationBuilder.add("item.obsidian.unbreaking.tooltip", "Unbreaking");
		translationBuilder.add("item.obsidian.fire_protection.tooltip", "Fire Protection");
		translationBuilder.add("item.obsidian.shift_hint", "Hold Shift for details");
	}
}
