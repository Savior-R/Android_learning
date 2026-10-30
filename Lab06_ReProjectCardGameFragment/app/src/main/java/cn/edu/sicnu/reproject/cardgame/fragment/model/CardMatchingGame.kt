package cn.edu.sicnu.reproject.cardgame.fragment.model

/**
 * 实验六 · Model 层之三：翻牌游戏的核心规则
 *
 * 在实验五的基础上增加了“存 / 取快照”能力：
 *  - [snapshot]：把当前局面导出成可序列化的 [GameState]；
 *  - [restore] ：用 [GameState] 还原分数与每张牌的状态。
 *
 * 这样一来，Fragment 只负责调用这两个方法 + 文件读写，
 * 游戏规则本身仍然只在 Model 里，不被界面逻辑污染。
 *
 * 计分规则：配对成功得 match 分 × 4；配对失败扣 2；每翻一张牌扣 1。
 *
 * @param count        本局发牌张数（竖屏 12、横屏 24，由资源限定符决定）
 * @param cardsToMatch 几张牌算配对成功：2 = 两张牌玩法，3 = 三张牌玩法
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class CardMatchingGame(
    private val count: Int,
    val cardsToMatch: Int = 2
) {

    /** 当前得分；对外只读 */
    var score = 0
        private set

    /** 本局的牌 */
    private val cards = mutableListOf<Card>()

    init {
        deal()
    }

    /** 本局牌的数量 */
    val cardCount: Int
        get() = cards.size

    /** 计分常量 */
    val MISMATCH_PENALTY = 2
    val MATCH_BONUS = 4
    val COST_TO_CHOOSE = 1

    /**
     * 发牌。
     *
     * 两张牌玩法：从一副新牌里随机抽 count 张（抽出即移除，天然不重复）。
     * 三张牌玩法：按点数成组发牌——随机挑 count/3 个点数，每个点数取 3 种花色，
     * 保证每一局都有解（纯随机抽 12 张时出现三张同点数的概率只有约四成）。
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

    /** 重新开始一局 */
    fun reset() {
        score = 0
        deal()
    }

    /** 取第 index 张牌 */
    fun cardAtIndex(index: Int): Card = cards.get(index)

    /** 导出当前局面，供序列化保存 */
    fun snapshot(): GameState = GameState(
        cardsToMatch = cardsToMatch,
        score = score,
        suits = cards.map { it.suitText },
        ranks = cards.map { it.rankText },
        chosen = cards.map { it.isChosen },
        matched = cards.map { it.isMatched }
    )

    /**
     * 用快照还原局面。
     * 牌数或玩法对不上时（例如中途换了屏幕方向），直接忽略并保留当前新开的这一局。
     */
    fun restore(state: GameState) {
        if (state.cardsToMatch != cardsToMatch || state.cardCount != cards.size) return
        cards.clear()
        for (i in state.suits.indices) {
            cards.add(
                Card(
                    suit = state.suits[i],
                    rank = state.ranks[i],
                    isChosen = state.chosen[i],
                    isMatched = state.matched[i]
                )
            )
        }
        score = state.score
    }

    /**
     * 翻开 / 收起第 index 张牌，并结算分数。
     *
     * 1. 已配对的牌忽略；2. 已翻开的牌再点一次就是收起；
     * 3. 否则检查场上“已翻开且未配对”的牌：数量达到 cardsToMatch - 1 时比较一次，
     *    成功则全部标记配对并加分，失败则扣分并把之前翻开的牌盖回去。
     */
    fun chooseCardAtIndex(index: Int) {
        val card = cardAtIndex(index)
        if (card.isMatched) return
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

        score -= COST_TO_CHOOSE
        card.isChosen = true
    }
}
