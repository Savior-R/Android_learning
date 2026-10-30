package cn.edu.sicnu.reproject.cardgame.fragment

import android.app.Application
import cn.edu.sicnu.reproject.cardgame.fragment.model.PlayerInfo
import cn.edu.sicnu.reproject.cardgame.fragment.store.GameStore

/**
 * 实验六 · 全局持有玩家信息，并负责启动时从文件恢复
 *
 * 与前两个实验不同，实验六要求“退出程序再打开可以恢复”，
 * 因此这里不再只是内存单例：进程启动时先用 [GameStore] 反序列化 player_info.obj，
 * 读不到才用默认值。个人信息的保存也统一走 [savePlayer]。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class CardGameApp : Application() {

    /** 全局唯一的玩家信息实例：优先来自文件，其次才是默认值 */
    lateinit var playerInfo: PlayerInfo
        private set

    override fun onCreate() {
        super.onCreate()
        playerInfo = GameStore.loadPlayer(this) ?: PlayerInfo()
    }

    /** 把当前玩家信息序列化写回文件 */
    fun savePlayer() {
        GameStore.savePlayer(this, playerInfo)
    }
}
