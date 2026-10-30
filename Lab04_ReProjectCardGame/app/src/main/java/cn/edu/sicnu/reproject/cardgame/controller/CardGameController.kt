package cn.edu.sicnu.reproject.cardgame.controller

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import cn.edu.sicnu.reproject.cardgame.model.CardMatchingGame
import cn.edu.sicnu.reproject.cardgame.model.PlayerInfo
import cn.edu.sicnu.reproject.cardgame.view.CardGameView

/**
 * 实验四 · 翻牌游戏的 Controller 层
 *
 * 职责边界（MVC 的 C）：
 *  - 接收用户交互：点牌、点"重置游戏"、点"退出"；
 *  - 处理模型：把点击转成 [CardMatchingGame.chooseCardAtIndex]，读回得分；
 *  - 同步界面：让 [CardGameView] 按最新模型状态重绘；
 *  - 重新开始一局：丢弃旧模型，new 一个新的并重绘。
 *
 * 它不直接操作布局文件里的控件树，而是通过 View 层暴露的方法去刷新，
 * 因此"换一套界面"时 Controller 的逻辑不需要改。
 *
 * @param context          宿主 Activity 的上下文
 * @param gameView         View 层
 * @param playerInfo       玩家信息，用来回写最高分
 * @param onExitRequested  点击"退出"时的回调（由 Activity 决定是 finish 还是别的动作）
 * @param onScoreChanged   得分变化回调，便于 Activity 弹 Toast 说明加减分
 * @param onAllCleared     全部配对完成时的回调
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class CardGameController(
    private val context: Context,
    private val gameView: CardGameView,
    private val playerInfo: PlayerInfo,
    private val onExitRequested: () -> Unit,
    private val onScoreChanged: (oldScore: Int, newScore: Int, matched: Boolean) -> Unit,
    private val onAllCleared: (finalScore: Int, isNewRecord: Boolean) -> Unit = { _, _ -> }
) {

    /** 当前这一局的模型；每次重置都会被替换 */
    lateinit var game: CardMatchingGame
        private set

    /** 发牌张数：16 张 = 4 列 × 4 行，偶数张才能全部配对 */
    val cardCount: Int = DEFAULT_CARD_COUNT

    /** 是否已经就"全部配对完成"提示过一次，避免重复提示 */
    private var allClearedNotified = false

    /**
     * 绑定界面控件并开第一局。
     * 采用 post 是因为 GridLayout 必须先完成一次测量，
     * 才能拿到宽度去平分每张牌的位置。
     */
    fun attach(root: View, scoreView: TextView, resetButton: Button, exitButton: Button) {
        gameView.scoreView = scoreView
        resetButton.setOnClickListener { startNewGame() }
        exitButton.setOnClickListener { onExitRequested() }
        root.post { startNewGame() }
    }

    /**
     * 重新开始一局：新建模型 -> 重建牌桌 -> 刷新得分。
     * 这是实验要求中"可以重新开始一局游戏"的落点。
     */
    fun startNewGame() {
        game = CardMatchingGame(cardCount)
        allClearedNotified = false
        gameView.bindCards(game)
        gameView.renderScore(game.score)
        renderAll()
    }

    /**
     * 处理"点击第 index 张牌"。
     *
     * 为了在 Toast 里说清这次是配对成功还是失败，
     * 这里在调用模型**之前**记下得分和这张牌的状态，调用之后再对比。
     * 这样 Model 只需要暴露最朴素的分数与状态，不必额外返回结果对象。
     */
    fun onCardClicked(index: Int) {
        val oldScore = game.score
        val card = game.cardAtIndex(index)
        val wasChosen = card.isChosen
        val wasMatched = card.isMatched

        game.chooseCardAtIndex(index)

        // 状态没有变化说明这次点击被模型忽略了（例如重复点击已配对的牌）
        if (card.isChosen == wasChosen && card.isMatched == wasMatched && game.score == oldScore) {
            return
        }

        val justMatched = !wasMatched && card.isMatched
        renderAll()
        gameView.renderScore(game.score)
        onScoreChanged(oldScore, game.score, justMatched)

        if (!allClearedNotified && isAllCleared()) {
            allClearedNotified = true
            val isNewRecord = playerInfo.updateBestScore(game.score)
            onAllCleared(game.score, isNewRecord)
        }
    }

    /** 当前翻开但尚未配对的牌数 */
    fun chosenCount(): Int = (0 until game.cardCount).count {
        val c = game.cardAtIndex(it)
        c.isChosen && !c.isMatched
    }

    /** 是否所有牌都配对完成 */
    fun isAllCleared(): Boolean = (0 until game.cardCount).all {
        game.cardAtIndex(it).isMatched
    }

    /** 本局当前得分 */
    fun currentScore(): Int = game.score

    // ------------------------------------------------------------------

    /** 把所有牌重新画一遍，并把点击回调重新挂上 */
    private fun renderAll() {
        for (index in 0 until game.cardCount) {
            val card = game.cardAtIndex(index)
            gameView.renderCard(index, card) { onCardClicked(index) }
        }
    }

    companion object {
        const val DEFAULT_CARD_COUNT = 16
    }
}
