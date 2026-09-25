# 06 · 后续发展建议（Roadmap）

> 本文是**发展建议**（非承诺清单），基于对 `src/main/java`、`src/generated/resources/`、`gradle.properties` 与工作区 WIP 的核实整理，供规划下一步开发方向使用。
> 每条建议给出：动机 → 建议方案 → 涉及文件 → 优先级（P0/P1/P2）→ 工作量（S 小 / M 中 / L 大）。
> 优先级含义：P0 = 提交前必做（收尾/稳定）；P1 = 提升核心玩法的关键补全；P2 = 技术债/锦上添花。

## 1. 现状盘点

### 1.1 已交付能力（已提交）

- **造纸主线闭环**：制浆（碎浆机+盆）→ 成形（Headbox 低空处理 / 造纸台+注液口）→ 压制（压片机）→ 原版纸；另有纸板、粗纸、湿浆、精浆中间物
- **化学品循环**：草木灰→灰汁→苛化→碱液+碳酸钙→熔烧→生石灰（碱回收闭环）；黑液出口两条：浓缩→苛化回碱（10% 回补）+ 骨粉兜底
- **黑液污染机制**：区块级持久化污染（1 游戏日）、作物停长/骨粉失效/中毒、污染指示仪（网络同步 + 双材质）
- **附魔纸体系**：独立附魔物品、附魔台等级压制、动力锯拆书、多纸绑定成书（CustomRecipe 动态输出）
- **联动**：FD（绞肉/树皮纸禁令）、CDG（植物油/乙醇/生物柴油/甘油/铸造）、Big Cannons（熔炼）、Confectionery（焦糖/巧克力）、Corn Delight、Bitterballen（炸油/玉米热狗）、Butchercraft（绞肉/猪油/肥皂）、Dragons Plus（风机染色）
- **配方禁令**：`BanRecipes` mixin + `RecipeTargets` 静态清单，覆盖原版/其他模组快捷纸、糖、纸板等配方

### 1.2 工作区未提交改动（WIP，见 doc/README.md「项目状态」）

| 改动 | 状态 |
|---|---|
| 灰汁/碱液加热制浆 ×2 + 碱液精浆 ×1（`CRPulpingRecipeGen`） | 源码已改，runData 生成物已补全 |
| `CRArmInteractionPointTypes`（造纸台机械臂交互点） | 新增索引类，未提交 |
| `PapermakingDepotBlockEntity.registerCapabilities`（ItemHandler） | 已改，未提交 |
| Steam 'n' Rails 依赖 + NeoForge 21.1.251（含 CraftGraph 开发环境依赖） | 已改，未提交 |
| `zh_cn.json`：`钾碱液桶` → `碱液桶` | 已改，未提交 |

### 1.3 已知遗留

| 项 | 位置 | 说明 |
|---|---|---|
| 碎浆机音效临时 | `MechanicalPulperBlockEntity` L250 `// TODO replace sound` | 复用 MIXING 音效 |
| Headbox TODO | `HeadboxBlockEntity` L122 `// TODO` | 待确认内容 |
| 孤儿纹理 | `assets/rationality/textures/block/coal_gangue.png`、`textures/item/paper_scrap.png` | 无任何模型/注册引用（`fabric`/`fan_blades` 有引用） |
| 未启用的染色配方 | `common/recipe/crafting/PaperColoring.java` + `CRRecipeTypes` 注释枚举 | 纸染色已改由染料合成实现 |
| 已注册未使用流体 | Glycerol、Yeast Liquid / Raw Yeast Liquid | 无下游配方（酵母液体连生成器都没有） |
| 已依赖未使用 | RPL、Enchantment Industry、Steam 'n' Rails ⚠ | 仅有依赖声明，无联动代码 |

## 2. 建议总览

