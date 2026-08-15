package com.vastosine.obsidian.block.entity;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class OCBlockEntityTypeIds {
    public static final ResourceKey<BlockEntityType<?>> COUNTER_BLOCK = create("counter_block");
    public static final ResourceKey<BlockEntityType<?>> OBSIDIAN_FURNACE = create("obsidian_furnace");

    private static ResourceKey<BlockEntityType<?>> create(final String name) {
        return ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, ObsidianCraft.id(name));
    }
}
