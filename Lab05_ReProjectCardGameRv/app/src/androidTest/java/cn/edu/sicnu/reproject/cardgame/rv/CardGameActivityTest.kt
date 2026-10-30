package cn.edu.sicnu.reproject.cardgame.rv

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
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
 * UI 测试：翻牌游戏界面。
 *
 * 这里只验证"界面能打开、牌桌有牌、点重置不崩溃"这类可稳定复现的行为；
 * 具体的计分正确性交给 JVM 单元测试（CardMatchingGameTest），
 * 因为发牌是随机的，用断言去猜某一次点击的分数会很脆弱。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
@RunWith(AndroidJUnit4::class)
class CardGameActivityTest {

    @get:Rule
    val rule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun 打开游戏界面 牌桌与得分均可见() {
        onView(withId(R.id.btn_play_game)).perform(click())

        onView(withId(R.id.grid_cards)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_score)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_reset)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_exit)).check(matches(isDisplayed()))
    }

    @Test
    fun 打开游戏界面 初始得分为0() {
        onView(withId(R.id.btn_play_game)).perform(click())
        onView(withId(R.id.tv_score)).check(
            matches(withText("得分：0"))
        )
    }

    @Test
    fun 点击重置游戏 得分回到0且界面正常() {
        onView(withId(R.id.btn_play_game)).perform(click())
        onView(withId(R.id.btn_reset)).perform(click())

        onView(withId(R.id.tv_score)).check(matches(withText("得分：0")))
        onView(withId(R.id.grid_cards)).check(matches(isDisplayed()))
    }

    @Test
    fun 点击退出 回到主界面() {
        onView(withId(R.id.btn_play_game)).perform(click())
        onView(withId(R.id.btn_exit)).perform(click())

        onView(withId(R.id.btn_play_game)).check(matches(isDisplayed()))
    }
}
