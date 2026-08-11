package com.chemiofitor.create.rationality.index;

import com.chemiofitor.create.rationality.CreateRationality;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class CRPartialModels {
    public static final PartialModel MECHANICAL_PULPER_POLE = block("mechanical_pulper/pole"),
            MECHANICAL_PULPER_HEAD = block("mechanical_pulper/head");

    private static PartialModel block(String path) {
        return PartialModel.of(CreateRationality.asResource("block/" + path));
    }

    public static void init() {

    }
}
