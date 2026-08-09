package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.block.ModBlocks;
import com.vastosine.obsidian.item.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TexturedModel;

import java.util.Map;

public class ModModelProvider extends FabricModelProvider {
	public ModModelProvider(FabricPackOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
		// Simple cube with the same texture on all six faces (the BlockItem's inventory model is generated automatically)
		blockStateModelGenerator.createTrivialCube(ModBlocks.OBSIDIAN_BLOCK);
		blockStateModelGenerator.createTrivialCube(ModBlocks.ROSE_GOLD_BLOCK);

		// Furnace-like machine: front/side/top textures, lit variant, blockstate and item model
		blockStateModelGenerator.createFurnace(ModBlocks.ALLOY_FURNACE, TexturedModel.ORIENTABLE_ONLY_TOP);
	}

	@Override
	public void generateItemModels(ItemModelGenerators itemModelGenerator) {
		itemModelGenerator.generateFlatItem(ModItems.OBSIDIAN_INGOT, ModelTemplates.FLAT_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.ROSE_GOLD_INGOT, ModelTemplates.FLAT_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.ROSE_GOLD_NUGGET, ModelTemplates.FLAT_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.OBSIDIAN_APPLE, ModelTemplates.FLAT_ITEM);

		// Tools use the handheld (first-person in hand) template
		itemModelGenerator.generateFlatItem(ModItems.OBSIDIAN_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.OBSIDIAN_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.OBSIDIAN_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.OBSIDIAN_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.OBSIDIAN_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.ROSE_GOLD_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.ROSE_GOLD_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.ROSE_GOLD_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.ROSE_GOLD_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.ROSE_GOLD_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);

		// Spears: flat item model plus the in-hand model (item model dispatch)
		itemModelGenerator.generateSpear(ModItems.OBSIDIAN_SPEAR);
		itemModelGenerator.generateSpear(ModItems.ROSE_GOLD_SPEAR);

		// Armor item models (with trim support, no dyed layer, no trim palette overrides)
		itemModelGenerator.generateTrimmableArmorSet(
			ModItems.OBSIDIAN_HELMET,
			ModItems.OBSIDIAN_CHESTPLATE,
			ModItems.OBSIDIAN_LEGGINGS,
			ModItems.OBSIDIAN_BOOTS,
			false,
			Map.of()
		);
		itemModelGenerator.generateTrimmableArmorSet(
			ModItems.ROSE_GOLD_HELMET,
			ModItems.ROSE_GOLD_CHESTPLATE,
			ModItems.ROSE_GOLD_LEGGINGS,
			ModItems.ROSE_GOLD_BOOTS,
			false,
			Map.of()
		);
	}
}
