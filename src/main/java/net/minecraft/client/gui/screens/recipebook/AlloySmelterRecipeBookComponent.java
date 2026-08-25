package net.minecraft.client.gui.screens.recipebook;


import com.vastosine.obsidian.inventory.menu.AbstractAlloySmelterMenu;
import com.vastosine.obsidian.recipe.crafting.display.AlloyingRecipeDisplay;
import com.vastosine.obsidian.utils.OCUtils;
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
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

import java.util.List;

@Environment(EnvType.CLIENT)
public class AlloySmelterRecipeBookComponent extends RecipeBookComponent<AbstractAlloySmelterMenu> {
    private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(
            Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled"),
            Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled"),
            Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled_highlighted"),
            Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled_highlighted")
    );
    private final Component recipeFilterName;
    private final int slotCount;
    private final int slotInputCount;
    private final int slotFuelCount;
    private final int slotResultCount;

    public AlloySmelterRecipeBookComponent(final AbstractAlloySmelterMenu menu, final Component recipeFilterName, final List<TabInfo> tabInfos, int slotInputCount, int slotFuelCount, int slotResultCount) {
        super(menu, tabInfos);
        this.recipeFilterName = recipeFilterName;
        this.slotInputCount = slotInputCount;
        this.slotFuelCount = slotFuelCount;
        this.slotResultCount = slotResultCount;
        this.slotCount = slotInputCount + slotFuelCount + slotResultCount;
    }

    @Override
    protected WidgetSprites getFilterButtonTextures() {
        return FILTER_SPRITES;
    }

    @Override
    protected boolean isCraftingSlot(final Slot slot) {
        return switch (slot.index) {
            case 0, 1, 2 -> true;
            default -> false;
        };
    }

    @Override
    protected void fillGhostRecipe(final GhostSlots ghostSlots, final RecipeDisplay recipe, final ContextMap context) {
        ghostSlots.setResult(this.menu.getResultSlots()[0], context, recipe.result());
        if (recipe instanceof FurnaceRecipeDisplay furnaceRecipe) {
            ghostSlots.setInput(this.menu.slots.getFirst(), context, furnaceRecipe.ingredient());
            Slot[] fuelSlot = menu.getFuelSlots();
            for (Slot slot : fuelSlot) {
                if (!slot.getItem().isEmpty()) return;
            }
            ghostSlots.setInput(fuelSlot[0], context, furnaceRecipe.fuel());
        } else if (recipe instanceof AlloyingRecipeDisplay alloyingRecipe) {
            for (int slot : OCUtils.getSequence(slotInputCount)) {
                ghostSlots.setInput(menu.slots.get(slot), context, alloyingRecipe.ingredients().get(slot));
            }
            Slot[] fuelSlot = menu.getFuelSlots();
            for (Slot slot : fuelSlot) {
                if (!slot.getItem().isEmpty()) return;
            }
            ghostSlots.setInput(fuelSlot[0], context, alloyingRecipe.fuel());
        }
    }

    @Override
    protected Component getRecipeFilterName() {
        return this.recipeFilterName;
    }

    @Override
    protected void selectMatchingRecipes(final RecipeCollection collection, final StackedItemContents stackedContents) {
        collection.selectRecipes(stackedContents, display -> display instanceof AlloyingRecipeDisplay || display instanceof FurnaceRecipeDisplay);
    }
}
