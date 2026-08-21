package com.vastosine.obsidian.inventory;

import com.vastosine.obsidian.ObsidianCraft;
import com.vastosine.obsidian.inventory.menu.AlloySmelterMenu;
import com.vastosine.obsidian.inventory.menu.ObsidianFurnaceMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class OCMenuTypes {
    public static final MenuType<ObsidianFurnaceMenu> OBSIDIAN_FURNACE = register("obsidian_furnace", ObsidianFurnaceMenu::new);
    public static final MenuType<AlloySmelterMenu> ALLOY_SMELTER = register("alloy_smelter", AlloySmelterMenu::new);

    private static <T extends AbstractContainerMenu> MenuType<T> register(final String name, final MenuType.MenuSupplier<T> constructor) {
        return Registry.register(BuiltInRegistries.MENU, ObsidianCraft.id(name), new MenuType<>(constructor, FeatureFlags.VANILLA_SET));
    }

    public static void onInitialize() {
        ObsidianCraft.onInitializeInfo("MenuTypes");
    }
}
