package com.chemiofitor.create.rationality.compat.cdg;

import com.jesz.createdieselgenerators.CDGRecipes;
import com.jesz.createdieselgenerators.content.molds.CastingRecipe;
import com.jesz.createdieselgenerators.content.molds.MoldItem;
import com.simibubi.create.content.fluids.spout.FillingBySpout;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.List;

public class CastingByHeadbox {

    public static List<ItemStack> processItem(Level level, int requiredAmount, ItemStack stack, FluidStack availableFluid) {
        for (RecipeHolder<Recipe<RecipeInput>> recipe : level.getRecipeManager().getAllRecipesFor(CDGRecipes.CASTING.getType())) {
            CastingRecipe castingRecipe =  (CastingRecipe) recipe.value();
            NonNullList<SizedFluidIngredient> fluidIngredients = castingRecipe.getFluidIngredients();
            if (fluidIngredients.size() == 1
                    && fluidIngredients.getFirst().test(availableFluid)
                    && MoldItem.getMold(stack) == castingRecipe.moldType) {
                List<ItemStack> results = new ArrayList<>();
                results.add(stack.copy());
                results.addAll(castingRecipe.rollResults(level.random));
                availableFluid.shrink(requiredAmount);
                stack.shrink(1);
                return results;
            }
        }
        return List.of(FillingBySpout.fillItem(level, requiredAmount, stack, availableFluid));
    }

    public static boolean canBeProcessing(Level world, ItemStack stack) {
        if (stack.getItem() instanceof MoldItem) {
            for (RecipeHolder<Recipe<RecipeInput>> recipe : world.getRecipeManager().getAllRecipesFor(CDGRecipes.CASTING.getType())) {
                CastingRecipe castingRecipe =  (CastingRecipe) recipe.value();
                return MoldItem.getMold(stack) == castingRecipe.moldType;
            }
        }
        return false;
    }

    public static int getRequiredAmountForItem(Level world, ItemStack stack, FluidStack availableFluid) {
        if (stack.getItem() instanceof MoldItem) {
            for (RecipeHolder<Recipe<RecipeInput>> recipe : world.getRecipeManager().getAllRecipesFor(CDGRecipes.CASTING.getType())) {
                CastingRecipe castingRecipe =  (CastingRecipe) recipe.value();
                NonNullList<SizedFluidIngredient> fluidIngredients = castingRecipe.getFluidIngredients();
                if (fluidIngredients.size() == 1
                        && fluidIngredients.getFirst().test(availableFluid)
                        && MoldItem.getMold(stack) == castingRecipe.moldType) {
                    return fluidIngredients.getFirst().amount();
                }
            }
        }
        return FillingBySpout.getRequiredAmountForItem(world, stack, availableFluid);
    }
}
