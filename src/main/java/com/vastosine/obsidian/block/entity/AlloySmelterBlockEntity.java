package com.vastosine.obsidian.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.level.block.state.BlockState;

public class AlloySmelterBlockEntity extends AbstractAlloySmelterBlockEntity{
    public static final Component DEFAULT_NAME = Component.translatable("container.obsidian.alloy_smelter");

    public AlloySmelterBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(OCBlockEntityTypes.ALLOY_SMELTER, worldPosition, blockState);
    }

    @Override
    protected Component getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new FurnaceMenu(containerId, inventory, this, dataAccess);
    }
}
