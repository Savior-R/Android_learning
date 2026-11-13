# 书籍信息管理 APP（上机测试）— 规格书（Spec）

> 只写「做什么」和「怎么算做对」，不写实现方案（实现写 plan.md）。
> 状态：approved　　负责人：犹锐　　最后更新：2026-11-13

## 已确认决策（2026-11-13 15:28 用户拍板）

| 编号 | 决策项 | 结论 |
|---|---|---|
| Q1 | 仓库子目录 | `Test01_YouRuiProject_BookManager`（原选项为 `Test01_YouReProject_BookManager`，2026-11-13 15:31 按推荐与 Q3 同步改名） |
| Q2 | 工程目录 / 项目名 | `YouRuiBookManager`（含姓名拼音 YouRui，满足题目「项目名包含姓名拼音」） |
| Q3 | 包名 / applicationId | `cn.edu.sicnu.YouRuiProject`（反域名前缀 `cn.edu.sicnu` + `YouRuiProject`；本项覆盖此前「必须包含 YouReProject」的约束） |
| Q4 | 推进方式 | 先出本规格，写完停手等用户逐条审，批准后才进 plan / tasks / implement |

## 1. 目标

交付一个由两个 Activity 组成的书籍信息管理 APP，逐条满足 2026-11-13 上机测试题目、题目截图补充要求与实验报告模板要求，并产出可直接提交的实验报告。

不做会怎样：本次为上机测试，代码与报告均需当场检查，缺任一项即不通过。

## 2. 技术栈

- 语言：Kotlin（沿用本项目历史实验的源码语言）
- 构建：Gradle + Kotlin DSL（`build.gradle.kts`），Android Gradle Plugin
- SDK：compileSdk / targetSdk 36，minSdk 24（沿用历史工程基线）
- JDK：21（`D:/soft/DevEnv/Java/JDK-21`）
- 依赖：仅 AndroidX / Material 基线依赖，不新增第三方库
- 构建镜像：沿用项目既定的阿里云（依赖仓库）与腾讯云（Gradle 发行版）镜像

## 3. 命令

构建（离线）：

```
JAVA_HOME=D:/soft/DevEnv/Java/JDK-21 GRADLE_USER_HOME=D:/soft/DevEnv/Android/.gradle ./gradlew :app:assembleDebug --offline
```

单元测试：

```
JAVA_HOME=D:/soft/DevEnv/Java/JDK-21 GRADLE_USER_HOME=D:/soft/DevEnv/Android/.gradle ./gradlew :app:testDebugUnitTest --offline
```

Lint：

```
JAVA_HOME=D:/soft/DevEnv/Java/JDK-21 GRADLE_USER_HOME=D:/soft/DevEnv/Android/.gradle ./gradlew :app:lintDebug --offline
```

安装与启动（模拟器 `Lab1_AVD`，API 36）：

```
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n cn.edu.sicnu.YouRuiProject/.MainActivity
```

## 4. 项目结构

```
YouRuiBookManager/
├── settings.gradle.kts              依赖仓库与模块声明
├── build.gradle.kts                 根构建脚本
├── gradle.properties                JVM / AndroidX 开关
├── gradle/wrapper/                  固定 Gradle 版本（腾讯云镜像）
├── app/
│   ├── build.gradle.kts             applicationId、SDK、依赖
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml  两个 Activity 声明
│       │   ├── java/cn/edu/sicnu/YouRuiProject/
│       │   │   ├── model/           书籍数据模型与内存数据源
│       │   │   ├── view/            列表项与详情页界面
│       │   │   ├── controller/      排序、点击跳转、参数传递
│       │   │   └── util/            系统栏 / 边距适配
│       │   └── res/                 strings、colors、layout、drawable、mipmap
│       └── test/java/…              JVM 单元测试
├── specs/SPEC-book-manager.md       本规格
└── README.md                        构建与运行说明
```

