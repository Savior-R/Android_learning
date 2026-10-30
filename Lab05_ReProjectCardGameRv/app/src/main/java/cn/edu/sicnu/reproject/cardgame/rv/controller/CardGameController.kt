package cn.edu.sicnu.reproject.cardgame.rv.controller

import android.content.Context
import android.widget.Button
import android.widget.TextView
import cn.edu.sicnu.reproject.cardgame.rv.R
import cn.edu.sicnu.reproject.cardgame.rv.model.CardMatchingGame
import cn.edu.sicnu.reproject.cardgame.rv.model.PlayerInfo
import cn.edu.sicnu.reproject.cardgame.rv.view.CardGameView

/**
 * 实验五 · 翻牌游戏的 Controller 层
 *
 * 职责：
 *  - 接收用户交互：点牌、点“重置游戏”、点“退出”；
 *  - 处理模型：点击转成 [CardMatchingGame.chooseCardAtIndex]，重置转成 [CardMatchingGame.reset]；
 *  - 同步界面：让 [CardGameView] 刷新对应牌与得分。
 *
 * 与实验四的差别：
 *  - 牌不再由 View 手工创建，全部交给 RecyclerView + Adapter；
 *  - 发牌张数与列数改为从资源读（竖屏 12 张 / 3 列，横屏 24 张 / 6 列）；
 *  - “重新开始一局”改为调用 Model 新增的 reset()。
 *
 * @param context         宿主 Activity
 * @param gameView        View 层
 * @param playerInfo      玩家信息，用来回写最高分
 * @param onExitRequested 点“退出”时的回调
 * @param onScoreChanged  得分变化回调（用于 Toast 提示）
 * @param onAllCleared    全部配对完成时的回调
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class CardGameController(
    private val context: Context,
    private val gameView: CardGameView,
    private val playerInfo: PlayerInfo,
    private val onExitRequested: () -> Unit,
    private val onScoreChanged: (oldScore: Int, newScore: Int, matched: Boolean, compared: Boolean) -> Unit,
    private val onAllCleared: (finalScore: Int, isNewRecord: Boolean) -> Unit = { _, _ -> }
) {

    /** 当前这一局的模型 */
    lateinit var game: CardMatchingGame
        private set

    /** 本局发牌张数：竖屏 12、横屏 24，由资源限定符决定 */
    val cardCount: Int = context.resources.getInteger(R.integer.card_count)

    /** 本局几张牌算配对成功：实验五的正式关卡是三张牌 */
    val cardsToMatch: Int = context.resources.getInteger(R.integer.cards_to_match)

    private var allClearedNotified = false

    /** 绑定控件并开第一局 */
    fun attach(scoreView: TextView, resetButton: Button, exitButton: Button) {
        gameView.scoreView = scoreView
        resetButton.setOnClickListener { startNewGame() }
        exitButton.setOnClickListener { onExitRequested() }
        startNewGame()
    }

    /**
     * 重新开始一局。
     * Model 有 reset() 时复用它（保持同一个模型实例，便于以后接持久化）；
     * 没有则新建一个。
     */
    fun startNewGame() {
        if (::game.isInitialized && game.cardCount == cardCount && game.cardsToMatch == cardsToMatch) {
            game.reset()
        } else {
            game = CardMatchingGame(cardCount, cardsToMatch)
        }
        allClearedNotified = false
        gameView.bindCards(game)
        gameView.renderScore(game.score)
    }

    /**
     * 处理“点击第 index 张牌”。
     * 为了在 Toast 里说清这次是配对成功还是失败，先在调用模型前记下得分与牌状态，
     * 调用后对比得出结果。
     */
    fun onCardClicked(index: Int) {
        if (index < 0 || index >= game.cardCount) return

        val oldScore = game.score
        val chosenBefore = chosenCount()
        val card = game.cardAtIndex(index)
        val wasChosen = card.isChosen
        val wasMatched = card.isMatched

        game.chooseCardAtIndex(index)

        // 状态与分数都没变，说明这次点击被模型忽略（例如重复点已配对的牌）
        if (card.isChosen == wasChosen && card.isMatched == wasMatched && game.score == oldScore) {
            return
        }

        val justMatched = !wasMatched && card.isMatched
        // 只有“凑满 cardsToMatch 张、真正比较过一次”才可能配对失败；
        // 三张牌玩法里前两张只是翻开，不应该提示失败
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

    /** 本局当前得分 */
    fun currentScore(): Int = game.score
}
