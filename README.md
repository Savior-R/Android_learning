# Android_learning

Android 移动应用开发课程实验代码仓库。

- 姓名 / 学号：犹锐 / 2024110544
- 班级：计算机科学学院 2024 级 5 班
- 指导老师：李贵洋

每个实验各自存放在一个**独立目录**中，互不干扰：目录内是一份完整可独立打开的 Android 工程（含自己的 `settings.gradle.kts`、`build.gradle.kts`、`gradle/wrapper`），可以单独用 Android Studio 打开、单独构建、单独安装。

| 实验 | 目录 | 包名（applicationId） | 应用标题 | 技术要点 |
|---|---|---|---|---|
| 实验二 UI 控件代码生成以及 MVC | [`Lab02_ReProject_UiCodeAndMvc/`](Lab02_ReProject_UiCodeAndMvc) | `cn.edu.sicnu.reproject` | 犹锐2024110544 | 纯代码动态生成界面（无 res/layout）、ScrollView 嵌套 LinearLayout、MVC 三层实现 ProgramAdviser |
| 实验三 多 Activity | [`Lab03_ReProjectMultiActivity/`](Lab03_ReProjectMultiActivity) | `cn.edu.sicnu.reproject.multi` | 犹锐2024110544 | 5 个 Activity、正向传参 / 结果回传、隐式 Intent 调系统相机、StopWatch 横竖屏连续计时 |

两个工程的 `applicationId` 不同（`.multi` 后缀），因此两个 APK 可以同时安装在同一台设备上，不会互相覆盖。

## 两个实验为什么能共存而不冲突

- **目录互相隔离**：每个实验的 Gradle 根目录就是自己的子目录，`settings.gradle.kts` 里的 `rootProject.name` 各自独立，不存在跨目录的 include。
- **包名 / applicationId 不同**：`cn.edu.sicnu.reproject` 与 `cn.edu.sicnu.reproject.multi` 是两个不同的应用。
- **没有共享文件**：两个目录之间不共享任何源码、资源或构建脚本，`local.properties`（本机 SDK 路径）不纳入版本管理，各自在本地生成。

## 如何运行其中某个实验

```bash
# 实验二
cd Lab02_ReProject_UiCodeAndMvc
./gradlew :app:assembleDebug

# 实验三
cd Lab03_ReProjectMultiActivity
./gradlew :app:assembleDebug
```

也可以在 Android Studio 中选择 `File → Open`，直接指向对应的子目录。

## 包名约定

任务书要求包名使用学院域名 `https://www.sicnu.edu.cn/` 的反域名：去掉 `www` 得到 `sicnu.edu.cn`，逐级反转得到 `cn.edu.sicnu`，再拼接 `ReProject`。实验三在其后追加 `multi` 以避免与实验二的 `applicationId` 冲突。
