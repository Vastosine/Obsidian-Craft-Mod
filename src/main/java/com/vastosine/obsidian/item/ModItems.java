package com.vastosine.obsidian.item;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {
    public static final Item OBSIDIAN_INGOT = register(ModItemIds.OBSIDIAN_INGOT);

    public static Item register(final ResourceKey<Item> id) {
        return register(id, Item::new, new Item.Properties());
    }

    public static Item register(final ResourceKey<Item> id, final Item.Properties properties) {
        return register(id, Item::new, properties);
    }

    public static Item register(final ResourceKey<Item> id, final Function<Item.Properties, Item> itemFactory) {
        return register(id, itemFactory, new Item.Properties());
    }

    public static Item register(final ResourceKey<Item> id, final Function<Item.Properties, Item> itemFactory, final Item.Properties properties) {
        Item item = itemFactory.apply(properties.setId(id));
        if (item instanceof BlockItem blockItem) {
            blockItem.registerBlocks(Item.BY_BLOCK, item);
        }
        return Registry.register(BuiltInRegistries.ITEM, id, item);
    }

    public static void onInitialize() {
        ObsidianCraft.LOGGER.info("Registering Mod Items for " + ObsidianCraft.MOD_ID);
    }
}
