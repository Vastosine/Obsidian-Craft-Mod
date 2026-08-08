# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

Fabric 模组 "Obsidian Craft"（mod id: `obsidian`），关于黑曜石的内容模组。当前包含：`obsidian_ingot`（物品）、`obsidian_block`（方块）、5 件黑曜石工具（铁质数值、钻石挖掘等级、镐对黑曜石 +50% 挖掘速度）、4 件黑曜石盔甲（韧性 1、自带火焰保护 2）。

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
- **26.3 无工具类/盔甲类**：工具是普通 `Item` + `Tool`/`ToolMaterial` 组件。自定义材料：`new ToolMaterial(incorrectTag, 耐久, 速度, 攻击加成, 附魔等级, 修复Tag)`，挖掘等级由 `incorrectBlocksForDrops` tag 决定（铁质数值+钻石等级 = `BlockTags.INCORRECT_FOR_DIAMOND_TOOL`）。物品用 `Item.Properties.tool(material, mineableTag, 攻击基准, 攻速基准, 禁用格挡秒)` / `.pickaxe/.axe/.shovel/.hoe/.sword(...)`。自定义 `Tool` 规则用 `.component(DataComponents.TOOL, new Tool(List.of(...), 1.0F, 1, true))`；`getMiningSpeed` 返回第一条带速度的匹配规则，加速规则必须放最前。`Tool.Rule.minesAndDrops/deniesDrops/overrideSpeed(HolderSet, ...)`，tag 转 HolderSet 用 `BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK).getOrThrow(tagKey)`（运行时按 datapack tag 惰性解析）。
- **盔甲**：`new ArmorMaterial(耐久系数, Map<ArmorType,Integer>防御, 附魔等级, Holder<SoundEvent>, 韧性, 击退抗性, 修复Tag, ResourceKey<EquipmentAsset>)`（`makeDefense` 顺序 boots, legs, chest, helm, body；铁 = (15, {2,5,6,2,5}, 9)）。物品用 `.humanoidArmor(material, ArmorType.X)`；asset key 用 `ResourceKey.create(EquipmentAssets.ROOT_ID, id)`，需在 `assets/<ns>/equipment/<name>.json` 提供 layers（datapack 外的静态资源）。盔甲物品模型用 `generateTrimmableArmorSet(helm, chest, legs, boots, false, Map.of())`。
- **附魔是纯 datapack registry**（BuiltInRegistries 无 ENCHANTMENT 字段），默认组件里要用 `Item.Properties.delayedComponent(DataComponents.ENCHANTMENTS, ctx -> ...)` 延迟解析（lambda 参数是 `HolderLookup.Provider`，可用 `ctx.getOrThrow(Enchantments.X)`）。item 默认组件对所有获取方式生效（合成/创造都是）。
- **shaped 配方空格符号是 `' '`（空格）**，不是 `_`——`ShapedRecipePattern` 只认 `EMPTY_SLOT = ' '`，`_` 会报 "Pattern references symbol '_'"。
- **工具提示**：数据化行用 `.component(DataComponents.LORE, new ItemLore(List.of(Component.translatable(key))))`——26.3 的 ItemLore 会给每行自动套深紫+斜体样式（`ComponentUtils.mergeStyles` + `Style.applyTo`，行自身已设的颜色/斜体会保留），需要自定义颜色/非斜体时用 `Component.translatable(key).withStyle(s -> s.withColor(Formatting.X).withItalic(false))`。26.3 `appendHoverText` **未废弃**，新签名 `(ItemStack, Item.TooltipContext, TooltipDisplay, Consumer<Component>, TooltipFlag)`（在 `addDetailsToTooltip` 中先于 LORE 调用，顺序在前）；"按住 Shift 显示详情"无 vanilla 机制，在 appendHoverText 里用 `Screen.hasShiftDown()`（仅客户端调用，安全）。
- **硬编码效果（本 mod 工具/盔甲现状）**：耐久 I = Fabric `Item.Properties.customDamage(CustomDamageHandler)`（hook 所有 `ItemStack.hurtAndBreak(amount, entity, slot)` 路径，覆盖挖矿与受击；handler 里 50% 概率返回 0）。火焰伤害 -10%/件 = mixin `LivingEntity.getDamageAfterMagicAbsorb` RETURN 按 `0.9^pieces`；火焰燃烧时间 -20%/件 = mixin **`Entity.setRemainingFireTicks`** HEAD（伤害管线无按 item 减伤 hook，`DamageTypeTags` 在 `net.minecraft.tags` 包）。燃烧 mixin 只在"新值 > 当前值"时缩放 `0.8^pieces` 并 clamp≥1——因为逐 tick 递减 `setRemainingFireTicks(remainingFireTicks - 1)` 也走 setter，不 gate 会让火越烧越久。件数统计统一走 `ModItems.countObsidianArmorPieces(LivingEntity)`。此类效果**不用** ENCHANTMENTS 组件（否则有附魔光泽）。混入目标方法按 dev 名写即可：Loom 1.16+ 禁用 mixin AP 不再生成 refmap，直接在 remapJar 时用 tiny-remapper 重映射注解，产物 jar 里 `mixins.json` 无 `refmap` 字段属正常。

## 用户偏好

- **不要主动生成贴图**——除非用户明确要求。工具贴图曾按用户要求用 Java 程序从原版贴图重着色生成（`C:\Users\Vastosine\AppData\Local\Temp\obsidian-tools\GenToolTextures.java`，原版形状+亮度映射到黑曜石色板）；盔甲贴图明确不生成（equipment asset JSON 已存在，贴图缺失时盔甲渲染为空）。
- 代码注释一律使用英文。
