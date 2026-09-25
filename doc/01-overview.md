# 01 · 项目总览

## 定位

**Create Rationality** 是 Minecraft 1.21.1 的 **Create（机械动力）Addon**，只支持 **NeoForge**（21.1.251）。主题锚点是**化工（实用主义化学）**：把原版"甘蔗合成纸"替换成完整的**工业造纸产线**（碱法蒸煮 → 精浆 → 流浆成形 → 压制）是当前的旗舰产线而非项目边界，并延伸出**化学品回收**（石灰循环、碱液苛化）与**黑液污染**机制；酸碱、气体、建材等新产线的准入走下方设计语言的判据。

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

1. **一切纸都走工业产线**：配方禁令（`BanRecipes` mixin）删除原版/其他模组的"快捷"纸配方，把纸的生产强制收拢到本模组的机械链条上（详见 04-recipe-ban.md）
2. **碱法蒸煮映射现代制浆**：低温简单制浆产出减半；加碱 + 加热的"碱法蒸煮"足量产浆，同时副产**黑液**（现代 Kraft 制浆的废液），黑液成为污染源与待回收资源
3. **化学品循环（碱回收）**：草木灰→灰汁→苛化（+生石灰）→碱液 + 碳酸钙→熔烧→生石灰，石灰在循环中复用；对应现代造纸的碱回收工艺
4. **环境反馈机制**：黑液接触区块产生 1 游戏日污染（作物停长、实体中毒），污染指示仪通过网络同步 + model override 双材质提示玩家——工业化有了代价
5. **复用 Create 原生机制**：盆地加工（BasinOperatingBlockEntity）、传送带处理（BeltProcessingBehaviour）、注液口（Spout）、顺序装配（Sequenced Assembly）全部继承/挂钩 Create 现有 API
6. **可选模组深度整合**：FD/CDG/Bitterballen 等的相关配方通过本产线的中间产物（Syrup、植物油等）串起来（详见 03-recipes.md）
7. **注册单一入口**：所有内容经 `index/` 索引类 + CreateRegistrate 注册，主类构造函数统一调用
8. **化学实用主义四判据**：功能（玩法相同即合并，钠/钾碱液合一）、密度（注册前必须有 ≥2 条真实下游）、层级（流体放宽类别，元素差异用固体物品承载）、工艺（浓度/纯度用加热条件与中间物表达，不注册新流体）
9. **禁令的判据是现实逻辑**：产物在现实中如何获得，配方就应当如何获得——回收/再生是合法来源（脏纸洗纸保留），"非前体一步跳到产物"的直达配方才禁（判据成文与审计方法见 04）
10. **无孤儿，预留挂账**：每个注册物至少一条真实下游；"预留"允许，但必须在路线图挂编号（如 06 F5）
