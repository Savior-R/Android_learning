package cn.edu.sicnu.reproject.cardgame.fragment.store

import android.content.Context
import cn.edu.sicnu.reproject.cardgame.fragment.model.GameState
import cn.edu.sicnu.reproject.cardgame.fragment.model.PlayerInfo
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * 实验六 · 序列化存储工具
 *
 * 按实验说明使用 openFileOutput / openFileInput 打开应用私有目录下的文件，
 * 再用 ObjectOutputStream / ObjectInputStream 读写对象——
 * 也就是“对象序列化”，而不是自己拼接文本。
 *
 * 存三类文件：
 *  - player_info.obj ：个人信息（[PlayerInfo]，含最高分）；
 *  - game_2.obj      ：两张牌游戏的一局快照（[GameState]）；
 *  - game_3.obj      ：三张牌游戏的一局快照。
 *
 * 所有读取都做了容错：文件不存在、反序列化失败（例如类结构变了）时返回 null，
 * 调用方拿到 null 就当新开局处理，不会因为一个坏文件让应用崩溃。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
object GameStore {

    private const val FILE_PLAYER = "player_info.obj"
    private const val FILE_GAME_2 = "game_2.obj"
    private const val FILE_GAME_3 = "game_3.obj"

    /** 把任意可序列化对象写到应用私有文件 */
    fun save(context: Context, fileName: String, data: Serializable): Boolean {
        return try {
            ObjectOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE)).use { out ->
                out.writeObject(data)
                out.flush()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    /** 从应用私有文件读回对象；读不到或类型不符时返回 null */
    inline fun <reified T : Serializable> load(context: Context, fileName: String): T? {
        val file: File = context.getFileStreamPath(fileName)
        if (!file.exists()) return null
        return try {
            ObjectInputStream(context.openFileInput(fileName)).use { input ->
                val obj = input.readObject()
                obj as? T
            }
        } catch (e: Exception) {
            null
        }
    }

    /** 保存个人信息 */
    fun savePlayer(context: Context, info: PlayerInfo) {
        save(context, FILE_PLAYER, info)
    }

    /** 读取个人信息 */
    fun loadPlayer(context: Context): PlayerInfo? = load(context, FILE_PLAYER)

    /** 保存某一局游戏的快照，按“几张牌配对”分文件存放 */
    fun saveGame(context: Context, state: GameState) {
        save(context, gameFile(state.cardsToMatch), state)
    }

    /** 读取某一局游戏的快照 */
    fun loadGame(context: Context, cardsToMatch: Int): GameState? =
        load(context, gameFile(cardsToMatch))

    private fun gameFile(cardsToMatch: Int): String =
        if (cardsToMatch >= 3) FILE_GAME_3 else FILE_GAME_2
}
