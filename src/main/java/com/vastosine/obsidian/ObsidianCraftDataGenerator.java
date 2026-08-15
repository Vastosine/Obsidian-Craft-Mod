package com.vastosine.obsidian;

import com.vastosine.obsidian.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ObsidianCraftDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(OCEnUsProvider::new);
		pack.addProvider(OCModelsProvider::new);
		pack.addProvider(OCRecipesProvider::new);
		pack.addProvider(OCLootTablesProvider::new);
		pack.addProvider(OCBlockTagsProvider::new);
		pack.addProvider(OCItemTagsProvider::new);
		pack.addProvider(OCAdvancementProvider::new);
	}
}
