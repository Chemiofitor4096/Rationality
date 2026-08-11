package com.chemiofitor.create.rationality.compat.jei.category;

import com.chemiofitor.create.rationality.compat.jei.category.animations.AnimatedHeadbox;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class AssemblySpoutingCategory extends SequencedAssemblySubCategory {
    AnimatedHeadbox headbox;

    public AssemblySpoutingCategory() {
        super(25);
        headbox = new AnimatedHeadbox();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SequencedRecipe<?> recipe, IFocusGroup focuses, int x) {
        SizedFluidIngredient fluidIngredient = recipe.getRecipe()
                .getFluidIngredients()
                .getFirst();

        CreateRecipeCategory.addFluidSlot(builder, x + 4, 15, fluidIngredient);
    }


    @Override
    public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
        PoseStack ms = graphics.pose();
        headbox.offset = index;
        ms.pushPose();
        ms.translate(-7, 50, 0);
        ms.scale(.75f, .75f, .75f);
        headbox.draw(graphics, getWidth() / 2, 0);
        ms.popPose();
    }
}
