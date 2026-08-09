package com.vastosine.obsidian.item;

import com.vastosine.obsidian.tags.OCBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
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
        Tool tool = itemStack.get(DataComponents.TOOL);
        if (tool == null) {
            return false;
        }

        int amount = tool.damagePerBlock();
        if (state.is(OCBlockTags.OBSIDIAN_BLOCK)) {
            amount -= 2;
        }

        if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0.0F && tool.damagePerBlock() > 0) {
            itemStack.hurtAndBreak(amount, owner, EquipmentSlot.MAINHAND);
        }

        return true;
    }
}
