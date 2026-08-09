package com.vastosine.obsidian.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vastosine.obsidian.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A smelting-style recipe for the Alloy Furnace (up to 3 ingredients).
 * Matching is order-insensitive: each ingredient (with its per-ingredient consume
 * count) is matched against the combined contents of the three input slots, so the
 * slots may hold the materials in any arrangement and one ingredient may be spread
 * across several slots (e.g. gold x2 in slot 0 and gold x1 in slot 1 satisfies a
 * gold x3 requirement).
 */
public record AlloyFurnaceRecipe(
	Recipe.CommonInfo commonInfo,
	AlloyBookInfo bookInfo,
	List<Ingredient> ingredients,
	List<Integer> counts,
	ItemStackTemplate result,
	float experience,
	int cookingTime
) implements Recipe<AlloyRecipeInput> {

	public AlloyFurnaceRecipe {
		if (counts.size() != ingredients.size()) {
			counts = Collections.nCopies(ingredients.size(), 1);
		}
	}

	/**
	 * Order-insensitive allocation of the three input slots to this recipe's ingredients.
	 * Returns how many items to take from each input slot per cook, or null if the input
	 * does not match. Duplicate ingredient entries are merged, so gold x2 + gold x1 in the
	 * recipe satisfies a gold x3 requirement no matter how the gold is arranged in the slots.
	 */
	public @Nullable int[] allocate(final AlloyRecipeInput input) {
		Map<Ingredient, Integer> needs = new LinkedHashMap<>();
		for (int i = 0; i < this.ingredients.size(); i++) {
			if (this.counts.get(i) <= 0) {
				continue;
			}
			needs.merge(this.ingredients.get(i), this.counts.get(i), Integer::sum);
		}
		for (Map.Entry<Ingredient, Integer> entry : needs.entrySet()) {
			int have = 0;
			for (int slot = 0; slot < 3; slot++) {
				if (entry.getKey().test(input.getItem(slot))) {
					have += input.getItem(slot).getCount();
				}
			}
			if (have < entry.getValue()) {
				return null;
			}
		}
		// The totals fit, so walking the slots in order always finds an allocation
		// (the ingredients are item categories that never share a slot in practice).
		int[] consume = new int[3];
		for (Map.Entry<Ingredient, Integer> entry : needs.entrySet()) {
			int need = entry.getValue();
			for (int slot = 0; slot < 3 && need > 0; slot++) {
				ItemStack stack = input.getItem(slot);
				if (stack.isEmpty() || !entry.getKey().test(stack)) {
					continue;
				}
				int take = Math.min(stack.getCount() - consume[slot], need);
				consume[slot] += take;
				need -= take;
			}
			if (need > 0) {
				return null; // overlapping ingredients left no items to take
			}
		}
		return consume;
	}

	public static final MapCodec<AlloyFurnaceRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
		i -> i.group(
			Recipe.CommonInfo.MAP_CODEC.forGetter(AlloyFurnaceRecipe::commonInfo),
			AlloyBookInfo.MAP_CODEC.forGetter(AlloyFurnaceRecipe::bookInfo),
			Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(AlloyFurnaceRecipe::ingredients),
			Codec.INT.listOf().optionalFieldOf("counts", List.of()).forGetter(AlloyFurnaceRecipe::counts),
			ItemStackTemplate.CODEC.fieldOf("result").forGetter(AlloyFurnaceRecipe::result),
			Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(AlloyFurnaceRecipe::experience),
			Codec.INT.fieldOf("cookingtime").orElse(300).forGetter(AlloyFurnaceRecipe::cookingTime)
		).apply(i, AlloyFurnaceRecipe::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, AlloyFurnaceRecipe> STREAM_CODEC = StreamCodec.composite(
		Recipe.CommonInfo.STREAM_CODEC,
		AlloyFurnaceRecipe::commonInfo,
		AlloyBookInfo.STREAM_CODEC,
		AlloyFurnaceRecipe::bookInfo,
		Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
		AlloyFurnaceRecipe::ingredients,
		ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()),
		AlloyFurnaceRecipe::counts,
		ItemStackTemplate.STREAM_CODEC,
		AlloyFurnaceRecipe::result,
		ByteBufCodecs.FLOAT,
		AlloyFurnaceRecipe::experience,
		ByteBufCodecs.INT,
		AlloyFurnaceRecipe::cookingTime,
		AlloyFurnaceRecipe::new
	);

	@Override
	public boolean matches(final AlloyRecipeInput input, final Level level) {
		return this.allocate(input) != null;
	}

	@Override
	public ItemStack assemble(final AlloyRecipeInput input) {
		return this.result.create();
	}

	@Override
	public RecipeSerializer<AlloyFurnaceRecipe> getSerializer() {
		return ModRecipeSerializers.ALLOY_FURNACE;
	}

	@Override
	public RecipeType<AlloyFurnaceRecipe> getType() {
		return ModRecipeTypes.ALLOY_FURNACE;
	}

	@Override
	public PlacementInfo placementInfo() {
		// NOT_PLACEABLE would make the recipe book reject placement server-side
		// (isImpossibleToPlace check) and never highlight the recipe as craftable.
		return PlacementInfo.create(this.ingredients);
	}

	@Override
	public RecipeBookCategory recipeBookCategory() {
		return this.bookInfo.category();
	}

	@Override
	public String group() {
		return this.bookInfo.group();
	}

	@Override
	public boolean showNotification() {
		return this.commonInfo.showNotification();
	}

	@Override
	public List<RecipeDisplay> display() {
		return List.of(
			new AlloyRecipeDisplay(
				this.ingredients.stream().map(Ingredient::display).toList(),
				SlotDisplay.AnyFuel.INSTANCE,
				new SlotDisplay.ItemStackSlotDisplay(this.result),
				new SlotDisplay.ItemSlotDisplay(ModBlocks.ALLOY_FURNACE.asItem()),
				this.cookingTime,
				this.experience
			)
		);
	}

	/**
	 * Book metadata for the alloy recipe: its own recipe book category (ModRecipeBookCategories.ALLOY)
	 * so the recipe book groups alloy recipes into a dedicated tab.
	 */
	public record AlloyBookInfo(RecipeBookCategory category, String group) implements Recipe.BookInfo<RecipeBookCategory> {
		public static final MapCodec<AlloyBookInfo> MAP_CODEC = Recipe.BookInfo.mapCodec(
			BuiltInRegistries.RECIPE_BOOK_CATEGORY.byNameCodec(),
			ModRecipeBookCategories.ALLOY,
			AlloyBookInfo::new
		);
		public static final StreamCodec<RegistryFriendlyByteBuf, AlloyBookInfo> STREAM_CODEC = Recipe.BookInfo.streamCodec(
			ByteBufCodecs.registry(Registries.RECIPE_BOOK_CATEGORY),
			AlloyBookInfo::new
		);
	}
}
