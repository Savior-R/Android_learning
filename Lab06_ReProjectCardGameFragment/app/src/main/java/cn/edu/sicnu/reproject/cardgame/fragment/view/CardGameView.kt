package cn.edu.sicnu.reproject.cardgame.fragment.view

import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.reproject.cardgame.fragment.R
import cn.edu.sicnu.reproject.cardgame.fragment.model.CardMatchingGame

/**
 * 实验五 · 翻牌游戏的 View 层
 *
 * 实验四用 GridLayout 手工摆牌，本实验改用 RecyclerView：
 *  - 牌桌容器是一个 [RecyclerView]，由 [GridLayoutManager] 决定列数；
 *  - 每张牌由 [CardAdapter] 创建与绑定，View 层不再自己留着 View 集合；
 *  - 竖屏 3 列、横屏 6 列，列数由资源 `integer/column_count` 提供。
 *
 * View 层只负责“长什么样”，能不能翻、加几分由 Model 与 Controller 决定。
 *
 * @param context 宿主 Activity
 * @param recycler 布局里的 RecyclerView
 * @param onCardClick 点击某张牌的回调，由 Controller 注入
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class CardGameView(
    private val context: Context,
    private val recycler: RecyclerView,
    onCardClick: (Int) -> Unit
) {

    /** 适配器：View 层与 Model 之间的桥梁 */
    val adapter = CardAdapter(onCardClick)

    /** 得分文本控件，由 Controller 在 attach 时注入 */
    lateinit var scoreView: TextView

    /** 按当前屏幕方向配置好列数与适配器；旋转屏幕后会重新调用 */
    fun setup() {
        val spanCount = context.resources.getInteger(R.integer.column_count)
        recycler.layoutManager = GridLayoutManager(context, spanCount)
        recycler.setHasFixedSize(true)
        recycler.adapter = adapter
    }

    /** 绑定新的一局，并把整桌牌重画一遍 */
    fun bindCards(game: CardMatchingGame) {
        adapter.bindGame(game)
    }

    /** 刷新第 index 张牌 */
    fun renderCard(index: Int) {
        adapter.notifyCardChanged(index)
    }

    /** 刷新整桌牌 */
    fun renderAll() {
        adapter.notifyAllCards()
    }

    /** 刷新得分显示 */
    fun renderScore(score: Int) {
        scoreView.text = context.getString(R.string.game_score_format, score)
    }
}
