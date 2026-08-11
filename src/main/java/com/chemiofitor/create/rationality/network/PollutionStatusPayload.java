package com.chemiofitor.create.rationality.network;

import com.chemiofitor.create.rationality.CreateRationality;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * 服务端 -> 客户端：同步当前玩家所在区块的污染状态，驱动污染指示器切换材质。
 * 仅承载一个布尔值（true=污染，false=无污染），流量极小。
 */
public record PollutionStatusPayload(boolean polluted) implements CustomPacketPayload {

    public static final ResourceLocation ID = CreateRationality.asResource("pollution_status");
    public static final Type<PollutionStatusPayload> TYPE = new Type<>(ID);

    public static final StreamCodec<ByteBuf, PollutionStatusPayload> STREAM_CODEC =
            ByteBufCodecs.BOOL.map(PollutionStatusPayload::new, PollutionStatusPayload::polluted);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
