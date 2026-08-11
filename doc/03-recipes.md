# 03 · 配方体系

> 对应源码：`index/CRRecipeTypes.java`、`common/recipe/`、`data/provider/`、`data/recipe/ban/`、`compat/jei/`

## 1. 配方类型（`index/CRRecipeTypes.java`）

| 类型 | 类 | 挂载机器 |
|---|---|---|
| `pulping` | `PulpingRecipe`（继承 BasinRecipe，支持概率输出） | 机械碎浆机 + 盆 |
| `forming` | `FormingRecipe`（继承 StandardProcessingRecipe，实现 `IAssemblyRecipe`，支持顺序装配 Sequenced Assembly） | Headbox / 造纸台+注液口 |
| `enchant_paper_binding` | `EnchantPaperBindingRecipe`（继承 CustomRecipe，**动态输出**配方） | 工作台（≥3 附魔纸 + 1 皮革 → 附魔书） |

注意：`common/recipe/crafting/PaperColoring.java`（纸染色合成配方）存在但**未注册**（`CRRecipeTypes` 中对应枚举被注释）。

## 1.5 附魔纸体系（enchanted_paper）

物品 `enchanted_paper`：**原版纸（minecraft:paper）在附魔台附魔后转换的物品**（与书→附魔书的机制一致）。材质复用原版纸，带附魔时 glint；`stacksTo(1)`；普通物品，**不能再被附魔**。

```
■ 产生途径：

原版纸 ──附魔台──► 附魔纸（点击时经 Item.applyEnchantments 转换）
   （EnchantmentMenuMixin 放行纸进入附魔台 + 单附魔限制 + 转换时附魔等级硬压 1；
     EnchantmentHelperMixin 放行纸接受所有附魔；
     EnchantingHandler 用 EnchantmentLevelSetEvent 压费用 = 1 青金石）

附魔书(锋利IV+保护III)
   │ 动力锯切割（SawEnchantedBook mixin，注入 SawBlockEntity.applyRecipe）
   ▼
7 张附魔纸：锋利I×4 + 保护I×3   ← 等级转为数量：每级 1 张纸，等级恒 1
   （空附魔书无附魔可拆，走原逻辑；槽位溢出部分掉落到锯旁）

■ 消耗：

≥3 张附魔纸 + 1 皮革 ──工作台（enchant_paper_binding 配方）──► 附魔书
   附魔 = 所有纸的总和：同名相加 ≤ maxLevel；互斥按输入顺序保留第一个
   （4 张锋利 I 纸 → 锋利 IV 书；等级由张数堆叠，守恒不降级）
```

- **附魔纸等级恒为 1**：附魔台转换与锯切产出都硬性压 1 级；"等级"概念由纸张数量承载
- **不能直接用于附魔**：附魔纸不是 `EnchantedBookItem`，铁砧"书→装备"特殊分支不触发（纸+装备不会转移附魔）
- 附魔纸 + 附魔纸可在铁砧合并（同名物品通用合并，与原版附魔书行为一致）
- 配方 JSON：`src/main/resources/data/rationality/recipe/enchant_paper_binding.json`（手写，仅 `type` + `category`）

## 2. 核心配方

### 碎浆（`CRPulpingRecipeGen`）

| 配方 id | 输入 | 输出 |
|---|---|---|
| `pulp_from_pulpifiable` | 可浆化物 ×4 + 水 1000mB（常温） | Pulp **500mB** |
| `pulp_from_pulpifiable_with_heat` | 可浆化物 ×4 + 碱性物 ×2 + 水 1000mB + **加热**（碱法蒸煮） | Pulp 1000mB + **黑液 250mB** |
| `fine_pulp_from_pulp` | 生石灰 ×2 + Pulp 1000mB | Fine Pulp 1000mB |
| `syrup` | 糖原料 ×4 + 水 1000mB + 加热 | Syrup 1000mB |

> 碱法蒸煮是产线主推路径：足量制浆但副产黑液（污染源，见 02 §4）；常温配方仅作低产应急。

碎浆机兼职配方：绞肉/培根/鸡块（各 4 原料 + 500mB 水 → 10 份，FD）、粘土→4 粘土球、砾石→沙 + 10% 燧石、萤石→4 萤石粉。

### 成形（`CRFormingRecipeGen`）

| 配方 id | 输入 | 输出 |
|---|---|---|
| `pulp_from_fluid` | 造纸框 + Pulp 100mB | 造纸框（返还）+ Create 湿浆 |
| `fine_pulp_from_fluid` | 造纸框 + Fine Pulp 100mB | 造纸框（返还）+ `fine_pulp` 物品 |

### 压制（`CRPressingRecipeGen`）

