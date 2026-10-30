# HelloWorldMultiLang · 实验一 多语言版本交互式 Hello World

Android 移动应用开发 实验一 —— 基于 **纯代码（pure code）** 实现的多语言版本交互式 Hello World App。

| 项目 | 内容 |
|---|---|
| 姓名 | 犹锐 |
| 学号 | 2024110544 |
| 班级 | 计算机科学学院 5 班 |
| 指导老师 | 李贵洋 |
| 应用标题 | 犹锐2024110544（英文环境下显示为 You Rui 2024110544） |
| 仓库地址 | https://github.com/Savior-R/Android_learning.git |

---

## 一、实验要求完成情况

| 要求 | 实现方式 | 状态 |
|---|---|---|
| (1) 搭建 Android 开发环境 | JDK 21 + Android Studio + Gradle 8.13 / AGP 8.13.0 | ✅ |
| (2a) 三种语言版本 | 简体中文 / English / Italiano（`values`、`values-en`、`values-it`） | ✅ |
| (2a) 自己下载第 3 种语言国旗资源 | 意大利国旗（自行下载的位图 `flag_italy.png`） | ✅ |
| (2b) 改变 App 图标 | 自适应图标（Adaptive Icon）替换默认图标 | ✅ |
| (3) 不采用布局文件，纯代码实现 | 无 `res/layout`，界面全部由 Java 代码动态创建 | ✅ |
| (4a) adb install 并启动 APP | `adb install` + `adb shell am start` | ✅ |
| (4b) adb 模拟触摸按钮 | `adb shell input tap` 点击国旗切换语言 | ✅ |
| (5) 提交到远程仓库 | https://github.com/Savior-R/Android_learning.git | ✅ |
| 提交要求(1) APP 标题为"姓名+学号" | `@string/app_name` = 犹锐2024110544 | ✅ |

---

## 二、三种语言效果

| 语言 | 资源目录 | 显示文本 |
|---|---|---|
| 简体中文 | `res/values/` | 你好，世界！ |
| English | `res/values-en/` | Hello, World! |
| Italiano | `res/values-it/` | Ciao, Mondo! |

点击界面下方的国旗按钮即可**实时切换**语言，无需重启应用；当前选中的国旗保持不透明，其余国旗变为半透明作为选中反馈。

---

## 三、项目结构

```
HelloWorldMultiLang/
├── app/src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/example/helloworldmultilang/MainActivity.java   ★ 核心代码
│   └── res/
│       ├── drawable/     flag_china.jpg / flag_usa.png / flag_italy.png / 图标
│       ├── mipmap-anydpi-v26/   自适应图标
│       ├── values/       中文（默认）
│       ├── values-en/    英文
│       └── values-it/    意大利文
└── gradle/                构建配置
```

> 注意：本项目**不存在 `res/layout` 目录**，界面完全由 `MainActivity.java` 中的代码构建。

---

## 四、构建与运行

### 1. 环境要求

- JDK 17 及以上（推荐 JDK 21）
- Android SDK，compileSdk 36
- Gradle 8.13（仓库自带 Wrapper，无需单独安装）

### 2. 配置 SDK 路径

在项目根目录创建 `local.properties`：

```properties
sdk.dir=D\:\\soft\\DevEnv\\Android\\Sdk
```

> 请改成你本机 Android SDK 的实际路径。该文件不需要提交到 Git。

### 3. 编译打包

```bash
# Windows
gradlew.bat :app:assembleDebug

# macOS / Linux
./gradlew :app:assembleDebug
```

产物路径：`app/build/outputs/apk/debug/app-debug.apk`

### 4. 安装并运行

```bash
# 查看已连接设备 / 模拟器
adb devices

# 安装 APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 启动应用
adb shell am start -n com.example.helloworldmultilang/.MainActivity
```

### 5. adb 调试（实验要求 4）

```bash
# 进入模拟器显示根目录
adb shell
cd /
ls -l
exit

# 模拟触摸按钮（坐标以实际设备为准）
adb shell input tap 100 760     # 点击中国国旗 → 中文
adb shell input tap 220 760     # 点击美国国旗 → English
adb shell input tap 540 760     # 点击意大利国旗 → Italiano
```

---

## 五、仓库地址

```
https://github.com/Savior-R/Android_learning.git
```

推送步骤：

```bash
git init
git add .
git commit -m "实验一：多语言版本 Hello World（纯代码实现）"
git remote add origin https://github.com/Savior-R/Android_learning.git
git branch -M main
git push -u origin main
```

---

## 六、说明

- `flag_china.jpg`、`flag_usa.png` 为课程提供的国旗图片资源，直接作为界面上的按钮图片使用。
- 第三种语言（意大利语）的国旗为**自行下载**的位图资源 `flag_italy.png`（绿-白-红三色旗）。
- 运行时切换语言使用 `Configuration.setLocale()` + `updateConfiguration()`；在较新 Android 版本上更推荐 `AppCompatDelegate.setApplicationLocales()`。
