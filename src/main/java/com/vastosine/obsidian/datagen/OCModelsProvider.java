package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.block.OCBlocks;
import com.vastosine.obsidian.item.OCItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;

public class OCModelsProvider extends FabricModelProvider {
    public OCModelsProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        blockModelGenerators.createTrivialCube(OCBlocks.OBSIDIAN_BLOCK);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.generateFlatItem(OCItems.OBSIDIAN_INGOT, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(OCItems.OBSIDIAN_SWORD, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(OCItems.OBSIDIAN_PICKAXE, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(OCItems.OBSIDIAN_SHOVEL, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(OCItems.OBSIDIAN_HOE, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(OCItems.OBSIDIAN_AXE, ModelTemplates.FLAT_ITEM);
    }
}
