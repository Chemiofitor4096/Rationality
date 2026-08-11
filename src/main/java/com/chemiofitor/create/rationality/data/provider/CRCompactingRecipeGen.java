package com.chemiofitor.create.rationality.data.provider;

import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.compat.RationalMods;
import com.chemiofitor.create.rationality.index.CRTagKeys;
import com.jesz.createdieselgenerators.CDGFluids;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.CompactingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class CRCompactingRecipeGen extends CompactingRecipeGen {
    public CRCompactingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateRationality.MODID);

        create("cardboard", b -> b
                .require(AllItems.PULP)
                .require(AllItems.PULP)
                .require(AllItems.PULP)
                .output(AllItems.CARDBOARD));

        create("plant_oil", b -> b
                .require(CRTagKeys.Items.OIL_PRESSING_RAW_MATERIAL)
                .require(CRTagKeys.Items.OIL_PRESSING_RAW_MATERIAL)
                .require(CRTagKeys.Items.OIL_PRESSING_RAW_MATERIAL)
                .require(CRTagKeys.Items.OIL_PRESSING_RAW_MATERIAL)
                .output(CDGFluids.PLANT_OIL.get(), 500)
                .whenModLoaded(RationalMods.DIESEL_GENERATORS.modid));
    }
}
