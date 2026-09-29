# 04 · 配方禁令系统

> 对应源码：`mixin/BanRecipes.java`、`data/recipe/ban/RecipeTargets.java`

## 1. 机制

`BanRecipes` mixin 注入 `RecipeManager.apply`（HEAD 位置），在**配方 JSON 解析前**直接从数据集中删除目标配方，游戏其余流程完全无感知。日志会打印尝试删除的数量及实际删除了哪些（`CreateRationality.LOGGER.info`）。

```
RecipeManager.apply(Map<ResourceLocation, JsonElement>)
        │  BanRecipes 注入（HEAD）
        ▼
   从 Map 中 remove 掉 RecipeTargets 清单里的 key
        ▼
   正常解析剩余配方
```

删除清单是**静态硬编码**的 `RecipeTargets.getRecipes()`，其中部分条目带 `RationalMods.isLoaded` 条件（对应模组存在才删，避免误删）。

## 2. 完整删除清单

| 命名空间 | 配方 | 原因 |
|---|---|---|
| minecraft | `paper`、`sugar_from_sugar_cane`、`sugar_from_honey_bottle` | 原版快捷纸/糖，逼进工业产线 |
| create | `pressing/cardboard`、`mixing/cardboard_pulp` | 纸板改由 Pulp 压实 |
| create | `crafting/materials/red_sand_paper`、`crafting/materials/sand_paper`、`crafting/schematics/empty_schematic` | 纸相关合成 |
| create | `milling/sugar_cane`、`pressing/sugar_cane` | 甘蔗只能走碎浆 |
| farmersdelight | `paper_from_tree_bark` | 树皮纸 |
| farmersdelight | `integration/create/mixing/tomato_sauce_from_mixing`（**仅 Bitterballen 加载时**） | 番茄酱改由 Bitterballen 产线提供 |
| createdieselgenerators | `bulk_fermenting/pulp` | 纸浆发酵 |
| createdieselgenerators | `bulk_fermenting/fermentable`、`basin_fermenting/fermentable`、`basin_fermenting/fermented_spider_eye` | 糖→乙醇改走 Syrup 发酵 |
| createdieselgenerators | `compacting/plant_oil` | 植物油改走本模组压实配方 |
| corn_delight | `paper_from_corncob`、`caramel_popcorn` | 玉米芯纸、焦糖爆米花重做 |

> **已撤禁（2026-09-26）**：`create_confectionery:caramel_recipe`（糖×2 + 水250 + 加热 → 焦糖）——原禁因为它直连糖浆被绕过；糖线改为阶梯结构（糖浆→糖→焦糖）后，它正是"糖焦化"这一现实步骤，已恢复使用（见 03 §5.1/§5.2）。
| create_bic_bit | `mixing/frying_oil`（**仅 CDG 加载时**） | 炸油改由植物油+糖制作 |

> 注：`create_bic_bit:splashing/paper_recipe`（脏纸洗成纸）**不禁**——纸回收符合模组语言，是合法的再生纸路径（2026-09-25 复核后保留）。

## 3. 使用规则

**判据（2026-09-25 成文）：禁令首先服务于现实世界的真实逻辑。** 产物在现实中如何获得，配方就应当如何获得——回收与再生是合法来源（如 Bitterballen 的脏纸洗纸，**保留**），"非前体一步跳到产物"的直达配方才禁（如甘蔗合成纸、玉米芯纸、树皮纸）。"保护工业产线"是现实判据的自然结果，不是禁令的出发点。

操作规则：

- **新增覆盖配方时，先在这里登记要删的旧配方**，否则会与原配方并存冲突（尤其是同输入的替代配方）
- 只有"同模组/同输入替换"类删除才需要登记；单纯新增新产物配方不需要
- 删除项一律写**原配方 id**（命名空间 + 路径），路径与 Create 数据包内的实际路径一致
- 条件删除用 `RationalMods.X.isLoaded` 包裹，保证无该模组时不误删

**审计方法（接 CraftGraph 后可执行）**：扫描全实例配方，对语言覆盖域的产物（纸、糖、纸板、植物油、乙醇、焦糖、空白蓝图……）逐一核对来源——现实逻辑不成立的来源进清单，现实逻辑成立的旁路（回收/再生/解包）保留。2026-09-25 首次审计结论：现有清单全部通过现实逻辑复核（`caramel_recipe_2` 实为焦糖棒→流体的逆向，合法）；唯一发现是**糖在全实例零来源**（原版制糖已禁、无工业替代，而 31 条配方依赖它，含酿造链的发酵蛛眼）。**2026-09-26 更新（已解决）**：糖的来源由**阶梯结构**补齐——糖浆 →（CCB 冷冻混合冷却结晶）→ 糖 →（糖焦化，原版 `caramel_recipe` 撤禁）→ 焦糖；该来源依赖机巧新生（蔗糖线为其扩展线之一），原版制糖禁令维持。

## 4. 排查提示

- 游戏日志搜 `"Rationality trys to remove"` / `"Remove recipe"` 可确认禁令是否生效
- 若目标模组更新后配方路径变化，删除会静默失效（只影响该配方，不崩溃）——改模组版本后建议核对清单

## 5. 清单核对与维护

- **改清单**：只改 `data/recipe/ban/RecipeTargets.java`（静态硬编码），**不要**在本文档之外另记一份
- **核对路径**：删除项路径必须与目标模组数据包内的实际路径一致——可在游戏内按 F3+H 查 JEI 配方 id，或解压目标模组 jar 对照 `data/<modid>/recipe/`
- **核对时机**：升级任一被禁模组后跑一次游戏看日志；改完本文档表格后与 `RecipeTargets` 逐条比对
- **规则提醒**：只有"同模组/同输入替换"类删除才需要登记；单纯新增新产物配方不需要
