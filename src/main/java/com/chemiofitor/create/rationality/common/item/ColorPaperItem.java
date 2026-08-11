package com.chemiofitor.create.rationality.common.item;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

public class ColorPaperItem extends Item {
    private final  DyeColor color;

    public ColorPaperItem(DyeColor color, Properties properties) {
        super(properties);
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }
}
