package com.vastosine.obsidian.item;

import com.vastosine.obsidian.ObsidianCraft;
import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;

public final class ModItems {
	// Repair tag shared by tools and armor (obsidian ingot)
	public static final TagKey<Item> OBSIDIAN_INGOT_TAG = TagKey.create(Registries.ITEM, ObsidianCraft.id("obsidian_ingot"));
	// Blocks the obsidian pickaxe mines faster (obsidian, crying obsidian, obsidian block)
	public static final TagKey<Block> OBSIDIAN_BLOCKS_TAG = TagKey.create(Registries.BLOCK, ObsidianCraft.id("obsidian_blocks"));

	private static final String UNBREAKING_TOOLTIP = "item.obsidian.unbreaking.tooltip";
	private static final String FIRE_PROTECTION_TOOLTIP = "item.obsidian.fire_protection.tooltip";

	// Hardcoded effect lines rendered like vanilla enchantment names (colored, non-italic)
	public static final Component UNBREAKING_LINE = tooltipLine(UNBREAKING_TOOLTIP, ChatFormatting.DARK_BLUE);
	public static final Component FIRE_PROTECTION_LINE = tooltipLine(FIRE_PROTECTION_TOOLTIP, ChatFormatting.GOLD);

	private static Component tooltipLine(String key, ChatFormatting color) {
		return Component.translatable(key).withStyle(style -> style.withColor(color).withItalic(false));
	}

	public static final ResourceKey<Item> OBSIDIAN_INGOT_KEY = key("obsidian_ingot");
	public static final Item OBSIDIAN_INGOT = registerItem(OBSIDIAN_INGOT_KEY, new Item.Properties());

	// Iron-tier stats with doubled durability (500 = 2x iron 250), diamond mining level,
	// repaired with obsidian ingots on an anvil
	private static final ToolMaterial OBSIDIAN_TOOL_MATERIAL = new ToolMaterial(
		BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
		500, 6.0F, 2.0F, 14,
		OBSIDIAN_INGOT_TAG
	);

	// Iron armor stats with doubled durability (30 = 2x iron 15) and 1.0 toughness
	private static final ArmorMaterial OBSIDIAN_ARMOR_MATERIAL = new ArmorMaterial(
		30,
		Map.of(
			ArmorType.BOOTS, 2,
			ArmorType.LEGGINGS, 5,
			ArmorType.CHESTPLATE, 6,
			ArmorType.HELMET, 2,
			ArmorType.BODY, 5
		),
		9,
		SoundEvents.ARMOR_EQUIP_IRON,
		1.0F,
		0.0F,
		OBSIDIAN_INGOT_TAG,
		ResourceKey.create(EquipmentAssets.ROOT_ID, ObsidianCraft.id("obsidian"))
	);

	// Hardcoded Unbreaking I (no enchantment): 50% chance to not consume durability.
	// Applied to every obsidian tool and armor piece; the handler runs on every
	// ItemStack.hurtAndBreak(amount, entity, slot), which covers both mining and armor damage.
	private static final CustomDamageHandler OBSIDIAN_UNBREAKING = (stack, amount, entity, slot, breakCallback) ->
		entity.level().getRandom().nextInt(2) == 0 ? 0 : amount;

	// The obsidian boost rule must come first: getMiningSpeed returns the first matching rule with a speed
	private static final Tool OBSIDIAN_PICKAXE_TOOL = new Tool(
		List.of(
			Tool.Rule.minesAndDrops(blocksTag(OBSIDIAN_BLOCKS_TAG), 15.0F), // 6.0 * 2.5 = +150% on obsidian
			Tool.Rule.minesAndDrops(blocksTag(BlockTags.MINEABLE_WITH_PICKAXE), 6.0F),
			Tool.Rule.deniesDrops(blocksTag(BlockTags.INCORRECT_FOR_DIAMOND_TOOL))
		),
		1.0F,
		1,
		true
	);

