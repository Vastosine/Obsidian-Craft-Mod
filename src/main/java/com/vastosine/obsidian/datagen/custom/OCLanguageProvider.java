package com.vastosine.obsidian.datagen.custom;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public abstract class OCLanguageProvider extends FabricLanguageProvider {
    protected OCLanguageProvider(FabricPackOutput packOutput, String languageCode, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(packOutput, languageCode, registryLookup);
    }

    public static final String ADVANCEMENT_NAME = "oc";

    public static void addAdvancement(TranslationBuilder translationBuilder, final String name, final String title, final String description) {
        translationBuilder.add("advancements." + ADVANCEMENT_NAME + "." + name + ".title", title);
        translationBuilder.add("advancements." + ADVANCEMENT_NAME + "." + name + ".description", description);
    }

    /**
     *
     * @param translationBuilder TranslationBuilder
     * @param translations       three Strings a group
     */
    public static void addAdvancement(TranslationBuilder translationBuilder, final String... translations) {
        for (int i = 0; i < translations.length; i += 3) {
            addAdvancement(translationBuilder, translations[i], translations[i + 1], translations[i + 2]);
        }
    }

    public static <T> void addSingle(TranslationBuilder translationBuilder, T translationKey, String translation) {
        if (translationKey instanceof String string) {
            translationBuilder.add(string, translation);
        } else if (translationKey instanceof Item item) {
            translationBuilder.add(item, translation);
        } else if (translationKey instanceof Block block) {
            translationBuilder.add(block, translation);
        }
    }

    @SafeVarargs
    public static <T> void add(TranslationBuilder translationBuilder, T... translations) {
        for (int i = 0; i < translations.length; i += 2) {
            addSingle(translationBuilder, translations[i], (String) translations[i + 1]);
        }
    }
}
