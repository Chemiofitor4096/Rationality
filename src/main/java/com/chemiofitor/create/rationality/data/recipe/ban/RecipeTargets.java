package com.chemiofitor.create.rationality.data.recipe.ban;

import com.chemiofitor.create.rationality.compat.RationalMods;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class RecipeTargets {
    private static final Set<ResourceLocation> RECIPES = new HashSet<>();

    static {
        add("minecraft", "paper");
        add("minecraft", "sugar_from_sugar_cane");
        add("minecraft", "sugar_from_honey_bottle");

        add("create", "pressing/cardboard");
        add("create", "mixing/cardboard_pulp");

        add("create", "crafting/materials/red_sand_paper");
        add("create", "crafting/materials/sand_paper");
        add("create", "crafting/schematics/empty_schematic");

        add("create", "milling/sugar_cane");
        add("create", "pressing/sugar_cane");

        add("farmersdelight", "paper_from_tree_bark");
        if (RationalMods.BITTERBALLEN.isLoaded) {
            add("farmersdelight", "integration/create/mixing/tomato_sauce_from_mixing");
        }

        add("createdieselgenerators", "bulk_fermenting/pulp");

        // sugar + water -> ethanol
        add("createdieselgenerators", "bulk_fermenting/fermentable");
        add("createdieselgenerators", "basin_fermenting/fermentable");
        add("createdieselgenerators", "basin_fermenting/fermented_spider_eye");

        // plant oil
        add("createdieselgenerators", "compacting/plant_oil");

        // biodiesel
        add("createdieselgenerators", "mixing/biodiesel");

        // corn -> paper
        add("corn_delight", "paper_from_corncob");

        // 焦糖阶梯（2026-09-26）：糖→焦糖是现实工艺（焦化），Confectionery 原版 caramel_recipe 已撤禁，
        // 作为阶梯第二级使用（糖浆→糖→焦糖）
        add("corn_delight", "caramel_popcorn");

        if (RationalMods.DIESEL_GENERATORS.isLoaded) {
            add("create_bic_bit", "mixing/frying_oil");
        }
    }

    private static void add(String namespace, String path) {
        RECIPES.add(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    public static Set<ResourceLocation> getRecipes() {
        return RECIPES;
    }
}
