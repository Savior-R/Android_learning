package cn.edu.sicnu.reproject.music.model

/**
 * 实验七 · Model 层之一：一首歌
 *
 * 只保存展示与播放所需的信息，不持有任何 Android 对象，
 * 因此可以在普通 JVM 上被单元测试覆盖。
 *
 * @param id       MediaStore 中的主键，用于构造 content:// 播放地址
 * @param title    歌名
 * @param artist   歌手（媒体库里可能为空，用 [UNKNOWN] 兜底）
 * @param duration 时长（毫秒）
 * @param size     文件大小（字节）
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val duration: Long,
    val size: Long
) {

    /** 媒体库中的 content:// URI，MediaPlayer 直接用它做数据源 */
    val uri: android.net.Uri
        get() = android.content.ContentUris.withAppendedId(
            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
        )

    /**
     * 列表里显示的第二行文字。
     * WAV 之类的文件没有歌手元数据时，MediaStore 里可能存空串或 "<unknown>"，
     * 这两种都当作未知歌手处理。
     */
    fun subtitle(): String =
        if (artist.isBlank() || artist.equals("<unknown>", ignoreCase = true)) UNKNOWN
        else artist

    /** 把毫秒时长格式化成 3:05 这样的文本 */
    fun durationText(): String = formatDuration(duration)

    companion object {
        const val UNKNOWN = "未知歌手"

        /** 毫秒 → m:ss */
        fun formatDuration(ms: Long): String {
            val total = (ms / 1000).coerceAtLeast(0)
            val m = total / 60
            val s = total % 60
            return "%d:%02d".format(m, s)
        }
    }
}
