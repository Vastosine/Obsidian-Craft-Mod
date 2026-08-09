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
		translationBuilder.add(ModBlocks.ALLOY_FURNACE, "Alloy Furnace");
		translationBuilder.add(ModBlocks.ROSE_GOLD_BLOCK, "Rose Gold Block");
		translationBuilder.add(ModItems.OBSIDIAN_INGOT, "Obsidian Ingot");
		translationBuilder.add(ModItems.ROSE_GOLD_INGOT, "Rose Gold Ingot");
		translationBuilder.add(ModItems.ROSE_GOLD_NUGGET, "Rose Gold Nugget");
		translationBuilder.add(ModItems.OBSIDIAN_APPLE, "Obsidian Apple");
		translationBuilder.add(ModCreativeModeTabs.OBSIDIAN, "Obsidian Craft");
		translationBuilder.add("container.alloy_furnace", "Alloy Furnace");
		translationBuilder.add("item.obsidian.alloy_furnace.slots.left", "Left face: Ingredient slot");
		translationBuilder.add("item.obsidian.alloy_furnace.slots.top", "Top face: Ingredient slot");
		translationBuilder.add("item.obsidian.alloy_furnace.slots.right", "Right face: Ingredient slot");
		translationBuilder.add("item.obsidian.alloy_furnace.slots.fuel", "Front/Back face: Fuel");
		translationBuilder.add("item.obsidian.alloy_furnace.slots.output", "Bottom face: Output");

		translationBuilder.add(ModItems.OBSIDIAN_PICKAXE, "Obsidian Pickaxe");
		translationBuilder.add(ModItems.OBSIDIAN_AXE, "Obsidian Axe");
		translationBuilder.add(ModItems.OBSIDIAN_SHOVEL, "Obsidian Shovel");
		translationBuilder.add(ModItems.OBSIDIAN_HOE, "Obsidian Hoe");
		translationBuilder.add(ModItems.OBSIDIAN_SWORD, "Obsidian Sword");
		translationBuilder.add(ModItems.OBSIDIAN_SPEAR, "Obsidian Spear");
		translationBuilder.add(ModItems.OBSIDIAN_HELMET, "Obsidian Helmet");
		translationBuilder.add(ModItems.OBSIDIAN_CHESTPLATE, "Obsidian Chestplate");
		translationBuilder.add(ModItems.OBSIDIAN_LEGGINGS, "Obsidian Leggings");
		translationBuilder.add(ModItems.OBSIDIAN_BOOTS, "Obsidian Boots");
		translationBuilder.add(ModItems.ROSE_GOLD_PICKAXE, "Rose Gold Pickaxe");
		translationBuilder.add(ModItems.ROSE_GOLD_AXE, "Rose Gold Axe");
		translationBuilder.add(ModItems.ROSE_GOLD_SHOVEL, "Rose Gold Shovel");
		translationBuilder.add(ModItems.ROSE_GOLD_HOE, "Rose Gold Hoe");
		translationBuilder.add(ModItems.ROSE_GOLD_SWORD, "Rose Gold Sword");
		translationBuilder.add(ModItems.ROSE_GOLD_SPEAR, "Rose Gold Spear");
		translationBuilder.add(ModItems.ROSE_GOLD_HELMET, "Rose Gold Helmet");
		translationBuilder.add(ModItems.ROSE_GOLD_CHESTPLATE, "Rose Gold Chestplate");
		translationBuilder.add(ModItems.ROSE_GOLD_LEGGINGS, "Rose Gold Leggings");
		translationBuilder.add(ModItems.ROSE_GOLD_BOOTS, "Rose Gold Boots");
		translationBuilder.add("item.obsidian.obsidian_pickaxe.tooltip", "+150% mining speed on obsidian");
		translationBuilder.add("item.obsidian.obsidian_pickaxe.repair", "Mining obsidian restores 2 durability");
		translationBuilder.add("item.obsidian.unbreaking.tooltip", "Unbreaking");
		translationBuilder.add("item.obsidian.fire_protection.tooltip", "Fire Protection");
		translationBuilder.add("item.obsidian.shift_hint", "Hold Shift for details");
	}
}
