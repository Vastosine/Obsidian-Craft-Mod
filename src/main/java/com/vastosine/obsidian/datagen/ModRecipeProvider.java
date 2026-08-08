package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.ObsidianCraft;
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
				// 黑曜石或哭泣黑曜石烧炼成黑曜石锭（熔炉）
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

				// 高炉烧炼，速度加倍（100 tick）
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
			}
		};
	}
}
