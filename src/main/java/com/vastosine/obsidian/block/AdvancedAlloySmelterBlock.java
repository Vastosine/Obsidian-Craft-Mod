package com.vastosine.obsidian.block;

import com.vastosine.obsidian.block.entity.AdvancedAlloySmelterBlockEntity;
import com.vastosine.obsidian.block.entity.OCBlockEntityTypes;
import com.vastosine.obsidian.stats.OCStats;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class AdvancedAlloySmelterBlock extends NeedFuelBlock {
    protected AdvancedAlloySmelterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return createFurnaceTicker(level, type, OCBlockEntityTypes.ADVANCED_ALLOY_SMELTER);
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof AdvancedAlloySmelterBlockEntity) {
            player.openMenu((MenuProvider) blockEntity);
            player.awardStat(OCStats.INTERACT_WITH_ADVANCED_ALLOY_SMELTER);
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new AdvancedAlloySmelterBlockEntity(worldPosition, blockState);
    }
}
