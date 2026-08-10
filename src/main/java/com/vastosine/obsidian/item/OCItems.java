package com.vastosine.obsidian.item;

import com.vastosine.obsidian.ObsidianCraft;
import com.vastosine.obsidian.tags.OCBlockTags;
import com.vastosine.obsidian.tags.OCItemTags;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;
import java.util.function.Function;

public class OCItems {

    // Simple Items
    public static final Item OBSIDIAN_INGOT = register(OCItemIds.OBSIDIAN_INGOT);

    // Tools
    public static final ToolMaterial OBSIDIAN_TOOL_MATERIAL = new ToolMaterial(OCBlockTags.INCORRECT_FOR_OBSIDIAN_TOOL, 500, 8.0F, 1.5F, 14, OCItemTags.OBSIDIAN_TOOL_MATERIALS);
    public static final Item OBSIDIAN_SWORD = register(OCItemIds.OBSIDIAN_SWORD, new Item.Properties().sword(OBSIDIAN_TOOL_MATERIAL, 3.0F, -2.4F));
    public static final Item OBSIDIAN_PICKAXE = register(OCItemIds.OBSIDIAN_PICKAXE, ObsidianPickaxe::new, new Item.Properties().pickaxe(OBSIDIAN_TOOL_MATERIAL, 2.0F, -2.0F));
    public static final Item OBSIDIAN_SHOVEL = register(OCItemIds.OBSIDIAN_SHOVEL, new Item.Properties().shovel(OBSIDIAN_TOOL_MATERIAL, 1.5F, -1.5F));
    public static final Item OBSIDIAN_HOE = register(OCItemIds.OBSIDIAN_HOE, new Item.Properties().hoe(OBSIDIAN_TOOL_MATERIAL, -3.0F, -1.0F));
    public static final Item OBSIDIAN_AXE = register(OCItemIds.OBSIDIAN_AXE, new Item.Properties().axe(OBSIDIAN_TOOL_MATERIAL, 6.0F, -3.0F));

    // Armor
    public static final ArmorMaterial OBSIDIAN_ARMOR_MATERIAL = new ArmorMaterial(
            30,
            Map.of(
                    ArmorType.BOOTS, 2,
                    ArmorType.LEGGINGS, 5,
                    ArmorType.CHESTPLATE, 7,
                    ArmorType.HELMET, 3
            ),
            14,
            SoundEvents.ARMOR_EQUIP_IRON,
            0.5F,
            0.1F,
            OCItemTags.OBSIDIAN_ARMOR_MATERIALS,
            ResourceKey.create(EquipmentAssets.ROOT_ID, ObsidianCraft.id("obsidian"))
    );
    public static final Item OBSIDIAN_HELMET = register(OCItemIds.OBSIDIAN_HELMET, new Item.Properties().humanoidArmor(OBSIDIAN_ARMOR_MATERIAL, ArmorType.HELMET));
    public static final Item OBSIDIAN_CHESTPLATE = register(OCItemIds.OBSIDIAN_CHESTPLATE, new Item.Properties().humanoidArmor(OBSIDIAN_ARMOR_MATERIAL, ArmorType.CHESTPLATE));
    public static final Item OBSIDIAN_LEGGINGS = register(OCItemIds.OBSIDIAN_LEGGINGS, new Item.Properties().humanoidArmor(OBSIDIAN_ARMOR_MATERIAL, ArmorType.LEGGINGS));
    public static final Item OBSIDIAN_BOOTS = register(OCItemIds.OBSIDIAN_BOOTS, new Item.Properties().humanoidArmor(OBSIDIAN_ARMOR_MATERIAL, ArmorType.BOOTS));

    // Custom(Special) Items
//    public static final Item TEST_ITEM = register(OCItemIds.TEST_ITEM, ObsidianPickaxe::new, new ObsidianPickaxe.Properties());

    // Items
    public static final Item[] Items = {
            OBSIDIAN_INGOT,
            OBSIDIAN_SWORD,
            OBSIDIAN_PICKAXE,
            OBSIDIAN_SHOVEL,
            OBSIDIAN_HOE,
            OBSIDIAN_AXE,
            OBSIDIAN_HELMET,
            OBSIDIAN_CHESTPLATE,
            OBSIDIAN_LEGGINGS,
            OBSIDIAN_BOOTS
    };

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
