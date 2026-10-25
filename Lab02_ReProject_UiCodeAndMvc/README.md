# 实验二 UI 控件代码生成以及 MVC

- 姓名 / 学号：犹锐 / 2024110544
- 包名（namespace = applicationId）：`cn.edu.sicnu.reproject`
- 应用标题（`@string/app_name`）：犹锐2024110544
- 语言：Kotlin；构建脚本：Kotlin DSL（`build.gradle.kts`）

## 实验要求与实现对照

| 任务书要求 | 本工程实现 |
|---|---|
| 区 1：通过交互用**代码动态添加 TextView** | `MainView.buildZone1()` 用代码 new 出按钮条 + `ScrollView`，`addTextView()` 每被调用一次就 `addView` 一个 `TextView` |
| 区 1 需采用 **ScrollView 嵌套 LinearLayout** 才能滚动 | `ScrollView → LinearLayout(textViewContainer)`，动态生成的 TextView 全部加在里层 LinearLayout 上，`fullScroll(FOCUS_DOWN)` 自动跟到最新一条 |
| 区 2：采用 **MVC 模式**完成交互式 ProgramAdviser，按钮响应并查询模型层 | `model/ProgramAdviserModel.kt`（数据 + 查询）、`view/MainView.kt`（界面）、`controller/MainController.kt`（事件与流程），点「确定」→ `model.adviceAt(index)` → 回填 View |
| 禁止在布局文件和代码中直接出现字符串 | 工程**没有 `res/layout` 目录**，界面 100% 由代码生成；全部文案集中在 `res/values/strings.xml`，代码里只有 `R.string.xxx` |
| APP 应用标题为「姓名+学号」 | `strings.xml` 中 `app_name = 犹锐2024110544`，`AndroidManifest.xml` 用 `android:label="@string/app_name"` |

## 目录结构

```
Lab02_ReProject_UiCodeAndMvc/
├── app/
│   ├── build.gradle.kts                      namespace / applicationId / 依赖
│   └── src/main/
│       ├── AndroidManifest.xml               android:label = @string/app_name
│       ├── java/cn/edu/sicnu/reproject/
│       │   ├── MainActivity.kt               装配 MVC 三层，setContentView(代码视图树)
│       │   ├── model/ProgramAdviserModel.kt  Model：语言知识库 + adviceAt() 查询接口
│       │   ├── view/MainView.kt              View：用代码生成区 1 / 区 2 的全部控件
│       │   └── controller/MainController.kt  Controller：绑定事件、查模型、回填视图
│       └── res/
│           ├── drawable/                     启动图标前景 / 背景
│           ├── mipmap-anydpi-v26/            自适应图标（Android 8.0+）
│           ├── mipmap/                       矢量图标兜底（minSdk 24）
│           └── values/                       strings.xml（全部文案）/ colors.xml / themes.xml
├── build.gradle.kts
├── settings.gradle.kts                       阿里云镜像 + rootProject.name = "ReProject"
├── gradle.properties
├── gradle/wrapper/                           Gradle wrapper
├── gradlew / gradlew.bat
└── .gitignore
```

> 注意：本工程**没有** `app/src/main/res/layout/`，这是实验要求「用代码动态生成控件」的直接体现。

## 运行

```bash
cd Lab02_ReProject_UiCodeAndMvc
./gradlew :app:assembleDebug     # 产物 app/build/outputs/apk/debug/app-debug.apk
```

或用 Android Studio 直接打开本目录，运行 `app` 配置。首次构建需要联网下载 Gradle 与依赖（`settings.gradle.kts` 已配好阿里云镜像）。
