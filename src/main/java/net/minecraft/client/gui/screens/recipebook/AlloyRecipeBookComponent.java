package net.minecraft.client.gui.screens.recipebook;

import com.vastosine.obsidian.block.entity.AlloyFurnaceBlockEntity;
import com.vastosine.obsidian.menu.AlloyFurnaceMenu;
import com.vastosine.obsidian.recipe.AlloyRecipeDisplay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;

/**
 * Recipe book UI for the Alloy Furnace. The alloy tab shows the custom alloy recipes
 * (three ingredient ghosts, one per input slot); the vanilla furnace tabs show the
 * smelting fallback recipes (single ingredient ghost into input slot 0).
 * The filter button sprites are the vanilla furnace ones (recolored by the global
 * assets/minecraft override).
 *
 * Lives in the vanilla recipe book package on purpose: GhostSlots.setInput/setResult
 * are protected (package-visible), so a subclass in a mod package cannot fill ghosts.
 */
@Environment(EnvType.CLIENT)
public class AlloyRecipeBookComponent extends RecipeBookComponent<AlloyFurnaceMenu> {
	private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(
		Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled"),
		Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled"),
		Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled_highlighted"),
		Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled_highlighted")
	);
	private static final Component FILTER_NAME = Component.translatable("gui.recipebook.toggleRecipes.smeltable");

	public AlloyRecipeBookComponent(final AlloyFurnaceMenu menu, final List<RecipeBookComponent.TabInfo> tabInfos) {
		super(menu, tabInfos);
	}

	@Override
	protected WidgetSprites getFilterButtonTextures() {
		return FILTER_SPRITES;
	}

	@Override
	protected boolean isCraftingSlot(final Slot slot) {
		return switch (slot.index) {
			case 0, 1, 2, 3 -> true; // three ingredient slots plus the fuel slot
			default -> false;
		};
	}

	@Override
	protected void selectMatchingRecipes(final RecipeCollection collection, final StackedItemContents stackedContents) {
		collection.selectRecipes(stackedContents, display -> display instanceof AlloyRecipeDisplay || display instanceof FurnaceRecipeDisplay);
	}

	@Override
	protected Component getRecipeFilterName() {
		return FILTER_NAME;
	}

	@Override
	protected void fillGhostRecipe(final GhostSlots ghostSlots, final RecipeDisplay recipe, final ContextMap context) {
		ghostSlots.setResult(this.menu.getSlot(AlloyFurnaceBlockEntity.SLOT_OUTPUT), context, recipe.result());
		if (recipe instanceof AlloyRecipeDisplay alloyRecipe) {
			List<Slot> inputSlots = List.of(this.menu.getSlot(0), this.menu.getSlot(1), this.menu.getSlot(2));
			for (int i = 0; i < alloyRecipe.ingredients().size() && i < inputSlots.size(); i++) {
				ghostSlots.setInput(inputSlots.get(i), context, alloyRecipe.ingredients().get(i));
			}
		} else if (recipe instanceof FurnaceRecipeDisplay furnaceRecipe) {
			ghostSlots.setInput(this.menu.getSlot(0), context, furnaceRecipe.ingredient());
		}
		SlotDisplay fuel = recipe instanceof AlloyRecipeDisplay alloyRecipe
			? alloyRecipe.fuel()
			: recipe instanceof FurnaceRecipeDisplay furnaceRecipe ? furnaceRecipe.fuel() : null;
		Slot fuelSlot = this.menu.getSlot(AlloyFurnaceBlockEntity.SLOT_FUEL);
		if (fuel != null && fuelSlot.getItem().isEmpty()) {
			ghostSlots.setInput(fuelSlot, context, fuel);
		}
	}
}
