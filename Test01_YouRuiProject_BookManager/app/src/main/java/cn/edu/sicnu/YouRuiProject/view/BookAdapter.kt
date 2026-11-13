package cn.edu.sicnu.YouRuiProject.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import cn.edu.sicnu.YouRuiProject.R
import cn.edu.sicnu.YouRuiProject.model.Book

/**
 * 书籍列表适配器：每项展示书名、作者、出版社与价格，颜色统一取主题蓝色。
 */
class BookAdapter(
    private val context: Context,
    private val books: List<Book>
) : BaseAdapter() {

    override fun getCount(): Int = books.size

    override fun getItem(position: Int): Book = books[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.item_book, parent, false)
        val book = books[position]

        view.findViewById<TextView>(R.id.tvBookTitle).text = book.title
        view.findViewById<TextView>(R.id.tvBookAuthor).text =
            context.getString(R.string.label_author) + book.author
        view.findViewById<TextView>(R.id.tvBookPublisher).text =
            context.getString(R.string.label_publisher) + book.publisher
        view.findViewById<TextView>(R.id.tvBookPrice).text =
            context.getString(R.string.price_format, book.price)

        return view
    }
}
