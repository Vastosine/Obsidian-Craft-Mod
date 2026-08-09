package com.vastosine.obsidian.menu;

import com.vastosine.obsidian.block.entity.AlloyFurnaceBlockEntity;
import com.vastosine.obsidian.item.ModItemTags;
import com.vastosine.obsidian.recipe.AlloyFurnaceRecipe;
import com.vastosine.obsidian.recipe.AlloyRecipeInput;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.Prediction;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Menu for the Alloy Furnace, with a vanilla-style recipe book (RecipeBookMenu).
 * Slot layout: 0/1/2 = ingredients, 3 = fuel, 4 = output, 5..40 = player inventory.
 * Alloy recipes are placed order-insensitively with their per-ingredient consume
 * counts (ServerPlaceRecipe only supports 1x1-style grids, so the placement is
 * hand-written); smelting fallback recipes use the vanilla placement logic.
 */
public class AlloyFurnaceMenu extends RecipeBookMenu {
	public static final int SLOT_COUNT = 5;
	public static final int DATA_COUNT = 4;
	private static final int INV_SLOT_START = 5;
	private static final int INV_SLOT_END = 32;
	private static final int USE_ROW_SLOT_START = 32;
	private static final int USE_ROW_SLOT_END = 41;

	private final Container container;
	private final ContainerData data;
	protected final Level level;

	public AlloyFurnaceMenu(final int containerId, final Inventory inventory) {
		this(containerId, inventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(DATA_COUNT));
	}

	public AlloyFurnaceMenu(final int containerId, final Inventory inventory, final Container container, final ContainerData data) {
		super(ModMenuTypes.ALLOY_FURNACE, containerId);
		checkContainerSize(container, SLOT_COUNT);
		checkContainerDataCount(data, DATA_COUNT);
		this.container = container;
		this.data = data;
		this.level = inventory.player.level();
		this.addSlot(new Slot(container, 0, 30, 17));
		this.addSlot(new Slot(container, 1, 56, 17));
		this.addSlot(new Slot(container, 2, 82, 17));
		this.addSlot(new Slot(container, AlloyFurnaceBlockEntity.SLOT_FUEL, 56, 53));
		this.addSlot(new AlloyFurnaceResultSlot(inventory.player, container, AlloyFurnaceBlockEntity.SLOT_OUTPUT, 116, 35));
		this.addStandardInventorySlots(inventory, 8, 84);
		this.addDataSlots(data);
	}

	@Override
	public boolean stillValid(final Player player) {
		return this.container.stillValid(player);
	}

