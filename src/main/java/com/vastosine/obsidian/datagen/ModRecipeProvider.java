package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.ObsidianCraft;
import com.vastosine.obsidian.block.ModBlocks;
import com.vastosine.obsidian.item.ModItemTags;
import com.vastosine.obsidian.item.ModItems;
import com.vastosine.obsidian.recipe.AlloyFurnaceRecipe;
import com.vastosine.obsidian.recipe.ModRecipeBookCategories;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
	public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(output, registryLookup);
	}

	// Fabric's default rewrites every recipe namespace to the mod id, which would turn
	// the minecraft:netherite_ingot override into obsidian:netherite_ingot (colliding
	// with the alloy recipe). Keep the keys as written.
	@Override
	protected Identifier getRecipeIdentifier(Identifier identifier) {
		return identifier;
	}

	@Override
	protected RecipeProvider createRecipeProvider(
		HolderLookup.Provider registries,
		BootstrapContext<Recipe<?>> recipeBootstrapContext,
		BootstrapContext<Advancement> advancementBootstrapContext
	) {
		return new RecipeProvider(recipeBootstrapContext, advancementBootstrapContext) {
			@Override
			public void buildRecipes() {
				// 4 obsidian ingots (shaped 2x2) craft an obsidian block
				this.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.OBSIDIAN_BLOCK)
					.pattern("AA")
					.pattern("AA")
					.define('A', ModItems.OBSIDIAN_INGOT)
					.unlockedBy("has_obsidian_ingot", this.has(ModItems.OBSIDIAN_INGOT))
					.save(
						this.output,
						ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("obsidian_block_from_ingots"))
					);

				// 1 apple surrounded by 4 obsidian ingots (up, down, left, right), shaped
				this.shaped(RecipeCategory.FOOD, ModItems.OBSIDIAN_APPLE)
					.pattern(" I ")
					.pattern("IAI")
					.pattern(" I ")
					.define('I', ModItems.OBSIDIAN_INGOT)
					.define('A', Items.APPLE)
					.unlockedBy("has_obsidian_ingot", this.has(ModItems.OBSIDIAN_INGOT))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("obsidian_apple")));

				// 1 obsidian block breaks down into 4 obsidian ingots
				this.shapeless(RecipeCategory.MISC, ModItems.OBSIDIAN_INGOT, 4)
					.requires(ModBlocks.OBSIDIAN_BLOCK)
					.unlockedBy("has_obsidian_block", this.has(ModBlocks.OBSIDIAN_BLOCK))
					.save(
						this.output,
						ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("obsidian_ingots_from_block"))
					);

				// Smelt obsidian or crying obsidian into an obsidian ingot (furnace)
				SimpleCookingRecipeBuilder.smelting(
						Ingredient.of(Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN),
						RecipeCategory.MISC,
						CookingBookCategory.BLOCKS,
						ModItems.OBSIDIAN_INGOT,
						0.1F,
						200
					)
					.unlockedBy("has_obsidian", this.has(Blocks.OBSIDIAN))
					.unlockedBy("has_crying_obsidian", this.has(Blocks.CRYING_OBSIDIAN))
					.save(
						this.output,
						ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("obsidian_ingot_from_obsidian"))
					);

				// Blast furnace smelting, twice as fast (100 ticks)
				SimpleCookingRecipeBuilder.blasting(
						Ingredient.of(Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN),
						RecipeCategory.MISC,
						CookingBookCategory.MISC,
						ModItems.OBSIDIAN_INGOT,
						0.1F,
						100
					)
					.unlockedBy("has_obsidian", this.has(Blocks.OBSIDIAN))
					.unlockedBy("has_crying_obsidian", this.has(Blocks.CRYING_OBSIDIAN))
					.save(
						this.output,
						ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("obsidian_ingot_from_obsidian_blasting"))
					);

				// Tools follow the vanilla patterns, but the core cell is an obsidian block
				// (e.g. the middle cell of the pickaxe head) instead of an obsidian ingot
				this.shaped(RecipeCategory.TOOLS, ModItems.OBSIDIAN_PICKAXE)
					.pattern("IBI")
					.pattern(" S ")
					.pattern(" S ")
					.define('I', ModItems.OBSIDIAN_INGOT)
					.define('B', ModBlocks.OBSIDIAN_BLOCK)
					.define('S', Items.STICK)
					.unlockedBy("has_obsidian_ingot", this.has(ModItems.OBSIDIAN_INGOT))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("obsidian_pickaxe")));

				this.shaped(RecipeCategory.TOOLS, ModItems.OBSIDIAN_AXE)
					.pattern("II")
					.pattern("BS")
					.pattern(" S")
					.define('I', ModItems.OBSIDIAN_INGOT)
					.define('B', ModBlocks.OBSIDIAN_BLOCK)
					.define('S', Items.STICK)
					.unlockedBy("has_obsidian_ingot", this.has(ModItems.OBSIDIAN_INGOT))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("obsidian_axe")));

				this.shaped(RecipeCategory.TOOLS, ModItems.OBSIDIAN_SHOVEL)
					.pattern("B")
					.pattern("S")
					.pattern("S")
					.define('B', ModBlocks.OBSIDIAN_BLOCK)
					.define('S', Items.STICK)
					.unlockedBy("has_obsidian_ingot", this.has(ModItems.OBSIDIAN_INGOT))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("obsidian_shovel")));

				this.shaped(RecipeCategory.TOOLS, ModItems.OBSIDIAN_HOE)
					.pattern("IB")
					.pattern(" S")
					.pattern(" S")
					.define('I', ModItems.OBSIDIAN_INGOT)
					.define('B', ModBlocks.OBSIDIAN_BLOCK)
					.define('S', Items.STICK)
					.unlockedBy("has_obsidian_ingot", this.has(ModItems.OBSIDIAN_INGOT))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("obsidian_hoe")));

				this.shaped(RecipeCategory.COMBAT, ModItems.OBSIDIAN_SWORD)
					.pattern("I")
					.pattern("B")
					.pattern("S")
					.define('I', ModItems.OBSIDIAN_INGOT)
					.define('B', ModBlocks.OBSIDIAN_BLOCK)
					.define('S', Items.STICK)
					.unlockedBy("has_obsidian_ingot", this.has(ModItems.OBSIDIAN_INGOT))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("obsidian_sword")));

				// Armor follows the vanilla patterns, with the core cell as an obsidian block
				// (e.g. the center of the chestplate)
				this.shaped(RecipeCategory.COMBAT, ModItems.OBSIDIAN_HELMET)
					.pattern("BBB")
					.pattern("I I")
					.define('I', ModItems.OBSIDIAN_INGOT)
					.define('B', ModBlocks.OBSIDIAN_BLOCK)
					.unlockedBy("has_obsidian_ingot", this.has(ModItems.OBSIDIAN_INGOT))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("obsidian_helmet")));

				this.shaped(RecipeCategory.COMBAT, ModItems.OBSIDIAN_CHESTPLATE)
					.pattern("I I")
					.pattern("IBI")
					.pattern("III")
					.define('I', ModItems.OBSIDIAN_INGOT)
					.define('B', ModBlocks.OBSIDIAN_BLOCK)
					.unlockedBy("has_obsidian_ingot", this.has(ModItems.OBSIDIAN_INGOT))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("obsidian_chestplate")));

				this.shaped(RecipeCategory.COMBAT, ModItems.OBSIDIAN_LEGGINGS)
					.pattern("IBI")
					.pattern("I I")
					.pattern("I I")
					.define('I', ModItems.OBSIDIAN_INGOT)
					.define('B', ModBlocks.OBSIDIAN_BLOCK)
					.unlockedBy("has_obsidian_ingot", this.has(ModItems.OBSIDIAN_INGOT))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("obsidian_leggings")));

				this.shaped(RecipeCategory.COMBAT, ModItems.OBSIDIAN_BOOTS)
					.pattern("IBI")
					.pattern("I I")
					.define('I', ModItems.OBSIDIAN_INGOT)
					.define('B', ModBlocks.OBSIDIAN_BLOCK)
					.unlockedBy("has_obsidian_ingot", this.has(ModItems.OBSIDIAN_INGOT))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("obsidian_boots")));

				// Alloy Furnace: obsidian ingots, furnaces, a blast furnace core, vanilla
				// obsidian and obsidian blocks (empty cells would use ' ', a space)
				this.shaped(RecipeCategory.DECORATIONS, ModBlocks.ALLOY_FURNACE)
					.pattern("IFI")
					.pattern("F#F")
					.pattern("OBO")
					.define('I', ModItems.OBSIDIAN_INGOT)
					.define('F', Items.FURNACE)
					.define('#', Items.BLAST_FURNACE)
					.define('O', Blocks.OBSIDIAN)
					.define('B', ModBlocks.OBSIDIAN_BLOCK)
					.unlockedBy("has_obsidian_ingot", this.has(ModItems.OBSIDIAN_INGOT))
					.unlockedBy("has_furnace", this.has(Items.FURNACE))
					.unlockedBy("has_blast_furnace", this.has(Items.BLAST_FURNACE))
					.unlockedBy("has_obsidian", this.has(Blocks.OBSIDIAN))
					.unlockedBy("has_obsidian_block", this.has(ModBlocks.OBSIDIAN_BLOCK))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("alloy_furnace")));

				// Rose gold: 3 gold + 1 copper -> 4 rose gold ingots, cooked in the alloy
				// furnace. Matching is order-insensitive (slot layout does not matter).
				// Gold and copper come from the material tags (ingots, ores, raw ores),
				// so adding a new source is a tag edit, not a recipe edit.
				alloyFurnaceRecipe(
					"rose_gold_ingot",
					Items.GOLD_INGOT,
					ModItems.ROSE_GOLD_INGOT,
					4,
					1.0F,
					160,
					List.of(3, 1),
					List.of(
						Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(ModItemTags.GOLD_MATERIALS)),
						Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(ModItemTags.COPPER_MATERIALS))
					)
				);

				// Netherite: the vanilla 4 scrap + 4 gold crafting recipe is removed and
				// replaced by an alloy furnace recipe. Ores work as substitutes (the gold
				// material tag and the debris tag both list ores).
				alloyFurnaceRecipe(
					"netherite_ingot",
					Items.NETHERITE_SCRAP,
					Items.NETHERITE_INGOT,
					1,
					2.0F,
					240,
					List.of(4, 4),
					List.of(
						Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(ModItemTags.DEBRIS_MATERIALS)),
						Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(ModItemTags.GOLD_MATERIALS))
					)
				);

				// The vanilla netherite recipe is overridden with a shapeless recipe whose
				// only ingredient is an empty tag: it decodes fine but can never be placed,
				// so the RecipeManager ignores it (warn + skip) — the alloy furnace recipe
				// above becomes the only way to craft netherite.
				this.shapeless(RecipeCategory.MISC, Items.NETHERITE_INGOT)
					.requires(ModItemTags.RECIPE_REMOVED)
					.unlockedBy("has_netherite_ingot", this.has(Items.NETHERITE_INGOT))
					.save(
						this.output,
						ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("minecraft", "netherite_ingot"))
					);

				// Rose gold block and nugget: 1:9 conversions in both directions
				this.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ROSE_GOLD_BLOCK)
					.pattern("###")
					.pattern("###")
					.pattern("###")
					.define('#', ModItems.ROSE_GOLD_INGOT)
					.unlockedBy("has_rose_gold_ingot", this.has(ModItems.ROSE_GOLD_INGOT))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("rose_gold_block_from_ingots")));

				this.shapeless(RecipeCategory.MISC, ModItems.ROSE_GOLD_INGOT, 9)
					.requires(ModBlocks.ROSE_GOLD_BLOCK)
					.unlockedBy("has_rose_gold_block", this.has(ModBlocks.ROSE_GOLD_BLOCK))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("rose_gold_ingots_from_block")));

				this.shaped(RecipeCategory.MISC, ModItems.ROSE_GOLD_INGOT)
					.pattern("###")
					.pattern("###")
					.pattern("###")
					.define('#', ModItems.ROSE_GOLD_NUGGET)
					.unlockedBy("has_rose_gold_nugget", this.has(ModItems.ROSE_GOLD_NUGGET))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("rose_gold_ingot_from_nuggets")));

				this.shapeless(RecipeCategory.MISC, ModItems.ROSE_GOLD_NUGGET, 9)
					.requires(ModItems.ROSE_GOLD_INGOT)
					.unlockedBy("has_rose_gold_ingot", this.has(ModItems.ROSE_GOLD_INGOT))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("rose_gold_nuggets_from_ingot")));

				// Rose gold tools: vanilla copper patterns with the rose gold tool materials tag
				this.shaped(RecipeCategory.TOOLS, ModItems.ROSE_GOLD_PICKAXE)
					.pattern("XXX")
					.pattern(" S ")
					.pattern(" S ")
					.define('X', ModItems.ROSE_GOLD_TOOL_MATERIALS_TAG)
					.define('S', Items.STICK)
					.unlockedBy("has_rose_gold_ingot", this.has(ModItems.ROSE_GOLD_TOOL_MATERIALS_TAG))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("rose_gold_pickaxe")));

				this.shaped(RecipeCategory.TOOLS, ModItems.ROSE_GOLD_AXE)
					.pattern("XX")
					.pattern("XS")
					.pattern(" S")
					.define('X', ModItems.ROSE_GOLD_TOOL_MATERIALS_TAG)
					.define('S', Items.STICK)
					.unlockedBy("has_rose_gold_ingot", this.has(ModItems.ROSE_GOLD_TOOL_MATERIALS_TAG))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("rose_gold_axe")));

				this.shaped(RecipeCategory.TOOLS, ModItems.ROSE_GOLD_SHOVEL)
					.pattern("X")
					.pattern("S")
					.pattern("S")
					.define('X', ModItems.ROSE_GOLD_TOOL_MATERIALS_TAG)
					.define('S', Items.STICK)
					.unlockedBy("has_rose_gold_ingot", this.has(ModItems.ROSE_GOLD_TOOL_MATERIALS_TAG))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("rose_gold_shovel")));

				this.shaped(RecipeCategory.TOOLS, ModItems.ROSE_GOLD_HOE)
					.pattern("XX")
					.pattern(" S")
					.pattern(" S")
					.define('X', ModItems.ROSE_GOLD_TOOL_MATERIALS_TAG)
					.define('S', Items.STICK)
					.unlockedBy("has_rose_gold_ingot", this.has(ModItems.ROSE_GOLD_TOOL_MATERIALS_TAG))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("rose_gold_hoe")));

				this.shaped(RecipeCategory.COMBAT, ModItems.ROSE_GOLD_SWORD)
					.pattern("X")
					.pattern("X")
					.pattern("S")
					.define('X', ModItems.ROSE_GOLD_TOOL_MATERIALS_TAG)
					.define('S', Items.STICK)
					.unlockedBy("has_rose_gold_ingot", this.has(ModItems.ROSE_GOLD_TOOL_MATERIALS_TAG))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("rose_gold_sword")));

				// Spears follow the vanilla pattern: material tip on top, stick diagonal
				this.shaped(RecipeCategory.COMBAT, ModItems.ROSE_GOLD_SPEAR)
					.pattern("  X")
					.pattern(" # ")
					.pattern("#  ")
					.define('X', ModItems.ROSE_GOLD_TOOL_MATERIALS_TAG)
					.define('#', Items.STICK)
					.unlockedBy("has_rose_gold_ingot", this.has(ModItems.ROSE_GOLD_TOOL_MATERIALS_TAG))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("rose_gold_spear")));

				this.shaped(RecipeCategory.COMBAT, ModItems.OBSIDIAN_SPEAR)
					.pattern("  X")
					.pattern(" # ")
					.pattern("#  ")
					.define('X', ModItems.OBSIDIAN_INGOT)
					.define('#', Items.STICK)
					.unlockedBy("has_obsidian_ingot", this.has(ModItems.OBSIDIAN_INGOT))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("obsidian_spear")));

				// Rose gold armor: vanilla copper patterns
				this.shaped(RecipeCategory.COMBAT, ModItems.ROSE_GOLD_HELMET)
					.pattern("XXX")
					.pattern("X X")
					.define('X', ModItems.ROSE_GOLD_INGOT)
					.unlockedBy("has_rose_gold_ingot", this.has(ModItems.ROSE_GOLD_INGOT))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("rose_gold_helmet")));

				this.shaped(RecipeCategory.COMBAT, ModItems.ROSE_GOLD_CHESTPLATE)
					.pattern("X X")
					.pattern("XXX")
					.pattern("XXX")
					.define('X', ModItems.ROSE_GOLD_INGOT)
					.unlockedBy("has_rose_gold_ingot", this.has(ModItems.ROSE_GOLD_INGOT))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("rose_gold_chestplate")));

				this.shaped(RecipeCategory.COMBAT, ModItems.ROSE_GOLD_LEGGINGS)
					.pattern("XXX")
					.pattern("X X")
					.pattern("X X")
					.define('X', ModItems.ROSE_GOLD_INGOT)
					.unlockedBy("has_rose_gold_ingot", this.has(ModItems.ROSE_GOLD_INGOT))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("rose_gold_leggings")));

				this.shaped(RecipeCategory.COMBAT, ModItems.ROSE_GOLD_BOOTS)
					.pattern("X X")
					.pattern("X X")
					.define('X', ModItems.ROSE_GOLD_INGOT)
					.unlockedBy("has_rose_gold_ingot", this.has(ModItems.ROSE_GOLD_INGOT))
					.save(this.output, ResourceKey.create(Registries.RECIPE, ObsidianCraft.id("rose_gold_boots")));
			}

			/**
			 * Builds an Alloy Furnace recipe (order-insensitive ingredients) and the
			 * advancement that unlocks it in the recipe book (the recipe reward).
			 * {@code consumes} holds how many items each ingredient needs (null = one each);
			 * it must line up with {@code ingredients}.
			 */
			private void alloyFurnaceRecipe(String name, ItemLike unlockItem, ItemLike result, int count, float experience, int cookingTime, List<Integer> consumes, List<Ingredient> ingredients) {
				ItemStackTemplate template = new ItemStackTemplate(result.asItem(), count);
				AlloyFurnaceRecipe recipe = new AlloyFurnaceRecipe(
					new Recipe.CommonInfo(true),
					new AlloyFurnaceRecipe.AlloyBookInfo(ModRecipeBookCategories.ALLOY, ""),
					ingredients,
					consumes != null ? consumes : List.of(),
					template,
					experience,
					cookingTime
				);

				ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, ObsidianCraft.id(name));
				RecipeUnlockAdvancementBuilder advancementBuilder = new RecipeUnlockAdvancementBuilder();
				advancementBuilder.unlockedBy("has_unlock_item", this.has(unlockItem));
				this.output.accept(key, recipe, advancementBuilder.build(this.output, key, RecipeCategory.MISC));
			}
		};
	}
}