| 编号 | 类别 | 建议 | 优先级 | 工作量 |
|---|---|---|---|---|
| R1 | 收尾 | 提交 WIP：统一命名 → runData 补全 → 同步文档 | P0 | S |
| R2 | 收尾 | 制浆配方 id/输入命名一致化（lye ↔ ash_lye 互换）✅已修复 | P0 | S |
| R3 | 收尾 | 版本/信息一致性（NeoForge 233、mod_version、README/AGENTS） | P0 | S |
| R4 | 收尾 | 清理孤儿纹理与未注册类（paper_scrap、coal_gangue、PaperColoring） | P0 | S |
| F1 | 玩法 | 黑液回收闭环：浓缩 → 苛化回碱 ✅已实施（含浓缩黑液流体 + 2 配方） | P1 | M |
| F2 | 玩法 | 造纸产品线延伸：**包装线**（纸箱）✅已实施；漂白特种纸、油纸下游待做 | P1 | M |
| F3 | 玩法 | 污染机制深化：净化手段、污染分级、指示仪升级 | P1 | M |
| F4 | 玩法 | 附魔纸体系数值平衡 + Enchantment Industry 联动 | P1 | M |
| F5 | 玩法 | 预留资源落地：酵母液体、甘油下游、肥皂生活线 | P2 | M |
| E1 | 体验 | Ponder 教程场景（依赖已含，当前零场景） | P1 | M |
| E2 | 体验 | 碎浆机专属音效（替换 TODO 临时音效） | P2 | S |
| E3 | 体验 | 纹理/模型打磨 + 进度（Advancement）引导 | P2 | M |
| T1 | 技术 | 自动化测试起步（GameTest/单元测试） | P2 | L |
| T2 | 技术 | mixin 瘦身与防御（defaultRequire:1 崩溃风险） | P2 | M |
| T3 | 技术 | 数值配置化（config 文件） | P2 | M |
| T4 | 技术 | 性能与网络优化（污染检测频率、粒子、包体） | P2 | S |
| T5 | 技术 | 文档自动核对（03 配方表 ↔ generated 产物） | P2 | S |
| C1 | 联动 | Steam 'n' Rails 联动（依赖已加未用） | P2 | M |
| C2 | 联动 | RPL / Enchantment Industry 深度整合 | P2 | M |
| C3 | 联动 | 新联动候选（克制清单） | P2 | M |
| M1 | 发布 | 发布准备：CurseForge/Modrinth 素材、更新日志、根 README | P1 | M |
| M2 | 发布 | 平衡性验证：碱法蒸煮 vs 简单制浆、黑液代价 | P1 | S |

## 3. P0 · 收尾与稳定性（提交前必做）

### R1 提交 WIP 并补全生成物

- **动机**：工作区有 5 处未提交改动；runData 已补全生成物，需按模块分批提交。
- **方案**：`git status` 确认无缺失 → 按模块分批提交 → 按 doc/README.md「文档维护规范」移除 ⚠ 标注。
- **涉及**：`src/generated/resources/`、`CRPulpingRecipeGen`、`CRArmInteractionPointTypes`、`CreateRationality`、`zh_cn.json`、doc/。
- **验收**：`git status` 干净；03 §2.1 表格与生成物逐条一致；⚠ 标注全部移除。

### R2 制浆配方 id/输入命名一致化 ✅（已修复）

- **原问题**：WIP 中 `pulp_from_pulpifiable_with_heat_and_lye` 实际消耗**灰汁**（ASH_LYE），`..._and_ash_lye` 实际消耗**碱液**（LYE）——id 与输入互换。
- **修复**：互换 `require` 使语义匹配——`..._with_heat_and_lye` 用碱液（LYE **1000mB**）、`..._with_heat_and_ash_lye` 用灰汁（ASH_LYE **1500mB**）；强碱用量更少、弱碱更多，数值保持合理。
- **涉及**：`data/provider/CRPulpingRecipeGen.java` + runData + 03 §2.1。
- **遗留**：`fine_pulp_from_pulp_from_lye` 命名略绕（建议 `fine_pulp_from_pulp_and_lye`），属可选美化（见 R1 提交时一并处理）。

### R3 版本/信息一致性

- **动机**：`gradle.properties` 已升到 NeoForge 21.1.251（1.21.1 线最后一版，CraftGraph 依赖要求 ≥251），文档已同步；`mod_version=1.0.0` 是否需要 bump 未决策；根 README 只有一行。
- **方案**：同步 AGENTS.md 与本文档集的版本号；为 WIP 的功能性改动（新配方/机械臂交互）决定是否 1.0.1；补根 README（见 M1）。
- **验收**：全仓搜索 `21.1.230` 无残留；版本号有明确口径。

### R4 清理孤儿资产与未注册类

