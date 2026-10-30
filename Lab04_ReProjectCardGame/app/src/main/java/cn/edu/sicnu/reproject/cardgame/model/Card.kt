package cn.edu.sicnu.reproject.cardgame.model

/**
 * 实验四 · Model 层之一：单张扑克牌
 *
 * 一张牌由三个部分描述：
 *  - suit  花色（♥ ♦ ♠ ♣）
 *  - rank  点数（A 2 3 ... 10 J Q K）
 *  - 两个运行期状态：isChosen（是否被翻开）、isMatched（是否已配对成功）
 *
 * [match] 是本实验的"配对判定"：与另一张牌比较，
 *   - 点数相同   -> 得 4 分（同点数不同花色的对子）
 *   - 花色相同   -> 得 1 分
 *   - 都不同     -> 得 0 分，即配对失败
 *
 * 注意 Model 层完全不认识 Android 的 View，这样才能被单元测试直接覆盖。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class Card(
    private var suit: String,
    private var rank: String,
    var isChosen: Boolean = false,
    var isMatched: Boolean = false
) {

    companion object {
        /** 13 个点数 */
        val rankStrings = arrayOf(
            "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"
        )

        /** 4 种花色（黑桃、红心、方块、梅花） */
        val validSuits = arrayOf("♠", "♥", "♦", "♣")
    }

    /** 花色，供 Controller 判断"红牌 / 黑牌"以决定文字颜色 */
    val suitText: String
        get() = suit

    /** 点数，供 View 显示 */
    val rankText: String
        get() = rank

    /** 牌面文字，例如 "♥A" */
    override fun toString(): String = suit + rank

    /**
     * 与另一组牌比较，返回匹配得分。
     * @param otherCards 目前只处理"一张 vs 一张"的情形，与附件实现保持一致
     */
    fun match(otherCards: Array<Card>): Int {
        var score = 0
        if (otherCards.size == 1) {
            val otherCard = otherCards[0]
            if (otherCard.rank == rank) {
                score = 4
            } else if (otherCard.suit == suit) {
                score = 1
            }
        }
        return score
    }
}
