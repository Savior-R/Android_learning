package cn.edu.sicnu.reproject.cardgame.rv

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI 测试：信息修改流程端到端验证。
 *
 * 验证的是跨 Activity 的数据流：
 *   主界面 -> 修改姓名 -> 保存 -> 回到主界面 -> 主界面显示新姓名。
 * 这条链路能通过，说明 Application 单例 + onResume 重新渲染的约定是有效的。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
@RunWith(AndroidJUnit4::class)
class EditInfoActivityTest {

    @get:Rule
    val rule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun 修改姓名并保存后 主界面显示新姓名() {
        onView(withId(R.id.btn_edit_info)).perform(click())

        onView(withId(R.id.et_name)).perform(clearText(), typeText("犹锐同学"), closeSoftKeyboard())
        onView(withId(R.id.btn_save)).perform(click())

        // 回到主界面后应立刻看到新姓名
        onView(withId(R.id.tv_name)).check(matches(withText("犹锐同学")))
    }

    @Test
    fun 取消修改后 主界面姓名保持不变() {
        onView(withId(R.id.btn_edit_info)).perform(click())

        onView(withId(R.id.et_name)).perform(clearText(), typeText("不应保存"), closeSoftKeyboard())
        onView(withId(R.id.btn_cancel)).perform(click())

        onView(withId(R.id.tv_name)).check(matches(withText("犹锐")))
    }

    @Test
    fun 编辑界面各控件均可见() {
        onView(withId(R.id.btn_edit_info)).perform(click())

        onView(withId(R.id.et_student_id)).check(matches(isDisplayed()))
        onView(withId(R.id.rb_male)).check(matches(isDisplayed()))
        onView(withId(R.id.rb_female)).check(matches(isDisplayed()))
        onView(withId(R.id.cb_reading)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_save)).check(matches(isDisplayed()))
    }
}
