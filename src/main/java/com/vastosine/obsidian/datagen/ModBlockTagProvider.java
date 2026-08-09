package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.block.ModBlocks;
import com.vastosine.obsidian.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
	public ModBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(output, registryLookup);
	}

	@Override
	protected void addTags(HolderLookup.Provider wrapperLookup) {
		// Mineable with a pickaxe
		this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
			.add(ModBlocks.OBSIDIAN_BLOCK_KEY)
			.add(ModBlocks.ALLOY_FURNACE_KEY)
			.add(ModBlocks.ROSE_GOLD_BLOCK_KEY);
		// Needs at least a diamond pickaxe to drop correctly
		this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
			.add(ModBlocks.OBSIDIAN_BLOCK_KEY)
			.add(ModBlocks.ALLOY_FURNACE_KEY);
		// Needs at least an iron pickaxe (like the gold block)
		this.tag(BlockTags.NEEDS_IRON_TOOL)
			.add(ModBlocks.ROSE_GOLD_BLOCK_KEY);
		// Blocks the obsidian pickaxe mines 50% faster
		this.tag(ModItems.OBSIDIAN_BLOCKS_TAG)
			.add(Blocks.OBSIDIAN.builtInRegistryHolder().key())
			.add(Blocks.CRYING_OBSIDIAN.builtInRegistryHolder().key())
			.add(ModBlocks.OBSIDIAN_BLOCK_KEY);
	}
}
