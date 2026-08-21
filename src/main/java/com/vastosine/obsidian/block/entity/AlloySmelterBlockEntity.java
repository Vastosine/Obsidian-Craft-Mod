package com.vastosine.obsidian.block.entity;

import com.vastosine.obsidian.gui.menu.AlloySmelterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class AlloySmelterBlockEntity extends AbstractAlloySmelterBlockEntity{
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final Component DEFAULT_NAME = Component.translatable("container.obsidian.alloy_smelter");

    public AlloySmelterBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(OCBlockEntityTypes.ALLOY_SMELTER, worldPosition, blockState, blockState.getValue(FACING), RecipeType.SMELTING);
    }

    @Override
    protected Component getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new AlloySmelterMenu(containerId, inventory, this, dataAccess);
    }
}
