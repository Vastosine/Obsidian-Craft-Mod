package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
	public ModBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(output, registryLookup);
	}

	@Override
	protected void addTags(HolderLookup.Provider wrapperLookup) {
		// Mineable with a pickaxe
		this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.OBSIDIAN_BLOCK_KEY);
		// Needs at least a diamond pickaxe to drop correctly
		this.tag(BlockTags.NEEDS_DIAMOND_TOOL).add(ModBlocks.OBSIDIAN_BLOCK_KEY);
	}
}
