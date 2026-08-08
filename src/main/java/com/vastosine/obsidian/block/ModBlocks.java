package com.vastosine.obsidian.block;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class ModBlocks {
	public static final ResourceKey<Block> OBSIDIAN_BLOCK_KEY = ResourceKey.create(
		Registries.BLOCK,
		ObsidianCraft.id("obsidian_block")
	);

	public static final Block OBSIDIAN_BLOCK = registerBlock(
		"obsidian_block",
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_BLACK)
			// 2.5s mining time (hardness 5.0, ~2.5s with a diamond pickaxe, same as diamond block)
			.strength(5.0F, 800.0F)
			.sound(SoundType.STONE)
			// Requires a correct tool (at least diamond pickaxe, decided by the needs_diamond_tool tag)
			.requiresCorrectToolForDrops()
	);

	private ModBlocks() {
	}

	public static void init() {
		// Trigger registration by initializing the static fields above
	}

	private static Block registerBlock(String path, BlockBehaviour.Properties properties) {
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, ObsidianCraft.id(path));
		Block block = Registry.register(BuiltInRegistries.BLOCK, OBSIDIAN_BLOCK_KEY, new Block(properties.setId(blockKey)));

		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, ObsidianCraft.id(path));
		// Same as vanilla registerBlock: block items use the "block." description prefix (otherwise it would become "item.")
		BlockItem item = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
		// Same as vanilla registerItem: keeps the block-to-item mapping
		item.registerBlocks(Item.BY_BLOCK, item);
		Registry.register(BuiltInRegistries.ITEM, itemKey, item);

		return block;
	}
}
