package com.vastosine.obsidian.item;

import com.vastosine.obsidian.tags.OCBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ObsidianPickaxe extends Item {
    public ObsidianPickaxe(Properties properties) {
        super(properties);
    }

    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState state) {
        float speed = super.getDestroySpeed(itemStack, state);
        if (state.is(OCBlockTags.OBSIDIAN_BLOCK)) speed *= 3.0F;
        return speed;
    }

    @Override
    public boolean mineBlock(ItemStack itemStack, Level level, BlockState state, BlockPos pos, LivingEntity owner) {
        boolean mined = super.mineBlock(itemStack, level, state, pos, owner);
        if (!level.isClientSide() && mined && itemStack.isDamageableItem() && state.is(OCBlockTags.OBSIDIAN_BLOCK)) {
            itemStack.setDamageValue(Math.max(itemStack.getDamageValue() - 2, 0));
        }
        return true;
    }
}
