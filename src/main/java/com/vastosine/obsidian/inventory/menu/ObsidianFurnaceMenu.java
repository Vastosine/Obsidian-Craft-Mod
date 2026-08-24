package com.vastosine.obsidian.inventory.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipePropertySet;

public class ObsidianFurnaceMenu extends AbstractFurnaceMenu {
    public ObsidianFurnaceMenu(final int containerId, final Inventory inventory) {
        super(OCMenuTypes.OBSIDIAN_FURNACE, RecipePropertySet.FURNACE_INPUT, RecipeBookType.FURNACE, containerId, inventory);
    }

    public ObsidianFurnaceMenu(final int containerId, final Inventory inventory, final Container container, final ContainerData data) {
        super(OCMenuTypes.OBSIDIAN_FURNACE, RecipePropertySet.FURNACE_INPUT, RecipeBookType.FURNACE, containerId, inventory, container, data);
    }
}
