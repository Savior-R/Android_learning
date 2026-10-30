package cn.edu.sicnu.reproject.cardgame.fragment.model

import java.io.Serializable

/**
 * 实验六 · 玩家信息 Model（可序列化）
 *
 * 与实验四、实验五不同：实验六要求“个人信息也要用序列化方式存储，
 * 退出程序再打开能恢复”，因此本类实现 [Serializable]，
 * 由 GameStore 用 ObjectOutputStream 写入 player_info.obj。
 *
 * [Gender] 是枚举，枚举本身也是可序列化的，不需要额外处理。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class PlayerInfo(
    var name: String = DEFAULT_NAME,
    var gender: Gender = Gender.MALE,
    var studentId: String = DEFAULT_STUDENT_ID,
    var phone: String = DEFAULT_PHONE,
    var email: String = DEFAULT_EMAIL,
    /** 爱好，多选 */
    var hobbies: MutableSet<String> = mutableSetOf(HOBBY_READING),
    /** 当前最高分，由翻牌游戏结束后回写 */
    var bestScore: Int = 0
) : Serializable {

    enum class Gender { MALE, FEMALE }

    companion object {
        private const val serialVersionUID = 2024110544L

        const val DEFAULT_NAME = "犹锐"
        const val DEFAULT_STUDENT_ID = "2024110544"
        const val DEFAULT_PHONE = "13800000000"
        const val DEFAULT_EMAIL = "2024110544@stu.sicnu.edu.cn"

        const val HOBBY_READING = "reading"
        const val HOBBY_GAME = "game"
        const val HOBBY_SHOPPING = "shopping"

        val ALL_HOBBIES = listOf(HOBBY_READING, HOBBY_GAME, HOBBY_SHOPPING)
    }

    /** 用另一份信息覆盖当前对象，保持实例引用不变 */
    fun copyFrom(other: PlayerInfo) {
        name = other.name
        gender = other.gender
        studentId = other.studentId
        phone = other.phone
        email = other.email
        hobbies = other.hobbies.toMutableSet()
        bestScore = other.bestScore
    }

    /** 生成一份可编辑的副本 */
    fun copy(): PlayerInfo = PlayerInfo(
        name = name,
        gender = gender,
        studentId = studentId,
        phone = phone,
        email = email,
        hobbies = hobbies.toMutableSet(),
        bestScore = bestScore
    )

    /** 刷新最高分：只有超过历史最高分才更新，返回是否刷新纪录 */
    fun updateBestScore(score: Int): Boolean {
        if (score > bestScore) {
            bestScore = score
            return true
        }
        return false
    }
}
