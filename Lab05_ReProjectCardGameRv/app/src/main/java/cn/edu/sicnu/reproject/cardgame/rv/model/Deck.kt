package cn.edu.sicnu.reproject.cardgame.rv.model

import java.util.Random

/**
 * 实验四 · Model 层之二：一副牌
 *
 * 构造时把 4 种花色 × 13 个点数 = 52 张牌全部放进 [cards]。构造方法里
 * 直接写 **独立的花色数组**，而不是复用 [Card.validSuits]：后者是各张牌
 * 显示顺序的"标准顺序"，两者用途不同，分开写不易搞混。
 *
 * [drawRandomCard] 每次从剩余牌堆里随机抽一张并**移除**，
 * 因此同一局游戏中不会抽到重复的牌；牌堆抽空后返回 null。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class Deck {

    private val cards = mutableListOf<Card>()
    private val r = Random()

    init {
        for (suit in SUITS) {
            for (rank in Card.rankStrings) {
                cards.add(Card(suit = suit, rank = rank))
            }
        }
    }

    /** 剩余牌数，测试与调试用 */
    val count: Int
        get() = cards.size

    /**
     * 随机抽一张牌，并从牌堆中移除。
     * @return 抽到的牌；牌堆为空时返回 null
     */
    fun drawRandomCard(): Card? {
        var randomCard: Card? = null
        if (cards.size > 0) {
            randomCard = cards.removeAt(r.nextInt(cards.size))
        }
        return randomCard
    }

    private companion object {
        val SUITS = arrayOf("♠", "♥", "♦", "♣")
    }
}
