package com.chemiofitor.create.rationality.index;

import com.chemiofitor.create.rationality.common.block.behaviour.PapermakingBlockSpoutingBehaviour;
import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour;

public class CRBlockSpoutingBehaviours {
    public static void registerDefaults() {
        BlockSpoutingBehaviour.BY_BLOCK.register(CRBlocks.PAPERMAKING_DEPOT.get(), PapermakingBlockSpoutingBehaviour.INSTANCE);
    }
}
