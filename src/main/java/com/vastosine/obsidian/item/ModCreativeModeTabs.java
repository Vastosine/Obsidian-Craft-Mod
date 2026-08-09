package com.vastosine.obsidian.item;

import com.vastosine.obsidian.ObsidianCraft;
import com.vastosine.obsidian.block.ModBlocks;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModCreativeModeTabs {
	public static final ResourceKey<CreativeModeTab> OBSIDIAN = ResourceKey.create(
		Registries.CREATIVE_MODE_TAB,
		ObsidianCraft.id("obsidian")
	);

	// Registered as a static field (like the other Mod classes): the datagen init
	// chain re-runs init(), by which time the registries are already frozen, so the
	// registration must happen once at class-init during onInitialize
	public static final CreativeModeTab TAB = Registry.register(
		BuiltInRegistries.CREATIVE_MODE_TAB,
		OBSIDIAN,
		FabricCreativeModeTab.builder()
			.title(Component.translatable("itemGroup.obsidian"))
			.icon(() -> new ItemStack(ModItems.OBSIDIAN_INGOT))
			.displayItems((parameters, output) -> {
				output.accept(ModBlocks.OBSIDIAN_BLOCK);
				output.accept(ModBlocks.ALLOY_FURNACE);
				output.accept(ModItems.ROSE_GOLD_INGOT);
				output.accept(ModItems.OBSIDIAN_INGOT);
				output.accept(ModItems.OBSIDIAN_APPLE);
				output.accept(ModItems.OBSIDIAN_PICKAXE);
				output.accept(ModItems.OBSIDIAN_AXE);
				output.accept(ModItems.OBSIDIAN_SHOVEL);
				output.accept(ModItems.OBSIDIAN_HOE);
				output.accept(ModItems.OBSIDIAN_SWORD);
				output.accept(ModItems.OBSIDIAN_HELMET);
				output.accept(ModItems.OBSIDIAN_CHESTPLATE);
				output.accept(ModItems.OBSIDIAN_LEGGINGS);
				output.accept(ModItems.OBSIDIAN_BOOTS);
			})
			.build()
	);

	private ModCreativeModeTabs() {
	}

	public static void init() {
		// Registration happens in the static field above
	}
}
