# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

Fabric 模组 "Obsidian Craft"（mod id: `obsidian`），关于黑曜石的内容模组。当前包含：`obsidian_ingot`（物品）、`obsidian_block`（方块）、`obsidian_apple`（食物：金苹果饱食度、抗火 30s/抗性 I 10s/缓慢 I 20s、食用 3.2s，贴图暂缺）、5 件黑曜石工具（铁质数值、钻石挖掘等级、镐对黑曜石 +150% 挖掘速度）、4 件黑曜石盔甲（韧性 1、自带火焰保护 2）、**合金炉**（3 输入/1 燃料/1 输出机器，详见下节）、**玫瑰金锭**（3 金 + 1 铜 → 4 锭，8s）。

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
├── ObsidianCraftClient.java      # 客户端入口：MenuScreens.register 合金炉屏幕
├── ObsidianCraftDataGenerator.java  # datagen 入口：开头重跑 init 链 + 注册全部 Provider
├── block/ModBlocks.java          # 方块 + BlockItem 注册
├── block/AlloyFurnaceBlock.java / item/AlloyFurnaceItem.java  # 合金炉方块（AbstractFurnaceBlock）/ 带 Shift 槽位说明的 BlockItem
├── block/entity/                 # ModBlockEntities + AlloyFurnaceBlockEntity（BaseContainerBlockEntity + WorldlyContainer）
├── menu/                         # ModMenuTypes + AlloyFurnaceMenu（extends RecipeBookMenu，槽 0-2 输入/3 燃料/4 输出）
├── recipe/                       # ModRecipeBookCategories / ModRecipeTypes / ModRecipeSerializers / AlloyRecipeDisplay / AlloyFurnaceRecipe / AlloyRecipeInput
├── client/screen/                # AlloyFurnaceScreen（extends AbstractRecipeBookScreen）+ AlloyRecipeBookComponent（在 vanilla recipebook 包）
├── item/ModItems.java            # 物品注册
├── item/ModItemTags.java         # GOLD_MATERIALS / COPPER_MATERIALS tag
├── item/ModCreativeModeTabs.java # 自定义创造模式物品栏
└── datagen/                      # 每个数据维度一个 Provider 类
src/main/generated/               # datagen 产物（提交到 git，勿手改）
```

## 合金炉（Alloy Furnace）

机器：3 个输入槽（方块左/上/右面漏斗 → 槽 0/1/2）、1 燃料槽（正/背面，燃烧时间**减半**）、1 输出槽（底面，只取）。烧所有原版熔炼配方（cook time = 熔炉的 2/5）+ 自定义合金配方（`obsidian:alloy_furnace` recipe type，槽位无关、按材料种类最多者优先、优先于熔炼回退）。

- **配方书**：完整 vanilla 式交互（点击自动填料/Shift 连放/ghost 预览/可合成高亮）。menu extends `RecipeBookMenu`；screen extends `AbstractRecipeBookScreen`（不能 extends AbstractFurnaceScreen——其构造器硬编码 FurnaceRecipeBookComponent）；书组件 extends `RecipeBookComponent<AlloyFurnaceMenu>`，**必须放在 `net.minecraft.client.gui.screens.recipebook` 包**（GhostSlots 的 setInput/setResult 是 protected）。
- **RecipeBookType 枚举不可扩展**：`getRecipeBookType()` 复用 `RecipeBookType.FURNACE`；自定义 tab 靠自定义 `RecipeBookCategory`（注册进 RECIPE_BOOK_CATEGORY registry）+ `TabInfo(ModBlocks.ALLOY_FURNACE.asItem(), ModRecipeBookCategories.ALLOY)`；无搜索 tab（SearchRecipeBookCategory 也是枚举）。
- **自定义 display 类型**：`AlloyRecipeDisplay`（3 输入槽位，FurnaceRecipeDisplay 只有 1 个）注册进 `BuiltInRegistries.RECIPE_DISPLAY`，配方书组件用 `selectRecipes(c -> c.display() instanceof AlloyRecipeDisplay || FurnaceRecipeDisplay)` 过滤。
- **server 端放置逻辑**：`handlePlacement` 手写——合金配方按 per-ingredient counts 从背包贪心移入 3 个输入槽（槽位无关，塞空槽或同物品槽）；熔炼回退走 `ServerPlaceRecipe.placeRecipe`（1x1 网格）。`ServerPlaceRecipe` 只支持每格 1 个，counts>1 必须手写。
- **多配方选择**：`(FabricRecipeManager) level.recipeAccess()).getAllMatches(ModRecipeTypes.ALLOY_FURNACE, input, level)` 遍历取 `ingredients.size()` 最大者（quickCheck 只返回第一个匹配）；熔炼回退保留 `RecipeManager.createCheck(RecipeType.SMELTING)` 快路径。
- **燃料减半**：`level.fuelValues()` 已删，用 `ResolvableNumber.getIntFromItem(stack, DataComponents.COOKING_FUEL, CookingFuel::burnTime, this.getLootContext(level), 0) / 2`（`getLootContext` 是 BaseContainerBlockEntity 的 protected 方法）。
- **配方书贴图**：全局覆盖 `assets/minecraft/textures/gui/recipe_book.png` + `textures/gui/sprites/recipe_book/*`（黑曜石色板重着色，用户已确认）。方块贴图从 26.1 项目复制（`textures/block/alloy_furnace_*`），GUI 贴图 256×256 已在资源里。

**注册时序（datagen 双跑，必踩）**：datagen 的 `onInitializeDataGenerator` 在 `onInitialize` **之后**、且所有 registry **已冻结** 时执行，但会重跑同一 init 链。因此：注册必须写成**静态字段**（类初始化发生在 onInitialize 期间的第一次触达）；**显式 register() 方法会被二次调用 → 抛 "Registry is already frozen"**（`AlloyRecipeDisplay.register` 与 `ModCreativeModeTabs` 都踩过此坑——后者已改静态字段，前者用 containsKey guard 幂等）。datagen 入口的 init 链保留，因为静态字段类第二次只是 no-op。

**注册模式**：`Registry.register(BuiltInRegistries.X, key, value)`，key 用 `ResourceKey.create(Registries.X, ObsidianCraft.id(path))`。新内容 = 在对应 ModXxx 类加静态字段 + 在 `ObsidianCraft.onInitialize` 调 `init()`。

**数据生成**：所有数据文件（模型、语言、配方、掉落表、tag）一律通过 datagen 生成，不手写 JSON。新增内容时在 `ObsidianCraftDataGenerator` 注册对应 Provider。语言文件生成 en_us + zh_cn 双份。

## 26.3 关键 API 约定（与旧版本不同，容易踩坑）

- **物品注册必须 `properties.setId(key)`**——`Registry.register` 不会自动设置，否则翻译 key 解析 NPE。
- **BlockItem 必须 `useBlockDescriptionPrefix()`**——否则翻译 key 是 `item.<ns>.<path>` 而非 `block.<ns>.<path>`，游戏里显示未翻译。
- **挖掘等级机制**：方块 `requiresCorrectToolForDrops()` + 通过 datagen 加入 `#minecraft:mineable/pickaxe` 和 `#minecraft:needs_diamond_tool` 等 tag（`TagAppender.add` 只接受 `ResourceKey<T>`，不接受实例）。
- **创造模式物品栏**：API 在 `fabric-creative-tab-api-v1`（旧 `ItemGroupEvents` 已移除）。自定义 tab：`FabricCreativeModeTab.builder()` + `Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, ...)`。
- **模型 datagen**：`FabricModelProvider`（`net.fabricmc.fabric.api.client.datagen.v1`，build.gradle 已开 `client = true`）。物品用 `generateFlatItem(item, ModelTemplates.FLAT_ITEM)`；方块用 `createTrivialCube(block)`；**26.3 物品模型输出在 `assets/<ns>/items/<id>.json`（不是 models/item/）**，且 BlockItem 的模型由 `ModelProvider.finalizeAndValidate` 自动生成（指向对应 block 模型），无需手写；`createFurnace(block, TexturedModel.ORIENTABLE_ONLY_TOP)` 只生成 blockstate（含 LIT 变体）+ block 模型。26.3 物品模型不需要 item_model 组件。
- **机器方块**：`AbstractFurnaceBlock` 26.3 已无 CODEC/codec()（注册时传 `AlloyFurnaceBlock::new` 工厂）；`BlockEntityType` 无 Builder：`new BlockEntityType<>(factory, Set.of(block))`；ticker 用 capturing lambda（`createFurnaceTicker` 只适配 AbstractFurnaceBlockEntity）；`BaseContainerBlockEntity` 提供 Container/ContainerData 基础设施，燃料判定 `stack.has(DataComponents.COOKING_FUEL)`。
- **配方 datagen**：`FabricRecipeProvider` 覆写 `createRecipeProvider(...)` 返回匿名 `RecipeProvider`，其中 `buildRecipes()` 是 **public**（fabric 扩宽）。
- **掉落表**：`FabricBlockLootSubProvider.dropSelf`；**tag**：`FabricTagsProvider.BlockTagsProvider`。
- **26.3 无工具类/盔甲类**：工具是普通 `Item` + `Tool`/`ToolMaterial` 组件。自定义材料：`new ToolMaterial(incorrectTag, 耐久, 速度, 攻击加成, 附魔等级, 修复Tag)`，挖掘等级由 `incorrectBlocksForDrops` tag 决定（铁质数值+钻石等级 = `BlockTags.INCORRECT_FOR_DIAMOND_TOOL`）。物品用 `Item.Properties.tool(material, mineableTag, 攻击基准, 攻速基准, 禁用格挡秒)` / `.pickaxe/.axe/.shovel/.hoe/.sword(...)`。自定义 `Tool` 规则用 `.component(DataComponents.TOOL, new Tool(List.of(...), 1.0F, 1, true))`；`getMiningSpeed` 返回第一条带速度的匹配规则，加速规则必须放最前。`Tool.Rule.minesAndDrops/deniesDrops/overrideSpeed(HolderSet, ...)`，tag 转 HolderSet 用 `BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK).getOrThrow(tagKey)`（运行时按 datapack tag 惰性解析）。
- **盔甲**：`new ArmorMaterial(耐久系数, Map<ArmorType,Integer>防御, 附魔等级, Holder<SoundEvent>, 韧性, 击退抗性, 修复Tag, ResourceKey<EquipmentAsset>)`（`makeDefense` 顺序 boots, legs, chest, helm, body；铁 = (15, {2,5,6,2,5}, 9)）。物品用 `.humanoidArmor(material, ArmorType.X)`；asset key 用 `ResourceKey.create(EquipmentAssets.ROOT_ID, id)`，需在 `assets/<ns>/equipment/<name>.json` 提供 layers（datapack 外的静态资源）。盔甲物品模型用 `generateTrimmableArmorSet(helm, chest, legs, boots, false, Map.of())`。
- **附魔是纯 datapack registry**（BuiltInRegistries 无 ENCHANTMENT 字段），默认组件里要用 `Item.Properties.delayedComponent(DataComponents.ENCHANTMENTS, ctx -> ...)` 延迟解析（lambda 参数是 `HolderLookup.Provider`，可用 `ctx.getOrThrow(Enchantments.X)`）。item 默认组件对所有获取方式生效（合成/创造都是）。
- **shaped 配方空格符号是 `' '`（空格）**，不是 `_`——`ShapedRecipePattern` 只认 `EMPTY_SLOT = ' '`，`_` 会报 "Pattern references symbol '_'"。
- **工具提示**：数据化行用 `.component(DataComponents.LORE, new ItemLore(List.of(Component.translatable(key))))`——26.3 的 ItemLore 会给每行自动套深紫+斜体样式（`ComponentUtils.mergeStyles` + `Style.applyTo`，行自身已设的颜色/斜体会保留），需要自定义颜色/非斜体时用 `Component.translatable(key).withStyle(s -> s.withColor(Formatting.X).withItalic(false))`。26.3 `appendHoverText` **未废弃**，新签名 `(ItemStack, Item.TooltipContext, TooltipDisplay, Consumer<Component>, TooltipFlag)`（在 `addDetailsToTooltip` 中先于 LORE 调用，顺序在前）；"按住 Shift 显示详情"无 vanilla 机制，在 appendHoverText 里用 **`Minecraft.getInstance().hasShiftDown()`**（26.3 已移除 `Screen.hasShiftDown()` 静态方法，键盘修饰键状态在 Minecraft 实例方法上；仅客户端调用，安全）。
- **26.3 食物（与 1.21 差异大）**：`FoodProperties` 记录只剩 `(nutrition, saturation, canAlwaysEat)`，营养值常量在 `Foods` 类（金苹果 = `Foods.GOLDEN_APPLE`：nutrition 4 / saturation 9.6）；**效果和食用时间都在 `Consumable` 组件**：`Consumable.builder().consumeSeconds(3.2F).animation(ItemUseAnimation.EAT).sound(SoundEvents.GENERIC_EAT).hasConsumeParticles(true).onConsume(new ApplyStatusEffectsConsumeEffect(List.of(new MobEffectInstance(MobEffects.X, ticks, amplifier)))).build()`，注册用 `Item.Properties.food(Foods.X, consumable)`。普通食物 `consumeSeconds = 1.6F`。药水等级：**代码 amplifier = 游戏等级 − 1**（游戏 1 级 = amplifier 0；0 级即无等级）。26.3 药水字段改名：`MOVEMENT_SLOWNESS` → `SLOWNESS`，`DAMAGE_RESISTANCE` → `RESISTANCE`；`ApplyStatusEffectsConsumeEffect` 在 `net.minecraft.world.item.consume_effects` 包。
- **硬编码效果（本 mod 工具/盔甲现状）**：耐久 I = Fabric `Item.Properties.customDamage(CustomDamageHandler)`（hook 所有 `ItemStack.hurtAndBreak(amount, entity, slot)` 路径，覆盖挖矿与受击；handler 里 50% 概率返回 0）。火焰伤害 -20%/件 = mixin `LivingEntity.getDamageAfterMagicAbsorb` RETURN 按 `0.8^pieces`；火焰燃烧时间 -20%/件（叠加） = mixin **`Entity.setRemainingFireTicks`** HEAD 的 `@ModifyVariable`（伤害管线无按 item 减伤 hook，`DamageTypeTags` 在 `net.minecraft.tags` 包；`CallbackInfo` 无 `setReturnValue`，改 void 方法参数只能用 `@ModifyVariable`）。燃烧 mixin 只在"新值 > 当前值"时缩放 `1 - 0.2*pieces` 并 clamp≥1——因为逐 tick 递减 `setRemainingFireTicks(remainingFireTicks - 1)` 也走 setter，不 gate 会让火越烧越久。件数统计统一走 `ModItems.countObsidianArmorPieces(LivingEntity)`。此类效果**不用** ENCHANTMENTS 组件（否则有附魔光泽）。混入目标方法按 dev 名写即可：Loom 1.16+ 禁用 mixin AP 不再生成 refmap，直接在 remapJar 时用 tiny-remapper 重映射注解，产物 jar 里 `mixins.json` 无 `refmap` 字段属正常。

## 用户偏好

- **不要主动生成贴图**——除非用户明确要求。贴图程序都在 `C:\Users\Vastosine\AppData\Local\Temp\obsidian-tools\`：`GenToolTextures.java`（黑曜石工具）、`GenRoseGold.java`（玫瑰金锭：gold_ingot → 粉色金 6 停色板 `{0x5A,0x2B,0x3E}…{0xFC,0xD6,0xDE}`）、`GenRecipeBook.java`（配方书黑曜石重着色：recipe_book.png + sprites/recipe_book/*，约 7 停色板 `{0x12,0x0E,0x1E}…{0xA9,0x8F,0xDE}`，输出覆盖 assets/minecraft，overlay_recipe.png.mcmeta 逐字节复制）。**重着色程序必须 clamp 插值系数 f∈[0,1]**——亮度超过最后 break 时插值会越过最亮停靠，通道值溢出 256 绕回（金锭高光 0.986 → 红色通道变 0 出青色）。盔甲贴图明确不生成（equipment asset JSON 已存在，贴图缺失时盔甲渲染为空）。
- 代码注释一律使用英文。
