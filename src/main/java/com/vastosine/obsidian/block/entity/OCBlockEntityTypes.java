package com.vastosine.obsidian.block.entity;

import com.vastosine.obsidian.ObsidianCraft;
import com.vastosine.obsidian.block.OCBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

public class OCBlockEntityTypes {
    public static final BlockEntityType<CounterBlockEntity> COUNTER_BLOCK = register(OCBlockEntityTypeIds.COUNTER_BLOCK, CounterBlockEntity::new, OCBlocks.COUNTER_BLOCK);
    public static final BlockEntityType<ObsidianFurnaceBlockEntity> OBSIDIAN_FURNACE = register(OCBlockEntityTypeIds.OBSIDIAN_FURNACE, ObsidianFurnaceBlockEntity::new, OCBlocks.OBSIDIAN_FURNACE);

    private static <T extends BlockEntity> BlockEntityType<T> register(
            final ResourceKey<BlockEntityType<?>> key, final BlockEntityType.BlockEntitySupplier<? extends T> factory, final Block... validBlocks
    ) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, key, new BlockEntityType<>(factory, Set.of(validBlocks)));
    }

    public static void onInitialize() {
        ObsidianCraft.LOGGER.info("Registering Mod Block Entities for " + ObsidianCraft.MOD_ID);
    }
}
