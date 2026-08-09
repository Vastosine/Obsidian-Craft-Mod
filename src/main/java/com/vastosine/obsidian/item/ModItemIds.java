package com.vastosine.obsidian.item;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModItemIds {
    public static final ResourceKey<Item> OBSIDIAN_INGOT = create("obsidian_ingot");

    public static ResourceKey<Item> create(final String name) {
        return ResourceKey.create(Registries.ITEM, ObsidianCraft.id(name));
    }
}
