package com.chemiofitor.create.rationality.compat.jei.category;

import com.chemiofitor.create.rationality.common.recipe.FormingRecipe;
import com.chemiofitor.create.rationality.compat.jei.category.animations.AnimatedHeadbox;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class FormingCategory extends CreateRecipeCategory<FormingRecipe> {
    private final AnimatedHeadbox headbox = new AnimatedHeadbox();

    public FormingCategory(Info<FormingRecipe> info) {
        super(info);
    }

    @Override
    protected void setRecipe(IRecipeLayoutBuilder builder, FormingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 51)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(recipe.getIngredients().getFirst())
                .addRichTooltipCallback((recipeSlotView, tooltip) ->
                        tooltip.add(Component.translatable("rationality.tooltip.not_consume").withStyle(ChatFormatting.GREEN))
                );

        addFluidSlot(builder, 27, 32, recipe.getRequiredFluid());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 132, 51)
                .setBackground(getRenderedSlot(), -1, -1)
                .addItemStack(getResultItem(recipe));
    }

    @Override
    protected void draw(FormingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SHADOW.render(graphics, 62, 57);
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 126, 29);

        headbox.draw(graphics, getBackground().getWidth() / 2 - 13, 40);
    }
}
