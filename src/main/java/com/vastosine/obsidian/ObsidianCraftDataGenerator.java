package com.vastosine.obsidian;

import com.vastosine.obsidian.block.ModBlocks;
import com.vastosine.obsidian.block.entity.ModBlockEntities;
import com.vastosine.obsidian.datagen.ModBlockTagProvider;
import com.vastosine.obsidian.datagen.ModChineseLanguageProvider;
import com.vastosine.obsidian.datagen.ModEnglishLanguageProvider;
import com.vastosine.obsidian.datagen.ModItemTagProvider;
import com.vastosine.obsidian.datagen.ModLootTableProvider;
import com.vastosine.obsidian.datagen.ModModelProvider;
import com.vastosine.obsidian.datagen.ModRecipeProvider;
import com.vastosine.obsidian.item.ModCreativeModeTabs;
import com.vastosine.obsidian.item.ModItems;
import com.vastosine.obsidian.menu.ModMenuTypes;
import com.vastosine.obsidian.recipe.AlloyRecipeDisplay;
import com.vastosine.obsidian.recipe.ModRecipeBookCategories;
import com.vastosine.obsidian.recipe.ModRecipeSerializers;
import com.vastosine.obsidian.recipe.ModRecipeTypes;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ObsidianCraftDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		// Same init chain as ObsidianCraft.onInitialize: datagen serializes recipe
		// JSONs through the codecs, which need the recipe book category registered.
		// Static-field classes are no-ops on this second pass (already initialized
		// by onInitialize); AlloyRecipeDisplay.register is idempotent, because this
		// hook runs after the registries were frozen.
		ModBlocks.init();
		ModItems.init();
		ModRecipeBookCategories.init();
		ModRecipeTypes.init();
		ModRecipeSerializers.init();
		ModBlockEntities.init();
		ModMenuTypes.init();
		AlloyRecipeDisplay.register();
		ModCreativeModeTabs.init();

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