	// The pickaxe renders its tooltip lines in ObsidianPickaxeItem (Unbreaking line always
	// visible, speed/repair lines only while holding Shift), so it has no LORE component.
	public static final Item OBSIDIAN_PICKAXE = registerItem(
		key("obsidian_pickaxe"),
		new Item.Properties()
			.tool(OBSIDIAN_TOOL_MATERIAL, BlockTags.MINEABLE_WITH_PICKAXE, 1.0F, -2.8F, 0.0F)
			.component(DataComponents.TOOL, OBSIDIAN_PICKAXE_TOOL)
			.customDamage(OBSIDIAN_UNBREAKING),
		ObsidianPickaxeItem::new
	);
	public static final Item OBSIDIAN_AXE = registerItem(
		key("obsidian_axe"),
		new Item.Properties()
			.axe(OBSIDIAN_TOOL_MATERIAL, 5.0F, -3.0F)
			.component(DataComponents.LORE, new ItemLore(List.of(UNBREAKING_LINE)))
			.customDamage(OBSIDIAN_UNBREAKING)
	);
	public static final Item OBSIDIAN_SHOVEL = registerItem(
		key("obsidian_shovel"),
		new Item.Properties()
			.shovel(OBSIDIAN_TOOL_MATERIAL, 1.5F, -3.0F)
			.component(DataComponents.LORE, new ItemLore(List.of(UNBREAKING_LINE)))
			.customDamage(OBSIDIAN_UNBREAKING)
	);
	public static final Item OBSIDIAN_HOE = registerItem(
		key("obsidian_hoe"),
		new Item.Properties()
			.hoe(OBSIDIAN_TOOL_MATERIAL, -3.0F, 0.0F)
			.component(DataComponents.LORE, new ItemLore(List.of(UNBREAKING_LINE)))
			.customDamage(OBSIDIAN_UNBREAKING)
	);
	public static final Item OBSIDIAN_SWORD = registerItem(
		key("obsidian_sword"),
		new Item.Properties()
			.sword(OBSIDIAN_TOOL_MATERIAL, 3.0F, -2.4F)
			.component(DataComponents.LORE, new ItemLore(List.of(UNBREAKING_LINE)))
			.customDamage(OBSIDIAN_UNBREAKING)
	);

	// All obsidian armor has hardcoded Fire Protection II (see LivingEntityMixin) and Unbreaking I.
	// No enchantment components: the effects are pure code, so tooltips explain them.
	public static final Item OBSIDIAN_HELMET = registerItem(key("obsidian_helmet"), armorProperties(ArmorType.HELMET));
	public static final Item OBSIDIAN_CHESTPLATE = registerItem(key("obsidian_chestplate"), armorProperties(ArmorType.CHESTPLATE));
	public static final Item OBSIDIAN_LEGGINGS = registerItem(key("obsidian_leggings"), armorProperties(ArmorType.LEGGINGS));
	public static final Item OBSIDIAN_BOOTS = registerItem(key("obsidian_boots"), armorProperties(ArmorType.BOOTS));

	private static Item.Properties armorProperties(ArmorType type) {
		return new Item.Properties()
			.humanoidArmor(OBSIDIAN_ARMOR_MATERIAL, type)
			.component(DataComponents.LORE, new ItemLore(List.of(FIRE_PROTECTION_LINE, UNBREAKING_LINE)))
			.customDamage(OBSIDIAN_UNBREAKING);
	}

	// Counts worn obsidian armor pieces (shared by the fire effect mixins:
	// LivingEntityMixin reduces fire damage, EntityMixin reduces burn time)
	public static int countObsidianArmorPieces(LivingEntity entity) {
		int pieces = 0;
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
				continue;
			}

			Item item = entity.getItemBySlot(slot).getItem();
			if (item == OBSIDIAN_HELMET
				|| item == OBSIDIAN_CHESTPLATE
				|| item == OBSIDIAN_LEGGINGS
				|| item == OBSIDIAN_BOOTS) {
				pieces++;
			}
		}
		return pieces;
	}

	private static HolderSet<Block> blocksTag(TagKey<Block> tag) {
		HolderGetter<Block> lookup = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
		return lookup.getOrThrow(tag);
	}

	private static ResourceKey<Item> key(String path) {
		return ResourceKey.create(Registries.ITEM, ObsidianCraft.id(path));
	}

	private ModItems() {
	}

	public static void init() {
		// Trigger registration by initializing the static fields above
	}

	private static Item registerItem(ResourceKey<Item> key, Item.Properties properties) {
		return Registry.register(BuiltInRegistries.ITEM, key, new Item(properties.setId(key)));
	}

	private static Item registerItem(ResourceKey<Item> key, Item.Properties properties, ItemFactory factory) {
		return Registry.register(BuiltInRegistries.ITEM, key, factory.create(properties.setId(key)));
	}

	@FunctionalInterface
	private interface ItemFactory {
		Item create(Item.Properties properties);
	}
}
