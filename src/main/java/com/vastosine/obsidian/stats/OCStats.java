package com.vastosine.obsidian.stats;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;

public class OCStats {
    public static final Identifier INTERACT_WITH_OBSIDIAN_FURNACE = makeCustomStat("interact_with_obsidian_furnace");
    public static final Identifier INTERACT_WITH_ALLOY_SMELTER = makeCustomStat("interact_with_alloy_smelter");

    private static Identifier makeCustomStat(final String id) {
        return makeCustomStat(id, StatFormatter.DEFAULT);
    }

    private static Identifier makeCustomStat(final String id, final StatFormatter formatter) {
        Identifier location = ObsidianCraft.id(id);
        Registry.register(BuiltInRegistries.CUSTOM_STAT, location, location);
        Stats.CUSTOM.get(location, formatter);
        return location;
    }

    private static <T> StatType<T> makeRegistryStatType(final String name, final Registry<T> registry) {
        Component displayName = Component.translatable("stat_type.obsidiann." + name);
        return Registry.register(BuiltInRegistries.STAT_TYPE, ObsidianCraft.id(name), new StatType<>(registry, displayName));
    }

    public static void onInitialize() {
        ObsidianCraft.onInitializeInfo("Stats");
    }
}
