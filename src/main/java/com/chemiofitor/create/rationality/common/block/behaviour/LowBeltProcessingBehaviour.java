package com.chemiofitor.create.rationality.common.block.behaviour;

import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;

public class LowBeltProcessingBehaviour extends BeltProcessingBehaviour {
    public static final BehaviourType<LowBeltProcessingBehaviour> TYPE = new BehaviourType<>();

    public LowBeltProcessingBehaviour(SmartBlockEntity be) {
        super(be);
    }

    @Override
    public BehaviourType<?> getType() {
        return LowBeltProcessingBehaviour.TYPE;
    }
}
