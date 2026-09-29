package com.chemiofitor.create.rationality.data.provider;

import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.compat.RationalMods;
import com.chemiofitor.create.rationality.data.recipe.PulpingRecipeGen;
import com.chemiofitor.create.rationality.index.CRFluids;
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

        // 常温制浆：无碱、产量减半、不产黑液（没有碱线时的过渡路径）
        create("pulp_from_pulpifiable", b -> b
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(FluidTags.WATER, 1000)
                .output(CRFluids.PULP.get(), 500));

        // 碱法蒸煮（唯一碱法路径）：现实白液蒸煮，碱液是蒸煮的唯一碱源——制碱是制浆的前置。
        // 2026-09-26 精简：删除"物品碱（2 碱性物）"与"灰汁 1500"两条——三条输出相同、物品碱成本严格最低，
        // 属纯支配关系（另两条成 JEI 噪音），且"往蒸煮锅里扔固体碱"不符合现实工艺。
        create("pulp_from_pulpifiable_with_heat_and_lye", b -> b
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(AllTags.AllItemTags.PULPIFIABLE.tag)
                .require(CRTagKeys.Fluids.LYE, 1000)
                .requiresHeat(HeatCondition.HEATED)
                .output(CRFluids.PULP.get(), 1000)
                .output(CRFluids.BLACK_LIQUOR.get(), 250));

        // 碱抽提精浆：碱液溶出残余木质素（现实碱抽提段），木质素随废液排出 → 必须副产黑液
        create("fine_pulp_from_pulp_and_lye", b -> b
                .require(CRTagKeys.Fluids.LYE, 500)
                .require(CRFluids.PULP.get(), 1000)
                .requiresHeat(HeatCondition.HEATED)
                .output(CRFluids.FINE_PULP.get(), 1000)
                .output(CRFluids.BLACK_LIQUOR.get(), 100));

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
