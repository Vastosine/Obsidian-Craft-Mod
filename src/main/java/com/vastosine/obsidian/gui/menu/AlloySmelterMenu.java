package com.vastosine.obsidian.gui.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipePropertySet;

public class AlloySmelterMenu extends AbstractAlloySmelterMenu{
    public AlloySmelterMenu(final int containerId, final Inventory inventory) {
        super(OCMenuTypes.ALLOY_SMELTER, RecipePropertySet.FURNACE_INPUT, RecipeBookType.FURNACE, containerId, inventory, 2, 1, 1);
    }

    public AlloySmelterMenu(final int containerId, final Inventory inventory, final Container container, final ContainerData data) {
        super(OCMenuTypes.ALLOY_SMELTER, RecipePropertySet.FURNACE_INPUT, RecipeBookType.FURNACE, containerId, inventory, container, data, 3, 1, 1);
    }
}
