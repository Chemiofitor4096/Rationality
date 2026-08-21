package com.chemiofitor.create.rationality.common.block.entity;

import com.chemiofitor.create.rationality.index.CRBlockEntityTypes;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class PapermakingDepotBlockEntity extends DepotBlockEntity {
    public PapermakingDepotBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CRBlockEntityTypes.DEPOT.get(),
                (be, context) -> be.getDepotBehaviour().itemHandler
        );
    }

    public DepotBehaviour getDepotBehaviour() {
        return this.getBehaviour(DepotBehaviour.TYPE);
    }
}
