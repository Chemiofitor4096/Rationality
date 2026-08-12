package com.chemiofitor.create.rationality.data;

import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.data.provider.CRRecipeProvider;
import com.chemiofitor.create.rationality.index.CRFluids;
import com.chemiofitor.create.rationality.index.CRTagKeys;
import com.jesz.createdieselgenerators.CDGItems;
import com.simibubi.create.AllTags;
import com.tterrag.registrate.providers.ProviderType;
import net.mcreator.createconfectionery.init.CreateConfectioneryModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class CRDatagen {
        public static void gatherDataHighPriority(GatherDataEvent event) {
                addExtraRegistrateData();
        }

        public static void gatherData(GatherDataEvent event) {
                DataGenerator generator = event.getGenerator();
                PackOutput output = generator.getPackOutput();
                CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

                if (event.includeServer()) {
                        event.addProvider(new CRRecipeProvider(output, lookupProvider));
                        CRRecipeProvider.registerAllProcessing(generator, output, lookupProvider);
                }
        }

        private static void addExtraRegistrateData() {
                CreateRationality.REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
                        BiConsumer<String, String> add = provider::add;

                        add.accept("tag.item.rationality.color_paper", "Color Paper");
                        add.accept("tag.item.rationality.alkaline", "Alkaline");
                        add.accept("tag.item.rationality.oil_pressing_raw_material", "Oil Pressing Raw Material");
                        add.accept("tag.item.rationality.sugar_raw_material", "Sugar Raw Material ");

                        add.accept("tag.fluid.rationality.lye", "Lye");

                        add.accept("rationality.recipe.pulping", "Pulping");
                        add.accept("rationality.recipe.forming", "Fluid Forming");
                        add.accept("recipe.assembly.headbox_forming_fluid", "Fluid Forming %1$s");

                        add.accept("rationality.tooltip.not_consume", "Not Consume");

                        add.accept("itemGroup.rationality.base", "Create Rationality");
                });

                CreateRationality.REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, provider -> {
                        provider.addTag(CRTagKeys.Items.ALKALINE)
                                        .add(Items.BONE_MEAL);

                        provider.addTag(CRTagKeys.Items.BUCKETS)
                                        .add(CRFluids.FINE_PULP.getBucket().orElseThrow())
                                        .add(CRFluids.PULP.getBucket().orElseThrow());

                        provider.addTag(CRTagKeys.Items.OIL_PRESSING_RAW_MATERIAL)
                                        .addOptional(com.pyzpre.createbitterballen.index.ItemRegistry.CRUSHED_SUNFLOWER_SEEDS
                                                        .getId())
                                        .addOptional(BuiltInRegistries.ITEM.getKey(ModItems.CABBAGE_SEEDS.get()));

                        provider.addTag(CRTagKeys.Items.SUGAR_RAW_MATERIAL)
                                        .add(Items.SUGAR_CANE)
                                        .add(Items.BEETROOT)
                                        .add(Items.APPLE)
                                        .add(Items.SWEET_BERRIES)
                                        .add(Items.CHORUS_FRUIT)
                                        .addOptional(cn.mcmod.corn_delight.item.ItemRegistry.CORN.getId());

                        provider.addTag(AllTags.AllItemTags.PULPIFIABLE.tag)
                                        .addOptional(CDGItems.WOOD_CHIPS.getId())
                                        .addOptional(BuiltInRegistries.ITEM.getKey(ModItems.TREE_BARK.get()))
                                        .addOptional(BuiltInRegistries.ITEM.getKey(ModItems.STRAW.get()));

                        provider.addTag(AllTags.AllItemTags.UPRIGHT_ON_BELT.tag)
                                        .addOptional(CreateConfectioneryModItems.BLACK_CHOCOLATE_BUCKET.getId())
                                        .addOptional(CreateConfectioneryModItems.CARAMEL_BUCKET.getId())
                                        .addOptional(CreateConfectioneryModItems.RUBY_CHOCOLATE_BUCKET.getId())
                                        .addOptional(CreateConfectioneryModItems.WHITE_CHOCOLATE_BUCKET.getId());
                });
        }
}
