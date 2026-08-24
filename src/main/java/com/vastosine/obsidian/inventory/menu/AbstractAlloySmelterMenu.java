package com.vastosine.obsidian.inventory.menu;

import com.vastosine.obsidian.inventory.slot.AlloySmelterFuelSlot;
import com.vastosine.obsidian.inventory.slot.AlloySmelterResultSlot;
import net.minecraft.core.component.DataComponents;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class AbstractAlloySmelterMenu extends RecipeBookMenu {
    private final Container container;
    private final ContainerData data;
    protected final Level level;
    private final RecipePropertySet acceptedInputs;
    private final RecipeBookType recipeBookType;
    private final int slotInputCount;
    private final int slotFuelCount;
    private final int slotResultCount;
    private final int slotCount;

    protected AbstractAlloySmelterMenu(
            final MenuType<?> menuType,
            final ResourceKey<RecipePropertySet> allowedInputs,
            final RecipeBookType recipeBookType,
            final int containerId,
            final Inventory inventory,
            final int slotInputCount,
            final int slotFuelCount,
            final int slotResultCount
    ) {
        int slotCount = slotInputCount + slotFuelCount + slotResultCount;
        this(menuType, allowedInputs, recipeBookType, containerId, inventory, new SimpleContainer(slotCount), new SimpleContainerData(slotCount), slotInputCount, slotFuelCount, slotResultCount);
    }

    protected AbstractAlloySmelterMenu(
            final MenuType<?> menuType,
            final ResourceKey<RecipePropertySet> allowedInputs,
            final RecipeBookType recipeBookType,
            final int containerId,
            final Inventory inventory,
            final Container container,
            final ContainerData data,
            final int slotInputCount,
            final int slotFuelCount,
            final int slotResultCount
    ) {
        this.slotInputCount = slotInputCount;
        this.slotFuelCount = slotFuelCount;
        this.slotResultCount = slotResultCount;
        slotCount = slotInputCount + slotFuelCount + slotResultCount;
        super(menuType, containerId);
        this.recipeBookType = recipeBookType;
        checkContainerSize(container, slotCount);
        checkContainerDataCount(data, 4);
        this.container = container;
        this.data = data;
        this.level = inventory.player.level();
        this.acceptedInputs = this.level.recipeAccess().propertySet(allowedInputs);
        for (int slot = 0; slot < slotInputCount; slot++) {
            this.addSlot(new Slot(container, slot, 56 + (2 * slot + 1 - slotInputCount) * 9, 17));
        }
        for (int slot = 0; slot < slotFuelCount; slot++) {
            this.addSlot(new AlloySmelterFuelSlot(this, container, slotInputCount + slot, 56 + (2 * slot + 1 - slotFuelCount) * 9, 53));
        }
        this.addSlot(new AlloySmelterResultSlot(inventory.player, container, slotInputCount + slotFuelCount, 116, 35));
        for (int slot = 1; slot < slotResultCount; slot++) {
            this.addSlot(new AlloySmelterResultSlot(inventory.player, container, slotInputCount + slotFuelCount + slot, 122 + 18 * slot, 36 + slotCount));
        }
        this.addStandardInventorySlots(inventory, 8, 84);
        this.addDataSlots(data);
    }

    public boolean isInputSlot(int slot) {
        return slot >= 0 && slot < slotInputCount;
    }

    public boolean isFuelSlot(int slot) {
        return slot >= slotInputCount && slot < slotInputCount + slotFuelCount;
    }

    public boolean isResultSlot(int slot) {
        return slot >= slotInputCount + slotFuelCount && slot < slotInputCount + slotFuelCount + slotResultCount;
    }

    public void fillCraftSlotsStackedContents(final StackedItemContents stackedContents) {
        if (this.container instanceof StackedContentsCompatible stackedContentsCompatible) {
            stackedContentsCompatible.fillStackedContents(stackedContents);
        }
    }

//    public Slot getResultSlot() {
//        return this.slots.get(2);
//    }

    @Override
    public boolean stillValid(final Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(final Player player, final int slotIndex) {
        ItemStack clicked = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            clicked = stack.copy();
            if (isResultSlot(slotIndex)) {
                if (!this.moveItemStackTo(stack, slotCount, 36 + slotCount, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(stack, clicked);
            } else if (!isInputSlot(slotIndex) && !isFuelSlot(slotIndex)) {
                if (this.canSmelt(stack) && !this.moveItemStackTo(stack, 0, slotInputCount, false) && !this.isFuel(stack)) {
                    return ItemStack.EMPTY;
                }
                if (this.isFuel(stack)) {
                    if (!this.moveItemStackTo(stack, slotInputCount, slotInputCount + slotFuelCount, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= slotCount && slotIndex < 27 + slotCount) {
                    if (!this.moveItemStackTo(stack, 27 + slotCount, 36 + slotCount, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= 27 + slotCount && slotIndex < 36 + slotCount && !this.moveItemStackTo(stack, 3, 27 + slotCount, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, slotCount, 36 + slotCount, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == clicked.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return clicked;
    }

    protected boolean canSmelt(final ItemStack itemStack) {
        return this.acceptedInputs.test(itemStack);
    }

    public boolean isFuel(final ItemStack itemStack) {
        return itemStack.has(DataComponents.COOKING_FUEL);
    }

    public float getBurnProgress() {
        int current = this.data.get(2);
        int total = this.data.get(3);
        return total != 0 && current != 0 ? Mth.clamp((float) current / total, 0.0F, 1.0F) : 0.0F;
    }

    public float getLitProgress() {
        int litDuration = this.data.get(1);
        if (litDuration == 0) {
            litDuration = 200;
        }

        return Mth.clamp((float) this.data.get(0) / litDuration, 0.0F, 1.0F);
    }

    public boolean isLit() {
        return this.data.get(0) > 0;
    }

    @Override
    public PostPlaceAction handlePlacement(boolean useMaxItems, boolean allowDroppingItemsToClear, RecipeHolder<?> recipe, ServerLevel level, Inventory inventory) {
        final List<Slot> slotsToClear = List.of(this.getSlot(0), this.getSlot(slotInputCount + slotFuelCount));
        RecipeHolder<AbstractCookingRecipe> typedRecipe = (RecipeHolder<AbstractCookingRecipe>)recipe;
        return ServerPlaceRecipe.placeRecipe(new ServerPlaceRecipe.CraftingMenuAccess<AbstractCookingRecipe>() {
            @Override
            public void fillCraftSlotsStackedContents(final StackedItemContents stackedContents) {
                AbstractAlloySmelterMenu.this.fillCraftSlotsStackedContents(stackedContents);
            }

            @Override
            public void clearCraftingContent() {
                slotsToClear.forEach(s -> s.set(ItemStack.EMPTY));
            }

            @Override
            public boolean recipeMatches(final RecipeHolder<AbstractCookingRecipe> recipe) {
                return recipe.value().matches(new SingleRecipeInput(AbstractAlloySmelterMenu.this.container.getItem(0)), level);
            }
        }, 1, 1, List.of(this.getSlot(0)), slotsToClear, inventory, typedRecipe, useMaxItems, allowDroppingItemsToClear);
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return recipeBookType;
    }

    public Slot[] getResultSlots() {
        Slot[] slots = new Slot[slotResultCount];
        for (int slot = 0; slot < slotResultCount; slot++) {
            slots[slot] = this.getSlot(slotInputCount + slotFuelCount + slot);
        }
        return slots;
    }

    public Slot[] getFuelSlots() {
        Slot[] slots = new Slot[slotFuelCount];
        for (int slot = 0; slot < slotFuelCount; slot++) {
            slots[slot] = this.getSlot(slotInputCount + slot);
        }
        return slots;
    }
}
