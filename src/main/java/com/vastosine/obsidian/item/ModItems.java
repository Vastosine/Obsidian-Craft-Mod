package com.vastosine.obsidian.item;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public final class ModItems {
	public static final Item OBSIDIAN_INGOT = registerItem(
		"obsidian_ingot",
		new Item.Properties()
	);

	private ModItems() {
	}

	public static void init() {
		// 触发上面的静态字段完成注册
	}

	private static Item registerItem(String path, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ObsidianCraft.id(path));
		return Registry.register(BuiltInRegistries.ITEM, key, new Item(properties.setId(key)));
	}
}