- **动机**：`coal_gangue.png`、`paper_scrap.png` 无引用；`PaperColoring` 类存在但枚举注释、`CRAFTING_PAPER_COLORING` 未注册——增加维护噪音。
- **方案**：删除无引用纹理（或为它们补真实用途）；`PaperColoring` 若确定废弃则删除，若保留则补注释说明"纸染色走染料合成"。
- **涉及**：`assets/rationality/textures/`、`common/recipe/crafting/PaperColoring.java`、`CRRecipeTypes`。
- **验收**：`grep` 不到孤儿资源名；无"死代码"类。

## 4. P1 · 玩法补全（核心价值）

### F1 黑液回收闭环（✅ 已实施）

> **状态**：已实现并随 runData 生成（浓缩黑液流体 + 浓缩/苛化两配方 + 文档同步）。以下为设计基准记录，落地后数值以源码与 03 §3.3 为准。
> **2026-09-19 复核（方向 B 定案）**：WIP 中对本闭环的推翻已回退——浓缩黑液恢复**安全形态**（普通 `LiquidBlock`）、`lye_from_concentrated_black_liquor` 恢复；骨粉改为**原版产量 ×3（取消放大）**并**移出 `alkaline` 标签**（防止"黑液→骨粉→蒸煮用碱"绕开碱线）。硫/沼气等新出口按 07 §5 留作后续单独立项。

- **动机**：黑液产出 250mB/次；"污染是错位的资源"是本模组主题，闭环后主题才完整。现代 Kraft 制浆的碱回收正是：黑液浓缩 → 回收碱 + 热能。
- **现状核实（2024 更新）**：`bone_meal` 为 **黑液 250mB/次**（黑液 250 + 骨头 → 骨粉×**3**，原版产量、不放大），与每批副产 250mB **量上持平**，并无旧文中"500mB 缺口"。真正缺口在**化学不回补**（骨粉把碱当废料，碱损失无法回补）与**无主动净化**。另：`浓缩黑液`曾只留占位（zh_cn + tag），`CRFluids` 未注册、无配方——即 F1 的遗留蓝本，已补上。
- **方案**（Kraft 回收）：
  1. **浓缩**：黑液 + 加热 → **浓缩黑液**（普通 `LiquidBlock`，**不产生区块污染**）；
  2. **化学回收（苛化回碱）**：浓缩液 + 生石灰 → **碱液** + 碳酸钙（碳酸钙熔烧回生石灰，维持石灰循环）；
  3. 保持 `bone_meal` 作为**零设备低门槛消耗**（产量＝原版 ×3，**不做放大**；骨粉同时移出 `alkaline` 标签，避免"黑液→骨粉→蒸煮用碱"绕开整条碱线）。
  - **裁撤燃烧回收**：原提议的 `black_liquor_ash`（浓缩液 + 超高温 → 草木灰×1）已**移除**——"超高温烧浓缩液得草木灰"与直接烧可浆化物产出同一物，语义空洞；苛化改为**量守恒**（浓缩 250→100、苛化 100→100，体积不虚增）。
- **机型**：复用**混合机**（浓缩 HEATED / 苛化普通 mixing）轻量落地，避免新增方块/BE/渲染；重机型（动力蒸发器）留待 F2 一并评估。
- **回报率（当前实现）**：

  | 路径 | 处理 250mB 黑液投入 | 产出 | 折算碱液当量 | 碱回补率 |
  |---|---|---|---|---|
  | 骨粉（兜底） | 骨头×1 | 骨粉×3 | 0 | **0%** |
  | 浓缩→苛化 | 浓缩(加热)+生石灰×1(熔烧回收) | 碱液 100mB + 碳酸钙→生石灰 | 100mB | **10%** |

  - 碱当量锚：草木灰×4+水→灰汁 1000mB，生石灰+灰汁 500mB→碱液 500mB；碱回补率 = 回收碱 ÷ 一次蒸煮碱入（1000mB）。
  - **量守恒原则**：浓缩 250→100（蒸发 150mB 水）、苛化 100→100（1:1），体积全程不虚增；回补率相应降至 **10%**（其余碱当作浓缩/回收损耗）。这是"改动最小 + 体积不涨"取向下的取舍。
  - **数值取向**：回收路径唯一（苛化，10% 回补），骨粉为零设备兜底；**碱损失永不归零**（约 90%），草木灰仍是最终碱源，保留"烧草木灰 vs 回收"张力；能耗（HEATED、熔烧、攒料存储）为设备门槛，即"污染可完全避免的代价"。
