# 02 · 核心产线

> 对应源码：`common/block/`、`common/block/entity/`、`common/block/behaviour/`、`common/world/`、`event/`、`network/`、`index/CRItems|CRFluids|CRTagKeys`

## 1. 纸产线总览

```
■ 制浆（机械碎浆机 + 盆）：

[可浆化物×4 + 水1000mB]                  ──(碎浆机，常温)──► Pulp 500mB
[可浆化物×4 + 碱性物×2 + 水1000mB + 加热] ──(碎浆机，碱法蒸煮)──► Pulp 1000mB + 黑液 250mB
                                                                       │
                                                          （黑液 = 污染源，见 §4）
[Pulp 1000mB + 生石灰×2]                 ──(碎浆机)──────► Fine Pulp 1000mB

■ 成形（FormingRecipe，Headbox / 造纸台+注液口）：
   造纸框 + Pulp 100mB      ──► 造纸框（返还）+ Create 湿浆（Pulp 物品）──(压制)──► 粗纸
   造纸框 + Fine Pulp 100mB ──► 造纸框（返还）+ fine_pulp 物品        ──(压制)──► 纸（成品）

■ 压制（压片机）：
   Pulp 物品     ──► 粗纸 Raw Paper
   fine_pulp 物品 ──► 原版纸 Paper

■ 包装线（F2，纸箱）：
   纸板（Create）×9 ──(工作台 3×3)──► 纸箱 cardboard_crate（9 格存储方块，可装载于装置/矿车）
```

**制浆双配方**（`CRPulpingRecipeGen`）的设计意图：不加碱不加热的简单制浆产量减半（500mB）；投入碱性物并加热的**碱法蒸煮**足量制浆（1000mB），但副产 250mB 黑液——玩家必须在产量与环境代价之间权衡，黑液要么妥善处理（详见 §4）要么接受污染。

> ⚠ **工作区 WIP（未提交）**：`CRPulpingRecipeGen` 另有两个以**流体为碱源**的加热制浆配方（灰汁 1500mB / 碱液 1000mB，产出同碱法蒸煮）与一个碱液精浆配方，详见 03 §2.1；原 id/输入互换问题已修复（见 06-roadmap.md R2）。

## 2. 化学品循环（碱回收）

现代 Kraft 制浆的碱回收映射。物品与流体在 `index/CRItems|CRFluids`，配方在 `CRMixingRecipeGen` 与 `CRRecipeProvider`：

```
■ 灰汁（草木灰浸出）：
   4× 草木灰 + 水 1000mB ──(混合机)──► 灰汁 1000mB

■ 苛化（制碱）：
   生石灰 + 灰汁 500mB ──(混合机)──► 碳酸钙 + 碱液 500mB

■ 石灰循环（闭环）：
   石灰石 ──(熔烧，鼓风炉)──► 生石灰×4
   碳酸钙 ──(熔烧)──────────► 生石灰      ▲ 熔烧回收，石灰循环复用
   生石灰 + 水 100mB ──(混合机)──► 熟石灰 ──┘（熟石灰也是碱性物）

■ 碱性物来源（rationality:alkaline 标签）：
   草木灰（可浆化物烧制）、生石灰、熟石灰
   炭灰（煤炭烧制，非碱性，暂无配方用途）

■ 副产物链：
   草木灰 ──► 灰汁 ──► 碱液（强碱，lye 标签）

■ 黑液回收（碱回收闭环，F1）：
   黑液 250mB + 加热 ──(混合机)──► 浓缩黑液 100mB            （浓缩黑液为安全形态：普通 LiquidBlock，不产生污染）
   浓缩黑液 100mB + 生石灰 ──(混合机)──► 碱液 100mB + 碳酸钙×1 （苛化回碱，量守恒 1:1；碳酸钙熔烧回生石灰）
   黑液 250mB + 骨头 ──(混合机)──► 骨粉×3                     （兜底减量出口；产量＝原版，不放大）
   岩浆源 + 黑液 ──(流体交互)──► 黑曜石；流动岩浆 + 黑液 ──► 煤矸石（岩浆被替换，黑液不消耗）→ 粉碎 → 煤矸石粉（燃料）
```

- 草木灰：`可浆化物` 标签物品烧制（smelting 100 tick）产出
- 生石灰：石灰石熔烧（blasting，产出 4）；或碳酸钙熔烧回收（blasting，1）
- 碳酸钙在循环中充当"白泥"角色：苛化沉淀后熔烧回生石灰
- `浓缩黑液`（`concentrated_black_liquor`）：黑液浓缩后安全形态，**不产生区块污染**（普通 `LiquidBlock`，非 `BlackLiquorBlock`）；浓度衰减（粘度/密度更高）；回收配方见上，回报率见 06 F1。
- 骨粉**不在** `alkaline` 标签内：否则"黑液→骨粉→蒸煮用碱"会绕开草木灰/石灰线（见 03 §3.3）
- 煤矸石粉（`crushed_coal_gangue`）：**定位已定（2026-09-26）**——劣质燃料（100 tick）+ 可无限刷的黑色石料，不接化工下游；已摘除 `c:crushed_raw_materials` 碎矿标签（它不是矿石，避免被其他模组的碎矿处理误吃）
- ⚠ 工作区 WIP：灰汁/碱液除产出途径外，已新增为碱法蒸煮的**流体碱源**与精浆原料（见 03 §2.1，未提交）

