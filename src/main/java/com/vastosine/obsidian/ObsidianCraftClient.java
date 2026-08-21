package com.vastosine.obsidian;

import com.vastosine.obsidian.inventory.OCMenuScreens;
import net.fabricmc.api.ClientModInitializer;

public class ObsidianCraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OCMenuScreens.onInitialize();
    }
}
