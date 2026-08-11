package com.chemiofitor.create.rationality.mixin;

import com.chemiofitor.create.rationality.index.CRBlocks;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AssemblyOperatorBlockItem.class, remap = false)
public class AssemblyOperatorInject {
    @Inject(
            method = "operatesOn",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    public void operate(LevelReader world, BlockPos pos, BlockState placedOnState, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || CRBlocks.PAPERMAKING_DEPOT.has(placedOnState));
    }
}
