# 03 · 配方产线总览

> 对应源码：`index/CRRecipeTypes.java`、`common/recipe/`、`data/provider/`（12 个 CR*RecipeGen + CRRecipeProvider）、`data/recipe/ban/`、`compat/jei/`
>
> 本文按**产线大类**组织全部配方：A 造纸主线 → B 化学品与碱回收 → C 附魔纸循环 → D 食品与甜食 → E 能源与油料 → F 其他联动与杂项。每张配方表均与 `src/generated/resources/data/rationality/recipe/` 逐条核对。

## 0. 产线地图

| 大类 | 核心产物                                           | 主要机器 | 依赖模组 |
|---|----------------------------------------------------|---|---|
| **A 造纸主线** | 纸、粗纸、纸板（+ 湿浆/精浆流体）                  | 机械碎浆机+盆、Headbox/造纸台+注液口、压片机、压实机 | 无（本体） |
| **B 化学品与碱回收** | 灰汁、碱液、生石灰、熟石灰、碳酸钙、骨粉           | 混合机、鼓风炉、熔炉 | 无 |
| **C 附魔纸循环** | 附魔纸（enchanting_paper）、附魔书                 | 附魔台、动力锯、工作台 | 无 |
| **D 食品与甜食** | Syrup、焦糖、炸油、肉制品、猪油、肥皂              | 碎浆机、混合机、注液口 | FD / Butchercraft / Bitterballen / Confectionery / Corn Delight |
| **E 能源与油料** | 植物油、乙醇、生物柴油、甘油、油纸、巧克力棒铸造   | 压实机、混合机、发酵机、铸造台 | CDG（+Confectionery） |
| **F 其他联动与杂项** | 彩色纸、漂白纸、造纸台、岩浆/巧克力/蜂蜜、机器合成 | 工作台、风机、机械手、熔炼炉 | Dragons Plus / Big Cannons |

```
■ 物料总览（仅本体产线）：

可浆化物 ──制浆──► Pulp ──精浆──► Fine Pulp ──成形+压制──► 原版纸（成品）
   │碱法蒸煮        │湿浆压实                │（纸板）
   └──► 黑液(污染)  └──► 纸板           ┌──► 粗纸（中间物）
                        └──► 湿浆 ────┘
草木灰/石灰石/煤炭 ──烧制──► 草木灰/生石灰/炭灰 ──► 灰汁 ──苛化──► 钾碱液 + 碳酸钙 ──► 生石灰（循环）
```

## 1. 配方类型（`index/CRRecipeTypes.java`）

| 类型 | 类 | 挂载机器 |
|---|---|---|
| `pulping` | `PulpingRecipe`（继承 BasinRecipe，支持概率输出） | 机械碎浆机 + 盆 |
| `forming` | `FormingRecipe`（继承 StandardProcessingRecipe，实现 `IAssemblyRecipe`，可嵌入顺序装配 Sequenced Assembly） | Headbox / 造纸台+注液口 |
| `enchant_paper_binding` | `EnchantPaperBindingRecipe`（继承 CustomRecipe，**动态输出**配方） | 工作台（≥3 附魔纸 + 1 皮革 → 附魔书） |

注意：`common/recipe/crafting/PaperColoring.java`（纸染色合成配方）存在但**未注册**（`CRRecipeTypes` 中对应枚举被注释；纸染色改由染料合成配方实现，见 §7.4）。

---

## 2. 大类 A · 造纸主线（本体核心）

### 2.1 制浆（`CRPulpingRecipeGen`，机械碎浆机 + 盆）

| 配方 id | 输入 | 输出 | 备注 |
|---|---|---|---|
| `pulp_from_pulpifiable` | `create:pulpifiable` ×4 + 水 1000mB | Pulp **500mB** | 常温简单制浆，产量减半 |
| `pulp_from_pulpifiable_with_heat` | `create:pulpifiable` ×4 + `rationality:alkaline` ×2 + 水 1000mB + **加热** | Pulp 1000mB + **黑液 250mB** | 碱法蒸煮，主推路径 |
| `fine_pulp_from_pulp` | 生石灰 ×2 + Pulp 1000mB | Fine Pulp 1000mB | 精浆（生石灰充当碱源） |
| `syrup` | `rationality:sugar_raw_material` ×4 + 水 1000mB + 加热 | Syrup 1000mB | 糖浆（D 类原料） |

