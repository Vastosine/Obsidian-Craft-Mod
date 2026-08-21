package com.vastosine.obsidian.inventory.screen;

import com.vastosine.obsidian.ObsidianCraft;
import com.vastosine.obsidian.inventory.menu.ObsidianFurnaceMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeBookCategories;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ObsidianFurnaceScreen extends AbstractFurnaceScreen<ObsidianFurnaceMenu> {
    private static final Identifier LIT_PROGRESS_SPRITE = ObsidianCraft.id("container/obsidian_furnace/lit_progress");
    private static final Identifier BURN_PROGRESS_SPRITE = ObsidianCraft.id("container/obsidian_furnace/burn_progress");
    private static final Identifier TEXTURE = ObsidianCraft.id("textures/gui/container/obsidian_furnace.png");
    private static final Component FILTER_NAME = Component.translatable("gui.recipebook.toggleRecipes.smeltable");
    private static final List<RecipeBookComponent.TabInfo> TABS = List.of(
            new RecipeBookComponent.TabInfo(SearchRecipeBookCategory.FURNACE),
            new RecipeBookComponent.TabInfo(Items.PORKCHOP, RecipeBookCategories.FURNACE_FOOD),
            new RecipeBookComponent.TabInfo(Items.STONE, RecipeBookCategories.FURNACE_BLOCKS),
            new RecipeBookComponent.TabInfo(Items.LAVA_BUCKET, Items.EMERALD, RecipeBookCategories.FURNACE_MISC)
    );

    public ObsidianFurnaceScreen(final ObsidianFurnaceMenu menu, final Inventory inventory, final Component title) {
        super(menu, inventory, title, FILTER_NAME, TEXTURE, LIT_PROGRESS_SPRITE, BURN_PROGRESS_SPRITE, TABS);
    }
}

