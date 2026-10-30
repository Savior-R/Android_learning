package cn.edu.sicnu.reproject.cardgame

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI 测试：主界面的三个关键点。
 *   1. 主界面能看到本人姓名与学号；
 *   2. 点"翻牌游戏"能打开游戏界面；
 *   3. 点"修改个人信息"能打开信息修改界面。
 *
 * 运行方式：./gradlew connectedDebugAndroidTest（需连接模拟器或真机）
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Test
    fun 主界面显示姓名与学号() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.tv_name)).check(matches(withText("犹锐")))
            onView(withId(R.id.tv_student_id)).check(matches(withText("2024110544")))
            onView(withId(R.id.btn_play_game)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun 点击翻牌游戏按钮打开游戏界面() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btn_play_game)).perform(click())
            onView(withId(R.id.btn_reset)).check(matches(isDisplayed()))
            onView(withId(R.id.grid_cards)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun 点击修改信息按钮打开编辑界面() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btn_edit_info)).perform(click())
            onView(withId(R.id.btn_save)).check(matches(isDisplayed()))
            onView(withId(R.id.et_name)).check(matches(isDisplayed()))
        }
    }
}
