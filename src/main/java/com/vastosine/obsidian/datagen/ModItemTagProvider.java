package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
	public ModItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(output, registryLookup);
	}

	@Override
	protected void addTags(HolderLookup.Provider wrapperLookup) {
		// Material used to repair obsidian tools and armor on an anvil
		this.tag(ModItems.OBSIDIAN_INGOT_TAG).add(ModItems.OBSIDIAN_INGOT_KEY);
	}
}
