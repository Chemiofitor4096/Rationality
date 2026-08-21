package com.chemiofitor.create.rationality.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import javax.annotation.Nullable;

public class FuelItem extends Item {
    public final int burnTime;

    public FuelItem(Item.Properties properties) {
        super(properties);
        this.burnTime = 100;
    }

    public FuelItem(Item.Properties properties, int burnTime) {
        super(properties);
        this.burnTime = burnTime;
    }

    public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
        return this.burnTime;
    }
}
