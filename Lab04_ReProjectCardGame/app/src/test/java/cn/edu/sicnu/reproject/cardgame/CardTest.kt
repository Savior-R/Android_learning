package cn.edu.sicnu.reproject.cardgame

import cn.edu.sicnu.reproject.cardgame.model.Card
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 单元测试：只测 [Card] 的配对规则。
 * 这类测试在 JVM 上直接跑（./gradlew test），不需要模拟器，几秒就能出结果。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class CardTest {

    @Test
    fun `同点数不同花色 匹配得4分`() {
        val a = Card(suit = "♥", rank = "A")
        val b = Card(suit = "♠", rank = "A")
        assertEquals(4, a.match(arrayOf(b)))
    }

    @Test
    fun `同花色不同点数 匹配得1分`() {
        val a = Card(suit = "♥", rank = "A")
        val b = Card(suit = "♥", rank = "K")
        assertEquals(1, a.match(arrayOf(b)))
    }

    @Test
    fun `点数和花色都不同 匹配得0分`() {
        val a = Card(suit = "♥", rank = "A")
        val b = Card(suit = "♠", rank = "K")
        assertEquals(0, a.match(arrayOf(b)))
    }

    @Test
    fun `两张以上时暂不结算 返回0分`() {
        val a = Card(suit = "♥", rank = "A")
        val b = Card(suit = "♥", rank = "A")
        val c = Card(suit = "♥", rank = "A")
        assertEquals(0, a.match(arrayOf(b, c)))
    }

    @Test
    fun `toString 输出花色加点数`() {
        assertEquals("♦10", Card(suit = "♦", rank = "10").toString())
    }
}