- **涉及**（已实施）：`index/CRFluids.java`（注册浓缩黑液，复用灰汁 translucent 模板）、`data/provider/CRMixingRecipeGen.java`（浓缩/苛化配方）、`assets/rationality/lang/zh_cn.json`（占位已存在，核对即可）、02 §4/§5、03 §3.3。
- **配方禁令**：纯新增产物/新增消耗，非"同输入替换"，按 04 §3 **无需登记 `BanRecipes`**。
- **验收**：黑液有 ≥2 条处理路径（骨粉 + 苛化）；浓缩黑液**不触发污染**；碱损失可部分回收（10%）；污染可完全避免（代价是投入设备 + 低回补）；无新增无用途中间物；03 §3.3 配方表与生成物一致。

### F2 造纸产品线延伸（包装线 ✅ 已实施）

> **状态**：**包装线已实施**（纸箱方块 + 1 条合成配方 + 文档同步，见 02 §1/§5、03 §7）；特种纸与漂白仍为开放项。

- **动机**：目前"纸"只有原版纸 + 粗纸 + 纸板 + 彩色纸，产品线偏薄；造纸题材天然可扩展下游制品。
- **方案**：
  1. **包装线（✅ 已实施）**：纸板（Create `cardboard`）×9 →（3×3 工作台）→ 纸箱（`CRBlocks.CARDBOARD_CRATE`，方块）。纸箱为 9 格存储方块（`CardboardCrateBlockEntity`，`ItemStackHandler(9)`），经 `mountedItemStorage(AllMountedStorageTypes.SIMPLE)` 注册**可装载于装置/矿车**（装配复制、拆解写回），拆毁掉落内容物——仿 Create 板条箱（`AllShapes.CRATE_BLOCK_SHAPE`）。原设"纸板→瓦楞纸→纸箱"中间物已移除（瓦楞纸无独立用途，属多余环节）。
  2. **特种纸（待做）**：油纸已有（CDG），可加"烘焙纸/滤纸"进 D 类食品线；"砂纸"已被禁（Create 原版），可考虑自制升级版；
  3. **漂白（待做）**：风机染色已能漂白彩色纸 → 可扩展为"漂白浆"提升纸张等级（白度 → 附魔纸效率？）。
- **涉及**：`CRBlocks`、`CRBlockEntityTypes`、`common/block/CardboardCrateBlock`、`common/block/entity/CardboardCrateBlockEntity`、`CRRecipeProvider`、03 §7。
- **验收**：每条新纸制品有明确下游用途，不制造"无用途物品"（当前 Glycerol 已是反面教材）。包装线已满足：纸箱用于批量存储/运输（装置/矿车装载），无中间冗余产物。

### F3 污染机制深化

- **动机**：污染目前是"全有/全无"（1 游戏日、作物全停）；深度有限，且**无净化手段**（只能等）。
- **方案**：
  1. **净化**：新增"净化/修复"手段（如石灰水泼洒、特定机器吸附、随时间自然衰减可调）；
  2. **分级**：污染量积累 → 程度分级（轻度：作物生长减慢；重度：当前全停），指示仪按级显示（三材质）；
  3. **边界**：污染扩散/衰减规则可配置（`PollutionSavedData` 已是 O(1) 结构，扩展友好）；
  4. **信息**：污染状态同步目前只有 1 个布尔，升级为分级数值包（`PollutionStatusPayload` 扩展）。
- **涉及**：`common/world/PollutionSavedData`、`event/CropGrowHandler`、`network/PollutionStatusPayload`、`client/PollutionIndicatorClient`、`data/model/PollutionIndicatorModels`。
- **验收**：玩家能主动清除污染；污染有梯度反馈；网络包向后兼容（或同版本升级）。

### F4 附魔纸体系平衡与联动

- **动机**：附魔纸体系机制完整但数值未经打磨（纸附魔等级恒 1、绑定惩罚公式、锯切均摊）；Enchantment Industry 依赖已声明但未联动。
- **方案**：
  1. **平衡**：验证"4 张锋利 I 纸 → 锋利 IV"的经济性 vs 直接附魔书；累积惩罚（×2+1）是否过苛；
  2. **联动 EI**：Enchantment Industry 的自动附魔机/洗附魔/附魔提取可接附魔纸体系（纸作为可洗可提的载体）；
  3. **体验**：锯切拆书返还率、绑定合成是否该吃附魔台/铁砧以外的成本。
