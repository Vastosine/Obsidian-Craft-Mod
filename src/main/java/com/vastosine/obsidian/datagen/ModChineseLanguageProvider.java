package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.block.ModBlocks;
import com.vastosine.obsidian.item.ModCreativeModeTabs;
import com.vastosine.obsidian.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModChineseLanguageProvider extends FabricLanguageProvider {
	public ModChineseLanguageProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(output, "zh_cn", registryLookup);
	}

	@Override
	public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
		translationBuilder.add(ModBlocks.OBSIDIAN_BLOCK, "黑曜石块");
		translationBuilder.add(ModItems.OBSIDIAN_INGOT, "黑曜石锭");
		translationBuilder.add(ModItems.OBSIDIAN_APPLE, "黑曜石苹果");
		translationBuilder.add(ModCreativeModeTabs.OBSIDIAN, "黑曜石工艺");

		translationBuilder.add(ModItems.OBSIDIAN_PICKAXE, "黑曜石镐");
		translationBuilder.add(ModItems.OBSIDIAN_AXE, "黑曜石斧");
		translationBuilder.add(ModItems.OBSIDIAN_SHOVEL, "黑曜石锹");
		translationBuilder.add(ModItems.OBSIDIAN_HOE, "黑曜石锄");
		translationBuilder.add(ModItems.OBSIDIAN_SWORD, "黑曜石剑");
		translationBuilder.add(ModItems.OBSIDIAN_HELMET, "黑曜石头盔");
		translationBuilder.add(ModItems.OBSIDIAN_CHESTPLATE, "黑曜石胸甲");
		translationBuilder.add(ModItems.OBSIDIAN_LEGGINGS, "黑曜石护腿");
		translationBuilder.add(ModItems.OBSIDIAN_BOOTS, "黑曜石靴子");
		translationBuilder.add("item.obsidian.obsidian_pickaxe.tooltip", "对黑曜石的挖掘速度提升150%");
		translationBuilder.add("item.obsidian.obsidian_pickaxe.repair", "挖掘黑曜石时恢复2点耐久");
		translationBuilder.add("item.obsidian.unbreaking.tooltip", "持久");
		translationBuilder.add("item.obsidian.fire_protection.tooltip", "抗火");
		translationBuilder.add("item.obsidian.shift_hint", "按住Shift查看详情");
	}
}
