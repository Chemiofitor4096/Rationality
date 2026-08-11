package com.chemiofitor.create.rationality.data.recipe;

import com.chemiofitor.create.rationality.common.recipe.PulpingRecipe;
import com.chemiofitor.create.rationality.index.CRRecipeTypes;
import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public abstract class PulpingRecipeGen extends StandardProcessingRecipeGen<PulpingRecipe> {
    public PulpingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CRRecipeTypes.PULPING;
    }
}
