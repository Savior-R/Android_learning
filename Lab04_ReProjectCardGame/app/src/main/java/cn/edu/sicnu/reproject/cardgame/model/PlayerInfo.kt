package cn.edu.sicnu.reproject.cardgame.model

/**
 * 实验四 · 玩家信息 Model
 *
 * 对应实验要求中的"主 Activity 显示当前玩家的基本信息（姓名、性别、电话、当前最高分等）"。
 * 按实验说明，修改后的信息**无需保存到文件**，因此这里只是一份内存对象：
 * 它由 Application 级单例持有，两个 Activity 通过同一个实例读写，
 * 因而"信息修改 Activity 保存 -> 返回主界面后立即看到新内容"可以成立。
 *
 * 把它独立成 Model 而不是塞进 Activity，是为了让 View / Controller 都不持有数据本身，
 * 符合 MVC 中"数据归 Model"的划分。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class PlayerInfo(
    var name: String = DEFAULT_NAME,
    /** 性别：使用字符串资源 id 之外的稳定枚举，界面再翻译成文字 */
    var gender: Gender = Gender.MALE,
    var studentId: String = DEFAULT_STUDENT_ID,
    var phone: String = DEFAULT_PHONE,
    var email: String = DEFAULT_EMAIL,
    /** 爱好，多选 */
    var hobbies: MutableSet<String> = mutableSetOf(HOBBY_READING),
    /** 当前最高分，由翻牌游戏结束后回写 */
    var bestScore: Int = 0
) {

    enum class Gender { MALE, FEMALE }

    companion object {
        /** 默认值使用本人真实信息，界面上的演示数据即为报告封面信息 */
        const val DEFAULT_NAME = "犹锐"
        const val DEFAULT_STUDENT_ID = "2024110544"
        const val DEFAULT_PHONE = "13800000000"
        const val DEFAULT_EMAIL = "2024110544@stu.sicnu.edu.cn"

        const val HOBBY_READING = "reading"
        const val HOBBY_GAME = "game"
        const val HOBBY_SHOPPING = "shopping"

        val ALL_HOBBIES = listOf(HOBBY_READING, HOBBY_GAME, HOBBY_SHOPPING)
    }

    /** 用另一份信息覆盖当前对象（"保存"按钮调用），保持实例引用不变 */
    fun copyFrom(other: PlayerInfo) {
        name = other.name
        gender = other.gender
        studentId = other.studentId
        phone = other.phone
        email = other.email
        hobbies = other.hobbies.toMutableSet()
        bestScore = other.bestScore
    }

    /** 生成一份可编辑的副本，供"信息修改"界面使用（取消时不影响原对象） */
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
