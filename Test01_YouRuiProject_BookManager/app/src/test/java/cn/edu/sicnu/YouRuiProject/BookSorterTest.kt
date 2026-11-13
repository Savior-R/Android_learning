package cn.edu.sicnu.YouRuiProject

import cn.edu.sicnu.YouRuiProject.controller.BookController
import cn.edu.sicnu.YouRuiProject.model.Book
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 排序验收：题目要求按价格从低到高排序。
 */
class BookSorterTest {

    private fun book(title: String, price: Double): Book =
        Book(title = title, author = "作者", publisher = "出版社", price = price)

    @Test
    fun sortsByPriceAscending() {
        val input = listOf(book("C", 59.0), book("A", 19.9), book("B", 39.5))

        val sorted = BookController.sortByPriceAscending(input)

        assertEquals(listOf("A", "B", "C"), sorted.map { it.title })
        sorted.zipWithNext().forEach { (former, latter) ->
            assertTrue(
                "价格应非递减，实际 ${former.price} -> ${latter.price}",
                former.price <= latter.price
            )
        }
    }

    @Test
    fun keepsAllBooksAndIsStableForEqualPrices() {
        val input = listOf(book("X", 30.0), book("Y", 10.0), book("Z", 30.0))

        val first = BookController.sortByPriceAscending(input)
        val second = BookController.sortByPriceAscending(first)

        assertEquals("排序不能丢书或重复", input.size, first.size)
        assertEquals(listOf("Y", "X", "Z"), first.map { it.title })
        assertEquals("相等价格应保持原有先后顺序", first.map { it.title }, second.map { it.title })
    }
}
