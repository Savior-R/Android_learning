package cn.edu.sicnu.reproject.music.model

import android.content.Context
import android.provider.MediaStore

/**
 * 实验七 · Model 层之二：用 ContentResolver 读取系统媒体库
 *
 * 对应实验要求(1)“使用 contentResolver 获得音乐文件列表”。
 * 查询只读 MediaStore.Audio.Media 这一张外部内容表，按标题排序，
 * 返回 [Song] 列表；MediaStore 的游标操作全部收在这里，界面层不直接碰 Cursor。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class SongRepository(private val context: Context) {

    /** 查询时要取的列 */
    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.SIZE
    )

    /**
     * 读取媒体库里的全部音乐。
     * @return 歌曲列表；没有读取权限或没有音乐时返回空列表
     */
    fun loadSongs(): List<Song> {
        val result = mutableListOf<Song>()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        // IS_MUSIC != 0 过滤掉录音、提示音等非音乐音频
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        // 注意：不能写 COLLATE LOCALIZED，新版 Android 的 SQLite 已不支持该 collation，
        // 会直接抛 IllegalArgumentException: Invalid token LOCALIZED
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        context.contentResolver.query(collection, projection, selection, null, sortOrder)
            ?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

                while (cursor.moveToNext()) {
                    val duration = cursor.getLong(durationCol)
                    // 时长为 0 的大多是损坏文件或铃声，跳过
                    if (duration <= 0) continue
                    result.add(
                        Song(
                            id = cursor.getLong(idCol),
                            title = cursor.getString(titleCol) ?: Song.UNKNOWN,
                            artist = cursor.getString(artistCol) ?: "",
                            duration = duration,
                            size = cursor.getLong(sizeCol)
                        )
                    )
                }
            }
        return result
    }
}