- **涉及**：`EnchantPaperBindingRecipe`、`SawInject`、`EnchantingHandler`、`EnchantingPaperItem` + `compat/`。
- **验收**：有明确的"纸↔书"转换经济模型；EI 加载时有真实联动玩法。

### F5 预留资源落地

- **动机**：Glycerol、Yeast Liquid / Raw Yeast Liquid 已注册无配方（02/03 已标注"预留"）；肥皂线只有 1 个配方。
- **方案**：酵母液体 → 发酵/烘焙线（联动 FD/Corn Delight）；甘油 → 肥皂/护肤品/爆炸物（联动 Big Cannons 火药？）；肥皂 → 清洁/去污（联动污染净化 F3 会非常出彩）。
- **涉及**：`CRFluids`、`data/provider/`（新生成器）、`CRTagKeys`。
- **验收**：所有已注册流体/物品有至少一个配方引用。

## 5. P1 · 玩家体验（上手与质感）

### E1 Ponder 教程场景（强烈推荐）

- **动机**：`ponder_version` 是**必装依赖**，但全项目**零 Ponder 场景**——产线步骤多（蒸煮→成形→压制→污染处理），无教程等于劝退新玩家；Ponder 是 Create Addon 的"默认说明书"。
- **方案**：为 4 个核心机制各做 1 个场景：① 碎浆机+碱法蒸煮（含黑液代价提示）；② Headbox 低空淋浆；③ 造纸台+注液口；④ 污染指示仪与净化（若 F3 落地）。复用 `PulperVisual`/`HeadboxRenderer` 的组件。
- **涉及**：新建 `src/main/resources/assets/rationality/ponder/`、`CRPonderScenes`（`compat/` 或 `client/`）、注册于 `CreateRationalityClient`。
- **验收**：游戏内 `Ponder` 键可查看 ≥4 个场景。

### E2 碎浆机专属音效

- **动机**：`tickAudio` 有 TODO，复用 MIXING 音效，机械感不足。
- **方案**：压锤下压/抬起两段音效（可复用 Create 的 press 音效变调），随 `runningTicks` 同步。
- **涉及**：`MechanicalPulperBlockEntity`、`assets/rationality/sounds.json`。
- **验收**：碎浆机运转有明显区别于混合机的听觉反馈。

### E3 纹理打磨与进度引导

- **动机**：多数纹理是基础色块（如 `black_paper.png` 等彩色纸、流体 still 图）；无进度引导，玩家不知下一步。
- **方案**：统一视觉风格（参考 Create 的线稿风）；加 2-3 个 Advancement（"第一桶黑液"、"碱回收闭环"、"污染指示仪"）；`en_us` 文案完善（zh_cn 已手写，en_us 由 datagen 生成，需人工过一遍英文质量）。
- **验收**：创意标签页物品观感统一；进度树覆盖核心产线。

## 6. P2 · 技术债与工程化

### T1 自动化测试起步

- **动机**：无任何测试，核心机制（配方、污染、附魔绑定）全靠手工 QA；`runGameTestServer` 已配置但空跑。
- **方案**：从"配方存在性 + 输入输出正确性"的 GameTest 起步（最易写、回报最高）；再为 `PollutionSavedData` 写单元测试（纯逻辑，无 MC 依赖部分可抽出来测）。
- **验收**：`./gradlew runGameTestServer` 能跑通 ≥10 个断言。

### T2 Mixin 瘦身与防御

- **动机**：6 个 mixin 注入 Create 内部类，`defaultRequire: 1` 意味着 Create 一升级就可能**直接崩溃**；`BeltLowProcessing`、`SawInject`、`BanRecipes` 都是"无替代 API 才用 mixin"。
- **方案**：① 逐个评估能否换 API（如 `SawBlockEntity` 是否提供事件/挂钩点）；② 至少给高风险 mixin 写"目标方法签名断言"（启动时反射验证，友好报错而非崩溃）；③ 升级 Create 后必跑 `runClient` 冒烟。
- **涉及**：`src/main/resources/rationality.mixins.json`、`mixin/` 全部、05 §3。
- **验收**：Create 小版本升级不再"一击崩溃"；mixin 数量不增（或减少）。

### T3 数值配置化

- **动机**：24000 刻污染期、20 刻同步间隔、40 tick 冲压循环、黑液 250mB 副产等硬编码；平衡性调整要改代码重编译。
- **方案**：引入 NeoForge config（或至少集中到 `CreateRationality` 常量类 + `gradle.properties` 文档化），污染/产率/时长可配。
- **验收**：核心数值可在 config 修改并热重载（或重启生效）。

