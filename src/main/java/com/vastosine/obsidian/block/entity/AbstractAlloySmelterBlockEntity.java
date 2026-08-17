package com.vastosine.obsidian.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractAlloySmelterBlockEntity extends BaseContainerBlockEntity {
    private int litTimeRemaining;
    private int litTotalTime;
    private int cookingTotalTime;
    private int cookingTimer;
    protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(final int dataId) {
            return switch (dataId) {
                case 0 -> AbstractAlloySmelterBlockEntity.this.litTimeRemaining;
                case 1 -> AbstractAlloySmelterBlockEntity.this.litTotalTime;
                case 2 -> AbstractAlloySmelterBlockEntity.this.cookingTimer;
                case 3 -> AbstractAlloySmelterBlockEntity.this.cookingTotalTime;
                default -> 0;
            };
        }

        @Override
        public void set(final int dataId, final int value) {
            switch (dataId) {
                case 0:
                    AbstractAlloySmelterBlockEntity.this.litTimeRemaining = value;
                    break;
                case 1:
                    AbstractAlloySmelterBlockEntity.this.litTotalTime = value;
                    break;
                case 2:
                    AbstractAlloySmelterBlockEntity.this.cookingTimer = value;
                    break;
                case 3:
                    AbstractAlloySmelterBlockEntity.this.cookingTotalTime = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };
    
    protected AbstractAlloySmelterBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    @Override
    public int getContainerSize() {
        return 3;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }
}
