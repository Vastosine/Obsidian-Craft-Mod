package com.vastosine.obsidian.block.entity;

import com.vastosine.obsidian.inventory.menu.ObsidianFurnaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public class ObsidianFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
    public ObsidianFurnaceBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(OCBlockEntityTypes.OBSIDIAN_FURNACE, worldPosition, blockState, RecipeType.SMELTING);
    }

    @Override
    protected float getSpeedMultiplier(@NonNull ServerLevel level, @NonNull ItemStack fuelItem) {
        return super.getSpeedMultiplier(level, fuelItem) * 2;
    }

    @Override
    protected int getBurnDuration(ServerLevel level, ItemStack fuelItem) {
        return super.getBurnDuration(level, fuelItem) / 2;
    }

    public static final Component DEFAULT_NAME = Component.translatable("container.obsidian.obsidian_furnace");

    @Override
    protected @NonNull Component getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    protected @NonNull AbstractContainerMenu createMenu(int containerId, @NonNull Inventory inventory) {
        return new ObsidianFurnaceMenu(containerId, inventory, this, dataAccess);
    }
}
