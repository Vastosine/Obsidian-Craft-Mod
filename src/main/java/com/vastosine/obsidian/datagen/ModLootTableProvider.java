package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootSubProvider {
	public ModLootTableProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(output, registryLookup);
	}

	@Override
	public void generate() {
		// Drops itself when mined with the correct tool
		this.dropSelf(ModBlocks.OBSIDIAN_BLOCK);
		this.dropSelf(ModBlocks.ALLOY_FURNACE);
	}
}