## 3. 流浆成形与压制（机制）

### 成形路径 A：流浆箱 Headbox（传送带上方）

`common/block/entity/HeadboxBlockEntity.java`

- 1500mB 流体罐，**只允许注入、禁止抽取**（`forbidExtraction().allowInsertion()`），支持瞄准镜（Goggles）液位显示与比较器输出
- **低空处理**：`mixin/BeltLowProcessing.java` 注入 Create 的 `BeltInventory.getBeltProcessingAtSegment`，让传送带把 Headbox 识别为"带上方 1 格"的处理装置（`LowBeltProcessingBehaviour`）。物品经过时被 HOLD 拦截，淋浆 **10 tick**（`FILLING_TIME`），完成后把产出写回传送带
- 产出使用 `rollResults`（支持配方概率输出），首位造纸框返还（配方输出首位）
- 配方查找顺序：FormingRecipe（含顺序装配 Sequenced Assembly 步骤）→ 回退 Create 原生注液（`FillingBySpout`）→ 若 CDG 已加载则回退铸造（`CastingByHeadbox`）

### 成形路径 B：造纸台 Papermaking Depot（注液口下方）

- 用机械手（Deployer）把造纸框装到普通 Depot 上合成造纸台（ItemApplication 配方）
- `mixin/AssemblyOperatorInject`：让 Create 机械手（`AssemblyOperatorBlockItem.operatesOn`）也识别造纸台为可操作目标
- 上方放注液口，`PapermakingBlockSpoutingBehaviour` 只查 FORMING 配方，匹配后扣流体、产出（造纸框原样返还）丢到台面

### 碎浆机机制（`common/block/entity/MechanicalPulperBlockEntity.java`）

- 继承 Create 的 `BasinOperatingBlockEntity`，要求**中速**（MEDIUM）动力
- 处理时长随转速变化：`processingTicks = clamp(log2(512/|speed|) × ceil(配方时长系数×15) + 1, 1, 512)` tick
- **40 tick 冲压循环**：0→20t 锤头下压，`runningTicks==20` 时执行配方并播粒子/音效，20→40t 抬起
- `matchStaticFilters` 排除压片机可压缩的配方
- 渲染：`MechanicalPulperRenderer` + `PulperVisual`（Flywheel 实例化渲染）

## 4. 黑液污染机制

黑液是碱法蒸煮的副产流体（黑色、粘度 2000/密度 1100）。它的特殊性：**接触环境会产生区块污染**。

```
黑液到达某位置（倒桶/流动/扩散/状态更新）
        │ BlackLiquorBlock.onPlace（LiquidBlock 子类）
        ▼
PollutionSavedData.markChunkPolluted（按维度持久化，rationality_pollution.dat）
        │ 污染持续 24000 刻（1 个游戏日）
        ▼
区块污染期间：
  ├─ 作物自然生长被阻止（CropGrowEvent.Pre → DO_NOT_GROW）
  ├─ 骨粉催熟被取消（BonemealEvent → setCanceled）
  └─ 实体浸泡在黑液中 → 中毒（POISON 200 刻，EntityTickEvent.Post）
        │
        ▼
玩家所在区块污染状态 → PollutionSyncHandler（每 20 刻检查，状态变化才发包）
        ▼
PollutionStatusPayload（playToClient 网络包，仅 1 个布尔值）
        ▼
PollutionIndicatorClient.setPolluted → model override predicate（rationality:pollution）
        ▼
污染指示仪物品双材质切换（_0 无污染 / _1 污染）
```

实现要点：

