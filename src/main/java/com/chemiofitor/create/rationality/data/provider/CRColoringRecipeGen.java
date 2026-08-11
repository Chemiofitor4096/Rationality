package com.chemiofitor.create.rationality.data.provider;

import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.index.CRTagKeys;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import plus.dragons.createdragonsplus.common.kinetics.fan.coloring.ColoringRecipe;
import plus.dragons.createdragonsplus.common.kinetics.fan.coloring.ColoringRecipeParams;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;

import java.util.concurrent.CompletableFuture;

public class CRColoringRecipeGen extends ProcessingRecipeGen<ColoringRecipeParams, ColoringRecipe, ColoringRecipe.Builder> {
    public CRColoringRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateRationality.MODID);

        create("paper", b -> b
                .require(CRTagKeys.Items.COLOR_PAPER)
                .output(Items.PAPER));
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CDPRecipes.COLORING;
    }

    @Override
    protected ColoringRecipe.Builder getBuilder(ResourceLocation id) {
        return ColoringRecipe.builder(id, DyeColor.WHITE);
    }
}
