package cn.edu.sicnu.reproject.cardgame.fragment.model

import java.io.Serializable

/**
 * 实验六 · Model 层之一：单张扑克牌
 *
 * 与实验五相比只多了一件事：实现 [Serializable]。
 * 一张牌只是“花色 + 点数 + 两个布尔状态”，本身没有任何 Android 依赖，
 * 因此可以直接被 ObjectOutputStream 写进文件，读回来后用同样的字段重建局面。
 *
 * [match] 支持两张牌（传 1 张）与三张牌（传 2 张）两种玩法：
 *  - 点数全部相同：两张得 4 分、三张得 8 分；
 *  - 仅两张且花色相同：得 1 分。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class Card(
    private var suit: String,
    private var rank: String,
    var isChosen: Boolean = false,
    var isMatched: Boolean = false
) : Serializable {

    companion object {
        private const val serialVersionUID = 2024110544L

        /** 13 个点数 */
        val rankStrings = arrayOf(
            "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"
        )

        /** 4 种花色（黑桃、红心、方块、梅花） */
        val validSuits = arrayOf("♠", "♥", "♦", "♣")
    }

    /** 花色，供 View 判断“红牌 / 黑牌”以决定文字颜色 */
    val suitText: String
        get() = suit

    /** 点数，供 View 显示 */
    val rankText: String
        get() = rank

    /** 牌面文字，例如 "♥A" */
    override fun toString(): String = suit + rank

    /**
     * 与一组已翻开的牌比较，返回匹配得分。
     *
     * @param otherCards 已经翻开、尚未配对的牌：两张牌玩法传 1 张，三张牌玩法传 2 张
     * @return 大于 0 表示配对成功，数值即基础得分；0 表示配对失败
     */
    fun match(otherCards: Array<Card>): Int {
        if (otherCards.isEmpty()) return 0

        // 参与比较的所有牌点数都相同才算配对成功
        if (otherCards.all { it.rank == rank }) {
            return if (otherCards.size == 1) 4 else 8
        }

        // 两张牌玩法里，“同花色”也算一次弱匹配，得 1 分
        if (otherCards.size == 1 && otherCards[0].suit == suit) {
            return 1
        }

        return 0
    }
}
