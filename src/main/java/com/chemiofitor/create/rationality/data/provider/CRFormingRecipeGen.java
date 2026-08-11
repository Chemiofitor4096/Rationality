package com.chemiofitor.create.rationality.data.provider;

import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.data.recipe.FormingRecipeGen;
import com.chemiofitor.create.rationality.index.CRBlocks;
import com.chemiofitor.create.rationality.index.CRFluids;
import com.chemiofitor.create.rationality.index.CRItems;
import com.simibubi.create.AllItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class CRFormingRecipeGen extends FormingRecipeGen {
    public CRFormingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateRationality.MODID);

        create("pulp_from_fluid", b -> b
                .require(CRBlocks.PAPERMAKING_FRAME)
                .require(CRFluids.PULP.get(), 100)
                .output(CRBlocks.PAPERMAKING_FRAME)
                .output(AllItems.PULP.get())
        );

        create("fine_pulp_from_fluid", b -> b
                .require(CRBlocks.PAPERMAKING_FRAME)
                .require(CRFluids.FINE_PULP.get(), 100)
                .output(CRBlocks.PAPERMAKING_FRAME)
                .output(CRItems.FINE_PULP.get())
        );
    }
}
