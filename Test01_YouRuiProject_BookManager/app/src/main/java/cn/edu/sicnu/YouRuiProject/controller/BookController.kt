package cn.edu.sicnu.YouRuiProject.controller

import cn.edu.sicnu.YouRuiProject.model.Book

/**
 * 书籍业务控制器：负责把书籍数组按价格从低到高排序。
 * Kotlin 的 sortedBy 为稳定排序，价格相等时保持数据源原有顺序。
 */
object BookController {

    fun sortByPriceAscending(books: List<Book>): List<Book> = books.sortedBy { it.price }
}
