# 03 · 配方产线总览

> 对应源码：`index/CRRecipeTypes.java`、`common/recipe/`、`data/provider/`（12 个 CR*RecipeGen + CRRecipeProvider）、`data/recipe/ban/`、`compat/jei/`
>
> 本文按**产线大类**组织全部配方：A 造纸主线 → B 化学品与碱回收 → C 附魔纸循环 → D 食品与甜食 → E 能源与油料 → F 其他联动与杂项。每张配方表均与 `src/generated/resources/data/rationality/recipe/` 逐条核对。

## 0. 产线地图

| 大类 | 核心产物                                           | 主要机器 | 依赖模组 |
|---|----------------------------------------------------|---|---|
| **A 造纸主线** | 纸、粗纸、纸板、纸箱（+ 湿浆/精浆流体）                  | 机械碎浆机+盆、Headbox/造纸台+注液口、压片机、压实机 | 无（本体） |
| **B 化学品与碱回收** | 灰汁、碱液、生石灰、熟石灰、碳酸钙、骨粉、浓缩黑液回收 | 混合机、鼓风炉、熔炉 | 无 |
| **C 附魔纸循环** | 附魔纸（enchanting_paper）、附魔书                 | 附魔台、动力锯、工作台 | 无 |
| **D 食品与甜食** | Syrup、焦糖、炸油、肉制品、猪油、肥皂              | 碎浆机、混合机、注液口 | FD / Butchercraft / Bitterballen / Confectionery / Corn Delight |
| **E 能源与油料** | 植物油、乙醇、生物柴油、甘油、油纸、巧克力棒铸造   | 压实机、混合机、发酵机、铸造台 | CDG（+Confectionery） |
| **F 其他联动与杂项** | 彩色纸、漂白纸、造纸台、岩浆/巧克力/蜂蜜、机器合成 | 工作台、风机、机械手、熔炼炉 | Dragons Plus / Big Cannons |

