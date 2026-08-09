package com.vastosine.obsidian.block.entity;

import com.vastosine.obsidian.block.AlloyFurnaceBlock;
import com.vastosine.obsidian.menu.AlloyFurnaceMenu;
import com.vastosine.obsidian.recipe.AlloyFurnaceRecipe;
import com.vastosine.obsidian.recipe.AlloyRecipeInput;
import com.vastosine.obsidian.recipe.ModRecipeTypes;
import net.fabricmc.fabric.api.recipe.v1.FabricRecipeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.providers.number.ResolvableNumber;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * The Alloy Furnace block entity.
 * Slots: 0/1/2 = ingredients, 3 = fuel, 4 = output.
 * Recipes: custom alloy recipes take priority (order-insensitive — the ingredients just
 * have to be present in the input slots, in any arrangement; if several alloy recipes
 * match, the one with the most distinct ingredient types wins); any single input slot
 * falls back to vanilla smelting recipes, cooked at two fifths of the furnace time.
 * Fuel burns half as long as in a furnace. Hopper access:
 * left -> slot 0, up -> slot 1, right -> slot 2, front/back -> fuel, down -> output (take only).
 */
public class AlloyFurnaceBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeCraftingHolder {
	public static final int SLOT_INPUT_FIRST = 0;
	public static final int SLOT_FUEL = 3;
	public static final int SLOT_OUTPUT = 4;
	private static final int CONTAINER_SIZE = 5;
	private static final int SMELTING_TIME_MULTIPLIER = 2;
	private static final int SMELTING_TIME_DIVISOR = 5;
	private static final int FUEL_TIME_DIVISOR = 2;

	// Hopper access (direction = from the container toward the hopper)
	private static final int[] SLOTS_LEFT = {0};
	private static final int[] SLOTS_UP = {1};
	private static final int[] SLOTS_RIGHT = {2};
	private static final int[] SLOTS_FUEL = {3};
	private static final int[] SLOTS_DOWN = {4};

	public static final int DATA_LIT_TIME = 0;
	public static final int DATA_LIT_DURATION = 1;
	public static final int DATA_COOKING_PROGRESS = 2;
	public static final int DATA_COOKING_TOTAL_TIME = 3;
	public static final int NUM_DATA_VALUES = 4;

	protected NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
	private int litTimeRemaining;
	private int litTotalTime;
	private int cookingTimer;
	private int cookingTotalTime = 200;
	/** Id of the recipe currently being cooked (set when a cook completes), used to detect recipe changes in setItem. */
	private @Nullable ResourceKey<Recipe<?>> currentRecipe;

	private final RecipeManager.CachedCheck<SingleRecipeInput, SmeltingRecipe> smeltingCheck;

	protected final ContainerData dataAccess = new ContainerData() {
		@Override
		public int get(final int dataId) {
			return switch (dataId) {
				case DATA_LIT_TIME -> AlloyFurnaceBlockEntity.this.litTimeRemaining;
				case DATA_LIT_DURATION -> AlloyFurnaceBlockEntity.this.litTotalTime;
				case DATA_COOKING_PROGRESS -> AlloyFurnaceBlockEntity.this.cookingTimer;
				case DATA_COOKING_TOTAL_TIME -> AlloyFurnaceBlockEntity.this.cookingTotalTime;
				default -> 0;
			};
		}

		@Override
		public void set(final int dataId, final int value) {
			switch (dataId) {
				case DATA_LIT_TIME -> AlloyFurnaceBlockEntity.this.litTimeRemaining = value;
				case DATA_LIT_DURATION -> AlloyFurnaceBlockEntity.this.litTotalTime = value;
				case DATA_COOKING_PROGRESS -> AlloyFurnaceBlockEntity.this.cookingTimer = value;
				case DATA_COOKING_TOTAL_TIME -> AlloyFurnaceBlockEntity.this.cookingTotalTime = value;
			}
		}

		@Override
		public int getCount() {
			return NUM_DATA_VALUES;
		}
	};

