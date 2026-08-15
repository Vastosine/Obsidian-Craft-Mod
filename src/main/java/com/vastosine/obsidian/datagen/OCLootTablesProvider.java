package com.vastosine.obsidian.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

import static com.vastosine.obsidian.block.OCBlocks.*;

public class OCLootTablesProvider extends FabricBlockLootSubProvider {
    public OCLootTablesProvider(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(packOutput, registriesFuture);
    }

    public void addNameableBlockEntityTable(Block block) {
        add(block, this::createNameableBlockEntityTable);
    }

    @Override
    public void generate() {
        dropSelf(OBSIDIAN_BLOCK);
        addNameableBlockEntityTable(OBSIDIAN_FURNACE);
    }
}
