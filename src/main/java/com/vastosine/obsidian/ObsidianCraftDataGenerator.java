package com.vastosine.obsidian;

import com.vastosine.obsidian.datagen.ModBlockTagProvider;
import com.vastosine.obsidian.datagen.ModChineseLanguageProvider;
import com.vastosine.obsidian.datagen.ModEnglishLanguageProvider;
import com.vastosine.obsidian.datagen.ModItemTagProvider;
import com.vastosine.obsidian.datagen.ModLootTableProvider;
import com.vastosine.obsidian.datagen.ModModelProvider;
import com.vastosine.obsidian.datagen.ModRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ObsidianCraftDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModEnglishLanguageProvider::new);
		pack.addProvider(ModChineseLanguageProvider::new);
		pack.addProvider(ModLootTableProvider::new);
		pack.addProvider(ModBlockTagProvider::new);
		pack.addProvider(ModItemTagProvider::new);
		pack.addProvider(ModRecipeProvider::new);
	}
}
