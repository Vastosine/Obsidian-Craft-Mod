package com.vastosine.obsidian.block;

import com.vastosine.obsidian.ObsidianCraft;
import com.vastosine.obsidian.item.AlloyFurnaceItem;
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

import java.util.function.Function;

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

	public static final ResourceKey<Block> ALLOY_FURNACE_KEY = ResourceKey.create(Registries.BLOCK, ObsidianCraft.id("alloy_furnace"));

	private static final ResourceKey<Item> ALLOY_FURNACE_ITEM_KEY = ResourceKey.create(Registries.ITEM, ObsidianCraft.id("alloy_furnace"));

	// Alloy Furnace: stone-like hardness, needs a correct tool (diamond, via the tags),
	// and its item shows the slot layout in a Shift-gated tooltip (AlloyFurnaceItem)
	public static final Block ALLOY_FURNACE = registerBlock(
		"alloy_furnace",
		AlloyFurnaceBlock::new,
		block -> new AlloyFurnaceItem(block, new Item.Properties().setId(ALLOY_FURNACE_ITEM_KEY).useBlockDescriptionPrefix()),
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_BLACK)
			.strength(3.5F, 8.0F)
			.sound(SoundType.STONE)
			.requiresCorrectToolForDrops()
	);

	private ModBlocks() {
	}

	public static void init() {
		// Trigger registration by initializing the static fields above
	}

	private static Block registerBlock(String path, BlockBehaviour.Properties properties) {
		return registerBlock(path, Block::new, null, properties);
	}

	private static Block registerBlock(String path, Function<BlockBehaviour.Properties, Block> blockFactory, Function<Block, Item> itemFactory, BlockBehaviour.Properties properties) {
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, ObsidianCraft.id(path));
		Block block = Registry.register(BuiltInRegistries.BLOCK, blockKey, blockFactory.apply(properties.setId(blockKey)));

		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, ObsidianCraft.id(path));
		if (itemFactory == null) {
			// Same as vanilla registerBlock: block items use the "block." description prefix (otherwise it would become "item.")
			itemFactory = b -> new BlockItem(b, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
		}
		Item item = itemFactory.apply(block);
		// Same as vanilla registerItem: keeps the block-to-item mapping (registerBlocks lives on BlockItem in 26.3)
		if (item instanceof BlockItem blockItem) {
			blockItem.registerBlocks(Item.BY_BLOCK, item);
		}
		Registry.register(BuiltInRegistries.ITEM, itemKey, item);

		return block;
	}
}
