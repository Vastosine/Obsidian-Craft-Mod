package com.vastosine.obsidian.client.screen;

import com.vastosine.obsidian.ObsidianCraft;
import com.vastosine.obsidian.block.OCBlocks;
import com.vastosine.obsidian.inventory.menu.AdvancedAlloySmelterMenu;
import com.vastosine.obsidian.recipe.crafting.OCRecipeBookCategories;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

@Environment(EnvType.CLIENT)
public class AdvancedAlloySmelterScreen extends AbstractAlloySmelterScreen<AdvancedAlloySmelterMenu> {
    private static final Identifier LIT_PROGRESS_SPRITE = ObsidianCraft.id("container/advanced_alloy_smelter/lit_progress");
    private static final Identifier BURN_PROGRESS_SPRITE = ObsidianCraft.id("container/advanced_alloy_smelter/burn_progress");
    private static final Identifier TEXTURE = ObsidianCraft.id("textures/gui/container/advanced_alloy_smelter.png");
    private static final Component FILTER_NAME = Component.translatable("gui.recipebook.toggleRecipes.smeltable");
    private static final List<RecipeBookComponent.TabInfo> TABS = List.of(
            new RecipeBookComponent.TabInfo(OCBlocks.ADVANCED_ALLOY_SMELTER.asItem(), OCRecipeBookCategories.ALLOYING),
            new RecipeBookComponent.TabInfo(SearchRecipeBookCategory.FURNACE)
    );

    public AdvancedAlloySmelterScreen(final AdvancedAlloySmelterMenu menu, final Inventory inventory, final Component title) {
        super(menu, inventory, title, FILTER_NAME, TEXTURE, LIT_PROGRESS_SPRITE, BURN_PROGRESS_SPRITE, TABS);
    }
}
