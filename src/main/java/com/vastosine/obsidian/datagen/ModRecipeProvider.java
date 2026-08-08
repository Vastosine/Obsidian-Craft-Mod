package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.ObsidianCraft;
import com.vastosine.obsidian.block.ModBlocks;
import com.vastosine.obsidian.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
	public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(output, registryLookup);
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
			}
		};
	}
}
