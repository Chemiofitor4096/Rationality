package com.chemiofitor.create.rationality.event;

import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.common.item.EnchantingPaperItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.enchanting.EnchantmentLevelSetEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEnchantItemEvent;

/**
 * 原版纸在附魔台只能得到 1 级附魔（value=1）：
 * 通过 NeoForge 的 {@link EnchantmentLevelSetEvent} 把附魔选项等级压为 1
 * （附魔费用 1 青金石、可选的附魔等级恒为 1 级）。
 */
@EventBusSubscriber(modid = CreateRationality.MODID)
public class EnchantingHandler {

    @SubscribeEvent
    public static void onEnchantmentLevelSet(EnchantmentLevelSetEvent event) {
        if (event.getItem().getItem() instanceof EnchantingPaperItem) {
            event.setEnchantLevel(1);
        }
    }

    /**
     * 附魔纸每次在附魔台成功附魔 = 一次"非仅重命名操作"：
     * 累积惩罚按原版铁砧公式 ×2+1 增长（1、3、7…2^n−1，封顶 2147483647）。
     * 惩罚存于原版 REPAIR_COST 组件，铁砧/砂轮后续按原版规则自动生效。
     */
    @SubscribeEvent
    public static void onPlayerEnchantItem(PlayerEnchantItemEvent event) {
        ItemStack stack = event.getEnchantedItem();
        if (stack.getItem() instanceof EnchantingPaperItem) {
            stack.set(DataComponents.REPAIR_COST,
                    AnvilMenu.calculateIncreasedRepairCost(stack.getOrDefault(DataComponents.REPAIR_COST, 0)));
        }
    }
}
