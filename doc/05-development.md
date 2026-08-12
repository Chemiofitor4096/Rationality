# 05 · 开发指南

> 动手改代码前先读本文。涉及注册、数据生成、mixin、网络、跨模组引用的规则与已知坑。

## 1. 常用命令（Windows Git Bash）

```bash
./gradlew build              # 构建
./gradlew runClient          # 启动游戏（手动 QA）
./gradlew runServer          # 无头服务器
./gradlew runData            # 重新生成 src/generated/resources/
./gradlew runGameTestServer  # 运行游戏测试（当前无测试用例）
```

- 依赖版本全部集中在 `gradle.properties`（Create 6.0.10-280、NeoForge 21.1.230、JEI 19.27.0.340 等），**不要**在 build.gradle 硬编码
- 运行目录 `run/`（含下载的依赖模组）已 gitignore
- 调试 mixin 可加 JVM 参数 `-Dmixin.debug.export=true`

## 2. 新增内容的标准流程

1. **注册**：在 `index/` 对应索引类加 `REGISTRATE.xxx("id", ...)` 条目（静态字段 + 末尾 `.register()`）；新索引类需在 `CreateRationality` 构造函数里调用其 `register()`
2. **配方**：在 `data/recipe/` 继承对应的抽象生成器（如 `PulpingRecipeGen`），在 `data/provider/CRXxxRecipeGen` 里 `create(...)`；依赖模组的配方必须 `whenModLoaded(...)`；烧制/合成类配方放 `CRRecipeProvider.buildRecipes`
3. **挂载生成器**：在 `CRDatagen.gatherData`（`CRRecipeProvider.registerAllProcessing`）注册
4. **跑 `./gradlew runData`**，把生成结果提交（`src/generated/resources/` 是仓库的一部分，**禁止手改**）
5. **检查禁令清单**：新配方若与旧配方冲突，在 `RecipeTargets` 登记删除（见 04-recipe-ban.md）
6. **补中文 lang**：`src/main/resources/assets/rationality/lang/zh_cn.json`（手写维护，en_us 由 datagen 生成）

## 3. Mixin（`src/main/resources/rationality.mixins.json`）

| Mixin | 侧 | 作用 |
|---|---|---|
| `BanRecipes` | common | 配方删除（RecipeManager.apply HEAD，见 04-recipe-ban.md） |
| `BeltLowProcessing` | common | 让传送带识别 Headbox 为低空处理装置（`BeltInventory.getBeltProcessingAtSegment` HEAD + cancel） |
| `AssemblyOperatorInject` | common | 让 Create 机械手（`AssemblyOperatorBlockItem.operatesOn` RETURN）可操作造纸台 |
| `SawInject` | common | 动力锯切割附魔书 → 每附魔按等级出 n 张附魔纸（`SawBlockEntity.getRecipes` HEAD + cancel，动态配方 `cutting/enchanted_book`，120 tick；每张一个附魔、等级恒 1、书惩罚均摊） |
| `BeltBlocked` | common | 传送带阻挡相关（配合成形拦截） |
| `BeltRenderFrame` | client | 客户端渲染（造纸框/传送带渲染相关） |

> 附魔台放行/转换类逻辑**已从 mixin 移除**：现在 `enchanting_paper` 是独立物品（`EnchantingPaperItem`，覆写 `getEnchantmentValue`/`supportsEnchantment`/`isEnchantable`），等级压制与累积惩罚走 `event/EnchantingHandler` 事件（见 §4 与 03 §4）。

**警告**：mixins.json 配置了 `"defaultRequire": 1` —— 任何 mixin 注入失败都会**直接崩溃**。改动 Create 内部类（尤其 `BeltInventory`、`RecipeManager`、`AssemblyOperatorBlockItem` 等被注入目标）时，必须同步验证 mixin 目标方法/签名仍存在。

## 4. 事件与网络（黑液污染系统）

