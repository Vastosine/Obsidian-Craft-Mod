增加合金炉，要求：
1. 三个输入槽对应方块左上右面（左右以玩家放置视角而不是方块视角）输入，一个输出槽（底面），一个燃料槽（正背面），均用tooltip描述（按住shift显示）
2. 燃料时间减半（相当于高炉）
3. 配方：IFI/F#F/OBO I:obsidian_ingot, F:熔炉，#:高炉，O:黑曜石，B:obsidian_block
4. 贴图请阅读 @src\main\resources\assets\obsidian\textures\gui 生成对应模型文件
5. 增加配方书功能（与原版类似），注意我给的贴图中没有，请添加，可参考原版，颜色可以贴合黑曜石一些
6. 自动适配所有熔炉配方，加工时间为熔炉的2/5
7. 增加玫瑰金配方（需添加物品玫瑰金，以金锭为基础，配色偏粉一些）3金+1铜=4玫瑰金，矿石粗矿均可，加工时间8s，注意合金配方只需材料正确即可，无需在意槽位，且合金配方优先级高于熔炼配方（防止与熔炉配方冲突）（材料种类越多越优先（之后有用））
8. 以下是之前26.1合金炉的CLAUDE.md,一些细节和其他要求可以参考这个, 注意验证是否在本版本有效：
``` md
### The Alloy Furnace (the machine)
Layers, in dependency order:
- **Recipe**: `AlloyFurnaceRecipe` (record: `ingredients` + parallel `counts` + `result` `ItemStackTemplate` + `experience` + `cookingtime`) with `MAP_CODEC`/`STREAM_CODEC`, registered via `ModRecipeTypes`/`ModRecipeSerializers` under `study:alloy_furnace`. Matching is **order-insensitive**: `allocate(input)` merges duplicate ingredient entries, checks material totals across the three slots, and returns the per-slot consume plan (null = no match); `matches()` delegates to it. `AlloyRecipeInput` is the 3-slot `RecipeInput`.
- **Block entity**: `AlloyFurnaceBlockEntity` extends `BaseContainerBlockEntity` + `WorldlyContainer`. Slots: 0-2 ingredients, 3 fuel, 4 output. `serverTick` picks a recipe every tick: alloy first (only if its result fits the output slot, `canBurn`), else the first input slot whose smelting recipe isn't blocked by the output — a blocked higher-priority recipe must not stop a burnable lower-priority one. Ingredient totals use `study:gold_materials` / `study:copper_materials` tags. Smelting time is 2/5 of vanilla, fuel burn 1/2. Hopper mapping (direction = container→hopper): left→slot 0, up→slot 1, right→slot 2, back→fuel, down→output (take only), front→none.
- **Progress reset logic**: the BE tracks `currentRecipe` (a `ResourceKey<Recipe<?>>`, set when a cook completes). `setItem` recomputes `findRecipe` (same "can it burn" rule as serverTick) and resets the timer only when the recipe id actually changes — including when a **count drop** makes the current recipe unmatchable (e.g. taking 2 gold out of 3 mid-cook). Unrelated slot changes keep progress.
- **Persistence**: item contents are saved automatically by `BaseContainerBlockEntity` (CONTAINER component); the four int fields use `loadAdditional`/`saveAdditional` with `ValueInput`/`ValueOutput` (`getShortOr`/`putShort`, snake_case names like vanilla).
- **Block**: `AlloyFurnaceBlock` extends `AbstractFurnaceBlock` (gives LIT/FACING, `use`→open menu, redstone signal); implements `codec()`, `newBlockEntity`, `getTicker` (capturing-lambda form, not a bare method reference), `openContainer`.
- **Menu/Screen**: `AlloyFurnaceMenu` (plain `AbstractContainerMenu`, deliberately not `RecipeBookMenu`/`AbstractFurnaceMenu` — vanilla's are closed to custom 3-input recipes) with vanilla `Slot`s; `AlloyFurnaceScreen` extends `AbstractContainerScreen` and draws the flame/arrow sprites via `blitSprite`.
```