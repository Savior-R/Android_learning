package cn.edu.sicnu.reproject.cardgame

import cn.edu.sicnu.reproject.cardgame.model.Deck
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * 单元测试：一副牌的张数与抽取行为。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class DeckTest {

    @Test
    fun `一副牌共52张`() {
        assertEquals(52, Deck().count)
    }

    @Test
    fun `抽牌后牌堆减少一张`() {
        val deck = Deck()
        val card = deck.drawRandomCard()
        assertNotNull(card)
        assertEquals(51, deck.count)
    }

    @Test
    fun `连续抽52次 每张牌都不重复`() {
        val deck = Deck()
        val drawn = mutableSetOf<String>()
        repeat(52) {
            val card = deck.drawRandomCard()
            assertNotNull(card)
            // toString = 花色 + 点数，52 张各不相同
            drawn.add(card.toString())
        }
        assertEquals(52, drawn.size)
        assertEquals(0, deck.count)
    }

    @Test
    fun `牌堆抽空后返回null`() {
        val deck = Deck()
        repeat(52) { deck.drawRandomCard() }
        assertNull(deck.drawRandomCard())
    }
}
