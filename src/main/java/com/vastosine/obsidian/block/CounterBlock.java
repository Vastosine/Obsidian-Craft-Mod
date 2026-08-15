package com.vastosine.obsidian.block;

import com.vastosine.obsidian.ObsidianCraft;
import com.vastosine.obsidian.block.entity.CounterBlockEntity;
import com.vastosine.obsidian.block.entity.OCBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class CounterBlock extends BaseEntityBlock {
    protected CounterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new CounterBlockEntity(worldPosition, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        ObsidianCraft.LOGGER.info("tick");
        return createTickerHelper(type, OCBlockEntityTypes.COUNTER_BLOCK, CounterBlockEntity::tick);
    }

        @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof CounterBlockEntity counterBlockEntity)) {
            return super.useWithoutItem(state, level, pos, player, hitResult);
        }
        counterBlockEntity.incrementClicks();
        if (level.isClientSide()) {
            player.sendOverlayMessage(Component.literal("You've click the block for " + counterBlockEntity.getClicks() + " times!"));
        }
        return InteractionResult.SUCCESS;
    }
}
