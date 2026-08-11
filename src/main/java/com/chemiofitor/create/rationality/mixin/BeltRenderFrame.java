package com.chemiofitor.create.rationality.mixin;

import com.chemiofitor.create.rationality.index.CRBlocks;
import com.simibubi.create.content.kinetics.belt.BeltRenderer;
import com.simibubi.create.content.logistics.box.PackageItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@OnlyIn(Dist.CLIENT)
@Mixin(value = BeltRenderer.class, remap = false)
public class BeltRenderFrame {

    @Redirect(
            method = "renderItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/logistics/box/PackageItem;isPackage(Lnet/minecraft/world/item/ItemStack;)Z"
            )
    )
    public boolean renderItem(ItemStack stack) {
        if (stack.is(CRBlocks.PAPERMAKING_FRAME.asItem()))
            return true;
        return PackageItem.isPackage(stack);
    }
}