```
■ 物料总览（仅本体产线）：

可浆化物 ──制浆──► Pulp ──精浆──► Fine Pulp ──成形+压制──► 原版纸（成品）
   │碱法蒸煮        │湿浆压实
   └──► 黑液(污染)  └──► 纸板 ──3×3──► 纸箱（可装载于装置/矿车）
  湿浆（Pulp 物品）──压制──► 粗纸（中间物）
草木灰/石灰石/煤炭 ──烧制──► 草木灰/生石灰/炭灰 ──► 灰汁 ──苛化──► 碱液 + 碳酸钙 ──► 生石灰（循环）
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
| `pulp_from_pulpifiable` | `create:pulpifiable` ×4 + 水 1000mB | Pulp **500mB** | 常温制浆，产量减半、不产黑液（无碱线时的过渡） |
| `pulp_from_pulpifiable_with_heat_and_lye` | `create:pulpifiable` ×4 + **碱液 1000mB** + 加热 | Pulp 1000mB + **黑液 250mB** | **唯一碱法路径**（现实白液蒸煮）；制碱是制浆的前置 |
| `fine_pulp_from_pulp_and_lye` | Pulp 1000mB + **碱液 500mB** + 加热 | Fine Pulp 1000mB + **黑液 100mB** | 碱抽提精浆（溶出残余木质素→废液） |

> 2026-09-26 精简（设计母文档 07 §6-1 定案）：删除"物品碱（2 碱性物）"与"灰汁 1500"两条制浆变体——三条输出相同而物品碱成本严格最低，属纯支配关系（另两条成 JEI 噪音），且不符合现实（蒸煮液只有液体白液）。连带：`rationality:alkaline` 标签失去蒸煮用途（去留见 07）；灰汁唯一去向=苛化。
> 碱法蒸煮产黑液（污染源，见 02 §4 与 03 §3.3）；精浆同样副产黑液（守恒修正）。

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
| `calcium_carbonate`（苛化） | 生石灰 + 灰汁 500mB | 碳酸钙 + **碱液 500mB** |
| `bone_meal` | **黑液 250mB** + 骨头 | 骨粉 ×3 |

> `bone_meal` 是黑液的**零设备兜底**减量出口（混合机）：产量＝原版（1 骨 → 3 粉），不放大、不回收碱，只是把黑液消耗掉；骨粉也**不再是碱性物**（`alkaline` 标签只剩草木灰/生石灰/熟石灰，见 3.3）。要回收化学碱走 3.3 的回收闭环。

### 3.2 石灰循环（熔烧/烧制，`CRRecipeProvider.buildRecipes`）

| 配方 id | 方式 | 输入 | 输出 |
|---|---|---|---|
| `blasting/quicklime_from_limestone` | 鼓风炉（200 tick） | 石灰石 | 生石灰 ×4 |
| `blasting/quicklime_from_calcium_carbonate` | 鼓风炉（200 tick） | 碳酸钙 | 生石灰（石灰循环回收） |
| `smelting/plant_ash` | 熔炉（100 tick） | `create:pulpifiable` 标签 | 草木灰 |
| `smelting/coal_ash` | 熔炉（200 tick） | `minecraft:coals` 标签 | 炭灰（非碱性，暂无配方用途） |

```
■ 碱回收闭环：
草木灰 ──浸出──► 灰汁 ──苛化(+生石灰)──► 碱液（强碱，lye 标签） + 碳酸钙
碳酸钙 ──熔烧──► 生石灰 ──水化──► 熟石灰（碱性物，参与碱法蒸煮）
生石灰 ──苛化消耗──┘（循环复用；石灰石熔烧可补充）
```

### 3.3 黑液回收闭环（F1，`CRMixingRecipeGen`，混合机）

黑液是碱法蒸煮副产（250mB/次）、也是**污染源**（倒弃污染区块 1 游戏日，见 02 §4）。回收把"污染是错位的资源"落地：**浓缩 → 苛化回碱**，浓缩黑液为**安全形态**（普通 `LiquidBlock`，不产生污染）；苛化**量守恒**（浓缩 250→100、苛化 100→100，体积不虚增）：

| 配方 id | 输入 | 输出 | 说明 |
|---|---|---|---|
| `concentrate_black_liquor` | 黑液 250mB + **加热** | **浓缩黑液 100mB** | 蒸发水，浓缩 2.5×；浓缩液无污染 |
| `lye_from_concentrated_black_liquor` | 浓缩黑液 100mB + 生石灰 | **碱液 100mB** + 碳酸钙 ×1 | 苛化回碱（量守恒 1:1）；碳酸钙熔烧回生石灰，维持石灰循环 |
| `bone_meal`（低门槛） | 黑液 250mB + 骨头 | 骨粉 ×3 | 兜底减量出口；产量＝原版（1 骨 → 3 粉），**不做放大** |

- **骨粉不再是碱性物**（`CRDatagen` 已从 `alkaline` 标签移除）：否则"黑液 → 骨粉 → 蒸煮用碱"会绕开整条草木灰/石灰线；碱性物现在只有草木灰、生石灰、熟石灰三种
- 岩浆与黑液的流体交互（`CRFluids.registerFluidInteractions`）：**岩浆源**接触黑液 → 黑曜石、**流动岩浆**接触黑液 → 煤矸石方块（与"岩浆+水"同构：**被替换/消耗的是岩浆，黑液只是触媒、不消耗**）→ 粉碎 → `crushed_coal_gangue`（燃料）。煤矸石粉定位已定（2026-09-26）：劣质燃料 + 可无限刷的黑色石料，**不接化工下游**，`c:crushed_raw_materials` 碎矿标签已摘除

> **回报率**（以一次碱液版蒸煮碱入 1000mB 为基准）：苛化回补 **10%**（量守恒下浓缩/苛化均不虚增，其余碱并入损失），骨粉 **0%**；碱损失永不归零，草木灰仍是最终碱源——详见 06 F1。

---

## 4. 大类 C · 附魔纸与附魔书（enchanting_paper）

物品 `enchanting_paper`（`CRItems.ENCHANTING_PAPER`，`EnchantingPaperItem`，UNCOMMON）：独立可附魔物品，**不再是"原版纸转换"**——由配方直接合成。

```
■ 产生途径：

  合成：8× 经验粒（EXP_NUGGET，环形）+ 1 原版纸 ──► 1× enchanting_paper（crafting/enchanting_paper）
  附魔台：enchanting_paper 可直接附魔（enchantability=1），等级恒压 1 级、费用 1 青金石
  锯切：附魔书 ──动力锯──► 每个附魔按等级产出 n 张**独立**纸（不可堆叠，各带等级 1 的该附魔）

