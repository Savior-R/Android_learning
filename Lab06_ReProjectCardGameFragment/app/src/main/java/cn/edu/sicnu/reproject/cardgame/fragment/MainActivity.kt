package cn.edu.sicnu.reproject.cardgame.fragment

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import cn.edu.sicnu.reproject.cardgame.fragment.fragment.GameFragment
import cn.edu.sicnu.reproject.cardgame.fragment.fragment.HelloFragment
import cn.edu.sicnu.reproject.cardgame.fragment.util.Insets

/**
 * 实验六 · 主 Activity：只做“容器 + 三个 Fragment 的切换”
 *
 * 三个 Fragment 对应实验要求的三块内容：
 *  0 -> [HelloFragment] 个人信息
 *  1 -> [GameFragment]  两张牌的翻牌游戏
 *  2 -> [GameFragment]  三张牌的翻牌游戏
 *
 * 切换用 FragmentManager.replace，每次切换都会销毁上一个 Fragment 的视图，
 * 所以每个 Fragment 在 onPause 里把状态序列化到文件、在 onViewCreated 里恢复，
 * 既满足实验要求，也保证来回切换不丢局面。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class MainActivity : AppCompatActivity() {

    private lateinit var tabs: List<Button>

    /** 当前选中的 Fragment 序号 */
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Insets.applyBottomInset(findViewById(R.id.bottom_nav))

        tabs = listOf(
            findViewById(R.id.btn_tab_hello),
            findViewById(R.id.btn_tab_game2),
            findViewById(R.id.btn_tab_game3)
        )
        tabs.forEachIndexed { index, button ->
            button.setOnClickListener { show(index) }
        }

        // 重建（如旋转屏幕）时 FragmentManager 会恢复原来的 Fragment，不必重复添加
        if (savedInstanceState == null) {
            show(0)
        } else {
            currentIndex = savedInstanceState.getInt(KEY_INDEX, 0)
            highlight(currentIndex)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_INDEX, currentIndex)
    }

    /** 切换到第 index 个 Fragment */
    private fun show(index: Int) {
        currentIndex = index
        val fragment: Fragment = when (index) {
            0 -> HelloFragment()
            1 -> GameFragment.newInstance(2)
            else -> GameFragment.newInstance(3)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        highlight(index)
    }

    /** 底部三个按钮的选中态：选中的用品牌色加粗 */
    private fun highlight(index: Int) {
        tabs.forEachIndexed { i, button ->
            val selected = i == index
            button.setTextColor(
                getColor(if (selected) R.color.brand_primary else R.color.text_secondary)
            )
            button.isSelected = selected
            button.alpha = if (selected) 1.0f else 0.8f
        }
    }

    private companion object {
        const val KEY_INDEX = "current_index"
    }
}
