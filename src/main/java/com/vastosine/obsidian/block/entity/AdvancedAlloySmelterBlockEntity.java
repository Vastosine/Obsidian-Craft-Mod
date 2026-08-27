package com.vastosine.obsidian.block.entity;

import com.vastosine.obsidian.inventory.menu.AdvancedAlloySmelterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class AdvancedAlloySmelterBlockEntity extends AbstractAlloySmelterBlockEntity {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final Component DEFAULT_NAME = Component.translatable("container.obsidian.alloy_smelter");

    public AdvancedAlloySmelterBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(
                OCBlockEntityTypes.ADVANCED_ALLOY_SMELTER, worldPosition, blockState, blockState.getValue(FACING),
                2.5F, 1.0F, 2.0F, 3, 2, 2
        );
    }

    @Override
    protected Component getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new AdvancedAlloySmelterMenu(containerId, inventory, this, dataAccess);
    }
}
