package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.block.ModBlocks;
import com.vastosine.obsidian.item.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;

public class ModModelProvider extends FabricModelProvider {
	public ModModelProvider(FabricPackOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
		// Simple cube with the same texture on all six faces (the BlockItem's inventory model is generated automatically)
		blockStateModelGenerator.createTrivialCube(ModBlocks.OBSIDIAN_BLOCK);
	}

	@Override
	public void generateItemModels(ItemModelGenerators itemModelGenerator) {
		itemModelGenerator.generateFlatItem(ModItems.OBSIDIAN_INGOT, ModelTemplates.FLAT_ITEM);
	}
}
