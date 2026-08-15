package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.ObsidianCraft;
import com.vastosine.obsidian.item.OCItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class OCAdvancementProvider extends FabricAdvancementProvider {
    public OCAdvancementProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    public static final String DEFAULT_BACKGROUND_PATH = "gui/advancements/background";

    private static @NonNull AdvancementHolder getSave(Consumer<AdvancementHolder> consumer, String name, Advancement.Builder builder) {
        return builder.save(consumer, ObsidianCraft.id("main/" + name));
    }

    private static Advancement.@NonNull Builder getDefaultBuilder(String name, @Nullable AdvancementHolder parent, ItemLike icon) {
        Advancement.Builder builder = Advancement.Builder.advancement();
        boolean isRoot = parent == null;
        if (!isRoot) {
            builder.parent(parent);
        }
        builder.display(
                icon,
                Component.translatable("advancements.oc." + name + ".title"),
                Component.translatable("advancements.oc." + name + ".description"),
                isRoot ? ObsidianCraft.id(DEFAULT_BACKGROUND_PATH) : null,
                AdvancementType.TASK,
                !isRoot,
                !isRoot,
                false
        );
        return builder;
    }

    public AdvancementHolder addDefaultAdvancement(
            Consumer<AdvancementHolder> consumer,
            final String name,
            @Nullable AdvancementHolder parent,
            ItemLike icon,
            final String criterionName,
            final Criterion<?> criterion
    ) {
        Advancement.Builder builder = getDefaultBuilder(name, parent, icon)
                .addCriterion(criterionName, criterion);
        return getSave(consumer, name, builder);
    }

    public AdvancementHolder addGetItemAdvancement(
            Consumer<AdvancementHolder> consumer,
            final String name,
            @Nullable AdvancementHolder parent,
            Item item
    ) {
        return addDefaultAdvancement(
                consumer, name, parent,
                item,
                item.toString(),
                InventoryChangeTrigger.TriggerInstance.hasItems(item)
        );
    }

    public AdvancementHolder addGetItemAdvancement(
            Consumer<AdvancementHolder> consumer,
            final String name,
            @Nullable AdvancementHolder parent,
            ItemLike icon,
            AdvancementRequirements.Strategy strategy,
            Item... items
    ) {
        Advancement.Builder builder = getDefaultBuilder(name, parent, icon);
        builder.requirements(strategy);
        for (Item item : items) {
            builder.addCriterion(item.toString(), InventoryChangeTrigger.TriggerInstance.hasItems(item));
        }
        return getSave(consumer, name, builder);
    }

    public AdvancementHolder addGetAnyItemAdvancement(
            Consumer<AdvancementHolder> consumer,
            final String name,
            @Nullable AdvancementHolder parent,
            ItemLike icon,
            Item... items
    ) {
        return addGetItemAdvancement(consumer, name, parent, icon, AdvancementRequirements.Strategy.OR, items);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        AdvancementHolder root = addGetItemAdvancement(consumer, "root", null, Items.OBSIDIAN);
        AdvancementHolder smeltObsidian = addGetItemAdvancement(consumer, "smelt_obsidian", root, OCItems.OBSIDIAN_INGOT);
        AdvancementHolder obtainObsidianPickaxe = addGetItemAdvancement(consumer, "obsidian_pickaxe", smeltObsidian, OCItems.OBSIDIAN_PICKAXE);
        AdvancementHolder obtainObsidianArmor = addGetAnyItemAdvancement(
                consumer,
                "obsidian_armor",
                smeltObsidian,
                OCItems.OBSIDIAN_CHESTPLATE,
                OCItems.OBSIDIAN_BOOTS,
                OCItems.OBSIDIAN_LEGGINGS,
                OCItems.OBSIDIAN_CHESTPLATE,
                OCItems.OBSIDIAN_HELMET
        );
    }
}
