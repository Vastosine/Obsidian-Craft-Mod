package com.vastosine.obsidian.block;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class OCBlockItemIds {
    public static final BlockItemId OBSIDIAN_BLOCK = create("obsidian_block");
    public static final BlockItemId COUNTER_BLOCK = create("counter_block");
    public static final BlockItemId OBSIDIAN_FURNACE = create("obsidian_furnace");

    private static @NonNull BlockItemId create(String name) {
        Identifier id = ObsidianCraft.id(name);
        return BlockItemId.create(id, id);
    }
}
