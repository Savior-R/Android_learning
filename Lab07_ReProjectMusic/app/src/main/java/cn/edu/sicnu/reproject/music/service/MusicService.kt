package cn.edu.sicnu.reproject.music.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import cn.edu.sicnu.reproject.music.R
import cn.edu.sicnu.reproject.music.model.Song

/**
 * 实验七 · 音乐播放 Service（实验核心）
 *
 * 对应实验要求：
 *  - (2a) 所有与播放相关的功能都在 Service 中实现：MediaPlayer 只活在 Service 里，
 *         Activity 不直接接触 MediaPlayer；
 *  - (2b) Activity 通过 bindService 拿到 [MusicBinder]，再由 Binder 取得本实例，
 *         从而读取歌名、播放进度、播放状态；
 *  - (3)  Service 内部另起一条线程每 200ms 读一次 MediaPlayer.currentPosition，
 *         通过 Handler 切回主线程回调 [onProgress]，SeekBar 由此实时刷新；
 *         Activity 拖动 SeekBar 时调用 [seekTo] 调整进度；
 *  - (4)  每开始播放一首新歌就发一条 Notification，把歌名显示在通知栏。
 *
 * Service 同时用 startService 与 bindService 两种方式启动：
 * startService 保证解绑后仍能在后台继续播放，bindService 提供 Binder 通道。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class MusicService : Service() {

    /** 绑定服务时返回的 Binder：Activity 通过它拿到 Service 实例 */
    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    private val binder = MusicBinder()

    /** 唯一的 MediaPlayer 实例，随 Service 创建与销毁 */
    private var player: MediaPlayer? = null

    /** 当前播放列表与下标 */
    private val playlist = mutableListOf<Song>()
    private var currentIndex = -1

    /** 播放进度回调（position, duration），由 Activity 设置 */
    var onProgress: ((position: Int, duration: Int) -> Unit)? = null

    /** 播放状态变化回调（是否正在播放, 当前歌曲） */
    var onPlayStateChanged: ((isPlaying: Boolean, song: Song?) -> Unit)? = null

    /** 主线程 Handler：把工作线程读到的进度切回 UI 线程 */
    private val mainHandler = Handler(Looper.getMainLooper())

    /** 进度轮询线程 */
    private var progressThread: Thread? = null

    /** 线程运行标志，用 volatile 保证跨线程可见 */
    @Volatile
    private var progressRunning = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder = binder

    /** 同时支持 startService，保证解绑后 Service 仍存活、音乐不中断 */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onDestroy() {
        stopProgressLoop()
        player?.release()
        player = null
        cancelNotification()
        super.onDestroy()
    }

    // ---------------- 对外提供的播放能力 ----------------

    /** 设置播放列表（首次进入或刷新媒体库时调用） */
    fun setPlaylist(songs: List<Song>) {
        playlist.clear()
        playlist.addAll(songs)
        if (currentIndex >= playlist.size) currentIndex = playlist.size - 1
    }

    /** 当前播放列表 */
    fun songs(): List<Song> = playlist.toList()

    /** 当前歌曲；还没开始播放时返回 null */
    fun currentSong(): Song? =
        if (currentIndex in playlist.indices) playlist[currentIndex] else null

    /** 当前下标 */
    fun currentIndex(): Int = currentIndex

    /** 是否正在播放 */
    fun isPlaying(): Boolean = player?.isPlaying == true

    /** 当前播放位置（毫秒） */
    fun position(): Int = player?.currentPosition ?: 0

    /** 当前歌曲总时长（毫秒） */
    fun duration(): Int = player?.duration ?: 0

    /** 播放指定下标的歌；重复点击同一首时相当于“暂停/继续” */
    fun playAt(index: Int) {
        if (index !in playlist.indices) return
        if (index == currentIndex && player != null) {
            toggle()
            return
        }
        currentIndex = index
        prepareAndStart(playlist[index])
    }

    /** 播放 / 暂停切换 */
    fun toggle() {
        val p = player ?: return
        if (p.isPlaying) {
            p.pause()
            stopProgressLoop()
        } else {
            p.start()
            startProgressLoop()
        }
        notifyState()
    }

    /** 暂停 */
    fun pause() {
        player?.takeIf { it.isPlaying }?.pause()
        stopProgressLoop()
        notifyState()
    }

    /** 继续播放 */
    fun resume() {
        player?.takeIf { !it.isPlaying }?.start()
        startProgressLoop()
        notifyState()
    }

    /** 停止：释放播放器并清掉通知，回到“未播放”状态 */
    fun stop() {
        stopProgressLoop()
        player?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        player = null
        currentIndex = -1
        cancelNotification()
        onProgress?.invoke(0, 0)
        notifyState()
    }

    /** 下一首 */
    fun next() {
        if (playlist.isEmpty()) return
        val nextIndex = if (currentIndex + 1 < playlist.size) currentIndex + 1 else 0
        currentIndex = nextIndex
        prepareAndStart(playlist[nextIndex])
    }

    /** 上一首 */
    fun previous() {
        if (playlist.isEmpty()) return
        val prevIndex = if (currentIndex - 1 >= 0) currentIndex - 1 else playlist.size - 1
        currentIndex = prevIndex
        prepareAndStart(playlist[prevIndex])
    }

    /** 拖动 SeekBar 时调整播放进度 */
    fun seekTo(ms: Int) {
        val p = player ?: return
        val target = ms.coerceIn(0, p.duration)
        p.seekTo(target)
        onProgress?.invoke(p.currentPosition, p.duration)
    }

    // ---------------- 内部实现 ----------------

    /** 准备并开始播放一首新歌 */
    private fun prepareAndStart(song: Song) {
        player?.release()
        player = null
        player = MediaPlayer().apply {
            setDataSource(applicationContext, song.uri)
            setOnCompletionListener { next() }   // 播放完自动下一首
            prepare()
            start()
        }
        showNotification(song)
        startProgressLoop()
        notifyState()
    }

    /**
     * 多线程刷新进度。
     * 新起一条线程每 200ms 读一次 MediaPlayer 的当前位置，
     * 再用 Handler.post 切回主线程通知界面，避免在工作线程直接操作 UI。
     */
    private fun startProgressLoop() {
        if (progressRunning) return
        progressRunning = true
        progressThread = Thread {
            while (progressRunning) {
                val p = player
                if (p != null && p.isPlaying) {
                    val position = p.currentPosition
                    val total = p.duration
                    mainHandler.post { onProgress?.invoke(position, total) }
                }
                try {
                    Thread.sleep(PROGRESS_INTERVAL_MS)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }.apply { start() }
    }

    private fun stopProgressLoop() {
        progressRunning = false
        progressThread?.interrupt()
        progressThread = null
    }

    private fun notifyState() {
        onPlayStateChanged?.invoke(isPlaying(), currentSong())
    }

    // ---------------- Notification ----------------

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = getString(R.string.notification_channel_desc) }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    /** 新播放一首歌时，把歌名显示在通知栏 */
    private fun showNotification(song: Song) {
        val manager = getSystemService(NotificationManager::class.java) ?: return
        // Android 13+ 需要通知权限，未授予时静默跳过（不影响播放）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && !manager.areNotificationsEnabled()
        ) return

        val intent = Intent(this, cn.edu.sicnu.reproject.music.MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentTitle(getString(R.string.notification_playing))
            .setContentText(song.title)          // 当前歌曲的名字
            .setSubText(song.subtitle())
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun cancelNotification() {
        getSystemService(NotificationManager::class.java)?.cancel(NOTIFICATION_ID)
    }

    companion object {
        private const val CHANNEL_ID = "music_playback"
        private const val NOTIFICATION_ID = 1001
        private const val PROGRESS_INTERVAL_MS = 200L
    }
}
