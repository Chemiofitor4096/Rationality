# AGENTS.md

## Project

**Create Rationality** — a Minecraft **Create addon** (NeoForge-only) that replaces vanilla paper with an industrial papermaking chain (pulping -> forming -> pressing). Mod id: `rationality`. Active development; git repository (main branch).

- Minecraft 1.21.1, NeoForge 21.1.251, Java 21, Parchment mappings 2024.11.17
- Build: Gradle wrapper, `net.neoforged.moddev` 2.0.141
- For Minecraft mod work, load the `minecraft-modder-neoforge` skill.

## Commands (Git Bash on Windows)

- Build: `./gradlew build`
- Run game: `./gradlew runClient` / `./gradlew runServer` (dev runtime in `run/`, gitignored)
- Regenerate data: `./gradlew runData` — writes to `src/generated/resources/`
- No unit tests / lint configured; QA happens in-game or via `runGameTestServer`.
- Dependency versions live in `gradle.properties` (never hardcode in build.gradle).

## Layout

```
src/main/java/com/chemiofitor/create/rationality/
  CreateRationality.java        -- main mod class (REGISTRATE, init, constants)
  CreateRationalityClient.java  -- client-only entry
  index/    -- registration indexes (CRBlocks, CRItems, CRFluids, ...)
  common/   -- block/ (5 blocks), behaviour/, entity/, item/, recipe/, world/
  client/   -- renderer/ (BEs), visual/ (Flywheel)
  compat/   -- jei/ categories, cdg/ (Diesel Generators), RationalMods (mod detection enum)
  data/     -- CRDatagen orchestrator, recipe/ (abstract gens), provider/ (concrete gens)
  mixin/    -- 5 common mixins + 1 client (BeltRenderFrame)
src/main/templates/META-INF/    -- templated neoforge.mods.toml (expanded by generateModMetadata)
src/generated/resources/        -- CHECKED-IN datagen output
doc/                            -- 中文文档集（索引见 doc/README.md）
```

## Rules

- **Registration goes through CreateRegistrate** (`CreateRationality.REGISTRATE`). Index classes follow the pattern: `static final` entries + a `register()` no-op called from the `CreateRationality` constructor.
- **Never hand-edit `src/generated/resources/`** — it is datagen output (plus a committed `.cache`). Change the generator in `data/provider/` or `data/recipe/` and run `./gradlew runData`.
- **读 `doc/README.md` 索引与相关专题（01-overview、02-production-chain、03-recipes、04-recipe-ban、05-development、06-roadmap）再动配方、兼容层或注册。** 文档集内容全部基于源码核实。
- Mixins are declared in `src/main/resources/rationality.mixins.json` with `defaultRequire: 1` — a mixin that fails to apply crashes the game. Keep mixins surgical against Create internals.
- Optional mods must be gated via `RationalMods` detection, not direct references.
- This mod aggressively overrides/bans other mods' paper-related recipes (BanRecipes mixin); keep new recipe logic consistent with that system.
- Java 21, UTF-8 (`options.encoding = 'UTF-8'`). Resource locations via `CreateRationality.asResource(path)`.
- Git repo (main branch). Commit/push only when the user asks. `run/`, `build/`, `repo/`, `.idea/` are gitignored.
- The working tree may contain uncommitted WIP (see doc/README.md「项目状态」); don't treat it as stable/committed state.
