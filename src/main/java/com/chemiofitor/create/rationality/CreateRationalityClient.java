package com.chemiofitor.create.rationality;

import com.chemiofitor.create.rationality.client.PollutionIndicatorClient;
import com.chemiofitor.create.rationality.index.CRPartialModels;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = CreateRationality.MODID, dist = Dist.CLIENT)
public class CreateRationalityClient {
    public CreateRationalityClient(IEventBus modEventBus, ModContainer container) {
        CRPartialModels.init();
        modEventBus.addListener(CreateRationalityClient::setup);
    }

    public static void setup(final FMLClientSetupEvent event) {
        event.enqueueWork(PollutionIndicatorClient::init);
    }
}
