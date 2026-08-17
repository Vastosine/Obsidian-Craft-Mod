package com.vastosine.obsidian.datagen;

import static com.vastosine.obsidian.item.OCItems.*;
import static com.vastosine.obsidian.block.OCBlocks.*;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OCRecipesProvider extends FabricRecipeProvider {
    public OCRecipesProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
        return new RecipeProvider(recipes, advancements) {
            public static final List<ItemLike> OBSIDIAN_ITEMS = List.of(Items.OBSIDIAN, Items.CRYING_OBSIDIAN);

            public void fourBlockStorageRecipes(
                    final RecipeCategory unpackedFormCategory, final ItemLike unpackedForm, final RecipeCategory packedFormCategory, final ItemLike packedForm
            ) {
                String packingRecipeId = getItemName(packedForm) + "_from_" + getItemName(unpackedForm);
                String unpackingRecipeId = getItemName(unpackedForm) + "_from_" + getItemName(packedForm);
                shapeless(unpackedFormCategory, unpackedForm, 4)
                        .requires(packedForm)
                        .unlockedBy(getHasName(packedForm), this.has(packedForm))
                        .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.parse(unpackingRecipeId)));
                shaped(packedFormCategory, packedForm)
                        .define('I', unpackedForm)
                        .pattern("II")
                        .pattern("II")
                        .unlockedBy(getHasName(unpackedForm), this.has(unpackedForm))
                        .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.parse(packingRecipeId)));
            }

            @Override
            public void buildRecipes() {
                oreSmelting(OBSIDIAN_ITEMS, RecipeCategory.MISC, CookingBookCategory.MISC, OBSIDIAN_INGOT, 0.1f, 200, "obsidian_ingot_from_smelting");
                oreBlasting(OBSIDIAN_ITEMS, RecipeCategory.MISC, CookingBookCategory.MISC, OBSIDIAN_INGOT, 0.1f, 100, "obsidian_ingot_from_blasting");
                fourBlockStorageRecipes(RecipeCategory.MISC, OBSIDIAN_INGOT, RecipeCategory.MISC, OBSIDIAN_BLOCK);
                shaped(RecipeCategory.COMBAT, OBSIDIAN_SWORD)
                        .define('I', OBSIDIAN_INGOT)
                        .define('B', OBSIDIAN_BLOCK)
                        .define('S', Items.STICK)
                        .pattern(" I ")
                        .pattern(" B ")
                        .pattern(" S ")
                        .unlockedBy(getHasName(OBSIDIAN_INGOT), this.has(OBSIDIAN_INGOT))
                        .save(this.output);
                shaped(RecipeCategory.TOOLS, OBSIDIAN_PICKAXE)
                        .define('I', OBSIDIAN_INGOT)
                        .define('B', OBSIDIAN_BLOCK)
                        .define('S', Items.STICK)
                        .pattern("IBI")
                        .pattern(" S ")
                        .pattern(" S ")
                        .unlockedBy(getHasName(OBSIDIAN_INGOT), this.has(OBSIDIAN_INGOT))
                        .save(this.output);
                shaped(RecipeCategory.TOOLS, OBSIDIAN_SHOVEL)
                        .define('B', OBSIDIAN_BLOCK)
                        .define('S', Items.STICK)
                        .pattern(" B ")
                        .pattern(" S ")
                        .pattern(" S ")
                        .unlockedBy(getHasName(OBSIDIAN_INGOT), this.has(OBSIDIAN_INGOT))
                        .save(this.output);
                shaped(RecipeCategory.TOOLS, OBSIDIAN_HOE)
                        .define('I', OBSIDIAN_INGOT)
                        .define('B', OBSIDIAN_BLOCK)
                        .define('S', Items.STICK)
                        .pattern("IB ")
                        .pattern(" S ")
                        .pattern(" S ")
                        .unlockedBy(getHasName(OBSIDIAN_INGOT), this.has(OBSIDIAN_INGOT))
                        .save(this.output);
                shaped(RecipeCategory.TOOLS, OBSIDIAN_AXE)
                        .define('I', OBSIDIAN_INGOT)
                        .define('B', OBSIDIAN_BLOCK)
                        .define('S', Items.STICK)
                        .pattern("IB ")
                        .pattern("IS ")
                        .pattern(" S ")
                        .unlockedBy(getHasName(OBSIDIAN_INGOT), this.has(OBSIDIAN_INGOT))
                        .save(this.output);
                shaped(RecipeCategory.COMBAT, OBSIDIAN_BOOTS)
                        .define('I', OBSIDIAN_INGOT)
                        .pattern("I I")
                        .pattern("I I")
                        .unlockedBy(getHasName(OBSIDIAN_INGOT), this.has(OBSIDIAN_INGOT))
                        .save(this.output);
                shaped(RecipeCategory.COMBAT, OBSIDIAN_LEGGINGS)
                        .define('I', OBSIDIAN_INGOT)
                        .define('B', OBSIDIAN_BLOCK)
                        .pattern("IBI")
                        .pattern("I I")
                        .pattern("I I")
                        .unlockedBy(getHasName(OBSIDIAN_INGOT), this.has(OBSIDIAN_INGOT))
                        .save(this.output);
                shaped(RecipeCategory.COMBAT, OBSIDIAN_CHESTPLATE)
                        .define('I', OBSIDIAN_INGOT)
                        .define('B', OBSIDIAN_BLOCK)
                        .pattern("I I")
                        .pattern("IBI")
                        .pattern("III")
                        .unlockedBy(getHasName(OBSIDIAN_INGOT), this.has(OBSIDIAN_INGOT))
                        .save(this.output);
                shaped(RecipeCategory.COMBAT, OBSIDIAN_HELMET)
                        .define('I', OBSIDIAN_INGOT)
                        .define('B', OBSIDIAN_BLOCK)
                        .pattern("IBI")
                        .pattern("I I")
                        .unlockedBy(getHasName(OBSIDIAN_INGOT), this.has(OBSIDIAN_INGOT))
                        .save(this.output);
                shaped(RecipeCategory.MISC, OBSIDIAN_FURNACE)
                        .define('I', OBSIDIAN_INGOT)
                        .define('F', Items.FURNACE)
                        .pattern("III")
                        .pattern("IFI")
                        .pattern("III")
                        .unlockedBy(getHasName(OBSIDIAN_INGOT), this.has(OBSIDIAN_INGOT))
                        .save(this.output);
                shaped(RecipeCategory.MISC, ALLOY_SMELTER)
                        .define('I', OBSIDIAN_INGOT)
                        .define('B', OBSIDIAN_BLOCK)
                        .define('F', Items.FURNACE)
                        .define('b', Items.BLAST_FURNACE)
                        .define('f', OBSIDIAN_FURNACE)
                        .define('C', Items.CRYING_OBSIDIAN)
                        .pattern("IbI")
                        .pattern("FfF")
                        .pattern("BCB")
                        .unlockedBy(getHasName(OBSIDIAN_FURNACE), this.has(OBSIDIAN_FURNACE))
                        .save(this.output);
            }
        };
    }
}