■ 消耗：

  ≥3 张带附魔的 enchanting_paper + 1 皮革 ──工作台（enchant_paper_binding）──► 附魔书
     附魔 = 所有纸的总和：同名相加 ≤ maxLevel；等级由张数堆叠（4 张锋利 I 纸 → 锋利 IV 书）
```

> 附魔纸 `stacksTo(1)`（同附魔书）：1 张 = 1 格 = 1 级，与绑定配方"每格 1 张计 1 级"的计数方式一致，张数守恒不降级。锯切产出的纸全部落在动力锯输出槽（共 31 格），单本书附魔总等级 >31 时超出部分会丢失（正常书籍远低于此上限）。

### 4.1 机制细节（源码核实）

| 环节 | 实现 | 说明 |
|---|---|---|
| 物品能力 | `EnchantingPaperItem` | `stacksTo(1)`（不可堆叠，同附魔书）；`getEnchantmentValue=1`（可进附魔台）；`supportsEnchantment=true`（接受全部附魔）；`isEnchantable=true`；`isBookEnchantable=false`（**不能用附魔书附魔**）；glint 走默认逻辑（`ENCHANTMENTS` 非空即发光） |
| 附魔台 | `event/EnchantingHandler` | `EnchantmentLevelSetEvent` 压等级=1；`PlayerEnchantItemEvent` 累加累积惩罚：每次附魔 = 1 次铁砧式操作，`REPAIR_COST` ×2+1（1、3、7…2^n−1，第 6 次 63，封顶 2147483647） |
| 锯切拆书 | `mixin/SawInject`（动态配方 `rationality:cutting/enchanted_book`，120 tick） | 注入 `SawBlockEntity.getRecipes` HEAD+cancel；每个附魔按其等级出 n 张**独立**纸（每张一个输出、`ENCHANTMENTS` 等级恒 1）；书惩罚按总张数均摊：每张 = ⌈书惩罚÷总张数⌉（书(7) 切 4 张 → 各 2） |
| 绑定合成 | `EnchantPaperBindingRecipe`（CustomRecipe，`enchant_paper_binding.json` 手写仅 type+category） | `matches`：≥3 张纸 + 恰好 1 皮革 + **至少一张纸带附魔**；`assemble`：合并 `ENCHANTMENTS`（同名相加 ≤ maxLevel），书惩罚 = max(纸惩罚)×2+1（全 0 纸 → 1），写 `STORED_ENCHANTMENTS` + glint override |

- **"等级"概念由张数承载**：纸等级恒 1 且不可堆叠，1 张占 1 格，4 张锋利 I 纸 = 锋利 IV
- **不能直接用于附魔装备**：纸非 `EnchantedBookItem`，铁砧"书→装备"分支不触发（纸+装备不会转移附魔）
- 纸+纸可在铁砧合并（与原版附魔书一致）；铁砧/砂轮花费与合并公式走原版规则（花费=两输入惩罚之和、结果=max×2+1、仅重命名不加、生存 ≥40 级 → Too expensive、砂轮清非诅咒附魔后惩罚 = 2^诅咒数−1）

---

## 5. 大类 D · 食品与甜食

### 5.1 糖浆 Syrup 与砂糖（阶梯结构，混合机 + CCB 冷却）

`syrup`（**混合机**：糖原料 ×4 + 水 1000mB + 加热 → 1000mB；糖原料 = 甘蔗/甜菜/苹果/甜浆果/紫颂果 + 可选玉米）。2026-09-26 从碎浆机迁至混合机——取汁是**浸出**不是打浆（操作正确性）；迁移后甘蔗不再"一料两解"：碎浆机里的甘蔗只出纸浆，混合机里的糖原料只出糖浆。

**糖的阶梯结构**（2026-09-26 定案并落地）：`糖浆 →（冷却结晶）→ 糖 →（加热焦化）→ 焦糖`——两个相变方向相反（冷/热），玩家从条件即可分辨，不再有"同机同输入"的平行歧义。

| 配方 id | 机器/类型 | 输入 | 输出 | 依赖 |
|---|---|---|---|---|
| `sugar_from_syrup`（手写 JSON `data/rationality/recipe/chilled_mixing/`） | **CCB 冷冻混合**（盆 + 已充能 Breeze Cooler） | Syrup 250mB | 砂糖 ×2 | 机巧新生（`whenModLoaded` 门控） |
| `create_confectionery:caramel_recipe`（**已撤禁**，原版配方） | 混合机（加热） | 砂糖 ×2 + 水 250mB | 焦糖 250mB | Confectionery |
| `caramel_from_sugar` | Big Cannons 熔炼（加热） | 砂糖 ×2 | 焦糖 250mB | Cannons + Confectionery |

> **依赖说明**：砂糖的工业来源依赖**机巧新生（CCB）的冷却系统**——CCB 缺席时糖线不可用（07 §4.4 的前置依赖线）。原版制糖禁令维持（04）；此前因与焦糖重叠而撤下的两条结晶配方已被本阶梯结构取代。

### 5.2 焦糖线（Confectionery + Corn Delight）

焦糖由**糖焦化**而来（阶梯第二级，糖浆不再直连）；`caramel`（Syrup→焦糖）与 `caramel_from_syrup` 两条旧配方已删除。

| 配方 id | 机器/类型 | 输入 | 输出 | 依赖 |
|---|---|---|---|---|
| `create_confectionery:caramel_recipe`（原版，已撤禁） | 混合机（加热） | 砂糖 ×2 + 水 250mB | 焦糖 250mB | Confectionery |
| `caramel_from_sugar` | Big Cannons 熔炼（加热） | 砂糖 ×2 | 焦糖 250mB | Cannons + Confectionery |
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
| `soap` | 猪油 + 花（`minecraft:flowers`）+ 碱液 100mB | 肥皂 | Butchercraft（把 B 类碱液接进生活用品） |

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
| `crafting/cardboard_crate` | Create 纸板 ×9（3×3 满铺） | 纸箱 cardboard_crate（9 格存储，可装载于装置/矿车） |
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
4. 改完跑 `./gradlew runData` 重新生成，并核对本表与生成物逐条一致
5. 新增物品/流体记得同步 `zh_cn.json`（手写）——en_us 由 datagen 生成
6. 附魔纸/附魔书逻辑在 `EnchantPaperBindingRecipe` + `SawInject` + `EnchantingHandler` 三处，**组件约定**：纸（附魔台附魔与锯切产物）统一用 `ENCHANTMENTS`，附魔书用 `STORED_ENCHANTMENTS`；`REPAIR_COST` 三处都读写同一组件（见 §4.1）
7. **配方 id 命名与输入语义保持一致**（如用灰汁就写 `ash_lye`、用碱液就写 `lye`）——此前 WIP 中 `with_heat_and_lye`/`with_heat_and_ash_lye` 的输入互换已修复（R2），后续新增配方时注意
