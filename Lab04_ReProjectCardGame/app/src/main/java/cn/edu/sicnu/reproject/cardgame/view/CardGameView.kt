package cn.edu.sicnu.reproject.cardgame.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.gridlayout.widget.GridLayout
import cn.edu.sicnu.reproject.cardgame.R
import cn.edu.sicnu.reproject.cardgame.model.Card
import cn.edu.sicnu.reproject.cardgame.model.CardMatchingGame

/**
 * 实验四 · 翻牌游戏的 View 层
 *
 * 只做三件事：把一张牌画成什么样、把得分显示出来、把整桌牌摆到网格里。
 * 它**不判断**能不能翻、该加几分——那是 Model 和 Controller 的事。
 *
 * 牌的三种外观严格对应 Model 里两个布尔量的组合：
 *   isMatched == true                 -> 白底 + 绿框 + 半透明（已锁定）
 *   isChosen  == true                 -> 白底 + 金框，显示花色点数
 *   其余                              -> 蓝底白字 "?"（牌背）
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class CardGameView(private val context: Context, private val grid: GridLayout) {

    /** 已经 inflate 出来的每张牌的根 View，下标与 Model 中的牌一一对应 */
    private val cardViews = mutableListOf<View>()

    /** 得分文本控件，由 Controller 在 attach 时注入 */
    lateinit var scoreView: TextView

    /**
     * 按模型里的牌数重建整桌牌。
     * 每次"重置游戏"都会换一个新模型，因此这里会先清空再加。
     */
    fun bindCards(game: CardMatchingGame) {
        grid.removeAllViews()
        cardViews.clear()

        val columnCount = COLUMN_COUNT
        val itemWidth = grid.width / columnCount
        val margin = dp(4)

        for (index in 0 until game.cardCount) {
            val item = LayoutInflater.from(context).inflate(R.layout.item_card, grid, false)
            item.contentDescription = context.getString(R.string.cd_card_back)

            // 4 列等宽，减去左右各一个 margin
            val params = GridLayout.LayoutParams().apply {
                width = if (itemWidth > 0) itemWidth - margin * 2 else GridLayout.LayoutParams.WRAP_CONTENT
                height = GridLayout.LayoutParams.WRAP_CONTENT
                setMargins(margin, margin, margin, margin)
                // 行、列都要显式指定：只给列号的话，所有牌会叠在第 0 行
                rowSpec = GridLayout.spec(index / columnCount)
                columnSpec = GridLayout.spec(index % columnCount, 1f)
            }
            item.layoutParams = params

            grid.addView(item)
            cardViews.add(item)
        }
    }

    /**
     * 把第 index 张牌刷新为当前状态。
     * @param onClick 点击回调，由 Controller 传入；已配对的牌会被直接禁用
     */
    fun renderCard(index: Int, card: Card, onClick: () -> Unit) {
        val item = cardViews.getOrNull(index) ?: return
        val face = item.findViewById<TextView>(R.id.tv_card_face)

        when {
            card.isMatched -> {
                // 已配对：露出牌面，绿色边框，不再响应点击
                item.setBackgroundResource(R.drawable.bg_card_matched_face)
                face.text = card.toString()
                face.setTextColor(faceColor(card))
                face.alpha = 0.55f
                item.isClickable = false
                item.setOnClickListener(null)
                item.contentDescription = card.toString()
            }

            card.isChosen -> {
                // 已翻开未配对：露出牌面，金色边框
                item.setBackgroundResource(R.drawable.bg_card_front)
                face.text = card.toString()
                face.setTextColor(faceColor(card))
                face.alpha = 1.0f
                item.isClickable = true
                item.setOnClickListener { onClick() }
                item.contentDescription = card.toString()
            }

            else -> {
                // 牌背
                item.setBackgroundResource(R.drawable.bg_card_back)
                face.text = context.getString(R.string.game_card_hidden)
                face.setTextColor(context.getColor(R.color.white))
                face.alpha = 1.0f
                item.isClickable = true
                item.setOnClickListener { onClick() }
                item.contentDescription = context.getString(R.string.cd_card_back)
            }
        }
    }

    /** 刷新得分显示 */
    fun renderScore(score: Int) {
        scoreView.text = context.getString(R.string.game_score_format, score)
    }

    /** 红桃、方块用红色，黑桃、梅花用深色 */
    private fun faceColor(card: Card): Int {
        val red = card.suitText == "♥" || card.suitText == "♦"
        return context.getColor(
            if (red) R.color.card_text_red else R.color.card_text_black
        )
    }

    private fun dp(value: Int): Int =
        Math.round(context.resources.displayMetrics.density * value)

    private companion object {
        const val COLUMN_COUNT = 4
    }
}
