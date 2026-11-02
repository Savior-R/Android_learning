package cn.edu.sicnu.reproject.music.controller

import android.widget.SeekBar
import cn.edu.sicnu.reproject.music.service.MusicService

/**
 * 实验七 · Controller 层：把界面操作转成 Service 调用
 *
 * Activity 只负责把事件转交到这里，不直接调用 MediaPlayer：
 *  - 点列表某一首 -> [playAt]
 *  - 播放/暂停、停止、上一首、下一首 -> Service 的同名方法
 *  - 拖动 SeekBar -> [onSeek]，由 [SeekBar.OnSeekBarChangeListener] 的
 *    onStopTrackingTouch 触发，避免拖动过程中被进度刷新打断
 *
 * @param service 由 Binder 取得的 MusicService 实例
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class MusicController(private val service: MusicService) {

    /** 播放列表里第 index 首 */
    fun playAt(index: Int) = service.playAt(index)

    /** 播放 / 暂停切换 */
    fun toggle() = service.toggle()

    /** 停止播放 */
    fun stop() = service.stop()

    /** 上一首 */
    fun previous() = service.previous()

    /** 下一首 */
    fun next() = service.next()

    /** 拖动进度条到指定毫秒 */
    fun onSeek(ms: Int) = service.seekTo(ms)

    /** 当前是否在播 */
    fun isPlaying(): Boolean = service.isPlaying()

    /** 当前歌曲名，供标题栏显示 */
    fun currentTitle(): String = service.currentSong()?.title ?: ""
}
