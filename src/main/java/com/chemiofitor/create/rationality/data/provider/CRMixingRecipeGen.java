package com.chemiofitor.create.rationality.data.provider;

import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.compat.RationalMods;
import com.chemiofitor.create.rationality.index.CRFluids;
import com.chemiofitor.create.rationality.index.CRItems;
import com.chemiofitor.create.rationality.index.CRTagKeys;
import com.jesz.createdieselgenerators.CDGFluids;
import com.lance5057.butchercraft.ButchercraftItems;
import com.pyzpre.createbitterballen.index.FluidRegistry;
import com.simibubi.create.api.data.recipe.MixingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.mcreator.createconfectionery.init.CreateConfectioneryModFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class CRMixingRecipeGen extends MixingRecipeGen {
    public CRMixingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateRationality.MODID);

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
                .output(Items.BONE_MEAL, 6));

        // 柴油动力
        create("oilpaper", b -> b.output(CRItems.OILPAPER)
                .require(Items.PAPER)
                .require(CDGFluids.PLANT_OIL.get(), 10)
                .whenModLoaded(RationalMods.DIESEL_GENERATORS.modid));

        create("biodiesel", b -> b
                .require(CRTagKeys.Fluids.PLANTOIL, 100)
                .require(CRTagKeys.Fluids.ETHANOL, 200)
                .output(CDGFluids.BIODIESEL.get(), 200)
                .output(CRFluids.GLYCEROL.get(), 100)
                .whenModLoaded(RationalMods.DIESEL_GENERATORS.modid));

        // 油炸
        create("frying_oil", b -> b
                .require(CRTagKeys.Fluids.PLANTOIL, 1000)
                .require(Items.SUGAR)
                .requiresHeat(HeatCondition.HEATED)
                .output(FluidRegistry.FRYING_OIL.get(), 1000)
                .whenModLoaded(RationalMods.BITTERBALLEN.modid));

        // 甜食
        create("caramel", b -> b
                .require(CRTagKeys.Fluids.SYRUP, 1000)
                .requiresHeat(HeatCondition.HEATED)
                .output(CreateConfectioneryModFluids.CARAMEL.get(), 500)
                .whenModLoaded(RationalMods.CONFECTIONERY.modid));

        // 屠夫乐事
        create("lard_from_heated", b -> b
                .require(ButchercraftItems.FAT)
                .requiresHeat(HeatCondition.HEATED)
                .output(ButchercraftItems.LARD, 2)
                .whenModLoaded(RationalMods.BUTCHER_CRAFT.modid));

        create("lard_from_super_heated", b -> b
                .require(ButchercraftItems.FAT)
                .requiresHeat(HeatCondition.SUPERHEATED)
                .output(ButchercraftItems.LARD, 3)
                .output(0.5f, ButchercraftItems.LARD, 1)
                .whenModLoaded(RationalMods.BUTCHER_CRAFT.modid));

        create("soap", b -> b
                .require(ButchercraftItems.LARD)
                .require(ItemTags.FLOWERS)
                .require(CRTagKeys.Fluids.LYE, 100)
                .output(ButchercraftItems.SOAP.get())
                .whenModLoaded(RationalMods.BUTCHER_CRAFT.modid));
    }
}
