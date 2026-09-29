# 01 · 项目总览

## 定位

**Create Rationality** 是 Minecraft 1.21.1 的 **Create（机械动力）Addon**，只支持 **NeoForge**（21.1.251）。主题锚点是**化工（实用主义化学）**：把原版"甘蔗合成纸"替换成完整的**工业造纸产线**（碱法蒸煮 → 精浆 → 流浆成形 → 压制）是当前的旗舰产线而非项目边界，并延伸出**化学品回收**（石灰循环、碱液苛化）与**黑液污染**机制；酸碱、气体、建材等新产线的准入走 [07-design-principles.md](07-design-principles.md) 的原则集。

- 模组 ID：`rationality`，包根：`com.chemiofitor.create.rationality`
- Java 21，Parchment 映射（2024.11.17），NeoGradle `net.neoforged.moddev` 2.0.141
- 注册全部走 **CreateRegistrate**（`CreateRationality.REGISTRATE`）；Create 内置注册表（机械臂交互点等）走 `DeferredRegister`（见 05 §2）
- 项目为 git 仓库（main 分支）；无单元测试/Lint，QA 依赖游戏内运行
- ⚠ 工作区存在未提交改动（新制浆配方、机械臂交互点、Steam 'n' Rails 依赖等），详见 doc/README.md「项目状态」

## 技术栈速查

| 项 | 值 |
|---|---|
| Minecraft / Loader | 1.21.1 / NeoForge 21.1.251（仅 NeoForge，无多加载器） |
| Java / 映射 | 21 / Parchment 2024.11.17 |
| 构建 | Gradle wrapper + `net.neoforged.moddev` 2.0.141 |
| 必装依赖 | Create 6.0.10-280、Ponder 1.0.82、Flywheel 1.0.6、Registrate MC1.21-1.3.0+67、JEI 19.27.0.340 |
| 可选依赖 | FD、CDG、Dragons Plus、Bitterballen、Confectionery、Corn Delight、MMLib、RPL、Big Cannons、Enchantment Industry（版本见 `gradle.properties`）；Steam 'n' Rails 0.3.0-beta.2 ⚠（依赖已加入 build.gradle，未提交，暂无联动代码） |

依赖版本集中在 `gradle.properties`，**不要**在 build.gradle 硬编码。

## 目录结构

```
src/main/java/com/chemiofitor/create/rationality/
  CreateRationality.java        # 主类：REGISTRATE、初始化、网络注册、常量
  CreateRationalityClient.java  # 客户端入口（污染指示器 predicate 注册）
  index/                        # 注册索引：CRBlocks/CRItems/CRFluids/CRBlockEntityTypes/
                                #   CRRecipeTypes/CRTagKeys/CRCreativeModeTabs/CRPartialModels/
                                #   CRMoldType/CRBlockSpoutingBehaviours/CRArmInteractionPointTypes⚠
  common/
    block/                      # 5 方块：Headbox/MechanicalPulper/PapermakingDepot/
                                #   PapermakingFrame/BlackLiquorBlock（污染流体方块）
    block/behaviour/            # LowBeltProcessingBehaviour、PapermakingBlockSpoutingBehaviour
    block/entity/               # Headbox / MechanicalPulper / PapermakingDepot 方块实体
    item/                       # ColorPaperItem、EnchantingPaperItem
    recipe/                     # PulpingRecipe、FormingRecipe、crafting/（EnchantPaperBindingRecipe、未启用的 PaperColoring）
    world/                      # PollutionSavedData（黑液污染区块标记，按维度持久化）
  client/
    renderer/                   # HeadboxRenderer、MechanicalPulperRenderer
    visual/                     # PulperVisual（Flywheel）
    PollutionIndicatorClient.java # 污染指示仪 model override predicate + 污染状态缓存
  event/                        # CropGrowHandler、EntityTickHandler、PollutionSyncHandler、EnchantingHandler
  network/                      # PollutionStatusPayload（playToClient 污染状态同步）
  compat/
    jei/                        # JEI 插件、配方分类与动画
    cdg/                        # CastingByHeadbox（CDG 铸造集成）
    RationalMods.java           # 运行时模组检测枚举
  data/
    CRDatagen.java              # 数据生成编排（gatherData / gatherDataHighPriority）
    model/                      # PollutionIndicatorModels（污染指示仪双材质模型）
    recipe/                     # 抽象配方生成基类（FormingRecipeGen、PulpingRecipeGen 等）
    recipe/ban/RecipeTargets.java  # 配方禁令清单（见 04-recipe-ban.md）
    provider/                   # 12 个具体配方生成器（CR*RecipeGen）+ CRRecipeProvider（烧制/合成）
  mixin/                        # 6 个 mixin：5 common + 1 client（见 05-development.md §3）

src/main/resources/
  assets/rationality/           # 模型/纹理/语言文件（zh_cn 手写）
  data/rationality/             # 手写数据包（casting 配方等）
  rationality.mixins.json       # mixin 声明
  META-INF 模板（src/main/templates）→ 生成 neoforge.mods.toml

src/generated/resources/        # runData 输出，**已提交**，禁止手改
doc/                            # 本文档集（索引见 README.md）
```

## 设计理念

**完整原则集与产线蓝图见 [07-design-principles.md](07-design-principles.md)（设计母文档）**——任何立项、改配方、加联动都以它为准。要点速览：

- **主题锚点是化工（实用主义化学）**：造纸是旗舰产线而非边界；一切内容服务于"可读的物料流"（每条流有出处、有代价、有去向）
- **现实逻辑优先**：配方是现实工艺的翻译——允许简化过程，不允许凭空造物（禁令判据见 04；黑液提硫被否即此例）
- **物料守恒与出处**：进料决定出料；数值可简化，守恒关系不可破
- **代价可见**：每条收益挂代价（污染/能耗/时间/物料），且玩家能感知（污染指示仪、浓缩液"处理即安全"）
- **四判据**：功能（玩法相同即合并，钠/钾碱液合一）、密度（注册前 ≥2 条真实下游）、层级（流体宽类别、元素差异放物品、气体走机巧新生）、工艺（温度/压力条件优先于新物料）
- **无孤儿，预留挂账**（06 路线图编号）；
- **依赖分层**：核心线（纸/碱/黑液/糖）零模组依赖；扩展线（气体/酸/压力/分馏）依托**机巧新生**（Create Crafted Beginning）
- **复用 Create 原生机制**：盆地加工、传送带处理、注液口、顺序装配、物品交互全部继承/挂钩 Create 现有 API
- **注册单一入口**：所有内容经 `index/` 索引类 + CreateRegistrate 注册，主类构造函数统一调用
