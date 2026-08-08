# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

Fabric 模组 "Obsidian Craft"（mod id: `obsidian`），关于黑曜石的内容模组。当前包含：`obsidian_ingot`（物品）、`obsidian_block`（方块）。

**技术栈（务必不要搞错）**：Minecraft `26.3-snapshot-7`（不是 1.21.x）、Java 25（JAVA_HOME 指向 zulu 25，PATH 上的 java 是 1.8 勿用）、Fabric Loom 1.17、Fabric API 0.156.3、Yarn 映射。官方映射源码位于 `.gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-merged-6974b2190e/26.3-snapshot-7/minecraft-merged-6974b2190e-26.3-snapshot-7-sources.jar`（API 与 1.21.x 差异较大，先查该 jar 再写代码）。

## 常用命令（Windows，用 `./gradlew.bat`）

- `./gradlew.bat runDatagen` — 运行数据生成（模型/语言/配方/掉落表/tag），输出到 `src/main/generated/`，loom 自动纳入资源
- `./gradlew.bat build` — 编译 + 打包 `build/libs/obsidian-1.0.0.jar`
- `./gradlew.bat runClient` — 启动游戏测试
- `./gradlew.bat runServer` — 启动服务器

## 架构

```
src/main/java/com/vastosine/obsidian/
├── ObsidianCraft.java            # 主入口：MOD_ID="obsidian"，id() 静态工厂，onInitialize 中调用各 init()
├── ObsidianCraftClient.java      # 客户端入口（暂空）
├── ObsidianCraftDataGenerator.java  # datagen 入口：注册全部 Provider
├── block/ModBlocks.java          # 方块 + BlockItem 注册
├── item/ModItems.java            # 物品注册
├── item/ModCreativeModeTabs.java # 自定义创造模式物品栏
└── datagen/                      # 每个数据维度一个 Provider 类
src/main/generated/               # datagen 产物（提交到 git，勿手改）
```

**注册模式**：`Registry.register(BuiltInRegistries.X, key, value)`，key 用 `ResourceKey.create(Registries.X, ObsidianCraft.id(path))`。新内容 = 在对应 ModXxx 类加静态字段 + 在 `ObsidianCraft.onInitialize` 调 `init()`。

**数据生成**：所有数据文件（模型、语言、配方、掉落表、tag）一律通过 datagen 生成，不手写 JSON。新增内容时在 `ObsidianCraftDataGenerator` 注册对应 Provider。语言文件生成 en_us + zh_cn 双份。

## 26.3 关键 API 约定（与旧版本不同，容易踩坑）

- **物品注册必须 `properties.setId(key)`**——`Registry.register` 不会自动设置，否则翻译 key 解析 NPE。
- **BlockItem 必须 `useBlockDescriptionPrefix()`**——否则翻译 key 是 `item.<ns>.<path>` 而非 `block.<ns>.<path>`，游戏里显示未翻译。
- **挖掘等级机制**：方块 `requiresCorrectToolForDrops()` + 通过 datagen 加入 `#minecraft:mineable/pickaxe` 和 `#minecraft:needs_diamond_tool` 等 tag（`TagAppender.add` 只接受 `ResourceKey<T>`，不接受实例）。
- **创造模式物品栏**：API 在 `fabric-creative-tab-api-v1`（旧 `ItemGroupEvents` 已移除）。自定义 tab：`FabricCreativeModeTab.builder()` + `Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, ...)`。
- **模型 datagen**：`FabricModelProvider`（`net.fabricmc.fabric.api.client.datagen.v1`，build.gradle 已开 `client = true`）。物品用 `generateFlatItem(item, ModelTemplates.FLAT_ITEM)`；方块用 `createTrivialCube(block)`，BlockItem 的手持模型自动生成。26.3 物品模型不需要 item_model 组件。
- **配方 datagen**：`FabricRecipeProvider` 覆写 `createRecipeProvider(...)` 返回匿名 `RecipeProvider`，其中 `buildRecipes()` 是 **public**（fabric 扩宽）。
- **掉落表**：`FabricBlockLootSubProvider.dropSelf`；**tag**：`FabricTagsProvider.BlockTagsProvider`。

## 用户偏好

- **不要主动生成贴图**——只生成模型 JSON，贴图路径留给用户自己放（方块贴图放 `src/main/resources/assets/obsidian/textures/block/`，物品贴图放 `textures/item/`）。
- 代码注释一律使用英文。
