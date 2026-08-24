package com.vastosine.obsidian.inventory.slot;

import net.minecraft.world.Container;
import com.vastosine.obsidian.inventory.menu.AbstractAlloySmelterMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AlloySmelterFuelSlot extends Slot {
    private final AbstractAlloySmelterMenu menu;

    public AlloySmelterFuelSlot(final AbstractAlloySmelterMenu menu, final Container container, final int slot, final int x, final int y) {
        super(container, slot, x, y);
        this.menu = menu;
    }

    @Override
    public boolean mayPlace(final ItemStack itemStack) {
        return this.menu.isFuel(itemStack) || isBucket(itemStack);
    }

    @Override
    public int getMaxStackSize(final ItemStack itemStack) {
        return isBucket(itemStack) ? 1 : super.getMaxStackSize(itemStack);
    }

    public static boolean isBucket(final ItemStack itemStack) {
        return itemStack.is(Items.BUCKET);
    }
}
