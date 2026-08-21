package com.chemiofitor.create.rationality.index;

import com.chemiofitor.create.rationality.CreateRationality;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CRArmInteractionPointTypes {
    public static final DeferredRegister<ArmInteractionPointType> TYPES = DeferredRegister.create(CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE, CreateRationality.MODID);

    public static final DeferredHolder<ArmInteractionPointType, DepotType> DEPOT = TYPES.register("papermaking_depot", DepotType::new);

    public static void register(IEventBus bus) {
        TYPES.register(bus);
    }

    public static class DepotType extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return CRBlocks.PAPERMAKING_DEPOT.has(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new AllArmInteractionPointTypes.DepotPoint(this, level, pos, state);
        }
    }
}
