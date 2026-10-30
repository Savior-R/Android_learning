package cn.edu.sicnu.reproject.cardgame.fragment.controller

import android.content.Context
import android.widget.Button
import android.widget.TextView
import cn.edu.sicnu.reproject.cardgame.fragment.R
import cn.edu.sicnu.reproject.cardgame.fragment.model.CardMatchingGame
import cn.edu.sicnu.reproject.cardgame.fragment.model.GameState
import cn.edu.sicnu.reproject.cardgame.fragment.model.PlayerInfo
import cn.edu.sicnu.reproject.cardgame.fragment.view.CardGameView

/**
 * 实验六 · 翻牌游戏的 Controller 层
 *
 * 与前两个实验的差别：Controller 不再属于某个 Activity，而是被 [cn.edu.sicnu.reproject.cardgame.fragment.fragment.GameFragment] 持有。
 *  - [cardsToMatch] 由 Fragment 通过参数传入（2 或 3），不再从资源里读；
 *  - [attach] 多了一个 savedState 参数：有存档就直接还原，没有才新开一局；
 *  - [snapshot] 把当前局面导出给 Fragment，由它在 onPause 时写入文件。
 *
 * @param context         宿主上下文
 * @param gameView        View 层
 * @param playerInfo      玩家信息，用来回写最高分
 * @param cardsToMatch    几张牌算配对成功
 * @param onScoreChanged  得分变化回调（用于 Toast 提示）
 * @param onAllCleared    全部配对完成时的回调
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class CardGameController(
    private val context: Context,
    private val gameView: CardGameView,
    private val playerInfo: PlayerInfo,
    val cardsToMatch: Int,
    private val onScoreChanged: (oldScore: Int, newScore: Int, matched: Boolean, compared: Boolean) -> Unit,
    private val onAllCleared: (finalScore: Int, isNewRecord: Boolean) -> Unit = { _, _ -> }
) {

    /** 当前这一局的模型 */
    lateinit var game: CardMatchingGame
        private set

    /** 本局发牌张数：竖屏 12、横屏 24 */
    val cardCount: Int = context.resources.getInteger(R.integer.card_count)

    private var allClearedNotified = false

    /**
     * 绑定控件并开局。
     * @param savedState 上次退出时保存的快照；为 null 或与当前配置不匹配时新开一局
     */
    fun attach(scoreView: TextView, resetButton: Button, savedState: GameState? = null) {
        gameView.scoreView = scoreView
        resetButton.setOnClickListener { startNewGame() }

        game = CardMatchingGame(cardCount, cardsToMatch)
        if (savedState != null) {
            game.restore(savedState)
            allClearedNotified = isAllCleared()
        }
        gameView.bindCards(game)
        gameView.renderScore(game.score)
    }

    /** 导出当前局面，供持久化 */
    fun snapshot(): GameState = game.snapshot()

    /** 重新开始一局：调用 Model 的 reset() */
    fun startNewGame() {
        game.reset()
        allClearedNotified = false
        gameView.bindCards(game)
        gameView.renderScore(game.score)
    }

    /**
     * 处理“点击第 index 张牌”。
     * 先在调用模型前记下得分与牌状态，调用后对比得出这次发生了什么。
     */
    fun onCardClicked(index: Int) {
        if (index < 0 || index >= game.cardCount) return

        val oldScore = game.score
        val chosenBefore = chosenCount()
        val card = game.cardAtIndex(index)
        val wasChosen = card.isChosen
        val wasMatched = card.isMatched

        game.chooseCardAtIndex(index)

        if (card.isChosen == wasChosen && card.isMatched == wasMatched && game.score == oldScore) {
            return
        }

        val justMatched = !wasMatched && card.isMatched
        // 只有凑满 cardsToMatch 张、真正比较过一次，才可能“配对失败”
        val compared = chosenBefore == cardsToMatch - 1
        gameView.renderAll()
        gameView.renderScore(game.score)
        onScoreChanged(oldScore, game.score, justMatched, compared)

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
}
