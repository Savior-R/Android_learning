# Android_learning

Android 移动应用开发课程实验代码仓库。

- 姓名 / 学号：犹锐 / 2024110544
- 班级：计算机科学学院 2024 级 5 班
- 指导老师：李贵洋

每个实验各自存放在一个**独立目录**中，互不干扰：目录内是一份完整可独立打开的 Android 工程（含自己的 `settings.gradle.kts`、`build.gradle.kts`、`gradle/wrapper`），可以单独用 Android Studio 打开、单独构建、单独安装。

## 分支策略

课程任务书对实验五、实验六明确要求使用分支开发，因此本仓库按下列分支组织：

| 分支 | 内容 | 说明 |
|---|---|---|
| `main` | 实验一 ~ 实验四 | 主分支，实验四的翻牌游戏应用（3 个 Activity） |
| `Dev` | `main` + 实验五 | 实验五在 Dev 分支上完成（RecyclerView 翻牌游戏） |
| `Frag` | `Dev` + 实验六 | 实验六在 Dev 分支上新建 Frag 分支完成（多 Fragment + 序列化存储） |

在远端仓库可以同时看到 `main`、`Dev`、`Frag` 三个分支，符合实验五「在 gitee 上能看到两个分支（Main 和 Dev）」与实验六「能看到三个分支（Main、Dev、Frag）」的要求。

## 各实验目录与包名

| 实验 | 目录 | 包名（applicationId） | 应用标题 | 技术要点 |
|---|---|---|---|---|
| 实验一 多语言 Hello World | [`Lab01_HelloWorldMultiLang/`](Lab01_HelloWorldMultiLang) | `com.example.helloworldmultilang` | 犹锐2024110544 | 纯代码界面、zh / en / it 三语言、自适应图标 |
| 实验二 UI 控件代码生成以及 MVC | [`Lab02_ReProject_UiCodeAndMvc/`](Lab02_ReProject_UiCodeAndMvc) | `cn.edu.sicnu.reproject` | 犹锐2024110544 | 纯代码动态生成界面（无 res/layout）、ScrollView 嵌套 LinearLayout、MVC 三层实现 ProgramAdviser |
| 实验三 多 Activity | [`Lab03_ReProjectMultiActivity/`](Lab03_ReProjectMultiActivity) | `cn.edu.sicnu.reproject.multi` | 犹锐2024110544 | 5 个 Activity、正向传参 / 结果回传、隐式 Intent 调系统相机、StopWatch 横竖屏连续计时 |
| 实验四 翻牌游戏应用 | [`Lab04_ReProjectCardGame/`](Lab04_ReProjectCardGame) | `cn.edu.sicnu.reproject.cardgame` | 犹锐2024110544 | 3 个 Activity、MVC 分层、CardMatchingGame 计分、GridLayout 4×4 牌桌、单元测试 21 例 |
| 实验五 RecyclerView 翻牌游戏应用（Dev 分支） | [`Lab05_ReProjectCardGameRv/`](Lab05_ReProjectCardGameRv) | `cn.edu.sicnu.reproject.cardgame.rv` | 犹锐2024110544 | RecyclerView + Adapter、选项菜单与关于对话框、`reset()`、横竖屏 12 / 24 张牌、三张牌配对 |
| 实验六 多 Fragment 应用（Frag 分支） | [`Lab06_ReProjectCardGameFragment/`](Lab06_ReProjectCardGameFragment) | `cn.edu.sicnu.reproject.cardgame.fragment` | 犹锐2024110544 | 3 个 Fragment、`Serializable` + `ObjectOutputStream` 持久化、重启可恢复牌局 |

各工程的 `applicationId` 互不相同，因此多个 APK 可以同时安装在同一台设备上，不会互相覆盖。

## 如何运行其中某个实验

```bash
# 实验四
cd Lab04_ReProjectCardGame
./gradlew :app:assembleDebug

# 实验五（Dev 分支）
cd Lab05_ReProjectCardGameRv
./gradlew :app:assembleDebug

# 实验六（Frag 分支）
cd Lab06_ReProjectCardGameFragment
./gradlew :app:assembleDebug
```

也可以在 Android Studio 中选择 `File → Open`，直接指向对应的子目录。

## 包名约定

任务书要求包名使用学院域名 `https://www.sicnu.edu.cn/` 的反域名：去掉 `www` 得到 `sicnu.edu.cn`，逐级反转得到 `cn.edu.sicnu`，再拼接 `ReProject`。后续实验在其后追加各自的段（`cardgame` / `cardgame.rv` / `cardgame.fragment`），既能看出实验之间的继承关系，又避免 `applicationId` 冲突。

## 环境说明

- JDK 21、Android SDK（compileSdk 36、minSdk 24）、Kotlin 1.9.24
- `local.properties`（本机 SDK 路径）不纳入版本管理，各工程在本地生成
- 构建脚本使用国内镜像（阿里云 Maven、腾讯云 Gradle 发行版）
