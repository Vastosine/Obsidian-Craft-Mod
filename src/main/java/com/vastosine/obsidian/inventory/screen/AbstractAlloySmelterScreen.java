package com.vastosine.obsidian.inventory.screen;

import com.vastosine.obsidian.ObsidianCraft;
import com.vastosine.obsidian.inventory.menu.AbstractAlloySmelterMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public abstract class AbstractAlloySmelterScreen<T extends AbstractAlloySmelterMenu> extends AbstractContainerScreen<T> {
    public static final Identifier ALLOY_SMELTER_LOCATION = ObsidianCraft.id("textures/gui/container/alloy_smelter.png");

    public AbstractAlloySmelterScreen(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    public AbstractAlloySmelterScreen(T menu, Inventory inventory, Component title, int imageWidth, int imageHeight) {
        super(menu, inventory, title, imageWidth, imageHeight);
    }
}
