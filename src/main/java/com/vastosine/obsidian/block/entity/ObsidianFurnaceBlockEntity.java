package com.vastosine.obsidian.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ObsidianFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
    public ObsidianFurnaceBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(OCBlockEntityTypes.OBSIDIAN_FURNACE, worldPosition, blockState, RecipeType.SMELTING);
    }

    @Override
    protected float getSpeedMultiplier(ServerLevel level, ItemStack fuelItem) {
        return super.getSpeedMultiplier(level, fuelItem) * 2;
    }

    public static final Component DEFAULT_NAME = Component.translatable("container.obsidian.obsidian_furnace");

    @Override
    protected Component getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new FurnaceMenu(containerId, inventory, this, dataAccess);
    }
}
