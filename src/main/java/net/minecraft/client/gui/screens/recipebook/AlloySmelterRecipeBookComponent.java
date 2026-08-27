package net.minecraft.client.gui.screens.recipebook;


import com.vastosine.obsidian.inventory.menu.NeedFuelMenu;
import com.vastosine.obsidian.recipe.crafting.display.NeedFuelRecipeDisplay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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

@Environment(EnvType.CLIENT)
public class AlloySmelterRecipeBookComponent extends RecipeBookComponent<NeedFuelMenu> {
    private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(
            Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled"),
            Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled"),
            Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled_highlighted"),
            Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled_highlighted")
    );
    private final Component recipeFilterName;

    public AlloySmelterRecipeBookComponent(final NeedFuelMenu menu, final Component recipeFilterName, final List<TabInfo> tabInfos) {
        super(menu, tabInfos);
        this.recipeFilterName = recipeFilterName;
    }

    @Override
    public void extractGhostRecipe(GuiGraphicsExtractor graphics, boolean isResultSlotBig) {
        super.extractGhostRecipe(graphics, false);
    }

    @Override
    protected WidgetSprites getFilterButtonTextures() {
        return FILTER_SPRITES;
    }

    @Override
    protected boolean isCraftingSlot(final Slot slot) {
        return menu.isInputSlot(slot.index) || menu.isResultSlot(slot.index);
    }

    @Override
    protected void fillGhostRecipe(final GhostSlots ghostSlots, final RecipeDisplay recipe, final ContextMap context) {
        List<Slot> ingredientSlots = menu.getInputSlots();
        List<Slot> fuelSlots = menu.getFuelSlots();
        List<Slot> resultSlots = menu.getResultSlots();
        SlotDisplay fuel = null;
        if (recipe instanceof FurnaceRecipeDisplay furnaceRecipe) {
            ghostSlots.setInput(ingredientSlots.getFirst(), context, furnaceRecipe.ingredient());
            fuel = furnaceRecipe.fuel();
            ghostSlots.setResult(resultSlots.getFirst(), context, recipe.result());
        } else if (recipe instanceof NeedFuelRecipeDisplay alloyingRecipe) {
            for (int i = 0; i < alloyingRecipe.ingredients().size() && i < ingredientSlots.size(); i++) {
                ghostSlots.setResult(ingredientSlots.get(i), context, alloyingRecipe.ingredients().get(i));
            }
            fuel = alloyingRecipe.fuel();
            for (int i = 0; i < alloyingRecipe.results().size() && i < resultSlots.size(); i++) {
                ghostSlots.setResult(resultSlots.get(i), context, alloyingRecipe.results().get(i));
            }
        }
        if (fuel != null && fuelSlots.stream().allMatch(i -> i.getItem().isEmpty())) {
            ghostSlots.setInput(fuelSlots.getFirst(), context, fuel);
        }
    }

    @Override
    protected Component getRecipeFilterName() {
        return this.recipeFilterName;
    }

    @Override
    protected void selectMatchingRecipes(final RecipeCollection collection, final StackedItemContents stackedContents) {
        collection.selectRecipes(stackedContents, display -> display instanceof NeedFuelRecipeDisplay || display instanceof FurnaceRecipeDisplay);
    }
}
