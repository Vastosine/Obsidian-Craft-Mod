package com.vastosine.obsidian;

import com.vastosine.obsidian.gui.screen.OCMenuScreens;
import net.fabricmc.api.ClientModInitializer;

public class ObsidianCraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OCMenuScreens.onInitialize();
    }
}
