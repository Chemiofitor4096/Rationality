package com.chemiofitor.create.rationality.compat.jei.category.animations;

import com.chemiofitor.create.rationality.index.CRBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.kinetics.belt.BeltRenderer;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.gui.GuiGraphics;

public class AnimatedHeadbox extends AnimatedKinetics {
    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 100);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
        int scale = 20;

        blockElement(CRBlocks.HEADBOX.get().defaultBlockState())
                .scale(scale)
                .render(graphics);

        for (boolean bottom : Iterate.trueAndFalse) {
            blockElement(BeltRenderer.getBeltPartial(false, true, false, bottom))
                    .rotate(0, 90, 0)
                    .atLocal(-1, 1, 1)
                    .scale(scale)
                    .render(graphics);

            blockElement(BeltRenderer.getBeltPartial(false, false, false, bottom))
                    .rotate(0, 90, 0)
                    .atLocal(0, 1, 1)
                    .scale(scale)
                    .render(graphics);

            blockElement(BeltRenderer.getBeltPartial(false, false, true, bottom))
                    .rotate(0, 90, 0)
                    .atLocal(1, 1, 1)
                    .scale(scale)
                    .render(graphics);
        }

        blockElement(AllPartialModels.BELT_PULLEY)
                .rotate(90, 0, 0)
                .atLocal(-1, 0, 0)
                .scale(scale)
                .render(graphics);

        blockElement(AllPartialModels.BELT_PULLEY)
                .rotate(90, 0, 0)
                .atLocal(1, 0, 0)
                .scale(scale)
                .render(graphics);

        matrixStack.popPose();
    }
}
