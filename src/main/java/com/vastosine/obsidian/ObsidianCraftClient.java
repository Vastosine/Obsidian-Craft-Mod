package com.vastosine.obsidian;

import com.vastosine.obsidian.client.screen.AlloyFurnaceScreen;
import com.vastosine.obsidian.menu.ModMenuTypes;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class ObsidianCraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(ModMenuTypes.ALLOY_FURNACE, AlloyFurnaceScreen::new);
    }
}
