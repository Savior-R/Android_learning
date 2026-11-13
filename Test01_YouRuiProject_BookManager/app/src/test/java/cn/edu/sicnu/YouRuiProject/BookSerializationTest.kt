package cn.edu.sicnu.YouRuiProject

import cn.edu.sicnu.YouRuiProject.model.Book
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 传参验收：书籍对象经序列化往返后，四项字段必须完整保留。
 */
class BookSerializationTest {

    @Test
    fun roundTripKeepsAllFields() {
        val original = Book(
            title = "Kotlin 实战",
            author = "Dmitry Jemerov",
            publisher = "人民邮电出版社",
            price = 89.0
        )

        val buffer = ByteArrayOutputStream()
        ObjectOutputStream(buffer).use { it.writeObject(original) }
        val restored = ObjectInputStream(ByteArrayInputStream(buffer.toByteArray()))
            .use { it.readObject() as Book }

        assertEquals(original.title, restored.title)
        assertEquals(original.author, restored.author)
        assertEquals(original.publisher, restored.publisher)
        assertEquals(original.price, restored.price, 0.0001)
    }
}
