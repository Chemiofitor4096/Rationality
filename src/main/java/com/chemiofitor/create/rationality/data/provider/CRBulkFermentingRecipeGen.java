package com.chemiofitor.create.rationality.data.provider;

import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.index.CRFluids;
import com.jesz.createdieselgenerators.CDGFluids;
import com.jesz.createdieselgenerators.CDGRecipes;
import com.jesz.createdieselgenerators.content.bulk_fermenter.BulkFermentingRecipe;
import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class CRBulkFermentingRecipeGen extends StandardProcessingRecipeGen<BulkFermentingRecipe> {
    public CRBulkFermentingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateRationality.MODID);

        create("ethanol", b -> b
                .require(CRFluids.SYRUP.get(), 1000)
                .duration(400)
                .output(CDGFluids.ETHANOL.get(), 1000));
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CDGRecipes.BULK_FERMENTING;
    }
}
