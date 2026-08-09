package com.vastosine.obsidian.tags;


import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class OCBlockTags {

    public static final TagKey<Block> INCORRECT_FOR_OBSIDIAN_TOOL = bind("incorrect_for_obsidian_tool");

    private static TagKey<Block> bind(final String name) {
        return TagKey.create(Registries.BLOCK, ObsidianCraft.id(name));
    }
}
