package com.vastosine.obsidian.inventory.menu;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class OCMenuTypes {
    public static final MenuType<ObsidianFurnaceMenu> OBSIDIAN_FURNACE = register("obsidian_furnace", ObsidianFurnaceMenu::new);
    public static final MenuType<AlloySmelterMenu> ALLOY_SMELTER = register("alloy_smelter", AlloySmelterMenu::new);
    public static final MenuType<AdvancedAlloySmelterMenu> ADVANCED_ALLOY_SMELTER = register("advanced_alloy_smelter", AdvancedAlloySmelterMenu::new);

    private static <T extends AbstractContainerMenu> MenuType<T> register(final String name, final MenuType.MenuSupplier<T> constructor) {
        return Registry.register(BuiltInRegistries.MENU, ObsidianCraft.id(name), new MenuType<>(constructor, FeatureFlags.VANILLA_SET));
    }

    public static void onInitialize() {
        ObsidianCraft.onInitializeInfo("MenuTypes");
    }
}
