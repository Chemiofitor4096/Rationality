package com.chemiofitor.create.rationality.data.model;

import com.chemiofitor.create.rationality.CreateRationality;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile;

/**
 * 污染指示器的模型数据生成。
 * <p>
 * 仿照原版时钟 / Create 剪贴板的 model override 方式：主模型默认使用无污染材质，
 * 当 predicate {@code rationality:pollution} 为 1.0 时切换为污染材质子模型。
 */
public class PollutionIndicatorModels {

    /** model override 的 predicate key：rationality:pollution */
    public static final ResourceLocation POLLUTION = CreateRationality.asResource("pollution");

    private PollutionIndicatorModels() {
    }

    /**
     * 生成主模型（无污染材质）与 override 子模型（污染材质）。
     *
     * @return 主模型 builder，Registrate 据此输出 {@code item/pollution_indicator.json}
     */
    public static ItemModelBuilder build(DataGenContext<Item, Item> ctx, RegistrateItemModelProvider provider) {
        // 污染态子模型（predicate=1）：item/generated + 污染纹理
        provider.getBuilder("pollution_indicator_1")
                .parent(new UncheckedModelFile("item/generated"))
                .texture("layer0", CreateRationality.asResource("item/pollution_indicator_1"));

        // 主模型（无污染，predicate=0）：item/generated + 无污染纹理，并携带 override
        // 纹理命名为 _0/_1，故手动构建主模型，而非 provider.generated()（后者要求 item/<name> 纹理存在）
        return provider.getBuilder("pollution_indicator")
                .parent(new UncheckedModelFile("item/generated"))
                .texture("layer0", CreateRationality.asResource("item/pollution_indicator_0"))
                .override()
                .predicate(POLLUTION, 1.0f)
                .model(new UncheckedModelFile(CreateRationality.asResource("item/pollution_indicator_1")))
                .end();
    }
}
