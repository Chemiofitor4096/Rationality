package com.chemiofitor.create.rationality.mixin;

import com.chemiofitor.create.rationality.common.block.behaviour.LowBeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BeltProcessingBehaviour.class, remap = false)
public class BeltBlocked {

    @Inject(
            method = "isBlocked",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private static void isNotLowBlocked(BlockGetter world, BlockPos processingSpace, CallbackInfoReturnable<Boolean> cir) {
        var behaviour = BlockEntityBehaviour.get(world, processingSpace.above(1), LowBeltProcessingBehaviour.TYPE);
        if (behaviour != null)
            cir.setReturnValue(false);
    }
}
