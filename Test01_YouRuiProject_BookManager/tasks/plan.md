# 实现计划（Plan）— 书籍信息管理 APP

## 1. 输入

- 规格：`specs/SPEC-book-manager.md`（状态 approved，2026-11-13）
- 已定决策：ListView + BaseAdapter（零新增依赖）；蓝色取 Material Blue 700 `#1976D2`；包名 `cn.edu.sicnu.YouRuiProject`；工程 `YouRuiBookManager`；仓库子目录 `Test01_YouRuiProject_BookManager`；`specs/` 随工程入库
- 上下文预算：12k tokens/切片

## 2. 组件与依赖方向

`view → controller → model`；`util` 独立；`test` 只依赖 `model` / `controller`。

| 组件 | 位置 | 职责 |
|---|---|---|
| 构建骨架 | `settings.gradle.kts`、根 `build.gradle.kts`、`app/build.gradle.kts`、wrapper | 插件版本、镜像、SDK、依赖 |
| `Book` | `model/Book.kt` | 数据类：书名、作者、出版社、价格 |
| `BookRepository` | `model/BookRepository.kt` | 内存数据源，≥6 本 |
| `BookController` | `controller/BookController.kt` | 价格升序排序、选中书籍字段打包 |
| `BookAdapter` | `view/BookAdapter.kt` | 列表项渲染（四字段，蓝色文字） |
| `MainActivity` | `view/MainActivity.kt` | 标题、ListView、点击跳转 |
| `DetailActivity` | `view/DetailActivity.kt` | 展示选中书籍完整信息 |
| `Insets` | `util/Insets.kt` | API 36 edge-to-edge 顶部内边距 |
| 资源 | `res/values/strings.xml`、`colors.xml`、`themes.xml`、`layout/`、`mipmap*` | 文案、颜色、主题、布局、图标 |

## 3. 实现顺序与验证检查点

| 步骤 | 内容 | 检查点 |
|---|---|---|
| 1 | 工程骨架：构建脚本、Manifest、主题与资源、空壳 Activity | CP1 `assembleDebug` 成功 |
| 2 | 数据层：`Book` + `BookRepository`（TDD） | CP2 数据层测试由红转绿 |
| 3 | 排序：`BookController.sortByPriceAscending`（TDD） | CP3 排序测试由红转绿 |
| 4 | 首屏：`MainActivity` + `BookAdapter` + ListView，标题与蓝色文字 | CP4 首屏可运行 |
| 5 | 跳转与详情：点击传参 + `DetailActivity`（TDD 覆盖字段打包） | CP5 传参测试绿 |
| 6 | 质量收口：lint、warning 清零、全量单元测试、assembleDebug | CP6 构建与测试全绿 |
| 7 | 实跑截图：`Lab1_AVD` 安装运行，按功能点取真实画面 | CP7 截图覆盖验收条目 |
| 8 | 报告：模板副本填表头、三节正文、主要代码、插图与图题 | CP8 报告完整 |
| 9 | 交付：导出干净树到仓库子目录、两次提交、推送、远端校验 | CP9 远端一致且检查后无新提交 |

## 4. 关键技术决策

- **列表控件**：`ListView` + 自定义 `BaseAdapter`。题目只要求「采用列表显示」，ListView 零新增依赖、代码量小，符合「不新增第三方库」的边界。
- **排序**：`sortedBy { it.price }`（Kotlin 稳定排序），价格相等时保持数据源原顺序，便于测试稳定性。
- **传参**：`Intent` 四个字符串/数值 extra（书名、作者、出版社、价格），不引入 Parcelable，详情页逐字段断言。
- **颜色**：`colors.xml` 定义 `text_primary = #1976D2`；`themes.xml` 与列表项布局统一引用，保证「任意文字为蓝色」。
- **edge-to-edge**：API 36 强制，`util/Insets.kt` 给根布局加状态栏高度顶部内边距。
- **图标**：`mipmap-anydpi-v26` 自适应图标 + `res/mipmap/ic_launcher.xml` vector 兜底（minSdk 24）。

## 5. 风险与缓解

| 风险 | 影响 | 缓解 |
|---|---|---|
| Gradle wrapper 缺失或需重新下载 | 无法构建 | 从历史工程复制 `gradlew`、`gradlew.bat`、`gradle/wrapper/*`；沿用腾讯云镜像 |
| 离线依赖缺失 | 构建失败 | 复用 `GRADLE_USER_HOME=D:/soft/DevEnv/Android/.gradle` 缓存；仍失败则去掉 `--offline` 联网一次 |
| 模拟器 adb offline | 无法截图 | 重启模拟器 / `adb reconnect`；必要时改用真机 |
| 构建 warning 非 0 | 验收不通过 | 逐条修复；不使用宽泛 `@Suppress` 掩盖真实告警 |
| 提交门禁拦截 | 推送受阻 | 源码与测试同批提交、Conventional Commits；仅文档类提交可用 `[no-test]` 并附不少于 8 字符理由 |
| 报告图题被全文替换误改 | 报告错误 | 按带上下文的完整图题文本替换，逐条核对 |

## 6. 上下文加载策略

每个切片只加载：规格对应小节 + 当前切片 1–3 个源文件 + 明确路径的参考文件；单切片不超过 3 文件 / 200 行，超出即拆分。超预算时按「规格 → 题目 → 报告模板 → 源码」顺序压缩，留痕写入规格第 8 节。
