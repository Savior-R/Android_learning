# 翻牌游戏应用（实验四）

Android 移动应用开发 实验四作业。
**犹锐 2024110544** · 计算机科学学院 5 班 · 指导老师 李贵洋

仓库地址：<https://github.com/Savior-R/Android_learning>

---

## 一、功能

三个 Activity，按 MVC 分层：

| 界面 | Activity | 功能 |
|---|---|---|
| 主界面 | `MainActivity` | 显示玩家姓名、学号、性别、电话、邮件、爱好、游戏最高分 |
| 信息修改 | `EditInfoActivity` | 修改上述信息，保存后返回主界面即时回显 |
| 翻牌游戏 | `CardGameActivity` | 16 张牌 4×4 排布，翻牌配对计分，可重置一局 |

## 二、MVC 分层

```
model/         纯 Kotlin，无 Android 依赖，可被 JVM 单元测试直接覆盖
  Card.kt              单张牌 + 配对判定（点数相同 4 分、花色相同 1 分）
  Deck.kt              52 张牌，随机抽牌且不重复
  CardMatchingGame.kt  计分规则：配对 +4×/×4，失败 −2，翻牌成本 −1
  PlayerInfo.kt        玩家信息与最高分刷新

view/
  CardGameView.kt      牌面的三种外观（牌背 / 翻开 / 已配对）与得分显示

controller/
  MainController.kt      主界面字段映射
  EditInfoController.kt  表单与模型之间的双向搬运
  CardGameController.kt  翻牌交互、计分提示、重开一局
```

玩家信息存在 `CardGameApp`（Application 级单例）里，不落盘，符合实验"修改后的信息无需保存到文件中"的要求。

## 三、构建与运行

```bash
# 构建 APK
gradlew assembleDebug

# 产物
app/build/outputs/apk/debug/app-debug.apk

# 安装到已连接的设备 / 模拟器
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

若命令行 `gradlew` 报 `ClassNotFoundException: org.gradle.wrapper.GradleWrapperMain`，
直接用本机已缓存的 Gradle：`~/.gradle/wrapper/dists/gradle-8.13-bin/*/gradle-8.13/bin/gradle.bat`，
或在 Android Studio 中打开工程由 IDE 管理。

## 四、测试

```bash
# 单元测试（JVM，不需要模拟器）—— 21 个用例
gradlew testDebugUnitTest

# UI 测试（需先启动模拟器或连接真机）—— 9 个用例
gradlew connectedDebugAndroidTest
```

单元测试覆盖：

| 测试类 | 用例数 | 内容 |
|---|---|---|
| `CardTest` | 5 | 配对判定的四种组合、toString |
| `DeckTest` | 4 | 52 张、抽牌减少、连续抽取不重复、抽空返回 null |
| `CardMatchingGameTest` | 8 | 初始分数、翻牌成本、收起不扣分、配对成功/失败、已配对锁定 |
| `PlayerInfoTest` | 4 | 默认值、副本独立性、copyFrom、最高分刷新边界 |

由于发牌是随机的，测试不去"猜"某一次点击的分数，而是走规则可推算的路径；
对概率分支采用"最多重开 200 局，命中即断言，从未命中则判失败"的写法。

## 五、环境

| 项目 | 版本 |
|---|---|
| 操作系统 | Windows 11 |
| JDK | OpenJDK 21.0.12 LTS |
| 构建工具 | Gradle 8.13 + AGP 8.13.0 |
| 编译 SDK | Android API 36 |
| 最低版本 | Android 7.0（API 24） |
| 语言 | Kotlin 1.9.24 |
| 包名 | `cn.edu.sicnu.reproject.cardgame` |
