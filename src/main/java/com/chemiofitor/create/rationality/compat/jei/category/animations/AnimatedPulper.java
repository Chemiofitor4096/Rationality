package com.chemiofitor.create.rationality.compat.jei.category.animations;

import com.chemiofitor.create.rationality.index.CRBlocks;
import com.chemiofitor.create.rationality.index.CRPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import plus.dragons.createdragonsplus.util.CodeReference;

@CodeReference(targets = "com.simibubi.create.compat.jei.category.animations.AnimatedMixer", source = "create", license = "mit")
public class AnimatedPulper extends AnimatedKinetics {
    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 200);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
        int scale = 23;

        blockElement(cogwheel())
                .rotateBlock(0, getCurrentAngle() * 2, 0)
                .atLocal(0, 0, 0)
                .scale(scale)
                .render(graphics);

        blockElement(CRBlocks.MECHANICAL_PULPER.getDefaultState())
                .atLocal(0, 0, 0)
                .scale(scale)
                .render(graphics);

        float animation = ((Mth.sin(AnimationTickHolder.getRenderTime() / 32f) + 1) / 5) + .5f;

        blockElement(CRPartialModels.MECHANICAL_PULPER_POLE)
                .atLocal(0, animation, 0)
                .scale(scale)
                .render(graphics);

        blockElement(CRPartialModels.MECHANICAL_PULPER_HEAD)
                .rotateBlock(0, getCurrentAngle() * 4, 0)
                .atLocal(0, animation, 0)
                .scale(scale)
                .render(graphics);

        blockElement(AllBlocks.BASIN.getDefaultState())
                .atLocal(0, 1.65, 0)
                .scale(scale)
                .render(graphics);

        matrixStack.popPose();
    }
}
