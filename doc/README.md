# Create Rationality — 文档索引

> 本文档集面向开发者/审阅者，内容以 `src/main/java` 与 `src/generated/resources/` 源码核实为准，禁止凭记忆编写。
> 项目：**Create Rationality** —— Minecraft 1.21.1 NeoForge 上的 Create Addon，工业造纸产线（"Craft papermaking your industrial way!"），含碱法蒸煮、化学品回收与黑液污染机制。

## 项目状态

| 项 | 值 |
|---|---|
| 模组 ID / 版本 | `rationality` / 1.0.0（`gradle.properties`） |
| 平台 | Minecraft 1.21.1 · NeoForge **21.1.251** · Java 21 · Parchment 2024.11.17 |
| 构建 | Gradle wrapper · `net.neoforged.moddev` 2.0.141 |
| 仓库 | git（main 分支）；无单元测试/Lint，QA 走 `runClient` / `runGameTestServer` |
| 内容核实基准 | `src/main/java` + `src/generated/resources/`（**当前工作区含未提交改动，见下**） |

### ⚠ 工作区未提交改动（WIP，提交后需同步本文档集）

`git status` 显示以下改动尚未提交，本文档已按"已提交状态"编写，涉及处用 ⚠ 标注：

| 改动 | 涉及文档章节 |
|---|---|
| 新增制浆配方：灰汁/碱液加热制浆 ×2、碱液精浆 ×1（`CRPulpingRecipeGen`） | 03 §2.1 |
| 新增机械臂交互点 `CRArmInteractionPointTypes`（造纸台可被机械臂抓取） | 01 目录树、05 §2 |
| `PapermakingDepotBlockEntity` 手动注册 ItemHandler capability | 05 §6 |
| 新增 Steam 'n' Rails 依赖、NeoForge 21.1.230 → **21.1.251**（`build.gradle`/`gradle.properties`；251 是 1.21.1 线最后一个版本，为 CraftGraph 的 `[21.1.251,)` 要求） | 01 技术栈 |
| 开发环境依赖 **CraftGraph**（`dev.craftgraph:craftgraph`，`runtimeOnly`，让 AI 读运行中游戏的配方；客户端侧，不进构建产物） | 05 §1 |
| 语言改名：`钾碱液桶` → `碱液桶`（`zh_cn.json`） | 02 §5 |
| **F1 黑液回收闭环**（方向 B 已定案）：浓缩黑液（安全形态，不污染）+ 浓缩/苛化回碱配方；骨粉改为原版产量（×3，取消放大）并移出 `alkaline` 标签（`CRFluids`、`CRMixingRecipeGen`、`CRDatagen`，runData 已补全） | 02 §2/§5、03 §3.3、05 §6、06 F1、07 §7-1 |
| **F2 造纸产品线延伸·包装线**：纸箱方块（可装载于装置/矿车）+ 1 条合成配方（`CRBlocks`、`CardboardCrateBlock[Entity]`，runData 已补全） | 02 §1/§5、03 §7、06 F2（✅ 包装线） |

**提交前必做**：核对 `git status` 无缺失生成物（`fine_pulp_from_pulp_from_lye` 等 WIP 配方已 runData 补全），并清理本文档 ⚠ 标记处。

## 文档导航

| 文档 | 内容 | 适合 |
|---|---|---|
| [01-overview.md](01-overview.md) | 项目定位、技术栈、目录结构、设计理念 | 首次接触项目 |
| [02-production-chain.md](02-production-chain.md) | 核心产线（碱法蒸煮→成形→压制）、化学品循环（石灰/碱液）、黑液污染机制 | 想理解玩法与机制 |
| [03-recipes.md](03-recipes.md) | 配方产线总览：按大类（造纸/化学品/附魔纸/食品/能源/联动）组织的全部配方清单、JEI 集成 | 想改/加配方 |
| [04-recipe-ban.md](04-recipe-ban.md) | 配方禁令系统：机制与完整删除清单 | 要覆盖其他模组配方 |
| [05-development.md](05-development.md) | 开发工作流：命令、注册/datagen/mixin/网络规则、已知注意点 | 准备写代码 |
| [06-roadmap.md](06-roadmap.md) | 后续发展建议：现状盘点、WIP 收尾、玩法/技术/联动/发布路线图 | 规划下一步 |
| [07-design-principles.md](07-design-principles.md) | **设计母文档**：原则集（母/派生/执行 + 裁决顺序）、现状诊断、机巧新生接入面、逐线蓝图、决策记录与待决项 | 任何立项、改配方、加联动的准入依据 |

## 建议阅读顺序

```
01-overview ──► 02-production-chain ──► 03-recipes
                                   │          └──► 04-recipe-ban（改配方时必看）
                                   └──► 05-development（动手前必看）
06-roadmap（规划下一步时看）
```

- **只想玩/测**：看 01、02
- **想加新配方**：看 03 + 04 + 05 的"新增内容的标准流程"
- **想动机器机制**：看 02 对应小节 + 05
- **想规划开发方向**：看 06；**做社区反馈驱动的重构**：看 07（含与 06 的冲突待决清单）

## 速查

```bash
./gradlew build        # 构建
./gradlew runClient    # 启动游戏（QA 用）
./gradlew runData      # 重新生成 src/generated/resources/（改配方后必跑）
./gradlew runGameTestServer  # 游戏测试（当前无测试用例）
```

- 包根：`com.chemiofitor.create.rationality`，模组 ID：`rationality`
- 注册一律走 CreateRegistrate（`CreateRationality.REGISTRATE`）；Create 内置注册表（如机械臂交互点）走 `DeferredRegister`（见 05 §2）
- `src/generated/resources/` 为已提交的 datagen 输出，**禁止手改**
- 项目为 git 仓库（main 分支）；无单元测试/Lint
- 环境备注：Windows + Git Bash，`run/`、`build/`、`repo/` 等已 gitignore

## 文档维护规范

改完代码后，按下面规则同步本文档集（避免文档再次失真）：

1. **配方**：改 `data/provider/` 或 `data/recipe/` 后必跑 `runData`，再核对 03 的配方表与 `src/generated/resources/data/rationality/recipe/` **逐条一致**。
2. **注册**：新增/改名索引类（`index/`）→ 更新 01 目录树与 05 §2 流程；新增注册表类型（机械臂交互点等）→ 05 §2。
3. **Mixin**：新增/删除 mixin → 05 §3 表格；若动 `BanRecipes`/`RecipeTargets` → 04 删除清单。
4. **依赖/版本**：改 `gradle.properties` → 更新 01 技术栈表与本文"项目状态"。
5. **跨模组**：新增联动 → 01 可选依赖表 + `RationalMods` 枚举 + 03 对应大类。
6. **WIP 标注**：未提交的改动在相关表格用 ⚠ 标注并说明"未提交"；提交后移除 ⚠。
7. **语言**：新物品/流体/方块必须补 `zh_cn.json`（手写），`en_us` 由 datagen 生成。
8. **事实核查**：所有表格内容以源码为准；改动后如不确定，先 grep 源码再落笔。
