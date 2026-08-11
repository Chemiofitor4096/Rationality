package com.chemiofitor.create.rationality.data.provider;

import cn.mcmod.corn_delight.item.ItemRegistry;
import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.compat.RationalMods;
import com.pyzpre.createbitterballen.index.FluidRegistry;
import com.simibubi.create.api.data.recipe.FillingRecipeGen;
import net.mcreator.createconfectionery.init.CreateConfectioneryModFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class CRFillingRecipeGen extends FillingRecipeGen {
    public CRFillingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateRationality.MODID);

        create("caramel_popcorn", b -> b
                .require(ItemRegistry.POPCORN)
                .require(CreateConfectioneryModFluids.CARAMEL.get(), 10)
                .output(ItemRegistry.CARAMEL_POPCORN)
                .whenModLoaded(RationalMods.CORN_DELIGHT.modid)
                .whenModLoaded(RationalMods.CONFECTIONERY.modid));

        create("classic_corn_dog", b -> b
                .require(ItemRegistry.CORN_DOG)
                .require(FluidRegistry.KETCHUP.get(), 10)
                .output(ItemRegistry.CLASSIC_CORN_DOG)
                .whenModLoaded(RationalMods.CORN_DELIGHT.modid)
                .whenModLoaded(RationalMods.BITTERBALLEN.modid));
    }
}
