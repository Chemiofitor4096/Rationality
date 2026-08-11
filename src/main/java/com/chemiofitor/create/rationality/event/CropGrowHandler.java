package com.chemiofitor.create.rationality.event;

import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.common.world.PollutionSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * 黑液污染对作物生长的影响：被污染区块内的作物自然生长与骨粉催熟均被阻止，
 * 并周期清理过期的污染记录以控制数据体积。
 */
@EventBusSubscriber(modid = CreateRationality.MODID)
public class CropGrowHandler {

    /** 后台清理过期污染记录的执行周期（刻），即每 10 秒一次 */
    private static final long CLEANUP_INTERVAL_TICKS = 200L;

    /** 作物处于污染区块时，阻止自然生长（覆盖小麦、甜菜、茎、甘蔗等所有走 CropGrowEvent.Pre 的作物） */
    @SubscribeEvent
    public static void grow(CropGrowEvent.Pre event) {
        if (!(event.getLevel() instanceof ServerLevel level))
            return;
        // 区块必然已加载（事件由区块内作物的随机刻触发），getChunk 仅做哈希查找，无额外 IO
        if (isPollutedChunk(level, level.getChunk(event.getPos()).getPos())) {
            event.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW);
        }
    }

    /** 骨粉走独立的 growCrops 路径（不触发 CropGrowEvent.Pre），需单独取消 */
    @SubscribeEvent
    public static void bonemeal(BonemealEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level))
            return;
        if (isPollutedChunk(level, level.getChunk(event.getPos()).getPos())) {
            event.setCanceled(true);
        }
    }

    /** 周期性清理已失效的污染记录，避免 SavedData 无限增长 */
    @SubscribeEvent
    public static void cleanup(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level))
            return;
        long gameTime = level.getGameTime();
        if (gameTime % CLEANUP_INTERVAL_TICKS != 0)
            return;
        PollutionSavedData.get(level).tick(gameTime);
    }

    private static boolean isPollutedChunk(ServerLevel level, ChunkPos chunkPos) {
        return PollutionSavedData.get(level).isChunkPolluted(chunkPos, level.getGameTime());
    }
}
