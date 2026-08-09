package com.vastosine.obsidian.block.entity;

import com.vastosine.obsidian.ObsidianCraft;
import com.vastosine.obsidian.block.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

/**
 * Block entity types (26.3 removed FabricBlockEntityTypeBuilder; the vanilla
 * BlockEntityType constructor takes the factory and a set of supported blocks).
 */
public final class ModBlockEntities {
	public static final BlockEntityType<AlloyFurnaceBlockEntity> ALLOY_FURNACE = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		ObsidianCraft.id("alloy_furnace"),
		new BlockEntityType<>(AlloyFurnaceBlockEntity::new, Set.of(ModBlocks.ALLOY_FURNACE))
	);

	private ModBlockEntities() {
	}

	public static void init() {
		// Registration happens in the static field above
	}
}