> 碱法蒸煮是产线主推：足量制浆但副产黑液（污染源，见 02 §4 与 03 §3.3）；常温配方仅作低产应急。

**碎浆机兼职配方**（同生成器）：

| 配方 id | 输入 | 输出 | 依赖 |
|---|---|---|---|
| `ground_beef` / `ground_porkchop` / `ground_chicken` | 生牛肉/生猪排/生鸡肉 ×1 | 绞肉 ×3（Butchercraft） | Butchercraft |
| `minced_beef` / `bacon` / `chicken_cuts` | 生肉 ×4 + 水 500mB | 绞肉/培根/鸡块 ×10（FD） | FD 且**无** Butchercraft（两线互斥） |
| `clay_ball` | 粘土 ×1 | 粘土球 ×4 | 无 |
| `sand_from_gravel` | 砾石 ×1 | 沙 + **10%** 燧石 | 无 |
| `glowstone_dust` | 萤石 ×1 | 萤石粉 ×4 | 无 |

### 2.2 成形（`CRFormingRecipeGen`，Headbox / 造纸台+注液口）

| 配方 id | 输入 | 输出 |
|---|---|---|
| `pulp_from_fluid` | 造纸框 + Pulp 100mB | 造纸框（**返还**）+ Create 湿浆（Pulp 物品） |
| `fine_pulp_from_fluid` | 造纸框 + Fine Pulp 100mB | 造纸框（返还）+ `fine_pulp` 物品 |

输出首位即造纸框（返还），可携带概率副产（`getResultItem` 对"输出即输入"配方特判最后一个 100% 概率输出，见 05 §6）。

### 2.3 压制（`CRPressingRecipeGen`，压片机）

| 配方 id | 输入 | 输出 |
|---|---|---|
| `raw_paper` | Create 湿浆（Pulp 物品） | 粗纸（中间物） |
| `paper` | `fine_pulp` 物品 | **原版纸**（成品） |

### 2.4 压实（`CRCompactingRecipeGen`，压实机/盆）

| 配方 id | 输入 | 输出 | 依赖 |
|---|---|---|---|
| `cardboard` | 湿浆（Pulp 物品）×3 | 纸板（Create） | 无 |
| `plant_oil` | `rationality:oil_pressing_raw_material` ×4 | 植物油 500mB（CDG） | CDG |

---

## 3. 大类 B · 化学品与碱回收

### 3.1 灰汁与苛化（`CRMixingRecipeGen`，混合机）

| 配方 id | 输入 | 输出 |
|---|---|---|
| `ash_lye` | 草木灰 ×4 + 水 1000mB | 灰汁 1000mB |
| `slaked_lime` | 生石灰 + 水 100mB | 熟石灰 |
| `calcium_carbonate`（苛化） | 生石灰 + 灰汁 500mB | 碳酸钙 + **钾碱液 500mB** |
| `bone_meal` | **黑液 500mB** + 骨头 | 骨粉 ×6 |

> `bone_meal` 是黑液目前**唯一**的消耗/处理配方（混合机）——黑液除了污染环境之外多了一条回收出口，但需玩家主动投入。

### 3.2 石灰循环（熔烧/烧制，`CRRecipeProvider.buildRecipes`）

| 配方 id | 方式 | 输入 | 输出 |
|---|---|---|---|
| `blasting/quicklime_from_limestone` | 鼓风炉（200 tick） | 石灰石 | 生石灰 ×4 |
| `blasting/quicklime_from_calcium_carbonate` | 鼓风炉（200 tick） | 碳酸钙 | 生石灰（石灰循环回收） |
| `smelting/plant_ash` | 熔炉（100 tick） | `create:pulpifiable` 标签 | 草木灰 |
| `smelting/coal_ash` | 熔炉（200 tick） | `minecraft:coals` 标签 | 炭灰（非碱性，暂无配方用途） |

