package com.vastosine.obsidian.item;

import com.vastosine.obsidian.ObsidianCraft;
import com.vastosine.obsidian.tags.OCBlockTags;
import com.vastosine.obsidian.tags.OCItemTags;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

import java.util.function.Function;

public class OCItems {
    // Simple Items
    public static final Item OBSIDIAN_INGOT = register(OCItemIds.OBSIDIAN_INGOT);

    // Tools
    public static final ToolMaterial OBSIDIAN_TOOL_MATERIAL = new ToolMaterial(OCBlockTags.INCORRECT_FOR_OBSIDIAN_TOOL, 500, 8.0F, 1.5F, 14, OCItemTags.OBSIDIAN_TOOL_MATERIALS);
    public static final Item OBSIDIAN_SWORD = register(OCItemIds.OBSIDIAN_SWORD, new Item.Properties().sword(OBSIDIAN_TOOL_MATERIAL, 3.0F, -2.4F));
    public static final Item OBSIDIAN_PICKAXE = register(OCItemIds.OBSIDIAN_PICKAXE, new Item.Properties().pickaxe(OBSIDIAN_TOOL_MATERIAL, 2.0F, -2.0F));
    public static final Item OBSIDIAN_SHOVEL = register(OCItemIds.OBSIDIAN_SHOVEL, new Item.Properties().shovel(OBSIDIAN_TOOL_MATERIAL, 1.5F, -1.5F));
    public static final Item OBSIDIAN_HOE = register(OCItemIds.OBSIDIAN_HOE, new Item.Properties().hoe(OBSIDIAN_TOOL_MATERIAL, -3.0F, -1.0F));
    public static final Item OBSIDIAN_AXE = register(OCItemIds.OBSIDIAN_AXE, new Item.Properties().axe(OBSIDIAN_TOOL_MATERIAL, 6.0F, -3.0F));

    // Functions
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
