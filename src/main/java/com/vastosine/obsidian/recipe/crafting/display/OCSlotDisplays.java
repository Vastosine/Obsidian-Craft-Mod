package com.vastosine.obsidian.recipe.crafting.display;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public class OCSlotDisplays {
    private static void register(final String id, final SlotDisplay.Type<?> type) {
        Registry.register(BuiltInRegistries.SLOT_DISPLAY, ObsidianCraft.id(id), type);
    }

    public static void onInitialize() {
        ObsidianCraft.onInitializeInfo("Slot Displays");
        register("ingredient_with_count", OCIngredientSlotDisplay.TYPE);
    }
}