```
■ 碱回收闭环：
草木灰 ──浸出──► 灰汁 ──苛化(+生石灰)──► 钾碱液（强碱，lye 标签） + 碳酸钙
碳酸钙 ──熔烧──► 生石灰 ──水化──► 熟石灰（碱性物，参与碱法蒸煮）
生石灰 ──苛化消耗──┘（循环复用；石灰石熔烧可补充）
```

### 3.3 黑液处理现状

黑液产出（碱法蒸煮 250mB/次）与消耗（`bone_meal` 500mB/次）之间存在缺口；倒弃会污染区块（1 游戏日，见 02 §4）。暂无浓缩/燃烧发电类回收配方——这是开放的扩展点。

---

## 4. 大类 C · 附魔纸与附魔书（enchanting_paper）

物品 `enchanting_paper`（`CRItems.ENCHANTING_PAPER`，`EnchantingPaperItem`，UNCOMMON）：独立可附魔物品，**不再是"原版纸转换"**——由配方直接合成。

```
■ 产生途径：

  合成：8× 经验粒（EXP_NUGGET，环形）+ 1 原版纸 ──► 1× enchanting_paper（crafting/enchanting_paper）
  附魔台：enchanting_paper 可直接附魔（enchantability=1），等级恒压 1 级、费用 1 青金石
  锯切：附魔书 ──动力锯──► 每个附魔按等级产出 n 张纸（各带等级 1 的该附魔）

■ 消耗：

  ≥3 张带附魔的 enchanting_paper + 1 皮革 ──工作台（enchant_paper_binding）──► 附魔书
     附魔 = 所有纸的总和：同名相加 ≤ maxLevel；等级由张数堆叠（4 张锋利 I 纸 → 锋利 IV 书）
```

### 4.1 机制细节（源码核实）

| 环节 | 实现 | 说明 |
|---|---|---|
| 物品能力 | `EnchantingPaperItem` | `getEnchantmentValue=1`（可进附魔台）；`supportsEnchantment=true`（接受全部附魔）；`isEnchantable=true`；`isBookEnchantable=false`（**不能用附魔书附魔**）；`isFoil` 按 `STORED_ENCHANTMENTS` 显示 glint |
| 附魔台 | `event/EnchantingHandler` | `EnchantmentLevelSetEvent` 压等级=1；`PlayerEnchantItemEvent` 累加累积惩罚：每次附魔 = 1 次铁砧式操作，`REPAIR_COST` ×2+1（1、3、7…2^n−1，第 6 次 63，封顶 2147483647） |
| 锯切拆书 | `mixin/SawInject`（动态配方 `rationality:cutting/enchanted_book`，120 tick） | 注入 `SawBlockEntity.getRecipes` HEAD+cancel；每个附魔按其等级出 n 张纸（`STORED_ENCHANTMENTS` 等级恒 1）；书惩罚按总张数均摊：每张 = ⌈书惩罚÷总张数⌉（书(7) 切 4 张 → 各 2） |
| 绑定合成 | `EnchantPaperBindingRecipe`（CustomRecipe，`enchant_paper_binding.json` 手写仅 type+category） | `matches`：≥3 张纸 + 恰好 1 皮革 + **至少一张纸带附魔**；`assemble`：合并 `ENCHANTMENTS`（同名相加 ≤ maxLevel），书惩罚 = max(纸惩罚)×2+1（全 0 纸 → 1），写 `STORED_ENCHANTMENTS` + glint override |

- **"等级"概念由张数承载**：纸等级恒 1，4 张锋利 I 纸 = 锋利 IV
- **不能直接用于附魔装备**：纸非 `EnchantedBookItem`，铁砧"书→装备"分支不触发（纸+装备不会转移附魔）
- 纸+纸可在铁砧合并（与原版附魔书一致）；铁砧/砂轮花费与合并公式走原版规则（花费=两输入惩罚之和、结果=max×2+1、仅重命名不加、生存 ≥40 级 → Too expensive、砂轮清非诅咒附魔后惩罚 = 2^诅咒数−1）

---

## 5. 大类 D · 食品与甜食

### 5.1 糖浆 Syrup（碎浆机中制取，D 类原料）