| 配方 id | 输入 | 输出 |
|---|---|---|
| `raw_paper` | Create 湿浆 | 粗纸 |
| `paper` | `fine_pulp` 物品 | 原版纸（成品） |

### 压实（`CRCompactingRecipeGen`）

| 配方 id | 输入 | 输出 |
|---|---|---|
| `cardboard` | 湿浆 ×3 | 纸板（Create） |
| `plant_oil` | 油料 ×4 | 500mB 植物油（CDG） |

## 3. 化学品配方

### 混合机（`CRMixingRecipeGen`）

| 配方 id | 输入 | 输出 |
|---|---|---|
| `ash_lye` | 草木灰 ×4 + 水 1000mB | 灰汁 1000mB |
| `slaked_lime` | 生石灰 + 水 100mB | 熟石灰 |
| `calcium_carbonate`（苛化） | 生石灰 + 灰汁 500mB | 碳酸钙 + **钾碱液 500mB** |
| `oilpaper` | 纸 + 10mB 植物油 | 油纸（CDG） |
| `frying_oil` | 1000mB 植物油 + 糖 + 加热 | 1000mB 炸油（Bitterballen） |
| `caramel` | 1000mB Syrup + 加热 | 500mB 焦糖（Confectionery） |

### 熔烧/烧制（`CRRecipeProvider.buildRecipes`）

| 配方 id | 方式 | 输入 | 输出 |
|---|---|---|---|
| `blasting/quicklime_from_limestone` | 鼓风炉 | 石灰石 | 生石灰 ×4 |
| `blasting/quicklime_from_calcium_carbonate` | 鼓风炉 | 碳酸钙 | 生石灰（石灰循环回收） |
| `smelting/plant_ash` | 熔炉 | `create:pulpifiable` 标签 | 草木灰 |
| `smelting/coal_ash` | 熔炉 | `minecraft:coals` 标签 | 炭灰 |
| `crafting/pollution_indicator` | 合成（十字形） | 锌锭 + 红石 | 污染指示仪 |

## 4. 跨模组整合

| 生成器 | 机器/类型 | 配方 | 依赖模组 |
|---|---|---|---|
| CRBulkFermentingRecipeGen / CRBasinFermentingRecipeGen | CDG 发酵（两种机器） | 1000mB Syrup → 1000mB 乙醇（400 tick） | CDG |
| CRMeltingRecipeGen | Big Cannons 熔炼（HEATED/SUPERHEATED） | 圆石→岩浆 1000mB（超高温）；巧克力棒→250mB 巧克力；蜜块→1000mB 蜂蜜；黑/宝石/白巧克力棒→250mB 对应流体；焦糖棒→250mB 焦糖；Syrup 1000mB→焦糖 500mB | Cannons（+Confectionery） |
| CRFillingRecipeGen | 注液口填充 | 爆米花 + 10mB 焦糖 → 焦糖爆米花；玉米热狗 + 10mB 番茄酱 → 经典玉米热狗 | Corn Delight + Confectionery/Bitterballen |
| CRColoringRecipeGen | Create Dragons Plus 风机染色 | 彩色纸 → 白纸（漂白） | Dragons Plus |
| CRItemApplicationRecipeGen | 机械手 | Depot + 造纸框 → 造纸台 | 无 |

联动网络（物料视角）：

```
Syrup ──► 乙醇（CDG 发酵）
   └──► 焦糖（Confectionery 混合/熔炼）──► 焦糖爆米花（Corn Delight 填充）
植物油 ──► 油纸（混合）
   └──► 炸油（Bitterballen 混合）──► 玉米热狗淋番茄酱（Corn Delight 填充）
巧克力/蜂蜜/岩浆 ──► Big Cannons 熔炼
彩色纸 ──► 白纸（Dragons Plus 风机染色）
```

## 5. JEI 集成（`compat/jei/CreateRationalityJEI.java`）

- 三个配方分类：**Pulping**、**Forming**、**AssemblySpouting**（顺序装配）
- 动画类：`AnimatedHeadbox`、`AnimatedPulper`
- `FormingRecipe` 实现 `IAssemblyRecipe`，`getJEISubCategory()` 返回 AssemblySpouting 分类，形成步骤可嵌入顺序装配展示

## 6. 改配方须知

1. 配方改在 `data/provider/`，**不要**手改 `src/generated/resources/`
2. 跨模组配方必须 `whenModLoaded(...)`
3. 新配方若与其他模组已有配方冲突，先到 `RecipeTargets` 登记删除（见 04-recipe-ban.md）
4. 改完跑 `./gradlew runData` 重新生成
5. 新增物品/流体记得同步 `zh_cn.json`（手写）——en_us 由 datagen 生成，zh_cn 需手动补条目
