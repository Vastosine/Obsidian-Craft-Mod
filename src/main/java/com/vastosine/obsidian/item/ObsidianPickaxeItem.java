package com.vastosine.obsidian.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

// The obsidian pickaxe restores 2 durability per obsidian block mined
// (net +1 without the built-in Unbreaking I, since mining consumes 1).
// Its tooltip shows the Unbreaking line always; the speed and repair lines
// only appear while holding Shift (with a hint line otherwise).
public class ObsidianPickaxeItem extends Item {
	private static final Component SPEED_LINE = Component.translatable("item.obsidian.obsidian_pickaxe.tooltip");
	private static final Component REPAIR_LINE = Component.translatable("item.obsidian.obsidian_pickaxe.repair");
	private static final Component SHIFT_HINT = Component.translatable("item.obsidian.shift_hint")
		.withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(true));

	public ObsidianPickaxeItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity owner) {
		boolean mined = super.mineBlock(stack, level, state, pos, owner);
		if (!level.isClientSide() && mined && state.is(ModItems.OBSIDIAN_BLOCKS_TAG) && stack.isDamageableItem()) {
			stack.setDamageValue(Math.max(0, stack.getDamageValue() - 2));
		}
		return mined;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
		builder.accept(ModItems.UNBREAKING_LINE);
		if (Minecraft.getInstance().hasShiftDown()) {
			builder.accept(SPEED_LINE);
			builder.accept(REPAIR_LINE);
		} else {
			builder.accept(SHIFT_HINT);
		}
	}
}
