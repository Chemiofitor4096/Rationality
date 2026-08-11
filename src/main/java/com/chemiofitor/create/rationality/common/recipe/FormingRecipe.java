package com.chemiofitor.create.rationality.common.recipe;

import com.chemiofitor.create.rationality.compat.jei.category.AssemblySpoutingCategory;
import com.chemiofitor.create.rationality.index.CRBlocks;
import com.chemiofitor.create.rationality.index.CRRecipeTypes;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class FormingRecipe extends StandardProcessingRecipe<SingleRecipeInput> implements IAssemblyRecipe {
    public FormingRecipe(ProcessingRecipeParams params) {
        super(CRRecipeTypes.FORMING, params);
    }

    @Override
    public boolean matches(SingleRecipeInput inv, Level level) {
        return ingredients.getFirst().test(inv.getItem(0));
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 3;
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 1;
    }

    public SizedFluidIngredient getRequiredFluid() {
        if (fluidIngredients.isEmpty())
            throw new IllegalStateException("Filling Recipe has no fluid ingredient!");
        return fluidIngredients.getFirst();
    }

    @Override
    public void addAssemblyIngredients(List<Ingredient> list) {}

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        ItemStack stack = super.getResultItem(provider);
        if (ingredients.getFirst().test(stack)) {
            // 主产物是最后一个 100% 概率输出（其后可能跟概率副产，如纸边角料）
            List<ProcessingOutput> outputs = getRollableResults();
            for (int i = outputs.size() - 1; i >= 0; i--)
                if (outputs.get(i).getChance() == 1)
                    return outputs.get(i).getStack();
            return outputs.getLast().getStack();
        }
        return stack;
    }

    @Override
    public void addAssemblyFluidIngredients(List<SizedFluidIngredient> list) {
        list.add(getRequiredFluid());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getDescriptionForAssembly() {
        List<FluidStack> matchingFluidStacks = Arrays.asList(fluidIngredients.getFirst().getFluids());
        if (matchingFluidStacks.isEmpty()) {
            return Component.literal("Invalid");
        }
        return CreateLang.translateDirect("recipe.assembly.headbox_forming_fluid",
                matchingFluidStacks.getFirst().getHoverName().getString());
    }


    @Override
    public void addRequiredMachines(Set<ItemLike> list) {
        list.add(CRBlocks.HEADBOX.get());
    }

    @Override
    public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
        return () -> AssemblySpoutingCategory::new;
    }
}
