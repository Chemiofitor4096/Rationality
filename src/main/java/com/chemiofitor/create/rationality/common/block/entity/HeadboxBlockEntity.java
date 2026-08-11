package com.chemiofitor.create.rationality.common.block.entity;

import com.chemiofitor.create.rationality.common.block.behaviour.LowBeltProcessingBehaviour;
import com.chemiofitor.create.rationality.common.recipe.FormingRecipe;
import com.chemiofitor.create.rationality.compat.RationalMods;
import com.chemiofitor.create.rationality.compat.cdg.CastingByHeadbox;
import com.chemiofitor.create.rationality.index.CRBlockEntityTypes;
import com.chemiofitor.create.rationality.index.CRRecipeTypes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.fluids.spout.FillingBySpout;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.HOLD;
import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.PASS;

public class HeadboxBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public static final int FILLING_TIME = 10;

    protected BeltProcessingBehaviour beltProcessing;

    public int processingTicks;
    public boolean sendSplash;

    SmartFluidTankBehaviour tank;

    public HeadboxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                CRBlockEntityTypes.HEADBOX.get(),
                (be, context) -> {
                    if (context != Direction.UP)
                        return be.getTank().getCapability();
                    return null;
                });
    }

    public SmartFluidTankBehaviour getTank() {
        return tank;
    }

    private FluidStack getCurrentFluidInTank() {
        return tank.getPrimaryHandler().getFluid();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(tank = SmartFluidTankBehaviour.single(this, 1500)
                .forbidExtraction()
                .allowInsertion());

        behaviours.add(beltProcessing = new LowBeltProcessingBehaviour(this)
                .whenItemEnters(this::onItemReceived).whileItemHeld(this::whenItemHeld));
    }

    protected BeltProcessingBehaviour.ProcessingResult onItemReceived(TransportedItemStack transported,
            TransportedItemStackHandlerBehaviour handler) {
        if (handler.blockEntity.isVirtual())
            return PASS;
        if (!canBeForming(level, transported.stack))
            return PASS;
        if (tank.isEmpty())
            return HOLD;
        if (getRequiredAmountForItem(level, transported.stack, getCurrentFluidInTank()) == -1)
            return PASS;
        return HOLD;
    }

    protected BeltProcessingBehaviour.ProcessingResult whenItemHeld(TransportedItemStack transported,
            TransportedItemStackHandlerBehaviour handler) {
        if (processingTicks != -1 && processingTicks != 5)
            return HOLD;
        if (!canBeForming(level, transported.stack))
            return PASS;
        if (tank.isEmpty())
            return HOLD;
        FluidStack fluid = getCurrentFluidInTank();
        int requiredAmountForItem = getRequiredAmountForItem(level, transported.stack, fluid.copy());
        if (requiredAmountForItem == -1)
            return PASS;
        if (requiredAmountForItem > fluid.getAmount())
            return HOLD;

        if (processingTicks == -1) {
            processingTicks = FILLING_TIME;
            notifyUpdate();
            // TODO
            AllSoundEvents.SPOUTING.playOnServer(level, worldPosition, 0.75f, 0.9f + 0.2f * (float) Math.random());
            return HOLD;
        }

        // Process finished
        List<ItemStack> out = fillItem(level, requiredAmountForItem, transported.stack, fluid);
        if (!out.isEmpty()) {
            transported.clearFanProcessingData();
            List<TransportedItemStack> outList = new ArrayList<>();
            TransportedItemStack held = null;

            if (!transported.stack.isEmpty())
                held = transported.copy();

            for (ItemStack stack : out) {
                TransportedItemStack result = transported.copy();
                result.stack = stack;
                outList.add(result);
            }

            handler.handleProcessingOnItem(transported,
                    TransportedItemStackHandlerBehaviour.TransportedResult.convertToAndLeaveHeld(outList, held));
        }

        tank.getPrimaryHandler().setFluid(fluid);
        sendSplash = true;
        notifyUpdate();
        return HOLD;
    }

    public static List<ItemStack> fillItem(Level level, int requiredAmount, ItemStack stack,
            FluidStack availableFluid) {
        FluidStack toFill = availableFluid.copy();
        toFill.setAmount(requiredAmount);

        SingleRecipeInput input = new SingleRecipeInput(stack);

        RecipeHolder<FormingRecipe> formingRecipe = SequencedAssemblyRecipe
                .getRecipe(level, input, CRRecipeTypes.FORMING.getType(), FormingRecipe.class,
                        matchItemAndFluid(level, availableFluid, input))
                .filter(fr -> fr.value().getRequiredFluid().test(toFill))
                .orElseGet(() -> {
                    for (RecipeHolder<Recipe<SingleRecipeInput>> recipe : level.getRecipeManager()
                            .getRecipesFor(CRRecipeTypes.FORMING.getType(), input, level)) {
                        FormingRecipe fr = (FormingRecipe) recipe.value();
                        SizedFluidIngredient requiredFluid = fr.getRequiredFluid();
                        if (requiredFluid.test(toFill))
                            return new RecipeHolder<>(recipe.id(), fr);
                    }
                    return null;
                });

        if (formingRecipe != null) {
            List<ItemStack> results = new ArrayList<>();
            results.add(stack.copy());
            List<ItemStack> rolled = formingRecipe.value().rollResults(level.random);
            if (!rolled.isEmpty())
                rolled.removeFirst(); // 造纸框返还（配方首位输出），原物品已放入 results
            results.addAll(rolled);
            availableFluid.shrink(requiredAmount);
            stack.shrink(1);
            return results;
        }

        if (RationalMods.DIESEL_GENERATORS.isLoaded) {
            return CastingByHeadbox.processItem(level, requiredAmount, stack, availableFluid);
        }

        return List.of(FillingBySpout.fillItem(level, requiredAmount, stack, availableFluid));
    }

    public static Predicate<RecipeHolder<FormingRecipe>> matchItemAndFluid(Level world, FluidStack availableFluid,
            SingleRecipeInput input) {
        return r -> r.value().matches(input, world) && r.value().getRequiredFluid().test(availableFluid);
    }

    public static boolean canBeForming(Level world, ItemStack stack) {
        SingleRecipeInput input = new SingleRecipeInput(stack);

        Optional<RecipeHolder<FormingRecipe>> assemblyFormingRecipe = SequencedAssemblyRecipe.getRecipe(world, input,
                CRRecipeTypes.FORMING.getType(), FormingRecipe.class);
        if (assemblyFormingRecipe.isPresent())
            return true;

        if (CRRecipeTypes.FORMING.find(input, world).isPresent())
            return true;

        boolean flag = FillingBySpout.canItemBeFilled(world, stack);

        if (RationalMods.DIESEL_GENERATORS.isLoaded) {
            flag = flag || CastingByHeadbox.canBeProcessing(world, stack);
        }

        return flag;
    }

    public static int getRequiredAmountForItem(Level world, ItemStack stack, FluidStack availableFluid) {
        SingleRecipeInput input = new SingleRecipeInput(stack);

        Optional<RecipeHolder<FormingRecipe>> assemblyFormingRecipe = SequencedAssemblyRecipe.getRecipe(world, input,
                CRRecipeTypes.FORMING.getType(), FormingRecipe.class, matchItemAndFluid(world, availableFluid, input));

        if (assemblyFormingRecipe.isPresent()) {
            SizedFluidIngredient requiredFluid = assemblyFormingRecipe.get().value().getRequiredFluid();
            if (requiredFluid.ingredient().test(availableFluid))
                return requiredFluid.amount();
        }

        for (RecipeHolder<Recipe<SingleRecipeInput>> recipe : world.getRecipeManager()
                .getRecipesFor(CRRecipeTypes.FORMING.getType(), input, world)) {
            FormingRecipe fillingRecipe = (FormingRecipe) recipe.value();
            SizedFluidIngredient requiredFluid = fillingRecipe.getRequiredFluid();
            if (requiredFluid.ingredient().test(availableFluid))
                return requiredFluid.amount();
        }

        if (RationalMods.DIESEL_GENERATORS.isLoaded) {
            return CastingByHeadbox.getRequiredAmountForItem(world, stack, availableFluid);
        }

        return FillingBySpout.getRequiredAmountForItem(world, stack, availableFluid);
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);

        compound.putInt("ProcessingTicks", processingTicks);
        if (sendSplash && clientPacket) {
            compound.putBoolean("Splash", true);
            sendSplash = false;
        }
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        processingTicks = compound.getInt("ProcessingTicks");

        if (!clientPacket)
            return;
        if (compound.contains("Splash"))
            spawnSplash(tank.getPrimaryTank()
                    .getRenderedFluid());
    }

    public void tick() {
        super.tick();

        FluidStack currentFluidInTank = getCurrentFluidInTank();

        if (processingTicks >= 0) {
            processingTicks--;
        }

        if (level != null && processingTicks >= 8 && level.isClientSide) {
            spawnProcessingParticles(tank.getPrimaryTank().getRenderedFluid());
        }
    }

    protected void spawnProcessingParticles(FluidStack fluid) {
        if (isVirtual() || fluid.isEmpty())
            return;
        Vec3 vec = VecHelper.getCenterOf(worldPosition);
        vec = vec.subtract(0, 8 / 16f, 0);
        ParticleOptions particle = FluidFX.getFluidParticle(fluid);
        if (level != null) {
            level.addAlwaysVisibleParticle(particle, vec.x, vec.y, vec.z, 0, -.1f, 0);
        }
    }

    protected static int SPLASH_PARTICLE_COUNT = 10;

    protected void spawnSplash(FluidStack fluid) {
        if (isVirtual() || fluid.isEmpty())
            return;
        Vec3 vec = VecHelper.getCenterOf(worldPosition);
        vec = vec.subtract(0, 1 - 5 / 16f, 0);
        ParticleOptions particle = FluidFX.getFluidParticle(fluid);
        if (level != null) {
            for (int i = 0; i < SPLASH_PARTICLE_COUNT; i++) {
                Vec3 m = VecHelper.offsetRandomly(Vec3.ZERO, level.random, 0.125f);
                m = new Vec3(m.x, Math.abs(m.y), m.z);
                level.addAlwaysVisibleParticle(particle, vec.x, vec.y, vec.z, m.x, m.y, m.z);
            }
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        invalidateCapabilities();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (level != null) {
            return containedFluidTooltip(tooltip, isPlayerSneaking,
                    level.getCapability(Capabilities.FluidHandler.BLOCK, worldPosition, null));
        }
        return false;
    }
}
