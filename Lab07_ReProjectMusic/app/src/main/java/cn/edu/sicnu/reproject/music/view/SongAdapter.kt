package cn.edu.sicnu.reproject.music.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.reproject.music.R
import cn.edu.sicnu.reproject.music.model.Song

/**
 * 实验七 · View 层：歌曲列表适配器
 *
 * 从 RecyclerView.Adapter 派生，重载 getItemCount / onCreateViewHolder /
 * onBindViewHolder，并把条目点击通过构造参数回调给 Controller。
 * 当前正在播放的条目用高亮背景标出，与 Service 的状态保持一致。
 *
 * @param onSongClick 点击某一首歌曲时的回调，参数为列表下标
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class SongAdapter(
    private val onSongClick: (position: Int) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private var songs: List<Song> = emptyList()

    /** 当前播放中的下标，-1 表示没有在播放 */
    private var playingIndex: Int = -1

    /** 换一批歌曲并刷新列表 */
    fun submit(list: List<Song>) {
        songs = list
        notifyDataSetChanged()
    }

    /** 更新“正在播放”高亮条目 */
    fun setPlayingIndex(index: Int) {
        val old = playingIndex
        playingIndex = index
        if (old in songs.indices) notifyItemChanged(old)
        if (index in songs.indices) notifyItemChanged(index)
    }

    override fun getItemCount(): Int = songs.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val item = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(item)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        val playing = position == playingIndex

        holder.title.text = song.title
        holder.subtitle.text = song.subtitle()
        holder.duration.text = song.durationText()
        holder.itemView.isSelected = playing
        holder.itemView.setOnClickListener {
            onSongClick(holder.adapterPosition)
        }
    }

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_song_title)
        val subtitle: TextView = itemView.findViewById(R.id.tv_song_artist)
        val duration: TextView = itemView.findViewById(R.id.tv_song_duration)
    }
}
