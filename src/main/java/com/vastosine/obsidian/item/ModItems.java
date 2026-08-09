package com.vastosine.obsidian.item;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {
    public static final Item OBSIDIAN_INGOT = register("obsidian_ingot");

    private static Item register(final String name) {
        return register(name, Item::new, new Item.Properties());
    }

    private static Item register(final String name, final Item.Properties properties) {
        return register(name, Item::new, properties);
    }

    private static Item register(final String name, final Function<Item.Properties, Item> itemFactory) {
        return register(name, itemFactory, new Item.Properties());
    }

    private static Item register(final String name, final Function<Item.Properties, Item> itemFactory, final Item.Properties properties) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ObsidianCraft.id(name));
        Item item = itemFactory.apply(properties.setId(key));
        if (item instanceof BlockItem blockItem) {
            blockItem.registerBlocks(Item.BY_BLOCK, item);
        }
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static void onInitialize() {
        ObsidianCraft.LOGGER.info("Registering Mod Items for " + ObsidianCraft.MOD_ID);
    }
}
