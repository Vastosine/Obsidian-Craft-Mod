package com.vastosine.obsidian.block;

import com.vastosine.obsidian.ObsidianCraft;
import com.vastosine.obsidian.item.OCItems;
import net.minecraft.references.BlockItemId;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public class OCBlocks {
    public static final Block OBSIDIAN_BLOCK = register(OCBlockItemIds.OBSIDIAN_BLOCK, BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK).strength(5.0f, 800));

    // Entity Blocks
    public static final Block COUNTER_BLOCK = register(
            OCBlockItemIds.COUNTER_BLOCK,
            CounterBlock::new,
            BlockBehaviour.Properties.of()
    );
    public static final Block OBSIDIAN_FURNACE = register(
            OCBlockItemIds.OBSIDIAN_FURNACE,
            ObsidianFurnaceBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE)
    );

    private static Block register(final BlockItemId id, final BlockBehaviour.Properties properties) {
        return register(id, factory -> new Block(properties), properties, true);
    }
    private static Block register(final BlockItemId id, final Function<BlockBehaviour.Properties, Block> factory, final BlockBehaviour.Properties properties) {
        return register(id, factory, properties, true);
    }

    private static Block register(final BlockItemId id, final Function<BlockBehaviour.Properties, Block> factory, final BlockBehaviour.Properties properties, boolean needBlockItem) {
        Block block = Blocks.register(id.block(), factory, properties);
        if (needBlockItem) {
            OCItems.register(id.item(), itemProperties -> new BlockItem(block, itemProperties), new Item.Properties().useBlockDescriptionPrefix().requiredFeatures(block.requiredFeatures()));
        }
        return block;
    }

    public static void onInitialize() {
        ObsidianCraft.LOGGER.info("Registering Mod Blocks for " + ObsidianCraft.MOD_ID);
    }
}
