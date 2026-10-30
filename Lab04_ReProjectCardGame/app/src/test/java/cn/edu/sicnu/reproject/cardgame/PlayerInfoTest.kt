package cn.edu.sicnu.reproject.cardgame

import cn.edu.sicnu.reproject.cardgame.model.PlayerInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 单元测试：玩家信息模型与最高分刷新规则。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class PlayerInfoTest {

    @Test
    fun `默认信息为本人姓名学号`() {
        val info = PlayerInfo()
        assertEquals("犹锐", info.name)
        assertEquals("2024110544", info.studentId)
        assertEquals(0, info.bestScore)
    }

    @Test
    fun `copy 生成独立副本 修改副本不影响原对象`() {
        val origin = PlayerInfo()
        val copy = origin.copy()
        copy.name = "张三"
        copy.hobbies.add(PlayerInfo.HOBBY_SHOPPING)

        assertEquals("犹锐", origin.name)
        assertFalse(origin.hobbies.contains(PlayerInfo.HOBBY_SHOPPING))
    }

    @Test
    fun `copyFrom 覆盖全部字段 但实例引用不变`() {
        val origin = PlayerInfo()
        val edited = origin.copy().apply {
            name = "李四"
            gender = PlayerInfo.Gender.FEMALE
            phone = "13900000000"
            hobbies = mutableSetOf(PlayerInfo.HOBBY_GAME)
        }
        val before = origin
        origin.copyFrom(edited)

        assertEquals("李四", origin.name)
        assertEquals(PlayerInfo.Gender.FEMALE, origin.gender)
        assertEquals("13900000000", origin.phone)
        assertEquals(setOf(PlayerInfo.HOBBY_GAME), origin.hobbies)
        assertTrue(before === origin)
    }

    @Test
    fun `最高分只在超过历史最高时刷新`() {
        val info = PlayerInfo()
        assertTrue(info.updateBestScore(10))
        assertEquals(10, info.bestScore)

        assertFalse("低于最高分不应刷新", info.updateBestScore(8))
        assertEquals(10, info.bestScore)

        assertFalse("等于最高分也不刷新", info.updateBestScore(10))
        assertEquals(10, info.bestScore)

        assertTrue(info.updateBestScore(21))
        assertEquals(21, info.bestScore)
    }
}
