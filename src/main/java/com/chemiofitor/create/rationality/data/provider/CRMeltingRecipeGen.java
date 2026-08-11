package com.chemiofitor.create.rationality.data.provider;

import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.compat.RationalMods;
import com.chemiofitor.create.rationality.index.CRFluids;
import com.simibubi.create.AllFluids;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.mcreator.createconfectionery.init.CreateConfectioneryModFluids;
import net.mcreator.createconfectionery.init.CreateConfectioneryModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import rbasamoyai.createbigcannons.crafting.foundry.MeltingRecipe;
import rbasamoyai.createbigcannons.index.CBCRecipeTypes;

import java.util.concurrent.CompletableFuture;

public class CRMeltingRecipeGen extends StandardProcessingRecipeGen<MeltingRecipe> {
    public CRMeltingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateRationality.MODID);
        create("lava", b -> b.whenModLoaded(RationalMods.CANNONS.modid)
                .require(Items.COBBLESTONE)
                .output(Fluids.LAVA, 1000)
                .requiresHeat(HeatCondition.SUPERHEATED));

        create("chocolate", b -> b.whenModLoaded(RationalMods.CANNONS.modid)
                .require(AllItems.BAR_OF_CHOCOLATE)
                .output(AllFluids.CHOCOLATE.get(), 250)
                .requiresHeat(HeatCondition.HEATED));

        create("honey", b -> b.whenModLoaded(RationalMods.CANNONS.modid)
                .require(Items.HONEY_BLOCK)
                .output(AllFluids.HONEY.get(), 1000)
                .requiresHeat(HeatCondition.HEATED));

        create("black_chocolate", b -> b.whenModLoaded(RationalMods.CANNONS.modid)
                .require(CreateConfectioneryModItems.BAR_OF_BLACK_CHOCOLATE)
                .output(CreateConfectioneryModFluids.BLACK_CHOCOLATE.get(), 250)
                .requiresHeat(HeatCondition.HEATED)
                .whenModLoaded(RationalMods.CONFECTIONERY.modid));

        create("ruby_chocolate", b -> b.whenModLoaded(RationalMods.CANNONS.modid)
                .require(CreateConfectioneryModItems.BAR_OF_RUBY_CHOCOLATE)
                .output(CreateConfectioneryModFluids.RUBY_CHOCOLATE.get(), 250)
                .requiresHeat(HeatCondition.HEATED)
                .whenModLoaded(RationalMods.CONFECTIONERY.modid));

        create("white_chocolate", b -> b.whenModLoaded(RationalMods.CANNONS.modid)
                .require(CreateConfectioneryModItems.BAR_OF_WHITE_CHOCOLATE)
                .output(CreateConfectioneryModFluids.WHITE_CHOCOLATE.get(), 250)
                .requiresHeat(HeatCondition.HEATED)
                .whenModLoaded(RationalMods.CONFECTIONERY.modid));

        create("caramel_from_item", b -> b.whenModLoaded(RationalMods.CANNONS.modid)
                .require(CreateConfectioneryModItems.BAR_OF_CARAMEL)
                .output(CreateConfectioneryModFluids.CARAMEL.get(), 250)
                .requiresHeat(HeatCondition.HEATED)
                .whenModLoaded(RationalMods.CONFECTIONERY.modid));

        create("caramel_from_syrup", b -> b.whenModLoaded(RationalMods.CANNONS.modid)
                .require(CRFluids.SYRUP.get(), 1000)
                .output(CreateConfectioneryModFluids.CARAMEL.get(), 500)
                .requiresHeat(HeatCondition.HEATED)
                .whenModLoaded(RationalMods.CONFECTIONERY.modid));
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CBCRecipeTypes.MELTING;
    }
}
