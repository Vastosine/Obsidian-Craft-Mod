package com.vastosine.obsidian.datagen.custom;

import com.vastosine.obsidian.ObsidianCraft;
import com.vastosine.obsidian.recipe.crafting.AlloyingRecipe;
import com.vastosine.obsidian.recipe.crafting.OCIngredient;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.triggers.ImpossibleTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.NonNull;

import java.util.List;

public abstract class OCCustomRecipeProvider extends RecipeProvider {

    protected OCCustomRecipeProvider(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
        super(recipeOutput, advancementOutput);
    }

    public void fourBlockStorageRecipes(
            final RecipeCategory unpackedFormCategory, final ItemLike unpackedForm, final RecipeCategory packedFormCategory, final ItemLike packedForm
    ) {
        String packingRecipeId = getItemName(packedForm) + "_from_" + getItemName(unpackedForm);
        String unpackingRecipeId = getItemName(unpackedForm) + "_from_" + getItemName(packedForm);
        shapeless(unpackedFormCategory, unpackedForm, 4)
                .requires(packedForm)
                .unlockedBy(getHasName(packedForm), this.has(packedForm))
                .save(this.output, getKey(unpackingRecipeId));
        shaped(packedFormCategory, packedForm)
                .define('I', unpackedForm)
                .pattern("II")
                .pattern("II")
                .unlockedBy(getHasName(unpackedForm), this.has(unpackedForm))
                .save(this.output, getKey(packingRecipeId));
    }

    private static @NonNull ResourceKey<Recipe<?>> getKey(String packingRecipeId) {
        return ResourceKey.create(Registries.RECIPE, ObsidianCraft.id(packingRecipeId));
    }


    public final <T extends AbstractCookingRecipe> void customOreCooking(
            final AbstractCookingRecipe.Factory<T> factory,
            final List<ItemLike> smeltables,
            final RecipeCategory craftingCategory,
            final CookingBookCategory cookingCategory,
            final ItemLike result,
            final float experience,
            final int cookingTime,
            final String group,
            final String fromDesc
    ) {
        for (ItemLike item : smeltables) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(item), craftingCategory, cookingCategory, result, experience, cookingTime, factory)
                    .group(group)
                    .unlockedBy(getHasName(item), this.has(item))
                    .save(this.output, getKey(getItemName(result) + fromDesc + "_" + getItemName(item)));
        }
    }

    @Override
    public void oreSmelting(
            final List<ItemLike> smeltables,
            final RecipeCategory craftingCategory,
            final CookingBookCategory cookingCategory,
            final ItemLike result,
            final float experience,
            final int cookingTime,
            final String group
    ) {
        this.customOreCooking(SmeltingRecipe::new, smeltables, craftingCategory, cookingCategory, result, experience, cookingTime, group, "_from_smelting");
    }

    @Override
    public void oreBlasting(
            final List<ItemLike> smeltables,
            final RecipeCategory craftingCategory,
            final CookingBookCategory cookingCategory,
            final ItemLike result,
            final float experience,
            final int cookingTime,
            final String group
    ) {
        this.customOreCooking(BlastingRecipe::new, smeltables, craftingCategory, cookingCategory, result, experience, cookingTime, group, "_from_blasting");
    }

    public void alloyingRecipe(
            final String packingRecipeId,
            final List<OCIngredient> ingredients,
            final List<ItemStackTemplate> results,
            final int cookingTime,
            final float experience,
            List<Item> unlockItems
    ) {
        AlloyingRecipe recipe = new AlloyingRecipe(
                ingredients,
                results,
                cookingTime,
                experience
        );

        ResourceKey<Recipe<?>> key = getKey(packingRecipeId);
        RecipeUnlockAdvancementBuilder advancementBuilder = new RecipeUnlockAdvancementBuilder();
        unlockItems.forEach(i -> {
                    if (i != null) {
                        advancementBuilder.unlockedBy("has_" + getItemName(i), has(i));
                    }
                }
        );

        output.accept(key, recipe, advancementBuilder.build(output, key, RecipeCategory.MISC));
    }

    public void alloyingRecipe(
            final String path,
            final List<OCIngredient> ingredients,
            final List<ItemStackTemplate> results,
            final int cookingTime,
            final float experience,
            Item... unlockItems
    ) {
        alloyingRecipe(path, ingredients, results, cookingTime, experience, List.of(unlockItems));
    }

    public void alloyingRecipeWithExtraName(
            final String extraName,
            final List<OCIngredient> ingredients,
            final List<ItemStackTemplate> results,
            final int cookingTime,
            final float experience,
            Item... unlockItems
    ) {
        alloyingRecipe("alloying/" + extraName, ingredients, results, cookingTime, experience, unlockItems);
    }

    public void alloyingRecipe(
            final List<OCIngredient> ingredients,
            final List<ItemStackTemplate> results,
            final int cookingTime,
            final float experience,
            Item... unlockItems
    ) {
        alloyingRecipe(
                "alloying/" + getItemName(results.getFirst().item().value()),
                ingredients, results, cookingTime, experience, unlockItems
        );
    }

    public void alloyingRecipe(
            final List<OCIngredient> ingredients,
            final List<ItemStackTemplate> results,
            final int cookingTime,
            final float experience
    ) {
        alloyingRecipe(
                "alloying/" + getItemName(results.getFirst().item().value()),
                ingredients, results, cookingTime, experience,
                ingredients.stream()
                        .map(i -> i.toVanilla().getSingleItem())
                        .map(i -> i.map(Holder::value).orElse(null))
                        .toList()
        );
    }

    public void alloyingRecipe(
            final List<OCIngredient> ingredients,
            final Item result,
            final int count,
            final int cookingTime,
            final float experience
    ) {
        alloyingRecipe(ingredients, List.of(new ItemStackTemplate(result, count)), cookingTime, experience);
    }

    public void alloyingRecipe(
            final List<OCIngredient> ingredients,
            final Item result,
            final int cookingTime,
            final float experience
    ) {
        alloyingRecipe(ingredients, List.of(new ItemStackTemplate(result, 1)), cookingTime, experience);
    }

    public void removeRecipe(final String namespace, final String path) {
        this.shapeless(RecipeCategory.MISC, Items.BARRIER)
                .requires(Items.BARRIER)
                .unlockedBy("impossible", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(
                        this.output,
                        ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(namespace, path))
                );
    }

    public void removeRecipe(final String path) {
        removeRecipe("minecraft", path);
    }
}
