package com.chemiofitor.create.rationality.common.recipe;

import com.chemiofitor.create.rationality.index.CRRecipeTypes;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;

public class PulpingRecipe extends BasinRecipe {
    public PulpingRecipe(ProcessingRecipeParams params) {
        super(CRRecipeTypes.PULPING, params);
    }
}
