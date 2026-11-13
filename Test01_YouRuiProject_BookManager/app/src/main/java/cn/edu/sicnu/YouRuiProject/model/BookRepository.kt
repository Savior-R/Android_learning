package cn.edu.sicnu.YouRuiProject.model

/**
 * 书籍数据源：在代码里新建多个书籍对象并放入数组，供第一个 Activity 展示。
 */
object BookRepository {

    fun loadBooks(): List<Book> = listOf(
        Book("Kotlin 实战", "Dmitry Jemerov", "人民邮电出版社", 89.0),
        Book("第一行代码（第 3 版）", "郭霖", "人民邮电出版社", 79.0),
        Book("Android 开发艺术探索", "任玉刚", "电子工业出版社", 69.0),
        Book("算法（第 4 版）", "Robert Sedgewick", "人民邮电出版社", 99.0),
        Book("深入理解计算机系统", "Randal E. Bryant", "机械工业出版社", 139.0),
        Book("代码整洁之道", "Robert C. Martin", "人民邮电出版社", 59.0),
        Book("Effective Java（第 3 版）", "Joshua Bloch", "机械工业出版社", 119.0),
        Book("Android 系统源码情景分析", "罗升阳", "电子工业出版社", 109.0)
    )
}
