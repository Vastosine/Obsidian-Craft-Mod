package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.block.OCBlocks;
import com.vastosine.obsidian.item.OCItems;
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
                oreSmelting(OBSIDIAN_ITEMS, RecipeCategory.MISC, CookingBookCategory.MISC, OCItems.OBSIDIAN_INGOT, 0.1f, 200, "obsidian_ingot_from_smelting");
                oreBlasting(OBSIDIAN_ITEMS, RecipeCategory.MISC, CookingBookCategory.MISC, OCItems.OBSIDIAN_INGOT, 0.1f, 100, "obsidian_ingot_from_blasting");
                fourBlockStorageRecipes(RecipeCategory.MISC, OCItems.OBSIDIAN_INGOT, RecipeCategory.MISC, OCBlocks.OBSIDIAN_BLOCK);
                shaped(RecipeCategory.COMBAT, OCItems.OBSIDIAN_SWORD)
                        .define('I', OCItems.OBSIDIAN_INGOT)
                        .define('B', OCBlocks.OBSIDIAN_BLOCK)
                        .define('S', Items.STICK)
                        .pattern(" I ")
                        .pattern(" B ")
                        .pattern(" S ")
                        .unlockedBy(getHasName(OCItems.OBSIDIAN_INGOT), this.has(OCItems.OBSIDIAN_INGOT))
                        .save(this.output);
                shaped(RecipeCategory.TOOLS, OCItems.OBSIDIAN_PICKAXE)
                        .define('I', OCItems.OBSIDIAN_INGOT)
                        .define('B', OCBlocks.OBSIDIAN_BLOCK)
                        .define('S', Items.STICK)
                        .pattern("IBI")
                        .pattern(" S ")
                        .pattern(" S ")
                        .unlockedBy(getHasName(OCItems.OBSIDIAN_INGOT), this.has(OCItems.OBSIDIAN_INGOT))
                        .save(this.output);
                shaped(RecipeCategory.TOOLS, OCItems.OBSIDIAN_SHOVEL)
                        .define('B', OCBlocks.OBSIDIAN_BLOCK)
                        .define('S', Items.STICK)
                        .pattern(" B ")
                        .pattern(" S ")
                        .pattern(" S ")
                        .unlockedBy(getHasName(OCItems.OBSIDIAN_INGOT), this.has(OCItems.OBSIDIAN_INGOT))
                        .save(this.output);
                shaped(RecipeCategory.TOOLS, OCItems.OBSIDIAN_HOE)
                        .define('I', OCItems.OBSIDIAN_INGOT)
                        .define('B', OCBlocks.OBSIDIAN_BLOCK)
                        .define('S', Items.STICK)
                        .pattern("IB ")
                        .pattern(" S ")
                        .pattern(" S ")
                        .unlockedBy(getHasName(OCItems.OBSIDIAN_INGOT), this.has(OCItems.OBSIDIAN_INGOT))
                        .save(this.output);
                shaped(RecipeCategory.TOOLS, OCItems.OBSIDIAN_AXE)
                        .define('I', OCItems.OBSIDIAN_INGOT)
                        .define('B', OCBlocks.OBSIDIAN_BLOCK)
                        .define('S', Items.STICK)
                        .pattern("IB ")
                        .pattern("IS ")
                        .pattern(" S ")
                        .unlockedBy(getHasName(OCItems.OBSIDIAN_INGOT), this.has(OCItems.OBSIDIAN_INGOT))
                        .save(this.output);
                shaped(RecipeCategory.COMBAT, OCItems.OBSIDIAN_BOOTS)
                        .define('I', OCItems.OBSIDIAN_INGOT)
                        .pattern("I I")
                        .pattern("I I")
                        .unlockedBy(getHasName(OCItems.OBSIDIAN_INGOT), this.has(OCItems.OBSIDIAN_INGOT))
                        .save(this.output);
                shaped(RecipeCategory.COMBAT, OCItems.OBSIDIAN_LEGGINGS)
                        .define('I', OCItems.OBSIDIAN_INGOT)
                        .define('B', OCBlocks.OBSIDIAN_BLOCK)
                        .pattern("IBI")
                        .pattern("I I")
                        .pattern("I I")
                        .unlockedBy(getHasName(OCItems.OBSIDIAN_INGOT), this.has(OCItems.OBSIDIAN_INGOT))
                        .save(this.output);
                shaped(RecipeCategory.COMBAT, OCItems.OBSIDIAN_CHESTPLATE)
                        .define('I', OCItems.OBSIDIAN_INGOT)
                        .define('B', OCBlocks.OBSIDIAN_BLOCK)
                        .pattern("I I")
                        .pattern("IBI")
                        .pattern("III")
                        .unlockedBy(getHasName(OCItems.OBSIDIAN_INGOT), this.has(OCItems.OBSIDIAN_INGOT))
                        .save(this.output);
                shaped(RecipeCategory.COMBAT, OCItems.OBSIDIAN_HELMET)
                        .define('I', OCItems.OBSIDIAN_INGOT)
                        .define('B', OCBlocks.OBSIDIAN_BLOCK)
                        .pattern("IBI")
                        .pattern("I I")
                        .unlockedBy(getHasName(OCItems.OBSIDIAN_INGOT), this.has(OCItems.OBSIDIAN_INGOT))
                        .save(this.output);

            }
        };
    }
}
