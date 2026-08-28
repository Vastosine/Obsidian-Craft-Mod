package com.vastosine.obsidian.tags;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class OCItemTags {
    public static final TagKey<Item> OBSIDIAN_TOOL_MATERIALS = bind("obsidian_tool_materials");
    public static final TagKey<Item> OBSIDIAN_ARMOR_MATERIALS = bind("obsidian_armor_materials");
    public static final TagKey<Item> OBSIDIAN_ARMORS = bind("obsidian_armors");
    public static final TagKey<Item> CAN_BE_SMELTED_INTO_OBSIDIAN = bind("can_be_smelted_into_obsidian");

    private static TagKey<Item> bind(final String name) {
        return TagKey.create(Registries.ITEM, ObsidianCraft.id(name));
    }
}
