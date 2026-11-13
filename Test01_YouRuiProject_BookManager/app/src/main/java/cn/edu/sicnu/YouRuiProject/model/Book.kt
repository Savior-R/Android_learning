package cn.edu.sicnu.YouRuiProject.model

import java.io.Serializable

/**
 * 书籍数据模型：题目要求的书名、作者、出版社、价格四项属性。
 * 实现 Serializable 以便通过 Intent 在 Activity 之间传递。
 */
data class Book(
    val title: String,
    val author: String,
    val publisher: String,
    val price: Double
) : Serializable
