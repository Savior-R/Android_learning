package cn.edu.sicnu.reproject.cardgame.rv.model

/**
 * 实验五 · Model 层之一：单张扑克牌
 *
 * 一张牌由三部分描述：
 *  - suit  花色（♠ ♥ ♦ ♣）
 *  - rank  点数（A 2 3 ... 10 J Q K）
 *  - 两个运行期状态：isChosen（是否被翻开）、isMatched（是否已配对成功）
 *
 * 相对实验四，[match] 由“只能比较一张”扩展为“可以比较多张”：
 *  - 传入 1 张：点数相同得 4 分，仅花色相同得 1 分（两张牌的游戏，与实验四一致）；
 *  - 传入 2 张：三张牌点数全部相同得 8 分，否则 0 分（三张牌的游戏）。
 *
 * Model 层不引用任何 Android 类，因此可以被 JVM 单元测试直接覆盖。
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
     * @param otherCards 已经翻开、尚未配对的牌：两张牌的游戏传 1 张，三张牌的游戏传 2 张
     * @return 大于 0 表示配对成功，数值即基础得分；0 表示配对失败
     */
    fun match(otherCards: Array<Card>): Int {
        if (otherCards.isEmpty()) return 0

        // 参与比较的所有牌点数都相同才算配对成功
        val allSameRank = otherCards.all { it.rank == rank }
        if (allSameRank) {
            // 两张牌：4 分；三张牌：8 分（组合越长，奖励越高）
            return if (otherCards.size == 1) 4 else 8
        }

        // 两张牌的游戏里，“同花色”也算一次弱匹配，得 1 分
        if (otherCards.size == 1 && otherCards[0].suit == suit) {
            return 1
        }

        return 0
    }
}
