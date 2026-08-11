package com.chemiofitor.create.rationality;

import com.chemiofitor.create.rationality.client.PollutionIndicatorClient;
import com.chemiofitor.create.rationality.common.block.entity.HeadboxBlockEntity;
import com.chemiofitor.create.rationality.data.CRDatagen;
import com.chemiofitor.create.rationality.index.*;
import com.chemiofitor.create.rationality.network.PollutionStatusPayload;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

@Mod(CreateRationality.MODID)
public class CreateRationality {
    public static final String MODID = "rationality";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
            .setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item))));

    public CreateRationality(IEventBus modEventBus, ModContainer modContainer) {
        CRCreativeModeTabs.register(modEventBus);

        REGISTRATE.registerEventListeners(modEventBus);
        REGISTRATE.defaultCreativeTab(CRCreativeModeTabs.MAIN, "base_tab");

        CRBlocks.register();
        CRBlockEntityTypes.register();
        CRFluids.register();
        CRItems.register();
        CRRecipeTypes.register(modEventBus);

        CRMoldType.register();

        modEventBus.addListener(CreateRationality::registerCapabilities);
        modEventBus.addListener(CreateRationality::registerNetworking);
        modEventBus.addListener(CreateRationality::init);

        modEventBus.addListener(EventPriority.HIGHEST, CRDatagen::gatherDataHighPriority);
        modEventBus.addListener(EventPriority.LOWEST, CRDatagen::gatherData);

        LOGGER.info("The Create Rationality has loaded!");
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        HeadboxBlockEntity.registerCapabilities(event);
    }

    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(CRBlockSpoutingBehaviours::registerDefaults);
    }

    /**
     * 注册网络通道（playToClient：污染状态同步）。handler 仅在客户端收到包时执行，
     * 因此直接操作客户端缓存类，不会在服务端运行。
     */
    public static void registerNetworking(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(PollutionStatusPayload.TYPE, PollutionStatusPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        PollutionIndicatorClient.setPolluted(payload.polluted())));
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