	public AlloyFurnaceBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
		super(ModBlockEntities.ALLOY_FURNACE, worldPosition, blockState);
		this.smeltingCheck = RecipeManager.createCheck(RecipeType.SMELTING);
	}

	public static void serverTick(final ServerLevel level, final BlockPos pos, BlockState state, final AlloyFurnaceBlockEntity entity) {
		boolean changed = false;
		boolean wasLit = entity.litTimeRemaining > 0;
		boolean isLit;
		if (wasLit) {
			entity.litTimeRemaining--;
			isLit = entity.litTimeRemaining > 0;
		} else {
			isLit = false;
		}

		ItemStack fuel = entity.items.get(SLOT_FUEL);
		boolean hasFuel = !fuel.isEmpty();
		boolean hasAnyInput = !entity.items.get(0).isEmpty() || !entity.items.get(1).isEmpty() || !entity.items.get(2).isEmpty();
		if (isLit || hasFuel && hasAnyInput) {
			if (hasAnyInput) {
				// A recipe can only burn when its result fits the output slot; a blocked
				// higher-priority recipe must not stop a lower-priority one that can burn
				// (e.g. leftover copper in the output while smelting copper ore).
				boolean burning = false;
				// 1) Alloy recipes take priority; matching is order-insensitive (material totals).
				AlloyRecipeInput alloyInput = new AlloyRecipeInput(entity.items.get(0), entity.items.get(1), entity.items.get(2));
				RecipeHolder<AlloyFurnaceRecipe> alloyRecipe = entity.findAlloyRecipe(level, alloyInput);
				int[] consume = alloyRecipe != null ? alloyRecipe.value().allocate(alloyInput) : null;
				if (consume != null) {
					ItemStack result = alloyRecipe.value().assemble(alloyInput);
					if (!result.isEmpty() && canBurn(entity.items, result)) {
						burning = true;
						if (!isLit && entity.ignite(level)) {
							isLit = true;
							changed = true;
						}

						if (isLit) {
							entity.cookingTimer++;
							if (entity.cookingTimer >= entity.cookingTotalTime) {
								entity.cookingTimer = 0;
								entity.cookingTotalTime = alloyRecipe.value().cookingTime();
								burnAlloy(entity.items, consume, result);
								entity.currentRecipe = alloyRecipe.id();
								entity.setRecipeUsed(alloyRecipe);
								changed = true;
							}
						} else {
							entity.cookingTimer = 0;
						}
					}
				}
				// 2) Fallback: vanilla smelting on the first input slot whose recipe result
				//    fits the output; slots whose recipe is blocked by the output are skipped.
				for (int slot = 0; slot < 3 && !burning; slot++) {
					ItemStack ingredient = entity.items.get(slot);
					if (ingredient.isEmpty()) {
						continue;
					}
					SingleRecipeInput singleInput = new SingleRecipeInput(ingredient);
					RecipeHolder<SmeltingRecipe> smeltRecipe = entity.smeltingCheck.getRecipeFor(singleInput, level).orElse(null);
					if (smeltRecipe == null) {
						continue;
					}
					ItemStack result = smeltRecipe.value().assemble(singleInput);
					if (result.isEmpty() || !canBurn(entity.items, result)) {
						continue;
					}
					burning = true;
					if (!isLit && entity.ignite(level)) {
						isLit = true;
						changed = true;
					}

					if (isLit) {
						entity.cookingTimer++;
						if (entity.cookingTimer >= entity.cookingTotalTime) {
							entity.cookingTimer = 0;
							entity.cookingTotalTime = Math.max(1, smeltRecipe.value().cookingTime() * SMELTING_TIME_MULTIPLIER / SMELTING_TIME_DIVISOR);
							burnSingle(entity.items, slot, result);
							entity.currentRecipe = smeltRecipe.id();
							entity.setRecipeUsed(smeltRecipe);
							changed = true;
						}
					} else {
						entity.cookingTimer = 0;
					}
				}
				if (!burning) {
					entity.cookingTimer = 0; // no recipe matched or every match is blocked by the output
				}
			} else {
				entity.cookingTimer = 0;
			}
		} else if (entity.cookingTimer > 0) {
			entity.cookingTimer = Mth.clamp(entity.cookingTimer - 2, 0, entity.cookingTotalTime);
		}

		if (wasLit != isLit) {
			changed = true;
			state = state.setValue(AlloyFurnaceBlock.LIT, isLit);
			level.setBlock(pos, state, 3);
		}

		if (changed) {
			setChanged(level, pos, state);
		}
	}

	/**
	 * Best matching alloy recipe for the given input: among all matches, the one with the
	 * most distinct ingredient types wins (ties keep the first match in registry order).
	 */
	private @Nullable RecipeHolder<AlloyFurnaceRecipe> findAlloyRecipe(final ServerLevel level, final AlloyRecipeInput input) {
		RecipeHolder<AlloyFurnaceRecipe> best = null;
		// getAllMatches is a default method: it must be called on the recipe manager instance
		// (fabric injects FabricRecipeManager into RecipeManager via mixin; 26.3 exposes it as level.recipeAccess())
		FabricRecipeManager recipeManager = (FabricRecipeManager) level.recipeAccess();
		for (RecipeHolder<AlloyFurnaceRecipe> holder : recipeManager.getAllMatches(ModRecipeTypes.ALLOY_FURNACE, input, level).toList()) {
			if (best == null || holder.value().ingredients().size() > best.value().ingredients().size()) {
				best = holder;
			}
		}
		return best;
	}

	/**
	 * Tries to light the furnace with the current fuel; returns true if it caught fire.
	 * Fuel burns half as long as in a vanilla furnace (26.3 fuel durations are read from
	 * the COOKING_FUEL component via ResolvableNumber, not from level.fuelValues()).
	 */
	private boolean ignite(final ServerLevel level) {
		ItemStack fuel = this.items.get(SLOT_FUEL);
		int newLitTime = ResolvableNumber.getIntFromItem(fuel, DataComponents.COOKING_FUEL, CookingFuel::burnTime, this.getLootContext(level), 0) / FUEL_TIME_DIVISOR;
		this.litTimeRemaining = newLitTime;
		this.litTotalTime = newLitTime;
		if (newLitTime > 0) {
			ItemStackTemplate remainder = fuel.getItem().getCraftingRemainder();
			fuel.shrink(1);
			if (fuel.isEmpty()) {
				this.items.set(SLOT_FUEL, remainder != null ? remainder.create() : ItemStack.EMPTY);
			}
			return true;
		}
		return false;
	}

	private static boolean canBurn(final NonNullList<ItemStack> items, final ItemStack burnResult) {
		ItemStack resultStack = items.get(SLOT_OUTPUT);
		if (resultStack.isEmpty()) {
			return true;
		}
		if (!ItemStack.isSameItemSameComponents(resultStack, burnResult)) {
			return false;
		}
		int resultCount = resultStack.getCount() + burnResult.getCount();
		int maxResultCount = Math.min(resultStack.getMaxStackSize(), burnResult.getMaxStackSize());
		return resultCount <= maxResultCount;
	}

	/** Consumes the allocated per-slot amounts and merges the result (alloy recipe). */
	private static void burnAlloy(final NonNullList<ItemStack> items, final int[] consume, final ItemStack result) {
		ItemStack resultStack = items.get(SLOT_OUTPUT);
		if (resultStack.isEmpty()) {
			items.set(SLOT_OUTPUT, result.copy());
		} else {
			resultStack.grow(result.getCount());
		}
		for (int slot = 0; slot < 3; slot++) {
			items.get(slot).shrink(consume[slot]);
		}
	}

	/** Consumes the one ingredient in {@code slot} and merges the result (smelting recipe). */
	private static void burnSingle(final NonNullList<ItemStack> items, final int slot, final ItemStack result) {
		ItemStack resultStack = items.get(SLOT_OUTPUT);
		if (resultStack.isEmpty()) {
			items.set(SLOT_OUTPUT, result.copy());
		} else {
			resultStack.grow(result.getCount());
		}
		items.get(slot).shrink(1);
	}

	private static int getTotalCookTime(final ServerLevel level, final AlloyFurnaceBlockEntity entity) {
		RecipeHolder<?> recipe = entity.findRecipe(level);
		if (recipe == null) {
			return 200;
		}
		if (recipe.value() instanceof AlloyFurnaceRecipe alloyRecipe) {
			return alloyRecipe.cookingTime();
		}
		if (recipe.value() instanceof SmeltingRecipe smeltRecipe) {
			return Math.max(1, smeltRecipe.cookingTime() * SMELTING_TIME_MULTIPLIER / SMELTING_TIME_DIVISOR);
		}
		return 200;
	}

	// --- Hopper access ---

	@Override
	public int[] getSlotsForFace(final Direction direction) {
		Direction facing = this.getBlockState().getValue(AlloyFurnaceBlock.FACING);
		if (direction == facing.getClockWise()) {
			return SLOTS_LEFT;   // hopper on the player's left of the front -> ingredient slot 0
		}
		if (direction == Direction.UP) {
			return SLOTS_UP;     // hopper above -> ingredient slot 1
		}
		if (direction == facing.getCounterClockWise()) {
			return SLOTS_RIGHT;  // hopper on the player's right of the front -> ingredient slot 2
		}
		if (direction == facing.getOpposite() || direction == facing) {
			return SLOTS_FUEL;   // hopper behind or in front -> fuel slot
		}
		if (direction == Direction.DOWN) {
			return SLOTS_DOWN;   // hopper below -> output slot
		}
		return new int[0];       // unreachable
	}

	@Override
	public boolean canPlaceItemThroughFace(final int slot, final ItemStack itemStack, final @Nullable Direction direction) {
		return this.canPlaceItem(slot, itemStack);
	}

	@Override
	public boolean canTakeItemThroughFace(final int slot, final ItemStack itemStack, final Direction direction) {
		return direction == Direction.DOWN && slot == SLOT_OUTPUT;
	}

	// --- Container ---

	@Override
	public int getContainerSize() {
		return CONTAINER_SIZE;
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.items;
	}

	@Override
	protected void setItems(final NonNullList<ItemStack> items) {
		this.items = items;
	}

	@Override
	public void setItem(final int slot, final ItemStack itemStack) {
		ItemStack oldStack = this.items.get(slot);
		// "Unchanged" means same item, same components AND same count. A count drop that leaves
		// the current recipe unmatched (e.g. taking 2 gold out of 3 mid-cook) must restart too.
		boolean same = !itemStack.isEmpty()
			&& oldStack.getCount() == itemStack.getCount()
			&& ItemStack.isSameItemSameComponents(oldStack, itemStack);
		this.items.set(slot, itemStack);
		itemStack.limitSize(this.getMaxStackSize(itemStack));
		// Only restart the timer when the currently burning recipe actually changes;
		// unrelated ingredient changes (e.g. another slot while smelting one item) keep the progress.
		if (slot < 3 && !same && this.level instanceof ServerLevel serverLevel) {
			RecipeHolder<?> newRecipe = this.findRecipe(serverLevel);
			if (newRecipe == null || !newRecipe.id().equals(this.currentRecipe)) {
				this.cookingTotalTime = getTotalCookTime(serverLevel, this);
				this.cookingTimer = 0;
				this.currentRecipe = newRecipe != null ? newRecipe.id() : null;
			}
			this.setChanged();
		}
	}

	/**
	 * Returns the recipe the current inventory would cook next, using the same "can it burn?"
	 * rule as serverTick: the alloy recipe if it matches and its result fits the output slot,
	 * otherwise the first input slot whose smelting recipe is not blocked by the output.
	 */
	private @Nullable RecipeHolder<?> findRecipe(final ServerLevel level) {
		AlloyRecipeInput alloyInput = new AlloyRecipeInput(this.items.get(0), this.items.get(1), this.items.get(2));
		RecipeHolder<AlloyFurnaceRecipe> alloyRecipe = this.findAlloyRecipe(level, alloyInput);
		if (alloyRecipe != null) {
			ItemStack result = alloyRecipe.value().assemble(alloyInput);
			if (!result.isEmpty() && canBurn(this.items, result)) {
				return alloyRecipe;
			}
		}
		for (int slot = 0; slot < 3; slot++) {
			ItemStack ingredient = this.items.get(slot);
			if (ingredient.isEmpty()) {
				continue;
			}
			SingleRecipeInput singleInput = new SingleRecipeInput(ingredient);
			RecipeHolder<SmeltingRecipe> smeltRecipe = this.smeltingCheck.getRecipeFor(singleInput, level).orElse(null);
			if (smeltRecipe != null) {
				ItemStack result = smeltRecipe.value().assemble(singleInput);
				if (!result.isEmpty() && canBurn(this.items, result)) {
					return smeltRecipe;
				}
			}
		}
		return null;
	}

	@Override
	public boolean canPlaceItem(final int slot, final ItemStack itemStack) {
		if (slot == SLOT_OUTPUT) {
			return false;
		}
		if (slot == SLOT_FUEL) {
			// 26.3 fuel is data-driven: the COOKING_FUEL component marks burnable items
			return itemStack.has(DataComponents.COOKING_FUEL);
		}
		return true;
	}

	// --- Name / menu ---

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.alloy_furnace");
	}

	@Override
	protected AbstractContainerMenu createMenu(final int containerId, final Inventory inventory) {
		return new AlloyFurnaceMenu(containerId, inventory, this, this.dataAccess);
	}

	// --- Recipe crafting holder (XP awarding intentionally skipped) ---

	@Override
	public void setRecipeUsed(final @Nullable RecipeHolder<?> recipeUsed) {
	}

	@Override
	public @Nullable RecipeHolder<?> getRecipeUsed() {
		return null;
	}

	@Override
	public void awardUsedRecipes(final Player player, final List<ItemStack> itemStacks) {
	}

	// --- Persistence (items are saved by the base class via the CONTAINER component) ---

	@Override
	protected void loadAdditional(final ValueInput input) {
		super.loadAdditional(input);
		this.cookingTimer = input.getShortOr("cooking_time_spent", (short) 0);
		this.cookingTotalTime = input.getShortOr("cooking_total_time", (short) 0);
		this.litTimeRemaining = input.getShortOr("lit_time_remaining", (short) 0);
		this.litTotalTime = input.getShortOr("lit_total_time", (short) 0);
	}

	@Override
	protected void saveAdditional(final ValueOutput output) {
		super.saveAdditional(output);
		output.putShort("cooking_time_spent", (short) this.cookingTimer);
		output.putShort("cooking_total_time", (short) this.cookingTotalTime);
		output.putShort("lit_time_remaining", (short) this.litTimeRemaining);
		output.putShort("lit_total_time", (short) this.litTotalTime);
	}
}
