# Create Rationality — 文档索引

> 本文档集面向开发者/审阅者，内容基于 `src/main/java` 源码核实整理。
> 项目：**Create Rationality** —— Minecraft 1.21.1 NeoForge 上的 Create Addon，工业造纸产线（"Craft papermaking your industrial way!"），含碱法蒸煮、化学品回收与黑液污染机制。

## 文档导航

| 文档 | 内容 | 适合 |
|---|---|---|
| [01-overview.md](01-overview.md) | 项目定位、技术栈、目录结构、设计理念 | 首次接触项目 |
| [02-production-chain.md](02-production-chain.md) | 核心产线（碱法蒸煮→成形→压制）、化学品循环（石灰/碱液）、黑液污染机制 | 想理解玩法与机制 |
| [03-recipes.md](03-recipes.md) | 配方产线总览：按大类（造纸/化学品/附魔纸/食品/能源/联动）组织的全部配方清单、JEI 集成 | 想改/加配方 |
| [04-recipe-ban.md](04-recipe-ban.md) | 配方禁令系统：机制与完整删除清单 | 要覆盖其他模组配方 |
| [05-development.md](05-development.md) | 开发工作流：命令、注册/datagen/mixin/网络规则、已知注意点 | 准备写代码 |

## 建议阅读顺序

```
01-overview ──► 02-production-chain ──► 03-recipes
                                  │          └──► 04-recipe-ban（改配方时必看）
                                  └──► 05-development（动手前必看）
```

- **只想玩/测**：看 01、02
- **想加新配方**：看 03 + 04 + 05 的"新增配方流程"
- **想动机器机制**：看 02 对应小节 + 05

## 速查

```bash
./gradlew build        # 构建
./gradlew runClient    # 启动游戏（QA 用）
./gradlew runData      # 重新生成 src/generated/resources/（改配方后必跑）
```

- 包根：`com.chemiofitor.create.rationality`，模组 ID：`rationality`
- 注册一律走 CreateRegistrate（`CreateRationality.REGISTRATE`）
- `src/generated/resources/` 为已提交的 datagen 输出，**禁止手改**
- 项目为 git 仓库（main 分支）；无单元测试/Lint
- 环境备注：Windows + Git Bash，`run/`、`build/` 等已 gitignore
