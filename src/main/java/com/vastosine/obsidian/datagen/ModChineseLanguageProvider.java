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
		translationBuilder.add(ModBlocks.ALLOY_FURNACE, "合金炉");
		translationBuilder.add(ModBlocks.ROSE_GOLD_BLOCK, "玫瑰金块");
		translationBuilder.add(ModItems.OBSIDIAN_INGOT, "黑曜石锭");
		translationBuilder.add(ModItems.ROSE_GOLD_INGOT, "玫瑰金锭");
		translationBuilder.add(ModItems.ROSE_GOLD_NUGGET, "玫瑰金粒");
		translationBuilder.add(ModItems.OBSIDIAN_APPLE, "黑曜石苹果");
		translationBuilder.add(ModCreativeModeTabs.OBSIDIAN, "黑曜石工艺");
		translationBuilder.add("container.alloy_furnace", "合金炉");
		translationBuilder.add("item.obsidian.alloy_furnace.slots.left", "左侧面：材料槽");
		translationBuilder.add("item.obsidian.alloy_furnace.slots.top", "顶面：材料槽");
		translationBuilder.add("item.obsidian.alloy_furnace.slots.right", "右侧面：材料槽");
		translationBuilder.add("item.obsidian.alloy_furnace.slots.fuel", "正面/背面：燃料槽");
		translationBuilder.add("item.obsidian.alloy_furnace.slots.output", "底面：输出槽");

		translationBuilder.add(ModItems.OBSIDIAN_PICKAXE, "黑曜石镐");
		translationBuilder.add(ModItems.OBSIDIAN_AXE, "黑曜石斧");
		translationBuilder.add(ModItems.OBSIDIAN_SHOVEL, "黑曜石锹");
		translationBuilder.add(ModItems.OBSIDIAN_HOE, "黑曜石锄");
		translationBuilder.add(ModItems.OBSIDIAN_SWORD, "黑曜石剑");
		translationBuilder.add(ModItems.OBSIDIAN_SPEAR, "黑曜石长矛");
		translationBuilder.add(ModItems.OBSIDIAN_HELMET, "黑曜石头盔");
		translationBuilder.add(ModItems.OBSIDIAN_CHESTPLATE, "黑曜石胸甲");
		translationBuilder.add(ModItems.OBSIDIAN_LEGGINGS, "黑曜石护腿");
		translationBuilder.add(ModItems.OBSIDIAN_BOOTS, "黑曜石靴子");
		translationBuilder.add(ModItems.ROSE_GOLD_PICKAXE, "玫瑰金镐");
		translationBuilder.add(ModItems.ROSE_GOLD_AXE, "玫瑰金斧");
		translationBuilder.add(ModItems.ROSE_GOLD_SHOVEL, "玫瑰金锹");
		translationBuilder.add(ModItems.ROSE_GOLD_HOE, "玫瑰金锄");
		translationBuilder.add(ModItems.ROSE_GOLD_SWORD, "玫瑰金剑");
		translationBuilder.add(ModItems.ROSE_GOLD_SPEAR, "玫瑰金长矛");
		translationBuilder.add(ModItems.ROSE_GOLD_HELMET, "玫瑰金头盔");
		translationBuilder.add(ModItems.ROSE_GOLD_CHESTPLATE, "玫瑰金胸甲");
		translationBuilder.add(ModItems.ROSE_GOLD_LEGGINGS, "玫瑰金护腿");
		translationBuilder.add(ModItems.ROSE_GOLD_BOOTS, "玫瑰金靴子");
		translationBuilder.add("item.obsidian.obsidian_pickaxe.tooltip", "对黑曜石的挖掘速度提升150%");
		translationBuilder.add("item.obsidian.obsidian_pickaxe.repair", "挖掘黑曜石时恢复2点耐久");
		translationBuilder.add("item.obsidian.unbreaking.tooltip", "持久");
		translationBuilder.add("item.obsidian.fire_protection.tooltip", "抗火");
		translationBuilder.add("item.obsidian.shift_hint", "按住Shift查看详情");
	}
}
