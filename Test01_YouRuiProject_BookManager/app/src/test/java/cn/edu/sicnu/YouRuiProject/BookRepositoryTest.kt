package cn.edu.sicnu.YouRuiProject

import cn.edu.sicnu.YouRuiProject.model.BookRepository
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 数据源验收：题目要求代码内新建多个书籍对象，且字段完整。
 */
class BookRepositoryTest {

    @Test
    fun providesAtLeastSixBooksWithAllFields() {
        val books = BookRepository.loadBooks()

        assertTrue("书籍数量应不少于 6 本，实际 ${books.size}", books.size >= 6)
        books.forEach { book ->
            assertTrue("书名不能为空", book.title.isNotBlank())
            assertTrue("作者不能为空", book.author.isNotBlank())
            assertTrue("出版社不能为空", book.publisher.isNotBlank())
            assertTrue("价格必须为正数，实际 ${book.price}", book.price > 0.0)
        }
    }

    @Test
    fun pricesAreNotAllTheSame() {
        val distinctPrices = BookRepository.loadBooks().map { it.price }.distinct()

        assertTrue("价格不应全部相同，否则排序结果无法验证", distinctPrices.size >= 2)
    }
}
