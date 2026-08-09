package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.item.ModItemTags;
import com.vastosine.obsidian.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
	public ModItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(output, registryLookup);
	}

	@Override
	protected void addTags(HolderLookup.Provider wrapperLookup) {
		// Material used to repair obsidian tools and armor on an anvil
		this.tag(ModItems.OBSIDIAN_INGOT_TAG).add(ModItems.OBSIDIAN_INGOT_KEY);

		// Alloy Furnace materials: any of these satisfy the gold/copper ingredient
		this.tag(ModItemTags.GOLD_MATERIALS)
			.add(Items.GOLD_INGOT.builtInRegistryHolder().key())
			.add(Items.GOLD_ORE.builtInRegistryHolder().key())
			.add(Items.DEEPSLATE_GOLD_ORE.builtInRegistryHolder().key())
			.add(Items.NETHER_GOLD_ORE.builtInRegistryHolder().key())
			.add(Items.RAW_GOLD.builtInRegistryHolder().key());
		this.tag(ModItemTags.COPPER_MATERIALS)
			.add(Items.COPPER_INGOT.builtInRegistryHolder().key())
			.add(Items.COPPER_ORE.builtInRegistryHolder().key())
			.add(Items.DEEPSLATE_COPPER_ORE.builtInRegistryHolder().key())
			.add(Items.RAW_COPPER.builtInRegistryHolder().key());
	}
}
