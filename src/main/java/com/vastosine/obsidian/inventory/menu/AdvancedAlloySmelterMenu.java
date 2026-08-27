package com.vastosine.obsidian.inventory.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;

public class AdvancedAlloySmelterMenu extends NeedFuelMenu {
    public static final int SLOT_INPUT_COUNT = 3;
    public static final int SLOT_FUEL_COUNT = 2;
    public static final int SLOT_RESULT_COUNT = 2;

    public AdvancedAlloySmelterMenu(final int containerId, final Inventory inventory) {
        super(OCMenuTypes.ADVANCED_ALLOY_SMELTER, RecipeBookType.FURNACE, containerId, inventory, SLOT_INPUT_COUNT, SLOT_FUEL_COUNT, SLOT_RESULT_COUNT);
    }

    public AdvancedAlloySmelterMenu(final int containerId, final Inventory inventory, final Container container, final ContainerData data) {
        super(OCMenuTypes.ADVANCED_ALLOY_SMELTER, RecipeBookType.FURNACE, containerId, inventory, container, data, SLOT_INPUT_COUNT, SLOT_FUEL_COUNT, SLOT_RESULT_COUNT);
    }
}
