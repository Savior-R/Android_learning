package cn.edu.sicnu.reproject.cardgame.model

/**
 * 实验四 · Model 层之三：翻牌游戏的核心规则
 *
 * 这是整个游戏唯一"有状态"的地方：它持有本局所有牌、累计得分，
 * 并规定翻牌时分数如何变化。界面（View）与交互（Controller）都只是它的外壳，
 * 因此本类可以在 JVM 上被单元测试完整覆盖，不需要模拟器。
 *
 * 计分规则（与附件代码一致）：
 *  - MATCH_BONUS     ：配对成功时，把 [Card.match] 的得分（4 或 1）乘以 4 加进总分；
 *  - MISMATCH_PENALTY：配对失败扣 2 分；
 *  - COST_TO_CHOOSE  ：每翻开一张牌先扣 1 分作为"翻牌成本"。
 *
 * @param count 本局发牌张数
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class CardMatchingGame(private val count: Int) {

    /** 当前得分；对外只读，只能由 [chooseCardAtIndex] 修改 */
    var score = 0
        private set

    private val cards: MutableList<Card>

    init {
        // 洗牌：从一副新牌里随机抽 count 张，抽到哪张完全随机
        val deck = Deck()
        cards = mutableListOf()
        for (i in 1..count) {
            val card: Card? = deck.drawRandomCard()
            if (card != null) {
                cards.add(card)
            }
        }
    }

    /** 本局牌的数量（牌堆不足时可能小于构造参数） */
    val cardCount: Int
        get() = cards.size

    /** 计分常量 */
    val MISMATCH_PENALTY = 2
    val MATCH_BONUS = 4
    val COST_TO_CHOOSE = 1

    /** 取第 index 张牌；index 越界时抛 IndexOutOfBoundsException */
    fun cardAtIndex(index: Int): Card {
        return cards.get(index)
    }

    /**
     * 翻开 / 收起第 index 张牌，并结算分数。
     *
     * 逻辑分三种情况：
     *  1. 该牌已配对成功 -> 直接忽略，不能再翻；
     *  2. 该牌当前是翻开的 -> 收起（isChosen = false）；
     *  3. 该牌当前是盖着的 -> 在已翻开的牌里找一张未配对的做比较：
     *       配对成功：双方标记 isMatched，按 [Card.match] 得分 × MATCH_BONUS 加分；
     *       配对失败：扣 MISMATCH_PENALTY，并把之前那张牌重新盖回去。
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

        for (otherCard in cards) {
            if (otherCard.isChosen && !otherCard.isMatched) {
                val matchScore = card.match(arrayOf(otherCard))
                if (matchScore > 0) {
                    score += matchScore * MATCH_BONUS
                    otherCard.isMatched = true
                    card.isMatched = true
                } else {
                    score -= MISMATCH_PENALTY
                    otherCard.isChosen = false
                }
                break
            }
        }

        // 翻牌成本：无论配对与否，翻开一张牌都要付 1 分
        score -= COST_TO_CHOOSE
        card.isChosen = true
    }
}
