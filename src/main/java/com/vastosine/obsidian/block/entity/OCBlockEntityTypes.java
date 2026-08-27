package com.vastosine.obsidian.block.entity;

import com.vastosine.obsidian.ObsidianCraft;
import com.vastosine.obsidian.block.OCBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

public class OCBlockEntityTypes {
    public static final BlockEntityType<CounterBlockEntity> COUNTER_BLOCK = register("counter_block", CounterBlockEntity::new, OCBlocks.COUNTER_BLOCK);
    public static final BlockEntityType<ObsidianFurnaceBlockEntity> OBSIDIAN_FURNACE = register("obsidian_furnace", ObsidianFurnaceBlockEntity::new, OCBlocks.OBSIDIAN_FURNACE);
    public static final BlockEntityType<AlloySmelterBlockEntity> ALLOY_SMELTER = register("alloy_smelter", AlloySmelterBlockEntity::new, OCBlocks.ALLOY_SMELTER);
    public static final BlockEntityType<AdvancedAlloySmelterBlockEntity> ADVANCED_ALLOY_SMELTER = register("advanced_alloy_smelter", AdvancedAlloySmelterBlockEntity::new, OCBlocks.ADVANCED_ALLOY_SMELTER);

    private static <T extends BlockEntity> BlockEntityType<T> register(
            final String name, final BlockEntityType.BlockEntitySupplier<? extends T> factory, final Block... validBlocks
    ) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, ObsidianCraft.id(name)), new BlockEntityType<>(factory, Set.of(validBlocks)));
    }

    public static void onInitialize() {
        ObsidianCraft.onInitializeInfo("Block Entity Types");
    }
}
