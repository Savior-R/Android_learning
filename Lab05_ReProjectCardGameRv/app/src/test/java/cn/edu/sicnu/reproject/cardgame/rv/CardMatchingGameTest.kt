package cn.edu.sicnu.reproject.cardgame.rv

import cn.edu.sicnu.reproject.cardgame.rv.model.CardMatchingGame
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 单元测试：翻牌游戏的计分与状态流转。
 *
 * 因为发牌是随机的，测试里不去"找一对点数相同的牌"（那样结果不稳定），
 * 而是利用规则本身可以推算出确定结果的路径：
 *   - 翻第一张牌：一定只扣 1 分翻牌成本；
 *   - 同一张牌再翻一次：收起，不扣分；
 *   - 翻第二张牌后若花色相同，则加分，若都不同则扣 2+1 分。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class CardMatchingGameTest {

    @Test
    fun `发16张牌 得分初始为0`() {
        val game = CardMatchingGame(16)
        assertEquals(16, game.cardCount)
        assertEquals(0, game.score)
    }

    @Test
    fun `翻第一张牌 只扣翻牌成本1分`() {
        val game = CardMatchingGame(16)
        game.chooseCardAtIndex(0)
        assertEquals(0 - game.COST_TO_CHOOSE, game.score)
        assertTrue(game.cardAtIndex(0).isChosen)
        assertFalse(game.cardAtIndex(0).isMatched)
    }

    @Test
    fun `同一张牌再翻一次 收起且不再扣分`() {
        val game = CardMatchingGame(16)
        game.chooseCardAtIndex(0)
        val afterFirst = game.score
        game.chooseCardAtIndex(0)
        assertEquals(afterFirst, game.score)
        assertFalse(game.cardAtIndex(0).isChosen)
    }

    @Test
    fun `翻第二张牌后的得分变化符合计分规则`() {
        val game = CardMatchingGame(16)
        val first = game.cardAtIndex(0)
        game.chooseCardAtIndex(0)
        val scoreAfterFirst = game.score

        val second = game.cardAtIndex(1)
        game.chooseCardAtIndex(1)

        val expected = when {
            // 点数相同：4 * MATCH_BONUS，再扣翻牌成本
            second.match(arrayOf(first)) == 4 ->
                scoreAfterFirst + 4 * game.MATCH_BONUS - game.COST_TO_CHOOSE
            // 花色相同：1 * MATCH_BONUS，再扣翻牌成本
            second.match(arrayOf(first)) == 1 ->
                scoreAfterFirst + 1 * game.MATCH_BONUS - game.COST_TO_CHOOSE
            // 都不相同：扣配对失败罚分，再扣翻牌成本
            else ->
                scoreAfterFirst - game.MISMATCH_PENALTY - game.COST_TO_CHOOSE
        }

        assertEquals(expected, game.score)
    }

    @Test
    fun `配对成功时两张牌都被标记为已配对`() {
        // 反复开局，直到前两张牌恰好点数相同，验证配对成功分支
        var checked = false
        repeat(200) {
            val game = CardMatchingGame(16)
            val first = game.cardAtIndex(0)
            val second = game.cardAtIndex(1)
            if (first.rankText == second.rankText) {
                game.chooseCardAtIndex(0)
                game.chooseCardAtIndex(1)
                assertTrue(first.isMatched)
                assertTrue(second.isMatched)
                checked = true
                return@repeat
            }
        }
        // 52 张牌中同点数只有 4 张，抽 16 张时前两张同点的概率不低；
        // 200 次都没遇到说明实现有问题。
        assertTrue("200 次开局都没有前两张同点数的情况，请检查发牌逻辑", checked)
    }

    @Test
    fun `配对失败时前一张牌被重新盖回`() {
        var checked = false
        repeat(200) {
            val game = CardMatchingGame(16)
            val first = game.cardAtIndex(0)
            val second = game.cardAtIndex(1)
            if (first.match(arrayOf(second)) == 0) {
                game.chooseCardAtIndex(0)
                game.chooseCardAtIndex(1)
                assertFalse("配对失败后前一张牌应收起", first.isChosen)
                assertFalse(first.isMatched)
                checked = true
                return@repeat
            }
        }
        assertTrue(checked)
    }

    @Test
    fun `已配对的牌再次点击不改变得分`() {
        repeat(200) {
            val game = CardMatchingGame(16)
            val first = game.cardAtIndex(0)
            val second = game.cardAtIndex(1)
            if (first.rankText == second.rankText) {
                game.chooseCardAtIndex(0)
                game.chooseCardAtIndex(1)
                val scoreAfterMatch = game.score
                game.chooseCardAtIndex(0)
                game.chooseCardAtIndex(1)
                assertEquals("已配对的牌应被模型忽略", scoreAfterMatch, game.score)
                return
            }
        }
    }

    @Test
    fun `索引发牌数超过牌堆时 最多发52张`() {
        val game = CardMatchingGame(60)
        assertEquals(52, game.cardCount)
    }
}
