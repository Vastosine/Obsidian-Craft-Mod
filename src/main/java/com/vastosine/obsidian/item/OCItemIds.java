package com.vastosine.obsidian.item;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class OCItemIds {
    public static final ResourceKey<Item> OBSIDIAN_INGOT = create("obsidian_ingot");
    public static final ResourceKey<Item> OBSIDIAN_SWORD = create("obsidian_sword");
    public static final ResourceKey<Item> OBSIDIAN_PICKAXE = create("obsidian_pickaxe");
    public static final ResourceKey<Item> OBSIDIAN_SHOVEL = create("obsidian_shovel");
    public static final ResourceKey<Item> OBSIDIAN_HOE = create("obsidian_hoe");
    public static final ResourceKey<Item> OBSIDIAN_AXE = create("obsidian_axe");
//    public static final ResourceKey<Item> TEST_ITEM = create("test_item");
    public static final ResourceKey<Item> OBSIDIAN_HELMET = create("obsidian_helmet");
    public static final ResourceKey<Item> OBSIDIAN_CHESTPLATE = create("obsidian_chestplate");
    public static final ResourceKey<Item> OBSIDIAN_LEGGINGS = create("obsidian_leggings");
    public static final ResourceKey<Item> OBSIDIAN_BOOTS = create("obsidian_boots");

    public static ResourceKey<Item> create(final String name) {
        return ResourceKey.create(Registries.ITEM, ObsidianCraft.id(name));
    }
}
