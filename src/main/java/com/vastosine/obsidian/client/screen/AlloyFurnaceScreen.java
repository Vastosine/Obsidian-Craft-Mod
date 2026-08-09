package com.vastosine.obsidian.client.screen;

import com.vastosine.obsidian.ObsidianCraft;
import com.vastosine.obsidian.block.ModBlocks;
import com.vastosine.obsidian.menu.AlloyFurnaceMenu;
import com.vastosine.obsidian.recipe.ModRecipeBookCategories;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.AlloyRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeBookCategories;

import java.util.List;

/**
 * The Alloy Furnace screen. Extends AbstractRecipeBookScreen directly (not
 * AbstractFurnaceScreen, whose constructor hard-codes the vanilla FurnaceRecipeBookComponent).
 * Tabs: the custom alloy tab plus the vanilla furnace food/blocks/misc tabs
 * (the machine also cooks every vanilla smelting recipe). No search tab: the
 * search category enum cannot be extended.
 */
@Environment(EnvType.CLIENT)
public class AlloyFurnaceScreen extends AbstractRecipeBookScreen<AlloyFurnaceMenu> {
	private static final Identifier TEXTURE = ObsidianCraft.id("textures/gui/container/alloy_furnace.png");
	private static final Identifier LIT_PROGRESS_SPRITE = ObsidianCraft.id("container/alloy_furnace/lit_progress");
	private static final Identifier BURN_PROGRESS_SPRITE = ObsidianCraft.id("container/alloy_furnace/burn_progress");

	private static final List<RecipeBookComponent.TabInfo> TABS = List.of(
		new RecipeBookComponent.TabInfo(ModBlocks.ALLOY_FURNACE.asItem(), ModRecipeBookCategories.ALLOY),
		new RecipeBookComponent.TabInfo(Items.PORKCHOP, RecipeBookCategories.FURNACE_FOOD),
		new RecipeBookComponent.TabInfo(Items.STONE, RecipeBookCategories.FURNACE_BLOCKS),
		new RecipeBookComponent.TabInfo(Items.LAVA_BUCKET, Items.EMERALD, RecipeBookCategories.FURNACE_MISC)
	);

	public AlloyFurnaceScreen(final AlloyFurnaceMenu menu, final Inventory inventory, final Component title) {
		// imageWidth/imageHeight default to the vanilla 176x166 container size
		super(menu, new AlloyRecipeBookComponent(menu, TABS), inventory, title);
	}

	@Override
	public void init() {
		super.init();
		this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
	}

	@Override
	protected ScreenPosition getRecipeBookButtonPosition() {
		return new ScreenPosition(this.leftPos + 20, this.height / 2 - 49);
	}

	@Override
	public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		super.extractBackground(graphics, mouseX, mouseY, a);
		int xo = this.leftPos;
		int yo = this.topPos;
		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
		if (this.menu.isLit()) {
			int litSpriteHeight = 14;
			int litProgressHeight = Mth.ceil(this.menu.getLitProgress() * 13.0F) + 1;
			graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED, LIT_PROGRESS_SPRITE, 14, 14, 0, 14 - litProgressHeight, xo + 56, yo + 36 + 14 - litProgressHeight, 14, litProgressHeight
			);
		}

		int burnSpriteWidth = 24;
		int burnProgressWidth = Mth.ceil(this.menu.getBurnProgress() * 24.0F);
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BURN_PROGRESS_SPRITE, 24, 16, 0, 0, xo + 79, yo + 34, burnProgressWidth, 16);
	}
}
