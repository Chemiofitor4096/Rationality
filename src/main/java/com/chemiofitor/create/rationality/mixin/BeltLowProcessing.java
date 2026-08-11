package com.chemiofitor.create.rationality.mixin;

import com.chemiofitor.create.rationality.common.block.behaviour.LowBeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BeltInventory.class, remap = false)
public class BeltLowProcessing {
    @Final
    @Shadow
    BeltBlockEntity belt;

    @Inject(
            method = "getBeltProcessingAtSegment",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    public void getBeltProcessing(int segment, CallbackInfoReturnable<BeltProcessingBehaviour> cir) {
        var behaviour = BlockEntityBehaviour.get(belt.getLevel(), BeltHelper.getPositionForOffset(belt, segment)
                .above(1), LowBeltProcessingBehaviour.TYPE);
        if (behaviour != null)
            cir.setReturnValue(behaviour);
    }
}