`syrup`（碎浆机：糖原料 ×4 + 水 1000mB + 加热 → 1000mB，糖原料 = 甘蔗/甜菜/苹果/甜浆果/紫颂果 + 可选玉米）。下游流向：

```
Syrup ──► 焦糖（5.2：Confectionery 混合 / Cannons 熔炼）
   └──► 乙醇（E 类：CDG 发酵）
```

### 5.2 焦糖线（Confectionery + Corn Delight）

| 配方 id | 机器/类型 | 输入 | 输出 | 依赖 |
|---|---|---|---|---|
| `caramel` | 混合机（加热） | Syrup 1000mB | 焦糖 500mB | Confectionery |
| `caramel_from_item` | Big Cannons 熔炼（加热） | 焦糖棒 | 焦糖 250mB | Cannons + Confectionery |
| `caramel_popcorn` | 注液口填充 | 爆米花 + 焦糖 10mB | 焦糖爆米花 | Corn Delight + Confectionery |

### 5.3 炸油线（Bitterballen + Corn Delight）

| 配方 id | 机器/类型 | 输入 | 输出 | 依赖 |
|---|---|---|---|---|
| `frying_oil` | 混合机（加热） | 植物油 1000mB + 糖 | 炸油 1000mB | Bitterballen |
| `classic_corn_dog` | 注液口填充 | 玉米热狗 + 番茄酱 10mB | 经典玉米热狗 | Corn Delight + Bitterballen |

> 番茄酱由 Bitterballen 产线提供（FD 番茄酱混合配方在 Bitterballen 加载时被禁，见 04-recipe-ban.md）。

### 5.4 肉制品线（Butchercraft / FD 互斥）

| 配方 id | 机器/类型 | 输入 | 输出 | 依赖 |
|---|---|---|---|---|
| `ground_beef` / `ground_porkchop` / `ground_chicken` | 碎浆机 | 生肉 ×1 | 绞肉 ×3 | Butchercraft |
| `minced_beef` / `bacon` / `chicken_cuts` | 碎浆机 | 生肉 ×4 + 水 500mB | 绞肉/培根/鸡块 ×10 | FD 且无 Butchercraft |

### 5.5 猪油与肥皂线（Butchercraft，混合机）

| 配方 id | 输入 | 输出 | 依赖 |
|---|---|---|---|
| `lard_from_heated` | 肥肉 + 加热 | 猪油 ×2 | Butchercraft |
| `lard_from_super_heated` | 肥肉 + **超高温** | 猪油 ×3 + **50%** 猪油 ×1 | Butchercraft |
| `soap` | 猪油 + 花（`minecraft:flowers`）+ 钾碱液 100mB | 肥皂 | Butchercraft（把 B 类钾碱液接进生活用品） |

---

## 6. 大类 E · 能源与油料（CDG 为主）

| 配方 id | 机器/类型 | 输入 | 输出 | 依赖 |
|---|---|---|---|---|
| `plant_oil` | 压实 | 油料 ×4 | 植物油 500mB | CDG |
| `oilpaper` | 混合机 | 纸 + 植物油 10mB | 油纸（UNCOMMON） | CDG |
| `biodiesel` | 混合机 | 植物油 100mB + 乙醇 200mB | 生物柴油 200mB + **甘油 100mB** | CDG |
| `ethanol` | CDG 发酵（basin + bulk） | Syrup 1000mB（400 tick） | 乙醇 1000mB | CDG |
| `black_chocolate_bar` / `caramel` / `ruby_chocolate_bar` / `white_chocolate_bar` | CDG 铸造（手写 JSON，`data/rationality/recipe/casting/`） | 对应巧克力/焦糖流体 250mB + 棒模具 | 对应棒 | CDG + Confectionery |

```
植物油 ──► 油纸（混合）
   └──► 炸油（D 类）──► 经典玉米热狗
   └──► 生物柴油（+乙醇）──► + 甘油（rationality 流体，已注册暂无下游用途）
Syrup ──► 乙醇（发酵）──► 生物柴油原料
熔炼流体 ──► 铸造回收为棒（黑/宝石/白巧克力、焦糖）
```