| 类 | 职责 |
|---|---|
| `common/block/BlackLiquorBlock` | `LiquidBlock` 子类，`onPlace` 标记区块污染（客户端方块自动排除：仅 `ServerLevel` 生效） |
| `common/world/PollutionSavedData` | 区块键→污染时刻的 `Long2LongOpenHashMap`，`DATA_KEY = rationality_pollution`，O(1) 读写 |
| `event/CropGrowHandler` | 作物生长/骨粉拦截 + 每 200 刻清理过期记录（`LevelTickEvent.Post`） |
| `event/EntityTickHandler` | 实体在黑液中 → 施加中毒效果（仅服务端） |
| `event/PollutionSyncHandler` | 每 20 刻向在线玩家同步所在区块污染，仅状态变化时发包（`LAST_SENT` 去重，自动清理离线玩家） |
| `network/PollutionStatusPayload` | `record` + `StreamCodec`（BOOL），`CreateRationality.registerNetworking` 注册 playToClient |
| `client/PollutionIndicatorClient` | `ItemProperties.register`（predicate `rationality:pollution`）+ 客户端污染状态缓存 |
| `data/model/PollutionIndicatorModels` | 主模型（`_0` 纹理）+ override 子模型（`_1` 纹理），仿原版时钟 |
| `index/CRItems.POLLUTION_INDICATOR` | 污染指示仪物品，`.model(PollutionIndicatorModels::build)`，合成：锌锭+红石 |

## 5. 物料清单

### 物品（`index/CRItems.java`）

| 物品 | 说明 |
|---|---|
| `plant_ash`（草木灰） | 可浆化物烧制；碱性物（`alkaline` 标签），浸出灰汁 |
| `coal_ash`（炭灰） | 煤炭烧制；非碱性，暂无用途 |
| `calcium_carbonate`（碳酸钙） | 苛化副产；可熔烧回生石灰（石灰循环） |
| `quicklime`（生石灰） | 石灰石/碳酸钙熔烧；碱性物；水化成熟石灰、苛化用 |
| `slaked_lime`（熟石灰） | 生石灰水化；碱性物 |
| `fine_pulp` / `raw_paper` | 精浆团 / 粗纸（普通物品） |
| `oilpaper` | 油纸（UNCOMMON，CDG 联动） |
| `enchanting_paper` | 附魔纸（UNCOMMON，`EnchantingPaperItem`，可直接附魔；见 03 §4） |
| `pollution_indicator`（污染指示仪） | 双材质状态显示（见 §4） |
| `color_paper` | 15 种彩色纸（染料色**除白色**），`ColorPaperItem`，`rationality:color_paper` 标签 |

### 方块（`index/CRBlocks.java`）

| 方块 | 说明 |
|---|---|
| `cardboard_crate`（纸箱） | 9 格存储方块（`CardboardCrateBlockEntity`，`ItemStackHandler(9)`）；仿 Create 板条箱：`AllShapes.CRATE_BLOCK_SHAPE`，经 `mountedItemStorage(AllMountedStorageTypes.SIMPLE)` **可装载于装置/矿车**（装配时复制内容、拆解时写回）；拆毁时掉落内容物 |

### 流体（`index/CRFluids.java`）

| 流体 | 粘度/密度 | 颜色 | 备注 |
|---|---|---|---|
| Pulp | 2000/1300 | 棕 | 制浆主产物 |
| Fine Pulp | 2000/1100 | 棕 | 精浆 |
| Syrup | 2000/1200 | 陶土白 | 糖浆（跨模组联动） |
| Ash Lye（灰汁） | 2000/1100 | 灰 | 草木灰浸出，`ash_lye` 标签 |
| Lye（碱液） | 2000/1100 | 灰 | 苛化产物，`lye` 标签，translucent 渲染 |
| Black Liquor（黑液） | 2000/1100 | 黑 | 碱法蒸煮副产；**污染源**，`BlackLiquorBlock` |
| Concentrated Black Liquor（浓缩黑液） | 3000/1400 | 黑 | 黑液浓缩（蒸发）后的**安全形态**，普通 `LiquidBlock`，**不产生污染**；回收闭环核心（见 §2、03 §3.3） |
| Glycerol（甘油） | 1100/2000 | 橙 | 生物柴油副产（CDG 联动），暂无下游用途 |
| Yeast Liquid / Raw Yeast Liquid | 2000/1200 | 陶土白 / 陶土棕 | 酵母液体，已注册**暂无配方**（预留） |

全部流体带桶 + 发射器行为；灰汁/碱液用 translucent 渲染。

### 标签（`index/CRTagKeys.java`）

| 标签 | 用途 |
|---|---|
| `create:pulpifiable` | 可浆化物（create 命名空间） |
| `rationality:alkaline` | 碱性物（草木灰/生石灰/熟石灰；骨粉已移出，见 §2） |
| `rationality:oil_pressing_raw_material` / `sugar_raw_material` | 油料 / 糖原料（跨模组） |
| `rationality:color_paper` | 彩色纸 |
| `c:dusts/wood` / `c:buckets` | 木屑（预留）/ 流体桶（Pulp、Fine Pulp 桶） |
| 流体：`pulp`/`fine_pulp`/`syrup`/`ash_lye`/`black_liquor`/`concentrated_black_liquor`/`lye`/`glycerol`/`yeast_liquid`/`raw_yeast_liquid`、`c:plantoil`/`c:ethanol` | 流体分类 |
