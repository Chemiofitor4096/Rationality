package com.chemiofitor.create.rationality.data.provider;

import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.common.item.ColorPaperItem;
import com.chemiofitor.create.rationality.compat.RationalMods;
import com.chemiofitor.create.rationality.index.CRBlocks;
import com.chemiofitor.create.rationality.index.CRItems;
import com.chemiofitor.create.rationality.index.CRTagKeys;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CRRecipeProvider extends RecipeProvider {
    static final List<ProcessingRecipeGen<?, ?, ?>> GENERATORS = new ArrayList<>();

    public CRRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        for (ItemEntry<ColorPaperItem> value : CRItems.COLOR_PAPERS.values()) {
            colorPaper(output, value.get());
        }

        SimpleCookingRecipeBuilder.blasting(
                Ingredient.of(AllPaletteStoneTypes.LIMESTONE.getBaseBlock().get()),
                RecipeCategory.MISC,
                CRItems.QUICKLIME.asStack(4),
                0.0F,
                200
        ).unlockedBy("has_limestone", RegistrateRecipeProvider.has(AllPaletteStoneTypes.LIMESTONE.getBaseBlock().get()))
                .save(output, location("blasting/quicklime_from_limestone"));

        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(CRItems.CALCIUM_CARBONATE),
                        RecipeCategory.MISC,
                        CRItems.QUICKLIME,
                        0.0F,
                        200
                ).unlockedBy("has_limestone", RegistrateRecipeProvider.has(CRItems.CALCIUM_CARBONATE))
                .save(output, location("blasting/quicklime_from_calcium_carbonate"));

        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(CRTagKeys.Items.PULPIFIABLE),
                RecipeCategory.MISC,
                CRItems.PLANT_ASH.asStack(),
                0.0F,
                100
        ).unlockedBy("has_pulpifiable", RegistrateRecipeProvider.has(CRTagKeys.Items.PULPIFIABLE))
                .save(output, location("smelting/plant_ash"));

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ItemTags.COALS),
                        RecipeCategory.MISC,
                        CRItems.COAL_ASH.asStack(),
                        0.0F,
                        200
                ).unlockedBy("has_coals", RegistrateRecipeProvider.has(ItemTags.COALS))
                .save(output, location("smelting/coal_ash"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AllItems.EMPTY_SCHEMATIC)
                .requires(CRItems.COLOR_PAPERS.get(DyeColor.LIGHT_BLUE))
                .unlockedBy("has_light_blue_paper", RegistrateRecipeProvider.has(CRItems.COLOR_PAPERS.get(DyeColor.LIGHT_BLUE)))
                .save(output, location("crafting/empty_schematic"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CRBlocks.HEADBOX.get())
                .pattern("   ")
                .pattern(" X ")
                .pattern(" A ")
                .define('X', AllBlocks.COPPER_CASING)
                .define('A', Items.IRON_BARS)
                .unlockedBy("has_copper_casing", RegistrateRecipeProvider.has(AllBlocks.COPPER_CASING))
                .save(output, location("crafting/headbox"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CRBlocks.MECHANICAL_PULPER.get())
                .pattern(" X ")
                .pattern(" A ")
                .pattern("BBB")
                .define('X', AllBlocks.COGWHEEL)
                .define('A', AllBlocks.ANDESITE_CASING)
                .define('B', AllItems.PROPELLER)
                .unlockedBy("has_propeller", RegistrateRecipeProvider.has(AllItems.PROPELLER))
                .save(output, location("crafting/pulper"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CRBlocks.PAPERMAKING_FRAME.get())
                .pattern("AXA")
                .pattern("XXX")
                .pattern("AXA")
                .define('X', Items.STRING)
                .define('A', Items.STICK)
                .unlockedBy("has_string", RegistrateRecipeProvider.has(Items.STRING))
                .save(output, location("crafting/frame_from_string"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CRBlocks.PAPERMAKING_FRAME.get())
                .pattern(" X ")
                .pattern("XAX")
                .pattern(" X ")
                .define('X', Items.STICK)
                .define('A', ModItems.CANVAS.get())
                .unlockedBy("has_string", RegistrateRecipeProvider.has(Items.STRING))
                .save(output.withConditions(RationalMods.FARMERS_DELIGHT.condition()), location("crafting/frame_from_canvas"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CRItems.POLLUTION_INDICATOR.get())
                .pattern(" X ")
                .pattern("XAX")
                .pattern(" X ")
                .define('X', AllItems.ZINC_INGOT)
                .define('A', Items.REDSTONE)
                .unlockedBy("has_zinc", RegistrateRecipeProvider.has(AllItems.ZINC_INGOT))
                .save(output, location("crafting/pollution_indicator"));
    }

    private void colorPaper(RecipeOutput output, ColorPaperItem colorPaper) {
        DyeColor color = colorPaper.getColor();
        DyeItem item = DyeItem.byColor(color);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, colorPaper)
                .requires(Items.PAPER)
                .requires(item)
                .unlockedBy("has_" + color.getName() + "_dye", RegistrateRecipeProvider.has(item))
                .save(output, location("crafting/dye_" + color.getName() + "_paper"));
    }

    private ResourceLocation location(String path) {
        return CreateRationality.asResource(path);
    }

    public static void registerAllProcessing(DataGenerator gen, PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        GENERATORS.add(new CRBasinFermentingRecipeGen(output, registries));
        GENERATORS.add(new CRBulkFermentingRecipeGen(output, registries));
        GENERATORS.add(new CRColoringRecipeGen(output, registries));
        GENERATORS.add(new CRCompactingRecipeGen(output, registries));
        GENERATORS.add(new CRCuttingRecipeGen(output, registries));
        GENERATORS.add(new CRFillingRecipeGen(output, registries));
        GENERATORS.add(new CRFormingRecipeGen(output, registries));
        GENERATORS.add(new CRItemApplicationRecipeGen(output, registries));
        GENERATORS.add(new CRMeltingRecipeGen(output, registries));
        GENERATORS.add(new CRMixingRecipeGen(output, registries));
        GENERATORS.add(new CRPressingRecipeGen(output, registries));
        GENERATORS.add(new CRPulpingRecipeGen(output, registries));


        gen.addProvider(true, new DataProvider() {
            @Override
            public @NotNull String getName() {
                return "Create's Processing Recipes";
            }

            @Override
            public @NotNull CompletableFuture<?> run(@NotNull CachedOutput dc) {
                return CompletableFuture.allOf(GENERATORS.stream()
                        .map(gen -> gen.run(dc))
                        .toArray(CompletableFuture[]::new));
            }
        });
    }
}
