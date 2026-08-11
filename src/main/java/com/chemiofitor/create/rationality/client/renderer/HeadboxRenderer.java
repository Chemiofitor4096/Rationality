package com.chemiofitor.create.rationality.client.renderer;

import com.chemiofitor.create.rationality.common.block.entity.HeadboxBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.neoforged.neoforge.fluids.FluidStack;

public class HeadboxRenderer extends SmartBlockEntityRenderer<HeadboxBlockEntity> {
    public HeadboxRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(HeadboxBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        renderFluid(be, partialTicks, ms, buffer, light);
    }

    protected void renderFluid(HeadboxBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                               int light) {
        SmartFluidTankBehaviour tank = be.getTank();
        if (tank == null)
            return;

        SmartFluidTankBehaviour.TankSegment primaryTank = tank.getPrimaryTank();
        FluidStack fluidStack = primaryTank.getRenderedFluid();
        float level = primaryTank.getFluidLevel().getValue(partialTicks);

        if (!fluidStack.isEmpty() && level != 0) {
            float y = 1f / 16f;
            float min = 2f / 16f;
            float max = min + (12 / 16f);
            ms.pushPose();
            NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluidStack, min, y, min, max, y,
                    max, buffer, ms, light, true, false);
            ms.popPose();
        }
    }
}
