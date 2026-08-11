package com.chemiofitor.create.rationality.compat.jei;

import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.common.recipe.FormingRecipe;
import com.chemiofitor.create.rationality.compat.jei.category.FormingCategory;
import com.chemiofitor.create.rationality.compat.jei.category.PulpingCategory;
import com.chemiofitor.create.rationality.index.CRBlocks;
import com.chemiofitor.create.rationality.index.CRRecipeTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;
import plus.dragons.createdragonsplus.util.CodeReference;

import java.util.ArrayList;
import java.util.List;

@CodeReference(targets = "com.simibubi.create.compat.jei.CreateRationalityJEI", source = "create", license = "mit")
@JeiPlugin
public class CreateRationalityJEI implements IModPlugin {
    private static final ResourceLocation ID = CreateRationality.asResource("jei_plugin");

    private final List<CreateRecipeCategory<?>> allCategories = new ArrayList<>();

    private void loadCategories() {
        allCategories.clear();

        CreateRecipeCategory<?> pulping = builder(BasinRecipe.class)
                .addTypedRecipes(CRRecipeTypes.PULPING)
                .catalyst(CRBlocks.MECHANICAL_PULPER::get)
                .catalyst(AllBlocks.BASIN::get)
                .doubleItemIcon(CRBlocks.MECHANICAL_PULPER.get(), AllBlocks.BASIN.get())
                .emptyBackground(177, 103)
                .build(CreateRationality.asResource("pulping"), PulpingCategory::standard);

        CreateRecipeCategory<?> forming = builder(FormingRecipe.class)
                .addTypedRecipes(CRRecipeTypes.FORMING)
                .catalyst(CRBlocks.HEADBOX::get)
                .catalyst(AllBlocks.DEPOT::get)
                .doubleItemIcon(CRBlocks.HEADBOX.get(), AllBlocks.DEPOT.get())
                .emptyBackground(177, 70)
                .build(CreateRationality.asResource("forming"), FormingCategory::new);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        loadCategories();
        registration.addRecipeCategories(allCategories.toArray(IRecipeCategory[]::new));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        allCategories.forEach(c -> c.registerRecipes(registration));
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        allCategories.forEach(c -> c.registerCatalysts(registration));
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    private <T extends Recipe<? extends RecipeInput>> CategoryBuilder<T> builder(Class<T> recipeClass) {
        return new CategoryBuilder<>(recipeClass);
    }

    private class CategoryBuilder<T extends Recipe<?>> extends CreateRecipeCategory.Builder<T> {
        public CategoryBuilder(Class<? extends T> recipeClass) {
            super(recipeClass);
        }

        @Override
        public @NotNull CreateRecipeCategory<T> build(@NotNull ResourceLocation id, CreateRecipeCategory.@NotNull Factory<T> factory) {
            CreateRecipeCategory<T> category = super.build(id, factory);
            allCategories.add(category);
            return category;
        }
    }
}
