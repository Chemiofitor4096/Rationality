package com.chemiofitor.create.rationality.client;

import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.index.CRItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 污染指示器的客户端逻辑：注册 model override predicate（rationality:pollution），
 * 并缓存服务端同步的当前区块污染状态。
 * <p>
 * 仿照原版时钟：predicate 在渲染时求值，污染区块返回 1.0、无污染返回 0.0，
 * 使物品模型在两个材质之间切换。
 */
@OnlyIn(Dist.CLIENT)
public final class PollutionIndicatorClient {

    /** 当前玩家所在区块是否污染（由服务端 PollutionStatusPayload 同步） */
    private static boolean polluted = false;

    private PollutionIndicatorClient() {
    }

    /**
     * 注册污染指示器的 model override predicate，客户端启动时调用一次。
     */
    public static void init() {
        ItemProperties.register(CRItems.POLLUTION_INDICATOR.get(),
                CreateRationality.asResource("pollution"),
                (stack, level, entity, seed) -> polluted ? 1.0f : 0.0f);
    }

    /** 接收服务端同步的当前区块污染状态（网络线程调用，由入队包装切回主线程） */
    public static void setPolluted(boolean value) {
        polluted = value;
    }
}
