package com.vastosine.obsidian.block.entity;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class CounterBlockEntity extends BlockEntity {
    public CounterBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(OCBlockEntityTypes.COUNTER_BLOCK, worldPosition, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CounterBlockEntity entity) {
        entity.clicks++;
    }

    private int clicks = 0;

    public int getClicks() {
        return clicks;
    }

    public void incrementClicks() {
        clicks++;
        setChanged();
        ObsidianCraft.LOGGER.info("Clicks: {}", clicks);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        output.putInt("clicks", clicks);
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        clicks = input.getIntOr("clicks", 0);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level == null) return;
        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
    }
}
