package com.chemiofitor.create.rationality.compat;

import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

public enum RationalMods {
    DIESEL_GENERATORS("createdieselgenerators"),
    CONFECTIONERY("create_confectionery"),
    BITTERBALLEN("create_bic_bit"),
    CANNONS("createbigcannons"),

    FARMERS_DELIGHT("farmersdelight"),
    CORN_DELIGHT("corn_delight"),
    BUTCHER_CRAFT("butchercraft")
    ;
    public final String modid;
    public final boolean isLoaded;

    public ModLoadedCondition condition() {
        return new ModLoadedCondition(modid);
    }

    RationalMods(String modid) {
        this.modid = modid;
        this.isLoaded = ModList.get().isLoaded(modid);
    }
}