	@Override
	public ItemStack quickMoveStack(final Player player, final int slotIndex) {
		ItemStack clicked = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotIndex);
		if (slot != null && slot.hasItem()) {
			ItemStack stack = slot.getItem();
			clicked = stack.copy();
			if (slotIndex == AlloyFurnaceBlockEntity.SLOT_OUTPUT) {
				if (!this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
					return ItemStack.EMPTY;
				}
				slot.onQuickCraft(stack, clicked);
			} else if (slotIndex < SLOT_COUNT) {
				if (!this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
					return ItemStack.EMPTY;
				}
			} else if (this.isIngredient(stack) && this.moveItemStackTo(stack, 0, AlloyFurnaceBlockEntity.SLOT_FUEL, false)) {
				// moved into an ingredient slot
			} else if (this.isFuel(stack)) {
				if (!this.moveItemStackTo(stack, AlloyFurnaceBlockEntity.SLOT_FUEL, AlloyFurnaceBlockEntity.SLOT_FUEL + 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (slotIndex >= INV_SLOT_START && slotIndex < INV_SLOT_END) {
				if (!this.moveItemStackTo(stack, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
					return ItemStack.EMPTY;
				}
			} else if (slotIndex >= USE_ROW_SLOT_START && slotIndex < USE_ROW_SLOT_END && !this.moveItemStackTo(stack, INV_SLOT_START, INV_SLOT_END, false)) {
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

	// --- Recipe book ---

	@Override
	public RecipeBookType getRecipeBookType() {
		// The recipe book type enum cannot be extended; FURNACE is reused for the
		// book open/filter settings (the alloy tab is keyed by its own category).
		return RecipeBookType.FURNACE;
	}

	@Override
	public void fillCraftSlotsStackedContents(final StackedItemContents stackedContents) {
		for (int i = 0; i < 4; i++) {
			stackedContents.accountSimpleStack(this.container.getItem(i));
		}
	}

	@Override
	public RecipeBookMenu.PostPlaceAction handlePlacement(
		final boolean useMaxItems, final boolean allowDroppingItemsToClear, final RecipeHolder<?> recipe, final ServerLevel level, final Inventory inventory
	) {
		if (recipe.value() instanceof AlloyFurnaceRecipe alloyRecipe) {
			return this.placeAlloyRecipe(alloyRecipe, useMaxItems, allowDroppingItemsToClear, level, inventory);
		}
		if (recipe.value() instanceof SmeltingRecipe smeltRecipe) {
			return this.placeSmeltingRecipe(recipe, smeltRecipe, useMaxItems, allowDroppingItemsToClear, level, inventory);
		}
		return RecipeBookMenu.PostPlaceAction.NOTHING;
	}

	/**
	 * Alloy placement: order-insensitive with per-ingredient counts. The ingredient and
	 * output slots are cleared back into the player inventory, then one fresh copy is
	 * placed per craft (all that fit when useMaxItems). PLACE_GHOST_RECIPE is returned
	 * when the materials are insufficient, so the client shows the ghost recipe.
	 */
	private RecipeBookMenu.PostPlaceAction placeAlloyRecipe(
		final AlloyFurnaceRecipe recipe,
		final boolean useMaxItems,
		final boolean allowDroppingItemsToClear,
		final ServerLevel level,
		final Inventory inventory
	) {
		// Merge duplicate ingredient entries into per-ingredient totals (counts-aware)
		Map<Ingredient, Integer> needs = new LinkedHashMap<>();
		for (int i = 0; i < recipe.ingredients().size(); i++) {
			int count = i < recipe.counts().size() ? recipe.counts().get(i) : 1;
			if (count <= 0) {
				continue;
			}
			needs.merge(recipe.ingredients().get(i), count, Integer::sum);
		}

		boolean alreadyMatches = recipe.matches(
			new AlloyRecipeInput(this.container.getItem(0), this.container.getItem(1), this.container.getItem(2)), level
		);
		// A plain click on an already-placed recipe adds one more copy (vanilla build-up behavior)
		if (alreadyMatches && !useMaxItems) {
			return RecipeBookMenu.PostPlaceAction.NOTHING;
		}

		int craftable = this.countCraftable(needs, inventory);
		if (craftable == 0) {
			return RecipeBookMenu.PostPlaceAction.PLACE_GHOST_RECIPE;
		}
		int crafts = useMaxItems ? craftable : Math.min(2, craftable);

		// Return the input slots and the output slot to the player inventory
		for (int i = 0; i < 3; i++) {
			this.clearSlotBack(this.getSlot(i), inventory);
		}
		this.clearSlotBack(this.getSlot(AlloyFurnaceBlockEntity.SLOT_OUTPUT), inventory);

		for (int c = 0; c < crafts; c++) {
			if (!this.placeOneCopy(needs, inventory)) {
				break;
			}
		}
		inventory.setChanged();
		return RecipeBookMenu.PostPlaceAction.NOTHING;
	}

	/** Smelting fallback placement: vanilla 1x1 logic into the first ingredient slot. */
	private RecipeBookMenu.PostPlaceAction placeSmeltingRecipe(
		final RecipeHolder<?> recipeHolder,
		final SmeltingRecipe smeltRecipe,
		final boolean useMaxItems,
		final boolean allowDroppingItemsToClear,
		final ServerLevel level,
		final Inventory inventory
	) {
		List<Slot> slotsToClear = List.of(
			this.getSlot(0), this.getSlot(1), this.getSlot(2), this.getSlot(AlloyFurnaceBlockEntity.SLOT_OUTPUT)
		);
		RecipeHolder<SmeltingRecipe> typedRecipe = (RecipeHolder<SmeltingRecipe>) recipeHolder;
		return ServerPlaceRecipe.placeRecipe(
			new ServerPlaceRecipe.CraftingMenuAccess<SmeltingRecipe>() {
				@Override
				public void fillCraftSlotsStackedContents(final StackedItemContents stackedContents) {
					AlloyFurnaceMenu.this.fillCraftSlotsStackedContents(stackedContents);
				}

				@Override
				public void clearCraftingContent() {
					for (Slot slot : slotsToClear) {
						slot.set(ItemStack.EMPTY);
					}
				}

				@Override
				public boolean recipeMatches(final RecipeHolder<SmeltingRecipe> recipe) {
					for (int i = 0; i < 3; i++) {
						ItemStack stack = AlloyFurnaceMenu.this.container.getItem(i);
						if (!stack.isEmpty() && recipe.value().matches(new SingleRecipeInput(stack), level)) {
							return true;
						}
					}
					return false;
				}
			},
			1, 1, List.of(this.getSlot(0)), slotsToClear, inventory, typedRecipe, useMaxItems, allowDroppingItemsToClear
		);
	}

	// --- Alloy placement helpers ---

	/** Moves a slot's contents back into the player inventory, emptying the slot (vanilla clearGrid pattern). */
	private void clearSlotBack(final Slot slot, final Inventory inventory) {
		ItemStack stackCopy = slot.getItem().copy();
		inventory.placeItemBackInInventory(stackCopy, false, Prediction.SERVER_ONLY);
		slot.set(stackCopy);
	}

	/** Total copies craftable from the inventory plus the current input slots (grid items return to the inventory). */
	private int countCraftable(final Map<Ingredient, Integer> needs, final Inventory inventory) {
		int crafts = Integer.MAX_VALUE;
		for (Map.Entry<Ingredient, Integer> entry : needs.entrySet()) {
			int available = this.countMatching(entry.getKey(), inventory);
			crafts = Math.min(crafts, available / entry.getValue());
			if (crafts == 0) {
				break;
			}
		}
		return crafts == Integer.MAX_VALUE ? 0 : crafts;
	}

	private int countMatching(final Ingredient ingredient, final Inventory inventory) {
		int total = 0;
		for (int i = 0; i < 3; i++) {
			ItemStack stack = this.container.getItem(i);
			if (ingredient.test(stack)) {
				total += stack.getCount();
			}
		}
		for (ItemStack stack : inventory.getNonEquipmentItems()) {
			if (ingredient.test(stack)) {
				total += stack.getCount();
			}
		}
		return total;
	}

	/** Places one full copy of the recipe: every ingredient total is moved into the input slots. */
	private boolean placeOneCopy(final Map<Ingredient, Integer> needs, final Inventory inventory) {
		for (Map.Entry<Ingredient, Integer> entry : needs.entrySet()) {
			int need = entry.getValue();
			while (need > 0) {
				int inventorySlot = this.findMatchingInventorySlot(entry.getKey(), inventory);
				if (inventorySlot == -1) {
					return false;
				}
				ItemStack stack = inventory.getItem(inventorySlot);
				int take = Math.min(need, stack.getCount());
				ItemStack taken = inventory.removeItem(inventorySlot, take);
				need -= take;
				while (!taken.isEmpty()) {
					Slot target = this.findInputSlotFor(taken);
					if (target == null) {
						inventory.placeItemBackInInventory(taken, false, Prediction.SERVER_ONLY);
						return false;
					}
					ItemStack targetStack = target.getItem();
					if (targetStack.isEmpty()) {
						target.set(taken.split(taken.getCount()));
					} else {
						int space = target.getMaxStackSize() - targetStack.getCount();
						targetStack.grow(Math.min(space, taken.getCount()));
						taken.shrink(Math.min(space, taken.getCount()));
					}
				}
			}
		}
		return true;
	}

	/**
	 * Finds an inventory slot (main inventory first, then equipment) holding the ingredient.
	 * 26.3 keeps the player items in a private list plus an EntityEquipment; the equipment
	 * indices come from Inventory.EQUIPMENT_SLOT_MAPPING, which getItem/removeItem resolve.
	 */
	private int findMatchingInventorySlot(final Ingredient ingredient, final Inventory inventory) {
		NonNullList<ItemStack> items = inventory.getNonEquipmentItems();
		for (int i = 0; i < items.size(); i++) {
			ItemStack stack = items.get(i);
			if (!stack.isEmpty() && ingredient.test(stack)) {
				return i;
			}
		}
		for (Int2ObjectMap.Entry<EquipmentSlot> entry : Inventory.EQUIPMENT_SLOT_MAPPING.int2ObjectEntrySet()) {
			ItemStack stack = inventory.getItem(entry.getIntKey());
			if (!stack.isEmpty() && ingredient.test(stack)) {
				return entry.getIntKey();
			}
		}
		return -1;
	}

	/** First ingredient slot that is empty or holds the same item with room. */
	private @Nullable Slot findInputSlotFor(final ItemStack stack) {
		for (int i = 0; i < 3; i++) {
			Slot slot = this.getSlot(i);
			ItemStack slotStack = slot.getItem();
			if (slotStack.isEmpty()) {
				return slot;
			}
			if (ItemStack.isSameItemSameComponents(slotStack, stack) && slotStack.getCount() < slot.getMaxStackSize()) {
				return slot;
			}
		}
		return null;
	}

	// --- Ingredient / fuel checks ---

	public boolean isIngredient(final ItemStack itemStack) {
		// Vanilla smeltable items, plus the alloy material tags (gold/copper ingots are
		// not smeltable; netherite scrap neither — they must be checked separately).
		// Same rule as the block entity's canPlaceItem for the ingredient slots.
		return this.level.recipeAccess().propertySet(RecipePropertySet.FURNACE_INPUT).test(itemStack)
			|| itemStack.is(ModItemTags.GOLD_MATERIALS)
			|| itemStack.is(ModItemTags.COPPER_MATERIALS)
			|| itemStack.is(ModItemTags.DEBRIS_MATERIALS);
	}

	public boolean isFuel(final ItemStack itemStack) {
		return itemStack.has(DataComponents.COOKING_FUEL);
	}

	public float getBurnProgress() {
		int current = this.data.get(AlloyFurnaceBlockEntity.DATA_COOKING_PROGRESS);
		int total = this.data.get(AlloyFurnaceBlockEntity.DATA_COOKING_TOTAL_TIME);
		return total != 0 && current != 0 ? Mth.clamp((float) current / total, 0.0F, 1.0F) : 0.0F;
	}

	public float getLitProgress() {
		int litDuration = this.data.get(AlloyFurnaceBlockEntity.DATA_LIT_DURATION);
		if (litDuration == 0) {
			litDuration = 200;
		}
		return Mth.clamp((float) this.data.get(AlloyFurnaceBlockEntity.DATA_LIT_TIME) / litDuration, 0.0F, 1.0F);
	}

	public boolean isLit() {
		return this.data.get(AlloyFurnaceBlockEntity.DATA_LIT_TIME) > 0;
	}

	/**
	 * Output slot that pops the accumulated XP orbs and awards the cooked recipes
	 * when the result is taken (vanilla FurnaceResultSlot pattern).
	 */
	private static class AlloyFurnaceResultSlot extends Slot {
		private final Player player;
		private int removeCount;

		AlloyFurnaceResultSlot(final Player player, final Container container, final int slot, final int x, final int y) {
			super(container, slot, x, y);
			this.player = player;
		}

		@Override
		public boolean mayPlace(final ItemStack itemStack) {
			return false;
		}

		@Override
		public ItemStack remove(final int amount) {
			if (this.hasItem()) {
				this.removeCount = this.removeCount + Math.min(amount, this.getItem().getCount());
			}

			return super.remove(amount);
		}

		@Override
		public void onTake(final Player player, final ItemStack carried) {
			this.checkTakeAchievements(carried);
			super.onTake(player, carried);
		}

		@Override
		protected void onQuickCraft(final ItemStack picked, final int count) {
			this.removeCount += count;
			this.checkTakeAchievements(picked);
		}

		@Override
		protected void checkTakeAchievements(final ItemStack carried) {
			carried.onCraftedBy(this.player, this.removeCount);
			if (this.player instanceof ServerPlayer serverPlayer && this.container instanceof AlloyFurnaceBlockEntity alloyFurnace) {
				alloyFurnace.awardUsedRecipesAndPopExperience(serverPlayer);
			}

			this.removeCount = 0;
		}
	}
}
