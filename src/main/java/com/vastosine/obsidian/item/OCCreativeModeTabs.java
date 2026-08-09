package com.vastosine.obsidian.item;

import com.vastosine.obsidian.ObsidianCraft;
import com.vastosine.obsidian.block.OCBlocks;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class OCCreativeModeTabs {
    public static final ResourceKey<CreativeModeTab> OBSIDIAN_CRAFT_KEY =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, ObsidianCraft.id("obsidian_craft"));

    public static final CreativeModeTab OBSIDIAN_CRAFT = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(OCItems.OBSIDIAN_INGOT))
            .title(Component.translatable("itemGroup.obsidian_craft"))
            .displayItems((context, output) -> {
                output.accept(OCItems.OBSIDIAN_INGOT);
                output.accept(OCBlocks.OBSIDIAN_BLOCK);
                output.accept(OCItems.OBSIDIAN_SWORD);
                output.accept(OCItems.OBSIDIAN_PICKAXE);
                output.accept(OCItems.OBSIDIAN_SHOVEL);
                output.accept(OCItems.OBSIDIAN_HOE);
                output.accept(OCItems.OBSIDIAN_AXE);
            }).build();

    public static void onInitialize() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, OBSIDIAN_CRAFT_KEY, OBSIDIAN_CRAFT);
    }
}
