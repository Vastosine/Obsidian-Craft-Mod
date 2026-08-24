package com.vastosine.obsidian.client.screen;

import com.vastosine.obsidian.ObsidianCraft;
import com.vastosine.obsidian.inventory.menu.OCMenuTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jspecify.annotations.NullMarked;

@Environment(EnvType.CLIENT)
public class OCMenuScreens {
    @NullMarked
    public static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
            final MenuType<? extends M> type, final MenuScreens.ScreenConstructor<M, U> factory
    ) {
        MenuScreens.register(type, factory);
    }

    public static void onInitialize() {
        ObsidianCraft.onInitializeInfo("Menu Screens");
        register(OCMenuTypes.OBSIDIAN_FURNACE, ObsidianFurnaceScreen::new);
        register(OCMenuTypes.ALLOY_SMELTER, AlloySmelterScreen::new);
    }
}
