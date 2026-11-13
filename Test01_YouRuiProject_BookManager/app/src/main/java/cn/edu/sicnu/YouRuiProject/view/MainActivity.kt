package cn.edu.sicnu.YouRuiProject.view

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.YouRuiProject.R
import cn.edu.sicnu.YouRuiProject.controller.BookController
import cn.edu.sicnu.YouRuiProject.model.Book
import cn.edu.sicnu.YouRuiProject.model.BookRepository
import cn.edu.sicnu.YouRuiProject.util.Insets

/**
 * 第一个 Activity：标题为姓名+学号，展示按价格升序排列的书籍列表。
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Insets.applyTopPadding(findViewById(R.id.rootMain))

        val books = BookController.sortByPriceAscending(BookRepository.loadBooks())
        val listView = findViewById<ListView>(R.id.lvBooks)
        listView.emptyView = findViewById(R.id.tvEmpty)
        listView.adapter = BookAdapter(this, books)

        listView.setOnItemClickListener { _, _, position, _ ->
            val book = listView.adapter.getItem(position) as Book
            startActivity(
                Intent(this, DetailActivity::class.java)
                    .putExtra(DetailActivity.EXTRA_BOOK, book)
            )
        }
    }
}
