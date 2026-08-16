package com.vastosine.obsidian.block;

import com.vastosine.obsidian.block.entity.OCBlockEntityTypes;
import com.vastosine.obsidian.block.entity.ObsidianFurnaceBlockEntity;
import com.vastosine.obsidian.stats.OCStats;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class ObsidianFurnaceBlock extends AbstractFurnaceBlock {
    public ObsidianFurnaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new ObsidianFurnaceBlockEntity(worldPosition, blockState);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(final Level level, final BlockState blockState, final BlockEntityType<T> type) {
        return createFurnaceTicker(level, type, OCBlockEntityTypes.OBSIDIAN_FURNACE);
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ObsidianFurnaceBlockEntity) {
            player.openMenu((MenuProvider) blockEntity);
            player.awardStat(OCStats.INTERACT_WITH_OBSIDIAN_FURNACE);
        }
    }

    @Override
    public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
        if (state.getValue(LIT)) {
            double x = pos.getX() + 0.5;
            double y = pos.getY();
            double z = pos.getZ() + 0.5;
            if (random.nextDouble() < 0.1 * 2) {
                level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = state.getValue(FACING);
            Direction.Axis axis = direction.getAxis();
            double r = 0.52;
            for (int i = 0; i < 2; i++) {
                double ss = (random.nextDouble() - 0.5) * 0.8, sss = random.nextDouble() * 1 / 16;
                double dx = axis == Direction.Axis.X ? direction.getStepX() * (r + sss) : ss;
                double dy = random.nextDouble() * 8.0 / 16.0;
                double dz = axis == Direction.Axis.Z ? direction.getStepZ() * (r + sss) : ss;
                level.addParticle(ParticleTypes.SMOKE, x + dx, y + dy, z + dz, 0.0, 0.0, 0.0);
                level.addParticle(ParticleTypes.FLAME, x + dx, y + dy, z + dz, 0.0, 0.0, 0.0);
            }
        }
    }
}