| 类 | 类型 | 说明 |
|---|---|---|
| `common/world/PollutionSavedData` | SavedData | 按维度持久化区块污染标记（`rationality_pollution.dat`），24000 刻污染期 |
| `event/CropGrowHandler` | `@EventBusSubscriber` | `CropGrowEvent.Pre`（DO_NOT_GROW）、`BonemealEvent`（取消）、`LevelTickEvent.Post`（每 200 刻清理） |
| `event/EntityTickHandler` | `@EventBusSubscriber` | `EntityTickEvent.Post`：实体在黑液中施加 POISON |
| `event/PollutionSyncHandler` | `@EventBusSubscriber` | `LevelTickEvent.Post`：每 20 刻同步玩家所在区块污染，状态变化才发包，离线玩家记录自动清理 |
| `event/EnchantingHandler` | `@EventBusSubscriber` | `EnchantmentLevelSetEvent`：`enchanting_paper` 附魔等级恒为 1、费用 1 青金石；`PlayerEnchantItemEvent`：每次附魔累加累积惩罚（`REPAIR_COST` ×2+1） |
| `network/PollutionStatusPayload` | CustomPacketPayload | record + StreamCodec(BOOL)；在 `CreateRationality.registerNetworking` 注册 playToClient |
| `client/PollutionIndicatorClient` | `@OnlyIn(CLIENT)` | `FMLClientSetupEvent`（`CreateRationalityClient.setup`）中注册 `ItemProperties` predicate |
| `data/model/PollutionIndicatorModels` | 模型 datagen | 主模型（`_0` 纹理）+ override 子模型（`_1` 纹理），predicate `rationality:pollution` |

新增污染相关逻辑时注意：
- `PollutionSavedData.get(level)` 只应传 `ServerLevel`（客户端没有维度数据存储）
- `BlackLiquorBlock` 的污染标记走 `onPlace`，因此**任何**放置路径（倒桶/流动/扩散）都覆盖；新增流体污染方块可复用该模式
- 网络包 handler 通过 `context.enqueueWork` 切回主线程后再操作客户端状态

## 5. 常用约定

- 资源位置一律用 `CreateRationality.asResource(path)`
- 索引类命名：`CR` + 注册表名（CRBlocks、CRItems、CRFluids、CRBlockEntityTypes、CRRecipeTypes、CRTagKeys…）
- 语言/标签补充走 `CRDatagen.addExtraRegistrateData`（LANG + ITEM_TAGS provider）
- 跨模组引用先查 `RationalMods` 枚举（`isLoaded`），直接引用其他模组的类时用 `whenModLoaded` 门控，避免无依赖崩溃

## 6. 已知注意点

- `HeadboxBlockEntity.registerCapabilities` 只暴露**非上方**方向的流体能力（上方留给注液口/输送）
- `FormingRecipe.getResultItem` 对"输出即输入"的配方（造纸框返还）做了特判：返回最后一个 **100% 概率**输出（跳过可能存在的概率副产）；`HeadboxBlockEntity.fillItem` 使用 `rollResults` 去首位（造纸框）——两处配合支持成形配方携带概率输出
- `PapermakingBlockSpoutingBehaviour` 只认 FORMING 配方；Headbox 则回退原生注液（`FillingBySpout`）与 CDG 铸造（`CastingByHeadbox`）
- 碎浆机的音效目前是临时的（`tickAudio` 有 TODO 注释，复用 MIXING 音效）
- 碱法蒸煮副产的黑液若直接倒掉会污染区块（1 游戏日作物停长）；黑液目前只有 `bone_meal`（混合机：黑液 500mB + 骨头 → 骨粉 ×6）一个消耗配方，且与产出有缺口——浓缩燃烧发电等回收路径仍是开放的扩展点
- `CRRecipeTypes` 中 `CRAFTING_PAPER_COLORING` 枚举被注释（`PaperColoring` 类存在但未注册）；`ENCHANT_PAPER_BINDING` 是第一个启用的自定义 crafting 配方（CustomRecipe，动态输出）
- 附魔纸相关：`SawInject` 注入 `getRecipes` HEAD 后必须自设动态配方（含 duration）；附魔纸不能直接附魔装备（非 EnchantedBookItem）；纸+纸铁砧可合并（与原版附魔书一致）；绑定配方匹配的是纸的 `ENCHANTMENTS` 组件，而锯切产物写入 `STORED_ENCHANTMENTS`——改 `SawInject`/`EnchantPaperBindingRecipe` 时注意两者组件约定（见 03 §4.1）；1.21 中 `DataComponents` 在 `net.minecraft.core.component` 包
- 项目无 CI、无测试框架；改完核心机制建议 `runClient` 手动走一遍：碱法蒸煮 → 成形 → 压制，并验证黑液污染与指示仪

## 7. 验证清单（改完自检）

- [ ] `./gradlew build` 通过
- [ ] 改配方后 `runData` 已跑且 `src/generated/resources/` 已更新
- [ ] mixin 目标未被 Create 更新破坏（`defaultRequire: 1` 崩溃风险）
- [ ] 跨模组引用均有 `RationalMods` / `whenModLoaded` 门控
- [ ] 覆盖配方已在 `RecipeTargets` 登记
- [ ] 新物品/流体/标签已补 `zh_cn.json`
