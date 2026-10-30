package cn.edu.sicnu.reproject.cardgame.rv.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.reproject.cardgame.rv.R
import cn.edu.sicnu.reproject.cardgame.rv.model.Card
import cn.edu.sicnu.reproject.cardgame.rv.model.CardMatchingGame

/**
 * 实验五 · RecyclerView 适配器（View 层核心）
 *
 * 继承 [RecyclerView.Adapter]，重载三个必需方法：
 *  - [getItemCount]       ：一局有多少张牌，由 Model 决定；
 *  - [onCreateViewHolder] ：把 item_card.xml 变成一张“牌”的壳；
 *  - [onBindViewHolder]   ：按 Model 里这张牌的状态决定它的外观。
 *
 * 另加两项本实验要求的能力：
 *  - 点击回调：构造时传入 [onCardClick]，在 ViewHolder 里转发，Adapter 自己不处理业务；
 *  - 状态刷新：[notifyCardChanged] / [notifyAllCards] 供 Controller 精确刷新某一张或整桌牌。
 *
 * @param onCardClick 点击第 position 张牌时的回调，由 Controller 注入
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class CardAdapter(
    private val onCardClick: (position: Int) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    /** 当前这一局的模型；每局开始时由 [bindGame] 换掉 */
    private var game: CardMatchingGame? = null

    /** 绑定新的一局，并整表刷新 */
    fun bindGame(game: CardMatchingGame) {
        this.game = game
        notifyDataSetChanged()
    }

    /** 只刷新某一张牌（翻牌时用，避免整表重绘） */
    fun notifyCardChanged(position: Int) {
        notifyItemChanged(position)
    }

    /** 整表刷新（重置游戏、旋转屏幕后重排时用） */
    fun notifyAllCards() {
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = game?.cardCount ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val item = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(item)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = game?.cardAtIndex(position) ?: return
        holder.bind(card) { onCardClick(holder.adapterPosition) }
    }

    /**
     * 一张牌的 ViewHolder。
     * 外观严格对应 Model 里两个布尔量的组合，与实验四的 View 规则保持一致：
     *   已配对 -> 白底绿框半透明且不可点；已翻开 -> 白底金框；否则 -> 蓝色牌背。
     */
    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val face: TextView = itemView.findViewById(R.id.tv_card_face)
        private val context = itemView.context

        fun bind(card: Card, onClick: () -> Unit) {
            when {
                card.isMatched -> {
                    itemView.setBackgroundResource(R.drawable.bg_card_matched_face)
                    face.text = card.toString()
                    face.setTextColor(faceColor(card))
                    face.alpha = 0.55f
                    itemView.isClickable = false
                    itemView.setOnClickListener(null)
                    itemView.contentDescription = card.toString()
                }

                card.isChosen -> {
                    itemView.setBackgroundResource(R.drawable.bg_card_front)
                    face.text = card.toString()
                    face.setTextColor(faceColor(card))
                    face.alpha = 1.0f
                    itemView.isClickable = true
                    itemView.setOnClickListener { onClick() }
                    itemView.contentDescription = card.toString()
                }

                else -> {
                    itemView.setBackgroundResource(R.drawable.bg_card_back)
                    face.text = context.getString(R.string.game_card_hidden)
                    face.setTextColor(ContextCompat.getColor(context, R.color.white))
                    face.alpha = 1.0f
                    itemView.isClickable = true
                    itemView.setOnClickListener { onClick() }
                    itemView.contentDescription = context.getString(R.string.cd_card_back)
                }
            }
        }

        /** 红桃、方块用红色，黑桃、梅花用深色 */
        private fun faceColor(card: Card): Int {
            val red = card.suitText == "♥" || card.suitText == "♦"
            return ContextCompat.getColor(
                context,
                if (red) R.color.card_text_red else R.color.card_text_black
            )
        }
    }
}
