package com.vastosine.obsidian;

import com.vastosine.obsidian.client.screen.OCMenuScreens;
import com.vastosine.obsidian.recipe.crafting.display.OCRecipeDisplays;
import net.fabricmc.api.ClientModInitializer;

public class ObsidianCraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OCMenuScreens.onInitialize();
    }
}
