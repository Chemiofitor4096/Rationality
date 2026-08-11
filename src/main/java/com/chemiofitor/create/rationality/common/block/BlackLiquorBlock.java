package com.chemiofitor.create.rationality.common.block;

import com.chemiofitor.create.rationality.common.world.PollutionSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

/**
 * 黑液流体方块。黑液以任何方式（倒桶、流动、扩散、状态更新）进入一个区块时，
 * 将该区块标记为污染，影响该区块内作物的生长。
 */
public class BlackLiquorBlock extends LiquidBlock {

    public BlackLiquorBlock(FlowingFluid fluid, BlockBehaviour.Properties properties) {
        super(fluid, properties);
    }

    /**
     * 方块被放置（黑液到达某位置）时，将所在区块标记为污染。
     * <p>
     * onPlace 在 Level#setBlockState 放置方块时统一触发，因此倒桶、流动、扩散三条
     * 路径都能覆盖；客户端方块不会进入此逻辑（ClientLevel 不是 ServerLevel）。
     */
    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (level instanceof ServerLevel serverLevel) {
            // 区块必然已加载（黑液已放置于此），getChunk 仅做哈希查找，无额外 IO
            PollutionSavedData.get(serverLevel)
                    .markChunkPolluted(serverLevel.getChunk(pos).getPos(), serverLevel.getGameTime());
        }
    }
}
