package com.vastosine.obsidian.item;

import com.vastosine.obsidian.ObsidianCraft;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import static com.vastosine.obsidian.block.OCBlocks.*;
import static com.vastosine.obsidian.item.OCItems.*;

public class OCCreativeModeTabs {
    public static final ResourceKey<CreativeModeTab> OBSIDIAN_CRAFT_KEY =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, ObsidianCraft.id("obsidian_craft"));

    @SafeVarargs
    public static <T> void accept(CreativeModeTab.Output output, T... itemOrBlocks) {
        for (T itemOrBlock : itemOrBlocks) {
            if (itemOrBlock instanceof Item item) {
                output.accept(item);
            } else if (itemOrBlock instanceof Block block) {
                output.accept(block);
            } else if (itemOrBlock instanceof Item[] items) {
                for (Item item : items) {
                    output.accept(item);
                }
            } else if (itemOrBlock instanceof Block[] blocks) {
                for (Block block : blocks) {
                    output.accept(block);
                }
            }
        }
    }

    public static final CreativeModeTab OBSIDIAN_CRAFT = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(OCItems.OBSIDIAN_INGOT))
            .title(Component.translatable("itemGroup.obsidian_craft"))
            .displayItems((context, output) -> {
                        accept(output,
                                // Items
                                OBSIDIAN_INGOT,
                                OBSIDIAN_SWORD,
                                OBSIDIAN_PICKAXE,
                                OBSIDIAN_SHOVEL,
                                OBSIDIAN_HOE,
                                OBSIDIAN_AXE,
                                OBSIDIAN_HELMET,
                                OBSIDIAN_CHESTPLATE,
                                OBSIDIAN_LEGGINGS,
                                OBSIDIAN_BOOTS,

                                // Blocks
                                OBSIDIAN_BLOCK,
                                OBSIDIAN_FURNACE
                        );
                    }
            ).build();

    public static void onInitialize() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, OBSIDIAN_CRAFT_KEY, OBSIDIAN_CRAFT);
    }
}
