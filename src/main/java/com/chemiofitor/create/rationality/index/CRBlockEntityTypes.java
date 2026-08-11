package com.chemiofitor.create.rationality.index;

import com.chemiofitor.create.rationality.client.renderer.HeadboxRenderer;
import com.chemiofitor.create.rationality.client.renderer.MechanicalPulperRenderer;
import com.chemiofitor.create.rationality.client.visual.PulperVisual;
import com.chemiofitor.create.rationality.common.block.entity.HeadboxBlockEntity;
import com.chemiofitor.create.rationality.common.block.entity.MechanicalPulperBlockEntity;
import com.chemiofitor.create.rationality.common.block.entity.PapermakingDepotBlockEntity;
import com.simibubi.create.content.logistics.depot.DepotRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.chemiofitor.create.rationality.CreateRationality.REGISTRATE;

public class CRBlockEntityTypes {
    public static final BlockEntityEntry<MechanicalPulperBlockEntity> MECHANICAL_PULPER = REGISTRATE
            .blockEntity("mechanical_pulp", MechanicalPulperBlockEntity::new)
            .visual(() -> PulperVisual::new)
            .validBlocks(CRBlocks.MECHANICAL_PULPER)
            .renderer(() -> MechanicalPulperRenderer::new)
            .register();

    public static final BlockEntityEntry<HeadboxBlockEntity> HEADBOX = REGISTRATE
            .blockEntity("headbox", HeadboxBlockEntity::new)
            .validBlocks(CRBlocks.HEADBOX)
            .renderer(() -> HeadboxRenderer::new)
            .register();

    public static final BlockEntityEntry<PapermakingDepotBlockEntity> DEPOT = REGISTRATE
            .blockEntity("papermaking_frame", PapermakingDepotBlockEntity::new)
            .validBlocks(CRBlocks.PAPERMAKING_DEPOT)
            .renderer(() -> DepotRenderer::new)
            .register();

    public static void register() {}
}
