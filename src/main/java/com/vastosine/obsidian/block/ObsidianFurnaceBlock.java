package com.vastosine.obsidian.block;

import com.vastosine.obsidian.block.entity.ObsidianFurnaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ObsidianFurnaceBlock extends FurnaceBlock {
    public ObsidianFurnaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new ObsidianFurnaceBlockEntity(worldPosition, blockState);
    }
}
