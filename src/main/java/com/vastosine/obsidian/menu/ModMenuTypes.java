package com.vastosine.obsidian.menu;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class ModMenuTypes {
	public static final MenuType<AlloyFurnaceMenu> ALLOY_FURNACE = Registry.register(
		BuiltInRegistries.MENU,
		ObsidianCraft.id("alloy_furnace"),
		new MenuType<>(AlloyFurnaceMenu::new, FeatureFlags.VANILLA_SET)
	);

	private ModMenuTypes() {
	}

	public static void init() {
		// Registration happens in the static field above
	}
}
