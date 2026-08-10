package com.vastosine.obsidian.datagen;

import com.vastosine.obsidian.block.OCBlocks;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.world.item.Item;

import static com.vastosine.obsidian.item.OCItems.*;

public class OCModelsProvider extends FabricModelProvider {
    public OCModelsProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        blockModelGenerators.createTrivialCube(OCBlocks.OBSIDIAN_BLOCK);
    }

    public static void generateFlatItem(ItemModelGenerators itemModelGenerators, ModelTemplate modelTemplate, Item...items) {
        for (Item item : items) {
            itemModelGenerators.generateFlatItem(item, modelTemplate);
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        generateFlatItem(itemModelGenerators, ModelTemplates.FLAT_ITEM,
                OBSIDIAN_INGOT,
                OBSIDIAN_BOOTS,
                OBSIDIAN_LEGGINGS,
                OBSIDIAN_CHESTPLATE,
                OBSIDIAN_HELMET
        );
        generateFlatItem(itemModelGenerators, ModelTemplates.FLAT_HANDHELD_ITEM,
                OBSIDIAN_SWORD,
                OBSIDIAN_PICKAXE,
                OBSIDIAN_SHOVEL,
                OBSIDIAN_HOE,
                OBSIDIAN_AXE
        );
    }
}
