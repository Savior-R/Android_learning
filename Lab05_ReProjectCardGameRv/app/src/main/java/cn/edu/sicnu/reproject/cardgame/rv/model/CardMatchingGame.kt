package cn.edu.sicnu.reproject.cardgame.rv.model

/**
 * 实验五 · Model 层之三：翻牌游戏的核心规则
 *
 * 与实验四相比有两处改动：
 *  1. 新增 [reset]：把这一局的牌全部换掉、得分清零，供“重置游戏”和旋转屏幕切换牌数时复用；
 *  2. [chooseCardAtIndex] 支持“几张牌算配对成功”：由构造参数 [cardsToMatch] 决定，
 *     传 2 就是实验四的两张牌玩法，传 3 就是本次新增的三张牌玩法。
 *
 * 计分规则：
 *  - MATCH_BONUS      ：配对成功时把 [Card.match] 的得分（1 / 4 / 8）乘以 4 计入总分；
 *  - MISMATCH_PENALTY ：配对失败扣 2 分；
 *  - COST_TO_CHOOSE   ：每翻开一张牌先扣 1 分作为翻牌成本。
 *
 * @param count        本局发牌张数（由横竖屏决定：竖屏 12 张、横屏 24 张）
 * @param cardsToMatch 几张牌算配对成功：2 = 两张牌玩法，3 = 三张牌玩法
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class CardMatchingGame(
    private val count: Int,
    val cardsToMatch: Int = 2
) {

    /** 当前得分；对外只读，只能由 [chooseCardAtIndex] / [reset] 修改 */
    var score = 0
        private set

    /** 本局的牌；[reset] 时会清空后重新发牌，引用本身不变 */
    private val cards = mutableListOf<Card>()

    init {
        deal()
    }

    /** 本局牌的数量（牌堆不足时可能小于构造参数） */
    val cardCount: Int
        get() = cards.size

    /** 计分常量 */
    val MISMATCH_PENALTY = 2
    val MATCH_BONUS = 4
    val COST_TO_CHOOSE = 1

    /**
     * 发牌。
     *
     * 两张牌玩法：从一副新牌里随机抽 count 张（抽出的牌会从牌堆移除，天然不重复）。
     *
     * 三张牌玩法：改为“按点数成组”发牌——随机挑 count/3 个点数，每个点数取 3 种花色。
     * 原因是从 52 张里纯随机抽 12 张时，出现“三张同点数”的概率只有约四成，
     * 多数局面根本无解；成组发牌保证每一局都能全部配对完成，游戏才成立。
     */
    private fun deal() {
        cards.clear()
        if (cardsToMatch <= 2) {
            val deck = Deck()
            for (i in 1..count) {
                val card: Card = deck.drawRandomCard() ?: break
                cards.add(card)
            }
            return
        }

        val groups = count / cardsToMatch
        val ranks = Card.rankStrings.toMutableList().apply { shuffle() }
        val suits = Card.validSuits.toMutableList()
        for (i in 0 until groups) {
            suits.shuffle()
            val rank = ranks[i % ranks.size]
            for (s in 0 until cardsToMatch) {
                cards.add(Card(suit = suits[s], rank = rank))
            }
        }
    }

    /**
     * 重新开始一局：清空得分并重新发牌。
     * 实验要求“可以重新开始一局游戏”，这里把它下沉到 Model，Controller 只管调用。
     */
    fun reset() {
        score = 0
        deal()
    }

    /** 取第 index 张牌；index 越界时抛 IndexOutOfBoundsException */
    fun cardAtIndex(index: Int): Card {
        return cards.get(index)
    }

    /**
     * 翻开 / 收起第 index 张牌，并结算分数。
     *
     * 逻辑：
     *  1. 该牌已配对成功 -> 直接忽略；
     *  2. 该牌当前是翻开的 -> 收起；
     *  3. 该牌当前是盖着的 -> 看场上“已翻开且未配对”的牌有几张：
     *       数量刚好等于 cardsToMatch - 1 时比较一次，
     *         配对成功：所有相关牌标记 isMatched，按 match 得分 × MATCH_BONUS 加分；
     *         配对失败：扣 MISMATCH_PENALTY，并把之前翻开的牌全部盖回去。
     *     两张牌玩法下，这个条件就是“场上有 1 张已翻开的牌”，与实验四完全一致。
     */
    fun chooseCardAtIndex(index: Int) {
        val card = cardAtIndex(index)
        if (card.isMatched) {
            return
        }
        if (card.isChosen) {
            card.isChosen = false
            return
        }

        val chosenCards = cards.filter { it.isChosen && !it.isMatched }
        if (chosenCards.size == cardsToMatch - 1) {
            val matchScore = card.match(chosenCards.toTypedArray())
            if (matchScore > 0) {
                score += matchScore * MATCH_BONUS
                chosenCards.forEach { it.isMatched = true }
                card.isMatched = true
            } else {
                score -= MISMATCH_PENALTY
                chosenCards.forEach { it.isChosen = false }
            }
        }

        // 翻牌成本：无论配对与否，翻开一张牌都要付 1 分
        score -= COST_TO_CHOOSE
        card.isChosen = true
    }
}
