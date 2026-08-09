package com.vastosine.obsidian.item;

import com.vastosine.obsidian.ObsidianCraft;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CreativeModeTabs {
    public static final ResourceKey<CreativeModeTab> OBSIDIAN_CRAFT_KEY =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, ObsidianCraft.id("obsidian_craft"));

    public static final CreativeModeTab OBSIDIAN_CRAFT = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.OBSIDIAN_INGOT))
            .title(Component.translatable("itemGroup.obsidian_craft"))
            .displayItems((context, output) -> {
                output.accept(ModItems.OBSIDIAN_INGOT);
            }).build();

    public static void onInitialize() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, OBSIDIAN_CRAFT_KEY, OBSIDIAN_CRAFT);
    }
}
