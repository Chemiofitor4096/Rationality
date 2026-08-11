package com.chemiofitor.create.rationality.common.world;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * 黑液污染的区块标记数据，按维度持久化（SavedData）。
 * <p>
 * 记录每个区块最近一次被黑液接触时的游戏刻。区块在 {@link #POLLUTION_DURATION_TICKS} 刻内
 * 判定为污染，超时后自动失效；为控制数据体积，由外部定时调用 {@link #tick(long)} 清理过期记录。
 * <p>
 * 性能说明：底层用 {@code Long2LongOpenHashMap}，写入（put）与读取（containsKey + get）
 * 均为 O(1)；实例由 {@link net.minecraft.world.level.storage.DimensionDataStorage} 内部缓存，
 * 因此重复 {@link #get(ServerLevel)} 只是哈希表查询，不会触发磁盘 IO。
 */
public class PollutionSavedData extends SavedData {

    /** SavedData 在维度数据存储中的键（对应磁盘文件名 rationality_pollution.dat） */
    public static final String DATA_KEY = "rationality_pollution";

    /** 污染持续刻数：一个游戏日（20 秒 × 20 刻） */
    private static final long POLLUTION_DURATION_TICKS = 24000L;

    /** 区块键 -> 最近一次被黑液污染的游戏刻 */
    private final Long2LongOpenHashMap pollutedChunks = new Long2LongOpenHashMap();

    private PollutionSavedData() {
    }

    /**
     * 获取指定服务端维度的污染数据，不存在则创建。
     * <p>
     * 首次访问会触发一次磁盘读取，之后由 DimensionDataStorage 缓存，查询为 O(1)。
     */
    public static PollutionSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(factory(), DATA_KEY);
    }

    public static SavedData.Factory<PollutionSavedData> factory() {
        return new SavedData.Factory<>(PollutionSavedData::new, PollutionSavedData::load);
    }

    /**
     * 标记某个区块为污染，并刷新其污染计时。
     *
     * @param chunkPos 目标区块
     * @param gameTime 当前游戏刻
     */
    public void markChunkPolluted(ChunkPos chunkPos, long gameTime) {
        pollutedChunks.put(chunkPos.toLong(), gameTime);
        setDirty();
    }

    /**
     * 判断区块当前是否处于污染状态（黑液接触后 {@link #POLLUTION_DURATION_TICKS} 刻内）。
     */
    public boolean isChunkPolluted(ChunkPos chunkPos, long gameTime) {
        long key = chunkPos.toLong();
        if (!pollutedChunks.containsKey(key))
            return false;
        return gameTime - pollutedChunks.get(key) < POLLUTION_DURATION_TICKS;
    }

    /**
     * 周期清理：移除已经失效（超过污染持续时长）的区块记录，防止数据无限增长。
     *
     * @param gameTime 当前游戏刻
     */
    public void tick(long gameTime) {
        if (pollutedChunks.isEmpty())
            return;
        long expireBefore = gameTime - POLLUTION_DURATION_TICKS;
        if (pollutedChunks.values().removeIf(tick -> tick < expireBefore))
            setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        pollutedChunks.long2LongEntrySet().forEach(e -> {
            CompoundTag entry = new CompoundTag();
            entry.putLong("Chunk", e.getLongKey());
            entry.putLong("Tick", e.getLongValue());
            list.add(entry);
        });
        nbt.put("PollutedChunks", list);
        return nbt;
    }

    private static PollutionSavedData load(CompoundTag nbt, HolderLookup.Provider registries) {
        PollutionSavedData data = new PollutionSavedData();
        ListTag list = nbt.getList("PollutedChunks", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            data.pollutedChunks.put(entry.getLong("Chunk"), entry.getLong("Tick"));
        }
        return data;
    }
}