> 注：`biodiesel` 的甘油与 `yeast_liquid`/`raw_yeast_liquid`（酵母液体）均为已注册流体但**暂无配方使用**（酵母液体连配方生成器都没有），属预留扩展。

---

## 7. 大类 F · 其他联动与杂项

### 7.1 Big Cannons 熔炼（`CRMeltingRecipeGen`，HEATED/SUPERHEATED）

| 配方 id | 输入 | 输出 |
|---|---|---|
| `lava` | 圆石 | 岩浆 1000mB（**超高温**） |
| `chocolate` | Create 巧克力棒 | 巧克力 250mB |
| `honey` | 蜜块 | 蜂蜜 1000mB |
| `black_chocolate` / `ruby_chocolate` / `white_chocolate` | 对应巧克力棒 | 对应流体 250mB |
| `caramel_from_item` | 焦糖棒 | 焦糖 250mB |
| `caramel_from_syrup` | Syrup 1000mB | 焦糖 500mB |

全部带 `whenModLoaded(CANNONS)`；巧克力类叠加 `CONFECTIONERY` 条件。

### 7.2 Dragons Plus 风机染色（`CRColoringRecipeGen`）

| 配方 id | 输入 | 输出 |
|---|---|---|
| `paper`（coloring） | `rationality:color_paper` 任意彩色纸 | 原版纸（漂白，白色） |

### 7.3 机械手装配（`CRItemApplicationRecipeGen`）

| 配方 id | 输入 | 输出 |
|---|---|---|
| `papermaking_depot` | Depot + 造纸框（机械手） | 造纸台 |

### 7.4 彩色纸（`CRRecipeProvider` + `CRItems.COLOR_PAPERS`）

- 合成：原版纸 + 染料 → 对应彩色纸（15 色，**除白色**；`crafting/dye_<color>_paper`）
- 漂白：§7.2 风机染色回原版纸
- 空白蓝图（Create）：1 淡蓝色纸 → `empty_schematic`

### 7.5 机器合成与杂项合成（`CRRecipeProvider`）

| 配方 id | 输入 | 输出 |
|---|---|---|
| `crafting/headbox` | 铜机壳 + 铁栏杆 | Headbox |
| `crafting/pulper` | 齿轮 + 安山机壳 + 3× 推进器 | 机械碎浆机 |
| `crafting/frame_from_string` | 线 + 木棍 | 造纸框 |
| `crafting/frame_from_canvas` | 木棍 + 帆布 | 造纸框（FD 加载时） |
| `crafting/pollution_indicator` | 4× 锌锭 + 红石（十字形） | 污染指示仪 |
| `crafting/enchanting_paper` | 8× 经验粒 + 原版纸（环形） | enchanting_paper（见 §4） |
| `crafting/empty_schematic` | 淡蓝色纸 | 空白蓝图（Create） |

---

## 8. JEI 集成（`compat/jei/CreateRationalityJEI.java`）

- 两个配方分类：**Pulping**（图标：碎浆机+盆）、**Forming**（图标：Headbox+Depot）
- `FormingRecipe` 实现 `IAssemblyRecipe`，`getJEISubCategory()` 返回自定义 `AssemblySpoutingCategory`——成形步骤可嵌入 Create 顺序装配展示
- 动画类：`AnimatedHeadbox`、`AnimatedPulper`（`compat/jei/category/animations/`）

## 9. 改配方须知

1. 配方改在 `data/provider/`，**不要**手改 `src/generated/resources/`
2. 跨模组配方必须 `whenModLoaded(...)`；模组互斥场景（如 FD/Butchercraft 绞肉）用 `whenModMissing(...)` 配对
3. 新配方若与其他模组已有配方冲突，先到 `RecipeTargets` 登记删除（见 04-recipe-ban.md）
4. 改完跑 `./gradlew runData` 重新生成
5. 新增物品/流体记得同步 `zh_cn.json`（手写）——en_us 由 datagen 生成
6. 附魔纸/附魔书逻辑在 `EnchantPaperBindingRecipe` + `SawInject` + `EnchantingHandler` 三处，改动时注意三者对 `ENCHANTMENTS` / `STORED_ENCHANTMENTS` / `REPAIR_COST` 组件的约定一致（见 §4.1）