截图不放进工程：统一落盘到工程外的 `D:\soft\HomeWork\Android\YouRuiBookManager_上机测试_截图\`，按两位序号命名。

## 5. 代码风格

- Kotlin，4 空格缩进；类名 PascalCase、函数与变量 camelCase、常量 UPPER_SNAKE_CASE。
- 采用 MVC 分包：`model` 只放数据与数据源，`view` 只放界面装配，`controller` 只放排序/跳转/传参逻辑，`util` 放通用适配。
- 所有面向用户的文案写入 `res/values/strings.xml`，代码与布局中不出现硬编码中文。
- 颜色统一在 `res/colors.xml` 定义并命名引用，不散落十六进制字面量。
- 单个 Kotlin 文件保持单一职责；公开类与关键方法写一行 KDoc 说明意图。
- 不使用已废弃 API；构建 warning 目标为 0。

## 6. 测试策略

- 框架：JUnit4（`app/src/test/java`），纯 JVM 测试，不依赖模拟器。
- 必测点：
  1. 书籍数据源至少 6 本，字段（书名、作者、出版社、价格）均非空，价格为正数且不全相同；
  2. 按价格升序排序后，相邻两项价格非递减，且元素数量与数据源一致（不丢书、不重复）；
  3. 价格相等时排序结果稳定（同一输入多次排序结果一致）；
  4. 选中书籍向详情页传递后，书名、作者、出版社、价格四项与源对象完全一致。
- UI 层：模拟器实跑，逐条对照第 9 节验收标准截图取证；截图不作为自动化断言，但必须与断言结果一致。
- 覆盖率：`model` 与 `controller` 的关键分支全覆盖；不设百分比硬指标。
- 每次提交前跑全量单元测试。

## 7. 边界与约束

**Always（必须）**

- 先规格后代码；规格变更先改本文件再改实现。
- 按 TDD：先写失败测试并确认失败原因为功能缺失，再写实现。
- 提交前跑全量单元测试；提交遵循 Conventional Commits 与全局门禁。
- 包名/applicationId 固定为 `cn.edu.sicnu.YouRuiProject`；首 Activity 标题固定为「犹锐2024110544」。
- 所有文字为蓝色（学号尾号 4 对应题目规则的蓝色档）。
- 截图必须来自本机模拟器真实运行画面。

**Ask first（先问）**

- 新增任何第三方依赖。
- 修改包名、applicationId、工程目录名、仓库子目录名。
- 调整仓库结构、根 README、.gitignore。
- 删除或移动任何历史工程、历史报告、历史截图。

**Never（禁止）**

- 从 `YouRuiBookManager/` 等工程自身目录直接 push（会覆盖仓库子目录结构）。
- 提交 `build/`、`.gradle/`、`local.properties`、`.idea/`、`.kotlin/`。
- 老师在检查后再次向仓库提交。
- 用示意图、占位图、代码渲染图或旧实验截图冒充运行画面。
- 修改原始报告模板文件；只在副本上填写。
- 编造测试结果、构建结果或截图来源。

**Out of scope（明确不做）**

- 补交缺失的 Lab05 子目录、整改历史实验代码或报告。
- Gitee 之外的平台发布、应用商店上架。
- 题目未要求的扩展功能（搜索、编辑、持久化、网络请求等）。

## 8. 上下文与验证记录（必填）

- **上下文来源**（逐条带路径与行号）：
  - 题目：`D:\soft\HomeWork\Android\试题\<编码乱码子目录>\棰樼洰.docx`，段落 2–15（两个 Activity、Book 属性、数组、价格升序、列表、点击跳转、详情、项目名含姓名拼音、标题姓名+学号、布局与代码规范）。
  - 报告模板：同目录 `涓婃満娴嬭瘯瀹為獙鎶ュ憡妯℃澘.docx`，段落 1–9（表头与要求）、段落 11–38（实验内容 / 核心代码 / 结果分析三节）。
  - 题目截图：提交规则（至少两次 commit、检查后不再提交）、文字颜色按学号尾号（4/5 → 蓝色）、报告提交智慧树、老师当场检查。
  - 项目规则：`C:\Users\19262\.workbuddy\user-f1973496-e20e-48ad-aa37-cb14987cbd47-personal\MEMORY.md:21-28`（需求澄清）、`:31-43`（SDD/TDD、上下文工程、隔离验证、反馈结构）、`:54-61`（提交门禁）；项目级 `D:\soft\HomeWork\Android\.workbuddy\memory\MEMORY.md`（工程与仓库约定）。
  - 用户决策：2026-11-13 15:28 会话（Q1=B、Q2=A、Q3 改 YouRuiProject、Q4=A）。
- **上下文预算 $L_{\max}$**：12k tokens；实际用量在 plan 阶段按切片统计；超出时按「规格 → 题目 → 报告模板 → 源码」优先级压缩，留痕写入本文件本节。
- **可复现测试命令**：见第 3 节单元测试与构建命令。
- **结构化失败信息**：预期 `<断言期望>` / 实际 `<实测值>` / 定位 `<文件:行号>` / 原因类别 `<功能缺失 | 笔误 | 环境>`。
- **迭代记录**：规格第 1 轮完成并获批准（2026-11-13 15:31），共 3 轮上限；2–3 轮后收益递减即转人工。
- **接受子集与修正信号**：接受题目与报告模板全部强制条目为必达集；实现细节（列表控件选型、蓝色具体色值、布局间距）属可自定子集。$\delta_1 = 0$（用户「全部按推荐」，5 项未决问题按推荐落定）。

## 9. 验收标准（EARS，每条可测）

- [ ] WHEN 应用启动, THEN 首个 Activity 的标题栏 SHALL 显示「犹锐2024110544」。（测试：`MainActivity - titleShowsNameAndStudentId`）
- [ ] WHEN 首屏列表加载, THEN SHALL 显示不少于 6 本书，每项 SHALL 含书名、作者、出版社、价格。（测试：`BookRepository - providesAtLeastSixBooksWithAllFields`）
- [ ] WHEN 列表展示, THEN 条目 SHALL 按价格从低到高排列，相邻两项价格非递减。（测试：`BookSorter - sortsByPriceAscending`）
- [ ] WHEN 观察界面上任意文字, THEN 文字颜色 SHALL 为蓝色（截图取色时蓝通道显著高于红、绿通道）。（测试：`Theme - textColorIsBlue`）
- [ ] WHEN 单击任一书籍条目, THEN SHALL 启动第 2 个 Activity。（测试：`MainActivity - clickItemOpensDetail`）
- [ ] WHEN 第 2 个 Activity 打开, THEN SHALL 显示所选书籍的书名、作者、出版社、价格，且四项与列表项一致。（测试：`DetailActivity - showsSelectedBookFields`）
- [ ] WHEN 执行构建, THEN SHALL 构建成功且无 error，warning 数为 0。
- [ ] WHEN 执行全量单元测试, THEN SHALL 全部通过。
- [ ] WHEN 检查工程配置, THEN applicationId SHALL 为 `cn.edu.sicnu.YouRuiProject`，项目名 SHALL 含 `YouRui`。
- [ ] WHEN 检查实验报告, THEN SHALL 含表头（姓名、学号、班级、题目、Gitee 网址）、三节正文、主要代码、真实运行截图及连续编号图题。
- [ ] WHEN 检查仓库, THEN SHALL 有至少两次提交（新建工程后一次、检查前一次），且检查后无新提交。

## 10. 未决问题（Open Questions）

| 问题 | 处理方式 | 截止 |
|---|---|---|
| 仓库子目录名是否随包名同步改为 `Test01_YouRuiProject_BookManager` | **已定**：同步改为 `Test01_YouRuiProject_BookManager`（2026-11-13 15:31） | 已定 |
| 蓝色是否指定具体色值（如 Material Blue 700）还是仅要求「蓝色」 | **已定**：只锁「蓝色」语义，实现取 Material Blue 700（`#1976D2`） | 已定 |
| `specs/` 是否随工程一并进入仓库子目录 | **已定**：纳入仓库子目录 | 已定 |
| 列表控件选型（ListView / RecyclerView） | **已定**：ListView + 自定义 Adapter，零新增依赖 | plan 阶段已定 |
| 报告「成绩」栏是否留空 | **已定**：留空，由老师填写 | 已定 |
