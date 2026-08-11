package com.chemiofitor.create.rationality.event;

import com.chemiofitor.create.rationality.CreateRationality;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.enchanting.EnchantmentLevelSetEvent;

/**
 * 原版纸在附魔台只能得到 1 级附魔（value=1）：
 * 通过 NeoForge 的 {@link EnchantmentLevelSetEvent} 把附魔选项等级压为 1
 * （附魔费用 1 青金石、可选的附魔等级恒为 1 级）。
 */
@EventBusSubscriber(modid = CreateRationality.MODID)
public class EnchantingHandler {

    @SubscribeEvent
    public static void onEnchantmentLevelSet(EnchantmentLevelSetEvent event) {
        if (event.getItem().is(Items.PAPER)) {
            event.setEnchantLevel(1);
        }

    }
}
