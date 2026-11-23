package com.chemiofitor.create.rationality.index;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class CRTagKeys {
    public static final class Items {
        public static final TagKey<Item> PULPIFIABLE = create("create", "pulpifiable");

        public static final TagKey<Item> COLOR_PAPER = create("rationality", "color_paper");

        public static final TagKey<Item> ALKALINE = create("rationality", "alkaline");

        public static final TagKey<Item> OIL_PRESSING_RAW_MATERIAL = create("rationality", "oil_pressing_raw_material");

        public static final TagKey<Item> SUGAR_RAW_MATERIAL = create("rationality", "sugar_raw_material");

        public static final TagKey<Item> WOOD_DUST = create("c", "dusts/wood");
        public static final TagKey<Item> BUCKETS = create("c", "buckets");

        private static TagKey<Item> create(String namespace, String id) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(namespace, id));
        }
    }

    public static final class Blocks {

        private static TagKey<Block> create(String namespace, String id) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(namespace, id));
        }
    }

    public static final class Fluids {
        public static final TagKey<Fluid> PLANTOIL = create("c", "plantoil");
        public static final TagKey<Fluid> ETHANOL = create("c", "ethanol");
        public static final TagKey<Fluid> HONEY = create("c", "honey");

        public static final TagKey<Fluid> PULP = create("rationality", "pulp");
        public static final TagKey<Fluid> FINE_PULP = create("rationality", "fine_pulp");
        public static final TagKey<Fluid> SYRUP = create("rationality", "syrup");
        public static final TagKey<Fluid> ASH_LYE = create("rationality", "ash_lye");
        public static final TagKey<Fluid> BLACK_LIQUOR = create("rationality", "black_liquor");
        public static final TagKey<Fluid> CONCENTRATED_BLACK_LIQUOR = create("rationality", "concentrated_black_liquor");
        public static final TagKey<Fluid> LYE = create("rationality", "lye");
        public static final TagKey<Fluid> LIME_MILK = create("rationality", "lime_milk");
        public static final TagKey<Fluid> GLYCEROL = create("rationality", "glycerol");
        public static final TagKey<Fluid> YEAST_LIQUID = create("rationality", "yeast_liquid");
        public static final TagKey<Fluid> RAW_YEAST_LIQUID = create("rationality", "raw_yeast_liquid");


        private static TagKey<Fluid> create(String namespace, String id) {
            return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(namespace, id));
        }
    }
}
