package com.chemiofitor.create.rationality.common.recipe.crafting;

import com.chemiofitor.create.rationality.index.CRItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

public class PaperColoring extends CustomRecipe {
    public PaperColoring(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int i = 0;
        int j = 0;
        for(int k = 0; k < input.size(); k++) {
            ItemStack itemstack = input.getItem(k);
            if (!itemstack.isEmpty()) {
                if (itemstack.is(Items.PAPER)) {
                    i++;
                } else {
                    if (!itemstack.is(Tags.Items.DYES)) {
                        return false;
                    }
                    j++;
                }

                if (j > 1 || i > 1) {
                    return false;
                }
            }
        }
        return i == 1 && j == 1;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider provider) {
        ItemStack itemstack = ItemStack.EMPTY;
        DyeColor dyecolor = DyeColor.WHITE;

        for(int i = 0; i < input.size(); ++i) {
            ItemStack itemstack1 = input.getItem(i);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.is(Items.PAPER)) {
                    itemstack = itemstack1;
                } else {
                    DyeColor tmp = DyeColor.getColor(itemstack1);
                    if (tmp != null) {
                        dyecolor = tmp;
                    }
                }
            }
        }

        Item paper = CRItems.COLOR_PAPERS.get(dyecolor).asItem();
        return itemstack.transmuteCopy(paper, 1);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    public @Nullable RecipeSerializer<?> getSerializer() {
        return null; //CRRecipeTypes.CRAFTING_PAPER_COLORING.getSerializer();
    }
}
