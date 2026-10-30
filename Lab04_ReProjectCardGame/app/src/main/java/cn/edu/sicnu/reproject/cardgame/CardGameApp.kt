package cn.edu.sicnu.reproject.cardgame

import android.app.Application
import cn.edu.sicnu.reproject.cardgame.model.PlayerInfo

/**
 * 全局持有玩家信息。
 *
 * 实验要求"修改后的信息无需保存到文件中"，但两个 Activity 之间仍要共享同一份数据，
 * 因此选择 Application 级单例：它随进程存活，既不落盘（不违反"无需保存到文件"），
 * 也不会因为 Activity 重建 / 返回而丢失。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class CardGameApp : Application() {

    /** 全局唯一的玩家信息实例 */
    val playerInfo: PlayerInfo by lazy { PlayerInfo() }
}
