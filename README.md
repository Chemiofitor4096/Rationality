# Create Rationality

> **Craft papermaking your industrial way!** —— Minecraft 1.21.1 · NeoForge 上的 Create（机械动力）Addon，把原版"甘蔗合成纸"替换成一条完整的**工业造纸产线**。

## 特性

- **碱法蒸煮制浆**：机械碎浆机 + 盆，常温简单制浆产量减半，加碱 + 加热的碱法蒸煮足量产浆但副产**黑液**（污染源）
- **流浆成形**：Headbox（传送带低空淋浆）或造纸台 + 注液口，造纸框返还的成形工艺
- **化学品循环（碱回收）**：草木灰 → 灰汁 → 苛化 → 碱液 + 碳酸钙 → 熔烧 → 生石灰，石灰在循环中复用
- **黑液污染机制**：黑液接触区块产生 1 游戏日污染（作物停长、实体中毒），污染指示仪双材质提示
- **附魔纸体系**：独立可附魔的纸、动力锯拆书、多纸绑定成附魔书
- **深度联动**：FD、CDG、Big Cannons、Confectionery、Corn Delight、Bitterballen、Butchercraft、Dragons Plus（详见 [doc/03-recipes.md](doc/03-recipes.md)）
- **配方禁令**：删除原版/其他模组的"快捷"纸配方，纸的生产强制收拢到工业链条（[doc/04-recipe-ban.md](doc/04-recipe-ban.md)）

## 快速开始

```bash
./gradlew build        # 构建
./gradlew runClient    # 启动游戏
./gradlew runData      # 重新生成 src/generated/resources/（改配方后必跑）
```

环境要求：Java 21，NeoForge 21.1.251（MC 1.21.1），依赖版本见 [gradle.properties](gradle.properties)。

## 文档

开发与玩法文档见 [doc/README.md](doc/README.md)（索引），含：

| 文档 | 内容 |
|---|---|
| [01-overview.md](doc/01-overview.md) | 项目总览、技术栈、目录结构、设计理念 |
| [02-production-chain.md](doc/02-production-chain.md) | 核心产线与污染机制 |
| [03-recipes.md](doc/03-recipes.md) | 全部配方清单（按产线大类） |
| [04-recipe-ban.md](doc/04-recipe-ban.md) | 配方禁令系统 |
| [05-development.md](doc/05-development.md) | 开发工作流与规则 |
| [06-roadmap.md](doc/06-roadmap.md) | 后续发展建议 |

## 许可

MIT License（见 [LICENSE.txt](LICENSE.txt)）。
