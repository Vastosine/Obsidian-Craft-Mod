package com.vastosine.obsidian.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

/**
 * The Alloy Furnace item. The slot layout is only described in a Shift-gated tooltip:
 * left face -> ingredient 1, top face -> ingredient 2, right face -> ingredient 3,
 * front/back faces -> fuel, bottom face -> output (take only).
 * Without Shift the existing "hold Shift for details" hint is shown instead.
 */
public class AlloyFurnaceItem extends BlockItem {
	private static final Component SLOT_LEFT = tooltipLine("item.obsidian.alloy_furnace.slots.left");
	private static final Component SLOT_TOP = tooltipLine("item.obsidian.alloy_furnace.slots.top");
	private static final Component SLOT_RIGHT = tooltipLine("item.obsidian.alloy_furnace.slots.right");
	private static final Component SLOT_FUEL = tooltipLine("item.obsidian.alloy_furnace.slots.fuel");
	private static final Component SLOT_OUTPUT = tooltipLine("item.obsidian.alloy_furnace.slots.output");
	private static final Component SHIFT_HINT = Component.translatable("item.obsidian.shift_hint")
		.withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(true));

	public AlloyFurnaceItem(final Block block, final Item.Properties properties) {
		super(block, properties);
	}

	private static Component tooltipLine(String key) {
		return Component.translatable(key).withStyle(style -> style.withColor(ChatFormatting.DARK_GRAY));
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
		if (Minecraft.getInstance().hasShiftDown()) {
			builder.accept(SLOT_LEFT);
			builder.accept(SLOT_TOP);
			builder.accept(SLOT_RIGHT);
			builder.accept(SLOT_FUEL);
			builder.accept(SLOT_OUTPUT);
		} else {
			builder.accept(SHIFT_HINT);
		}
	}
}
