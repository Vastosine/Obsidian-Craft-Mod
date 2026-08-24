package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.tags.OCBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.BlockItemTagAppender;
import net.minecraft.references.BlockItemId;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

import static com.vastosine.obsidian.block.OCBlockItemIds.*;

public class OCBlockTagsProvider extends FabricTagsProvider.BlockTagsProvider {
    public OCBlockTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    protected BlockItemTagAppender<Block> tag(final TagKey<Block> tag) {
        return new BlockItemTagAppender<Block>(super.tag(tag)) {
            @Override
            protected ResourceKey<Block> convertElement(final BlockItemId element) {
                return element.block();
            }
        };
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        // vanilla
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                OBSIDIAN_BLOCK,
                OBSIDIAN_FURNACE,
                ALLOY_SMELTER
        );
        tag(BlockTags.NEEDS_DIAMOND_TOOL).add(
                OBSIDIAN_BLOCK
        );

        // custom
        tag(OCBlockTags.INCORRECT_FOR_OBSIDIAN_TOOL);
        tag(OCBlockTags.OBSIDIAN_BLOCK).add(
                BlockItemIds.OBSIDIAN,
                BlockItemIds.CRYING_OBSIDIAN,
                OBSIDIAN_BLOCK
        );
    }
}
