package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.block.OCBlockItemIds;
import com.vastosine.obsidian.tags.OCBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public class OCBlockTagsProvider extends FabricTagsProvider.BlockTagsProvider {
    public OCBlockTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        // vanilla
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OCBlockItemIds.OBSIDIAN_BLOCK.block());
        tag(BlockTags.NEEDS_DIAMOND_TOOL).add(OCBlockItemIds.OBSIDIAN_BLOCK.block());

        // custom
        tag(OCBlockTags.INCORRECT_FOR_OBSIDIAN_TOOL);
    }
}
