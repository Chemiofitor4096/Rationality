package com.chemiofitor.create.rationality.data.provider;

import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.compat.RationalMods;
import com.chemiofitor.create.rationality.index.CRFluids;
import com.chemiofitor.create.rationality.index.CRItems;
import com.chemiofitor.create.rationality.index.CRTagKeys;
import com.jesz.createdieselgenerators.CDGFluids;
import com.pyzpre.createbitterballen.index.FluidRegistry;
import com.simibubi.create.api.data.recipe.MixingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.mcreator.createconfectionery.init.CreateConfectioneryModFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class CRMixingRecipeGen extends MixingRecipeGen {
    public CRMixingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateRationality.MODID);

        create("oilpaper", b -> b.output(CRItems.OILPAPER)
                .require(Items.PAPER)
                .require(CDGFluids.PLANT_OIL.get(), 10)
                .whenModLoaded(RationalMods.DIESEL_GENERATORS.modid));

        create("frying_oil", b -> b
                .require(CRTagKeys.Fluids.PLANTOIL, 1000)
                .require(Items.SUGAR)
                .requiresHeat(HeatCondition.HEATED)
                .output(FluidRegistry.FRYING_OIL.get(), 1000)
                .whenModLoaded(RationalMods.BITTERBALLEN.modid));

        create("caramel", b -> b
                .require(CRTagKeys.Fluids.SYRUP, 1000)
                .requiresHeat(HeatCondition.HEATED)
                .output(CreateConfectioneryModFluids.CARAMEL.get(), 500)
                .whenModLoaded(RationalMods.CONFECTIONERY.modid));

        create("ash_lye", b -> b
                .require(CRItems.PLANT_ASH)
                .require(CRItems.PLANT_ASH)
                .require(CRItems.PLANT_ASH)
                .require(CRItems.PLANT_ASH)
                .require(FluidTags.WATER, 1000)
                .output(CRFluids.ASH_LYE.get(), 1000));

        create("slaked_lime", b -> b
                .require(CRItems.QUICKLIME)
                .require(FluidTags.WATER, 100)
                .output(CRItems.SLAKED_LIME));

        create("calcium_carbonate", b -> b
                .require(CRItems.QUICKLIME)
                .require(CRTagKeys.Fluids.ASH_LYE, 500)
                .output(CRItems.CALCIUM_CARBONATE)
                .output(CRFluids.LYE.get(), 500));

        create("bone_meal", b -> b
                .require(CRTagKeys.Fluids.BLACK_LIQUOR, 500)
                .require(Items.BONE)
                .output(Items.BONE_MEAL, 6)
        );
    }
}