### T4 性能与网络

- **动机**：污染同步每 20 刻全服玩家检查（已有去重，尚可）；黑液放置即标污染（O(1) 已优）；粒子/音效频率未验证。
- **方案**：实测 TPS；粒子按距离降级；`PollutionStatusPayload` 若升级分级（F3）注意包体。
- **验收**：100 区块污染场景 TPS 无可见下降。

### T5 文档自动核对

- **动机**：03 配方表 40+ 行手写，易与生成物漂移（本次整理已发现 WIP 未同步）。
- **方案**：小脚本（PowerShell/Java）比对 03 表格中的配方 id 与 `src/generated/resources/data/rationality/recipe/` 文件集合，作为提交前检查。
- **验收**：脚本可一键输出"文档有/生成物无"清单。

## 7. P2 · 兼容与联动

### C1 Steam 'n' Rails（依赖已加，方向待定）

- **动机**：`steam_n_rails_version` 已加入 build.gradle（⚠ 未提交）但无联动代码。
- **方案**（选一深耕）：① 黑液浓缩物 → 锅炉燃料（接 F1，联动最自然）；② 火车运输纸/纸箱（接 F2）；③ 先移除依赖，等 F1/F2 定型再引入（避免死依赖）。
- **验收**：联动功能真实可用，或依赖被移除。

### C2 RPL / Enchantment Industry 深度整合

- **动机**：两个依赖声明了但零代码（RPL 可能仅作传递依赖）。
- **方案**：EI 接附魔纸（F4）；RPL 若不确定用途，评估移除依赖以减少加载面。
- **验收**：每个 `implementation` 依赖都有对应 `compat/` 代码，或从 build.gradle 移除。

### C3 新联动候选（克制）

- **动机**：联动过多会拖累维护（本模组已联动 9 个模组）。
- **候选**：Create: Crafts & Additions（电 → 造纸厂电力主题）；Create: Steam 'n' Rails（见 C1）。**建议**：以"复用本模组中间产物"为唯一准入标准（现有 FD/CDG/Bitterballen 模式），拒绝纯凑数联动。

## 8. 发布与社区

### M1 发布准备

- **动机**：根 README 仅一行；无更新日志；无发布渠道物料。
- **方案**：补根 README（简介 + 特性 + 截图位 + 安装 + 联动表 + 链接 doc/）；建 `CHANGELOG.md`；CurseForge/Modrinth 页面素材（横幅、4 张玩法截图）；确认许可证 MIT 与依赖许可兼容。
- **验收**：根 README 可独立阅读；发布页素材就绪。

### M2 平衡性验证

- **动机**：核心数值未系统验证：简单制浆 500mB vs 碱法蒸煮 1000mB+250mB 黑液；污染 1 天代价 vs 产率收益；附魔纸经济模型。
- **方案**：`runClient` 跑通完整产线并记录：从种甘蔗到第一张纸的耗时/投入；黑液污染对前期发展的实际影响；与"直接种甘蔗烧纸"（已被禁）做替代成本对比。
- **验收**：产出数值有文档化的设计依据；黑液代价"痛但可解"（结合 F1）。

## 9. 里程碑路线图

```
v1.0.1（近期，收尾）        R1 提交 WIP · R2 命名 · R3 版本同步 · R4 清理
v1.1（玩法补全）            F1 黑液回收闭环 ✅ · F2 包装线（纸箱）✅ · F2 特种纸/漂白 · E1 Ponder
v1.2（体验与深度）          F3 污染深化 · F4 附魔平衡 · E2 音效 · E3 纹理/进度
v2.0（工程化与发布）        T1 测试 · T2 mixin 瘦身 · T3 配置化 · M1 发布 · C1/C2 联动
长期（社区驱动）            F5 预留资源 · T4 性能 · T5 文档核对 · C3 新联动
```

> 建议节奏：**先做 P0 收尾（半天内），再攻 F1**——黑液回收闭环是主题完整性的关键缺口，也是联动（C1）和污染深化（F3）的共同地基。

## 10. 维护提示

- 落地任何建议后，按 doc/README.md「文档维护规范」同步对应章节（03 配方表、01 目录树/依赖、05 流程、04 禁令清单）。
- 本文自身的编号（R/F/E/T/C/M）在落地时可标注"已实施"或移除，避免文档与代码漂移。
