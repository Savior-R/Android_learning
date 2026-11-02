package cn.edu.sicnu.reproject.music

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.reproject.music.controller.MusicController
import cn.edu.sicnu.reproject.music.model.Song
import cn.edu.sicnu.reproject.music.model.SongRepository
import cn.edu.sicnu.reproject.music.service.MusicService
import cn.edu.sicnu.reproject.music.util.Insets
import cn.edu.sicnu.reproject.music.view.SongAdapter

/**
 * 实验七 · 主界面
 *
 * 职责只做三件事：
 *  1. 申请读媒体库权限，用 [SongRepository] 取回歌曲列表交给 [SongAdapter] 显示；
 *  2. 用 startService + bindService 连接 [MusicService]，从 Binder 拿到 Service 实例，
 *     再包一个 [MusicController] 处理按钮与列表点击；
 *  3. 把 Service 的进度回调接到 SeekBar 上实时刷新，并把拖动事件回传给 Service。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class MainActivity : AppCompatActivity() {

    private lateinit var adapter: SongAdapter
    private lateinit var repository: SongRepository
    private lateinit var controller: MusicController

    private lateinit var tvCurrent: TextView
    private lateinit var tvTime: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var btnPlayPause: ImageButton

    /** Service 是否已绑定；未绑定时按钮点击直接忽略 */
    private var service: MusicService? = null
    private var bound = false

    /** 用户是否正在拖动进度条：拖动期间不接收 Service 的进度刷新 */
    private var draggingSeekBar = false

    /** 已从媒体库读到的歌曲，Service 连上后立即同步给它 */
    private var loadedSongs: List<Song> = emptyList()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val musicBinder = binder as? MusicService.MusicBinder ?: return
            service = musicBinder.getService()
            bound = true
            controller = MusicController(musicBinder.getService())

            // 把 Service 的进度回调接到 SeekBar 上
            // 连接建立后把播放列表同步给 Service
            musicBinder.getService().setPlaylist(loadedSongs)

            musicBinder.getService().onProgress = { position, duration ->
                if (!draggingSeekBar) {
                    seekBar.max = duration
                    seekBar.progress = position
                    tvTime.text = "%s / %s".format(
                        Song.formatDuration(position.toLong()),
                        Song.formatDuration(duration.toLong())
                    )
                }
            }
            musicBinder.getService().onPlayStateChanged = { playing, song ->
                btnPlayPause.setImageResource(
                    if (playing) R.drawable.ic_pause else R.drawable.ic_play
                )
                tvCurrent.text = song?.title ?: getString(R.string.main_no_song)
                adapter.setPlayingIndex(musicBinder.getService().currentIndex())
            }

            // 连接是异步建立的：重新绑定（例如从后台返回、旋转屏幕）后
            // Service 可能已经在播某首歌，此时不会再有状态回调，
            // 必须主动把当前状态同步一次，否则界面会显示成“尚未选择歌曲”。
            val svc = musicBinder.getService()
            svc.onPlayStateChanged?.invoke(svc.isPlaying(), svc.currentSong())
            svc.onProgress?.invoke(svc.position(), svc.duration())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
            service = null
        }
    }

    /** 运行时权限申请结果 */
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grant ->
        val audioGranted = grant[audioPermission()] == true
        if (audioGranted) {
            loadSongs()
        } else {
            Toast.makeText(this, R.string.main_permission_denied, Toast.LENGTH_LONG).show()
        }
        // 通知权限与播放无关，未授予也不阻塞
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Insets.applyBottomInset(findViewById(R.id.control_panel))

        repository = SongRepository(this)
        tvCurrent = findViewById(R.id.tv_current_song)
        tvTime = findViewById(R.id.tv_time)
        seekBar = findViewById(R.id.seek_bar)
        btnPlayPause = findViewById(R.id.btn_play_pause)

        // 歌曲列表
        adapter = SongAdapter { position ->
            if (::controller.isInitialized) {
                controller.playAt(position)
            } else {
                Toast.makeText(this, R.string.main_service_not_ready, Toast.LENGTH_SHORT).show()
            }
        }
        val recycler = findViewById<RecyclerView>(R.id.rv_songs)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // 控制按钮
        findViewById<ImageButton>(R.id.btn_prev).setOnClickListener { controller.previous() }
        findViewById<ImageButton>(R.id.btn_next).setOnClickListener { controller.next() }
        btnPlayPause.setOnClickListener { controller.toggle() }
        findViewById<ImageButton>(R.id.btn_stop).setOnClickListener { controller.stop() }

        // SeekBar：实时显示进度 + 拖动调整进度
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(bar: SeekBar?, progress: Int, fromUser: Boolean) {
                // 用户拖动时即时更新时间文字，松手后才真正 seek
                if (fromUser && bar != null && bar.max > 0) {
                    tvTime.text = "%s / %s".format(
                        Song.formatDuration(progress.toLong()),
                        Song.formatDuration(bar.max.toLong())
                    )
                }
            }

            override fun onStartTrackingTouch(bar: SeekBar?) {
                draggingSeekBar = true
            }

            override fun onStopTrackingTouch(bar: SeekBar?) {
                draggingSeekBar = false
                bar?.let { controller.onSeek(it.progress) }
            }
        })

        requestPermissionsThenLoad()
    }

    override fun onStart() {
        super.onStart()
        // 绑定 Service；先 startService 保证解绑后音乐继续
        val intent = Intent(this, MusicService::class.java)
        startService(intent)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (bound) {
            service?.onProgress = null
            service?.onPlayStateChanged = null
            unbindService(connection)
            bound = false
        }
    }

    // ---------------- 权限与列表 ----------------

    private fun requestPermissionsThenLoad() {
        val needed = mutableListOf<String>()
        // 读音乐文件：API 33+ 用 READ_MEDIA_AUDIO，之前用 READ_EXTERNAL_STORAGE
        if (!hasPermission(audioPermission())) needed.add(audioPermission())
        // Android 13+ 发通知需要单独权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && !hasPermission(Manifest.permission.POST_NOTIFICATIONS)
        ) needed.add(Manifest.permission.POST_NOTIFICATIONS)

        if (needed.isEmpty()) loadSongs() else permissionLauncher.launch(needed.toTypedArray())
    }

    private fun hasPermission(p: String): Boolean =
        ContextCompat.checkSelfPermission(this, p) == PackageManager.PERMISSION_GRANTED

    private fun audioPermission(): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_AUDIO
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

    /** 用 ContentResolver 读媒体库并刷新列表 */
    private fun loadSongs() {
        val songs: List<Song> = repository.loadSongs()
        loadedSongs = songs
        adapter.submit(songs)
        service?.setPlaylist(songs)
        if (songs.isEmpty()) {
            Toast.makeText(this, R.string.main_no_songs, Toast.LENGTH_LONG).show()
        }
    }
}
