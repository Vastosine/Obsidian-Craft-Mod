package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.tags.OCItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.BlockItemTagAppender;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

import static com.vastosine.obsidian.item.OCItemIds.*;

public class OCItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {
    public OCItemTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    protected BlockItemTagAppender<Item> tag(final TagKey<Item> tag) {
        return new BlockItemTagAppender<Item>(super.tag(tag)) {
            @Override
            protected ResourceKey<Item> convertElement(final BlockItemId element) {
                return element.item();
            }
        };
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        // vanilla
        tag(ItemTags.SWORDS).add(OBSIDIAN_SWORD);
        tag(ItemTags.PICKAXES).add(OBSIDIAN_PICKAXE);
        tag(ItemTags.SHOVELS).add(OBSIDIAN_SHOVEL);
        tag(ItemTags.HOES).add(OBSIDIAN_HOE);
        tag(ItemTags.AXES).add(OBSIDIAN_AXE);
        tag(ItemTags.FOOT_ARMOR).add(OBSIDIAN_BOOTS);
        tag(ItemTags.LEG_ARMOR).add(OBSIDIAN_LEGGINGS);
        tag(ItemTags.CHEST_ARMOR).add(OBSIDIAN_CHESTPLATE);
        tag(ItemTags.HEAD_ARMOR).add(OBSIDIAN_HELMET);
//        tag(ItemTags.TRIMMABLE_ARMOR).add(OBSIDIAN_HELMET);

        // custom
        tag(OCItemTags.OBSIDIAN_TOOL_MATERIALS).add(OBSIDIAN_INGOT);
        tag(OCItemTags.OBSIDIAN_ARMOR_MATERIALS).add(OBSIDIAN_INGOT);
        tag(OCItemTags.OBSIDIAN_ARMORS).add(
                OBSIDIAN_HELMET,
                OBSIDIAN_CHESTPLATE,
                OBSIDIAN_LEGGINGS,
                OBSIDIAN_BOOTS
        );
    }
}
