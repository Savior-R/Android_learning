# Lab03_ReProjectMultiActivity —— 实验三 多 Activity（含隐式 Intent 调用相机拍照）

Android 移动应用开发 · 实验三：包含 1 个 MainActivity 与 4 个二级 Activity 的程序。

- 姓名 / 学号：犹锐 / 2024110544
- 指导老师：李贵洋
- 包名（applicationId / namespace）：`cn.edu.sicnu.reproject.multi`
  - `https://www.sicnu.edu.cn/` 的反域名 `cn.edu.sicnu` + `ReProject` + `multi`
  - 之所以在 `cn.edu.sicnu.reproject` 后面再加一段 `multi`，是为了与实验二工程
    （包名 `cn.edu.sicnu.reproject`）区分开：两者 applicationId 不同，
    生成的 APK 可以同时安装到同一台设备上，不会互相覆盖。
- 构建脚本：Kotlin DSL（`build.gradle.kts`），业务代码：Java
- 仓库：https://github.com/Savior-R/Android_learning

## 目录结构

```
Lab03_ReProjectMultiActivity/
├── app/
│   ├── build.gradle.kts                 构建脚本（依赖、版本、applicationId）
│   └── src/main/
│       ├── AndroidManifest.xml          权限、queries 包可见性、5 个 Activity 声明
│       ├── java/cn/edu/sicnu/reproject/multi/
│       │   ├── MainActivity.java        主界面：4 个入口按钮 + 生命周期回调显示
│       │   ├── ParamActivity.java       二级①：接收正向传递的参数
│       │   ├── ResultActivity.java      二级②：输入内容并回传结果
│       │   ├── CameraActivity.java      二级③：隐式 Intent 调用系统相机拍照
│       │   └── StopWatchActivity.java   二级④：StopWatch 计时器
│       └── res/
│           ├── layout/                  5 个界面的 XML 布局
│           ├── values/strings.xml       中文文案（默认）
│           ├── values-en/strings.xml    英文文案
│           ├── mipmap/                  Android 8.0 以下的启动图标兜底
│           └── mipmap-anydpi-v26/       Android 8.0+ 自适应启动图标
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/wrapper/
```

## 功能对照表

| 按钮 | 目标 Activity | 关键知识点 | 实现要点 |
| --- | --- | --- | --- |
| ① 正向传参 | `ParamActivity` | 显式 Intent + `putExtra` / `getStringExtra` | MainActivity 放入姓名、学号、专业、成绩四项参数，二级 Activity 取出并显示 |
| ② 带回传结果 | `ResultActivity` | `startActivityForResult` + `setResult` | 二级 Activity 输入后 `setResult(RESULT_OK, intent)` 并 `finish()`，MainActivity 在 `onActivityResult` 显示 |
| ③ 隐式 Intent 拍照 | `CameraActivity` | 隐式 Intent + 运行时权限 + `onActivityResult` 取 Bitmap | `new Intent("android.media.action.IMAGE_CAPTURE")`，从返回 Intent 的 `data` extra 取缩略图并显示到 ImageView |
| ④ StopWatch 计时 | `StopWatchActivity` | `onSaveInstanceState` / `onCreate` 状态恢复 + `SystemClock.elapsedRealtime()` | 以开机单调时钟为计时基准，旋转屏幕后计时连续不中断 |

## 禁止硬编码字符串

任务书要求「禁止在布局文件和代码中直接出现字符串，请使用字符串资源间接应用」。

本工程的做法：

- 所有界面文案、按钮文字、提示语统一放在 `res/values/strings.xml`（英文版在 `res/values-en/strings.xml`）；
- 布局文件中通过 `@string/xxx` 引用；
- 代码中通过 `getString(R.string.xxx)` 读取；
- 连 **Intent 的 extra key**、**相机动作字符串**、**`onSaveInstanceState` 的 Bundle key**
  也一并放入 `strings.xml`，代码中不出现字面量。

## 隐式 Intent 调相机（对应参考 PDF《采用隐式 Intent 调用相机拍照功能》）

`AndroidManifest.xml` 中需要两处配置：

```xml
<!-- 1) 权限 -->
<uses-permission android:name="android.permission.CAMERA" />

<!-- 2) Android 11+ 包可见性：不声明 queries，隐式 Intent 匹配不到相机应用 -->
<queries>
    <intent>
        <action android:name="android.media.action.IMAGE_CAPTURE" />
    </intent>
</queries>
```

运行时先用 `ContextCompat.checkSelfPermission` 判断 CAMERA 权限，未授予则
`ActivityCompat.requestPermissions` 动态申请，授予后再发隐式 Intent。
拍照成功后系统把缩略图 Bitmap 放在返回 Intent 的 extras 的 `data` 键中，
在 `onActivityResult` 里取出并 `setImageBitmap` 显示。

**「隐式」与「显式」的区别**：显式 Intent 直接写目标组件
（`new Intent(this, ParamActivity.class)`），系统没有选择余地；
隐式 Intent 只给出 action（`new Intent("android.media.action.IMAGE_CAPTURE")`），
由系统从所有声明了该动作的应用中挑选一个来处理，因此换成第三方相机 App 也能正常工作。

## 横竖屏连续计时的实现原理

`StopWatchActivity` **不声明** `configChanges="orientation"`，旋转屏幕时 Activity
正常销毁重建，由 `onSaveInstanceState` / `onCreate` 完成状态保存与恢复：

- `mBaseTime`：最近一次开始 / 恢复计时的 `SystemClock.elapsedRealtime()` 绝对时刻
- `mAccumulated`：本段计时之前已累计的毫秒数
- `mRunning`：当前是否在计时

显示时长由 `currentElapsed()` 计算：

```java
private long currentElapsed() {
    return mRunning ? mAccumulated + (SystemClock.elapsedRealtime() - mBaseTime) : mAccumulated;
}
```

关键在于**基准是开机以来的单调时钟**，而不是从 0 开始累加的局部计数器。
所以即便 Activity 被销毁重建，只要 `mBaseTime` 被保存下来，计时就能接着走，
不会出现「一转屏就归零」的问题。

## 构建方式

工程可以直接用 Android Studio 打开，也可在命令行执行：

```bash
cd Lab03_ReProjectMultiActivity
./gradlew :app:assembleDebug
```

APK 产物路径：`app/build/outputs/apk/debug/app-debug.apk`
