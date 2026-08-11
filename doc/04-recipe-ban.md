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
| create_confectionery | `caramel_recipe` | 焦糖改由 Syrup 制作 |
| create_bic_bit | `mixing/frying_oil`（**仅 CDG 加载时**） | 炸油改由植物油+糖制作 |

## 3. 使用规则

- **新增覆盖配方时，先在这里登记要删的旧配方**，否则会与原配方并存冲突（尤其是同输入的替代配方）
- 只有"同模组/同输入替换"类删除才需要登记；单纯新增新产物配方不需要
- 删除项一律写**原配方 id**（命名空间 + 路径），路径与 Create 数据包内的实际路径一致
- 条件删除用 `RationalMods.X.isLoaded` 包裹，保证无该模组时不误删

## 4. 排查提示

- 游戏日志搜 `"Rationality trys to remove"` / `"Remove recipe"` 可确认禁令是否生效
- 若目标模组更新后配方路径变化，删除会静默失效（只影响该配方，不崩溃）——改模组版本后建议核对清单
