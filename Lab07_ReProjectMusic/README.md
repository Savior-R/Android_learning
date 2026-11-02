# ReProjectMusic（实验七 · 基于 Service 的音乐播放应用）

包名：`cn.edu.sicnu.reproject.music`（sicnu.edu.cn 反域名 + ReProject + music）
应用标题：犹锐2024110544

## 功能

- **ContentResolver 读媒体库**：查询 `MediaStore.Audio.Media`，按标题排序，`IS_MUSIC != 0` 过滤非音乐音频
- **播放控制全部在 Service 中**：播放 / 暂停 / 停止 / 上一首 / 下一首，MediaPlayer 只在 `MusicService` 内创建与释放
- **Activity 通过 Binder 通信**：`bindService` 取得 `MusicBinder` → `getService()`，从而读取歌名、播放进度与播放状态；同时 `startService` 保活，退到后台音乐不中断
- **多线程刷新进度**：Service 内线程每 200ms 读一次 `MediaPlayer.currentPosition`，`Handler.post` 回主线程更新 SeekBar；拖动 SeekBar 松手后 `seekTo` 调整进度
- **Notification 显示歌名**：每开始播放一首新歌发一条常驻通知，内容为"正在播放 / 歌名"

## 权限

| 权限 | 说明 |
|---|---|
| `READ_MEDIA_AUDIO` | API 33+ 读取音乐文件 |
| `READ_EXTERNAL_STORAGE` | API 32 及以下读取音乐文件（`maxSdkVersion="32"`） |
| `POST_NOTIFICATIONS` | API 33+ 发通知 |
| `FOREGROUND_SERVICE` / `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | 后台继续播放 |

## 目录结构

```
app/src/main/java/cn/edu/sicnu/reproject/music/
├── MainActivity.kt            主界面：列表 + 控制面板 + 权限申请
├── MusicApp.kt                Application
├── service/MusicService.kt    Service：MediaPlayer + Binder + 通知 + 进度线程
├── model/
│   ├── Song.kt                一首歌（id / 标题 / 歌手 / 时长 / content URI）
│   └── SongRepository.kt      ContentResolver 读媒体库
├── view/SongAdapter.kt        RecyclerView.Adapter 派生适配器
├── controller/MusicController.kt  界面操作 → Service 调用
└── util/Insets.kt             系统栏（状态栏/导航栏）适配
```

## 运行

```bash
cd Lab07_ReProjectMusic
./gradlew :app:assembleDebug
```

模拟器 / 真机需要有音乐文件才会列出歌曲（可先用 `adb push xx.mp3 /sdcard/Music/` 推几首进去）。
