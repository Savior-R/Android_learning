package cn.edu.sicnu.YouRuiProject.view

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.YouRuiProject.R
import cn.edu.sicnu.YouRuiProject.model.Book
import cn.edu.sicnu.YouRuiProject.util.Insets

/**
 * 第二个 Activity：展示被选中书籍的书名、作者、出版社与价格。
 */
class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_BOOK = "extra_book"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        Insets.applyTopPadding(findViewById(R.id.rootDetail))

        val book = intent.bookExtra()
        if (book == null) {
            finish()
            return
        }

        findViewById<TextView>(R.id.tvDetailTitle).text = book.title
        findViewById<TextView>(R.id.tvDetailAuthor).text =
            getString(R.string.label_author) + book.author
        findViewById<TextView>(R.id.tvDetailPublisher).text =
            getString(R.string.label_publisher) + book.publisher
        findViewById<TextView>(R.id.tvDetailPrice).text =
            getString(R.string.price_format, book.price)
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun android.content.Intent.bookExtra(): Book? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getSerializableExtra(EXTRA_BOOK, Book::class.java)
        } else {
            @Suppress("DEPRECATION")
            getSerializableExtra(EXTRA_BOOK) as? Book
        }
}
