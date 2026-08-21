package com.chemiofitor.create.rationality.data.provider;

import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.index.CRBlocks;
import com.chemiofitor.create.rationality.index.CRItems;
import com.simibubi.create.api.data.recipe.CrushingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class CRCrushingRecipeGen extends CrushingRecipeGen {
    public CRCrushingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateRationality.MODID);

        create("crushed_coal_gangue", b -> b
                .require(CRBlocks.COAL_GANGUE)
                .output(CRItems.CRUSHED_COAL_GANGUE));
    }
}
