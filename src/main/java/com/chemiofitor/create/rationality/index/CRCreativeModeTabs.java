package com.chemiofitor.create.rationality.index;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.chemiofitor.create.rationality.CreateRationality.MODID;
import static com.chemiofitor.create.rationality.CreateRationality.REGISTRATE;

public class CRCreativeModeTabs {
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register("base", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.rationality.base"))
            .icon(CRItems.RAW_PAPER::asStack)
            .displayItems(REGISTRATE.getAll(Registries.ITEM))
            .build());

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
