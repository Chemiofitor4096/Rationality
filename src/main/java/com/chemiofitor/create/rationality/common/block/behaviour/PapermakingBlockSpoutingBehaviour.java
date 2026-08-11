package com.chemiofitor.create.rationality.common.block.behaviour;

import com.chemiofitor.create.rationality.common.block.entity.PapermakingDepotBlockEntity;
import com.chemiofitor.create.rationality.common.recipe.FormingRecipe;
import com.chemiofitor.create.rationality.index.CRBlocks;
import com.chemiofitor.create.rationality.index.CRRecipeTypes;
import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;

import static com.chemiofitor.create.rationality.common.block.entity.HeadboxBlockEntity.matchItemAndFluid;

public class PapermakingBlockSpoutingBehaviour implements BlockSpoutingBehaviour {
    public static final PapermakingBlockSpoutingBehaviour INSTANCE = new PapermakingBlockSpoutingBehaviour();

    @Override
    public int fillBlock(Level level, BlockPos pos, SpoutBlockEntity spout, FluidStack availableFluid, boolean simulate) {
        if (level.getBlockEntity(pos) instanceof PapermakingDepotBlockEntity entity) {
            DepotBehaviour behaviour = entity.getBehaviour(DepotBehaviour.TYPE);
            if (behaviour.isEmpty()) {
                SingleRecipeInput input = new SingleRecipeInput(new ItemStack(CRBlocks.PAPERMAKING_FRAME));
                RecipeHolder<FormingRecipe> formingRecipe = SequencedAssemblyRecipe
                        .getRecipe(level, input, CRRecipeTypes.FORMING.getType(), FormingRecipe.class,
                                matchItemAndFluid(level, availableFluid, input))
                        .filter(fr -> fr.value().getRequiredFluid().test(availableFluid))
                        .orElseGet(() -> {
                            for (RecipeHolder<Recipe<SingleRecipeInput>> recipe : level.getRecipeManager()
                                    .getRecipesFor(CRRecipeTypes.FORMING.getType(), input, level)) {
                                FormingRecipe fr = (FormingRecipe) recipe.value();
                                SizedFluidIngredient requiredFluid = fr.getRequiredFluid();
                                if (requiredFluid.test(availableFluid))
                                    return new RecipeHolder<>(recipe.id(), fr);
                            }
                            return null;
                        });
                if (formingRecipe != null) {
                    List<ItemStack> results = formingRecipe.value().rollResults(level.random);
                    results.removeFirst();
                    results.stream()
                            .map(TransportedItemStack::new)
                            .forEachOrdered(stack -> behaviour.insert(stack, simulate));
                    entity.notifyUpdate();
                    return formingRecipe.value().getRequiredFluid().amount();
                }
            }
        }
        return 0;
    }
}
