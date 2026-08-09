package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.item.OCItemIds;
import com.vastosine.obsidian.tags.OCItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.BlockItemTagAppender;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

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


        // custom
        tag(OCItemTags.OBSIDIAN_TOOL_MATERIALS).add(OCItemIds.OBSIDIAN_INGOT);
    }
}
