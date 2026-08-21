package com.chemiofitor.create.rationality.data.provider;

import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.compat.RationalMods;
import com.chemiofitor.create.rationality.data.recipe.PulpingRecipeGen;
import com.chemiofitor.create.rationality.index.CRFluids;
import com.chemiofitor.create.rationality.index.CRItems;
import com.chemiofitor.create.rationality.index.CRTagKeys;
import com.lance5057.butchercraft.ButchercraftItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.concurrent.CompletableFuture;

public class CRPulpingRecipeGen extends PulpingRecipeGen {
    public CRPulpingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateRationality.MODID);

        create("pulp_from_pulpifiable", b -> b
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(FluidTags.WATER, 1000)
                .output(CRFluids.PULP.get(), 500));

        create("pulp_from_pulpifiable_with_heat", b -> b
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(CRTagKeys.Items.ALKALINE)
                .require(CRTagKeys.Items.ALKALINE)
                .require(FluidTags.WATER, 1000)
                .requiresHeat(HeatCondition.HEATED)
                .output(CRFluids.PULP.get(), 1000)
                .output(CRFluids.BLACK_LIQUOR.get(), 250));

        create("pulp_from_pulpifiable_with_heat_and_lye", b -> b
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(CRTagKeys.Fluids.LYE, 1000)
                .requiresHeat(HeatCondition.HEATED)
                .output(CRFluids.PULP.get(), 1000)
                .output(CRFluids.BLACK_LIQUOR.get(), 250));

        create("pulp_from_pulpifiable_with_heat_and_ash_lye", b -> b
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(CRTagKeys.Fluids.ASH_LYE, 1500)
                .requiresHeat(HeatCondition.HEATED)
                .output(CRFluids.PULP.get(), 1000)
                .output(CRFluids.BLACK_LIQUOR.get(), 250));

        create("fine_pulp_from_pulp", b -> b
                .require(CRItems.QUICKLIME)
                .require(CRItems.QUICKLIME)
                .require(CRFluids.PULP.get(), 1000)
                .output(CRFluids.FINE_PULP.get(), 1000));

        create("fine_pulp_from_pulp_from_lye", b -> b
                .require(CRTagKeys.Fluids.LYE, 250)
                .require(CRFluids.PULP.get(), 1000)
                .output(CRFluids.FINE_PULP.get(), 1000));

        create("syrup", b ->b
                .require(CRTagKeys.Items.SUGAR_RAW_MATERIAL)
                .require(CRTagKeys.Items.SUGAR_RAW_MATERIAL)
                .require(CRTagKeys.Items.SUGAR_RAW_MATERIAL)
                .require(CRTagKeys.Items.SUGAR_RAW_MATERIAL)
                .require(Fluids.WATER, 1000)
                .requiresHeat(HeatCondition.HEATED)
                .output(CRFluids.SYRUP.get(), 1000));

        // 屠夫乐事
        create("ground_beef",  b ->b
                .require(Items.BEEF)
                .output(ButchercraftItems.GROUND_BEEF.get(), 3)
                .whenModLoaded(RationalMods.BUTCHER_CRAFT.modid));

        create("ground_porkchop",  b ->b
                .require(Items.PORKCHOP)
                .output(ButchercraftItems.GROUND_PORK.get(), 3)
                .whenModLoaded(RationalMods.BUTCHER_CRAFT.modid));

        create("ground_chicken",  b ->b
                .require(Items.CHICKEN)
                .output(ButchercraftItems.GROUND_CHICKEN.get(), 3)
                .whenModLoaded(RationalMods.BUTCHER_CRAFT.modid));

        // 农夫乐事联动
        create("minced_beef",  b ->b
                .require(Items.BEEF)
                .require(Items.BEEF)
                .require(Items.BEEF)
                .require(Items.BEEF)
                .require(Fluids.WATER, 500)
                .output(ModItems.MINCED_BEEF.get(), 10)
                .whenModLoaded(RationalMods.FARMERS_DELIGHT.modid)
                .whenModMissing(RationalMods.BUTCHER_CRAFT.modid));

        create("bacon",  b ->b
                .require(Items.PORKCHOP)
                .require(Items.PORKCHOP)
                .require(Items.PORKCHOP)
                .require(Items.PORKCHOP)
                .require(Fluids.WATER, 500)
                .output(ModItems.BACON.get(), 10)
                .whenModLoaded(RationalMods.FARMERS_DELIGHT.modid)
                .whenModMissing(RationalMods.BUTCHER_CRAFT.modid));

        create("chicken_cuts",  b ->b
                .require(Items.CHICKEN)
                .require(Items.CHICKEN)
                .require(Items.CHICKEN)
                .require(Items.CHICKEN)
                .require(Fluids.WATER, 500)
                .output(ModItems.CHICKEN_CUTS.get(), 10)
                .whenModLoaded(RationalMods.FARMERS_DELIGHT.modid)
                .whenModMissing(RationalMods.BUTCHER_CRAFT.modid));

        // 原版
        create("clay_ball", b -> b
                .require(Items.CLAY)
                .output(Items.CLAY_BALL, 4));

        create("sand_from_gravel", b -> b
                .require(Items.GRAVEL)
                .output(Items.SAND)
                .output(0.1f, Items.FLINT));

        create("glowstone_dust", b -> b
                .require(Items.GLOWSTONE)
                .output(Items.GLOWSTONE_DUST, 4));
    }
}
