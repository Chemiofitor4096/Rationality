package com.chemiofitor.create.rationality.event;

import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.common.world.PollutionSavedData;
import com.chemiofitor.create.rationality.network.PollutionStatusPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 将每个在线玩家所在区块的污染状态定期同步到客户端，供污染指示器切换材质。
 * <p>
 * 仅在状态发生变化时发包：记录每个玩家上次已发送的状态，避免每刻重复发送。
 */
@EventBusSubscriber(modid = CreateRationality.MODID)
public class PollutionSyncHandler {

    /** 同步周期（刻）：每 1 秒检查一次 */
    private static final long SYNC_INTERVAL_TICKS = 20L;

    /** 玩家 UUID -> 最近一次已发送的污染状态，用于状态变化检测 */
    private static final Map<UUID, Boolean> LAST_SENT = new HashMap<>();

    @SubscribeEvent
    public static void sync(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level))
            return;
        if (level.getGameTime() % SYNC_INTERVAL_TICKS != 0)
            return;

        PollutionSavedData data = PollutionSavedData.get(level);
        long gameTime = level.getGameTime();

        for (ServerPlayer player : level.players()) {
            // 玩家所在区块必然已加载，getChunk 仅做哈希查找
            ChunkPos chunkPos = level.getChunk(player.blockPosition()).getPos();
            boolean polluted = data.isChunkPolluted(chunkPos, gameTime);

            Boolean sent = LAST_SENT.get(player.getUUID());
            if (sent == null || sent != polluted) {
                PacketDistributor.sendToPlayer(player, new PollutionStatusPayload(polluted));
                LAST_SENT.put(player.getUUID(), polluted);
            }
        }

        // 清理已离线的玩家记录，防止内存增长
        if (LAST_SENT.size() > level.players().size() + 8) {
            var players = level.getServer().getPlayerList().getPlayers();
            LAST_SENT.keySet().removeIf(uuid -> players.stream().noneMatch(p -> p.getUUID().equals(uuid)));
        }
    }
}
