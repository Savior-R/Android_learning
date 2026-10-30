package cn.edu.sicnu.reproject.cardgame

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout
import cn.edu.sicnu.reproject.cardgame.controller.CardGameController
import cn.edu.sicnu.reproject.cardgame.util.Insets
import cn.edu.sicnu.reproject.cardgame.view.CardGameView

/**
 * 实验四 · 翻牌游戏 Activity（二级 Activity）
 *
 * 三个角色的分工在这个类里体现得最清楚：
 *   Model      -> CardMatchingGame / Card / Deck（纯 Kotlin，无 Android 依赖）
 *   View       -> [CardGameView]（决定一张牌长什么样）
 *   Controller -> [CardGameController]（处理点击、驱动模型、刷新界面）
 *
 * 本类只负责把这三者装配起来，并处理 Toast / 返回这类"平台相关"的琐事。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class CardGameActivity : AppCompatActivity() {

    private lateinit var controller: CardGameController

    private val playerInfo
        get() = (application as CardGameApp).playerInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_game)

        val grid = findViewById<GridLayout>(R.id.grid_cards)
        val scoreView = findViewById<TextView>(R.id.tv_score)
        val btnReset = findViewById<Button>(R.id.btn_reset)
        val btnExit = findViewById<Button>(R.id.btn_exit)

        Insets.applyTopPaddingToHeader(findViewById(R.id.header_game))

        val gameView = CardGameView(this, grid)

        controller = CardGameController(
            context = this,
            gameView = gameView,
            playerInfo = playerInfo,
            onExitRequested = { finish() },
            onScoreChanged = { oldScore, newScore, matched ->
                showScoreToast(oldScore, newScore, matched)
            },
            onAllCleared = { finalScore, isNewRecord ->
                Toast.makeText(
                    this,
                    getString(R.string.game_toast_all_cleared, finalScore),
                    Toast.LENGTH_LONG
                ).show()
                if (isNewRecord) {
                    Toast.makeText(
                        this,
                        getString(R.string.game_toast_new_record, playerInfo.bestScore),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )

        val root = findViewById<android.view.View>(R.id.game_root)
        controller.attach(root, scoreView, btnReset, btnExit)
        // attach 内部用 post 才真正开局，日志必须排在同一队列之后
        root.post { logDeal() }
    }

    /**
     * 调试用：把本局牌序打印到 Logcat，便于开发阶段核对发牌。
     */
    private fun logDeal() {
        val order = (0 until controller.game.cardCount)
            .joinToString(" ") { controller.game.cardAtIndex(it).toString() }
        Log.d("CardGame", "deal(${controller.game.cardCount}): $order")
    }

    /**
     * 把"这次翻牌发生了什么"翻译成人话。
     * 由于 Model 只返回分数，这里用前后的分差反推原因：
     *   配对成功时分数一定上升（+4 或 +1 再乘 4，扣掉 1 分成本后仍为正）；
     *   配对失败时分数变化为 -(2+1)；
     *   其余情况是单纯翻牌 / 收牌。
     */
    private fun showScoreToast(oldScore: Int, newScore: Int, matched: Boolean) {
        val text = when {
            matched -> {
                val gained = newScore - oldScore
                getString(R.string.game_toast_matched, gained)
            }
            newScore < oldScore -> {
                val lost = oldScore - newScore
                getString(R.string.game_toast_mismatch, lost)
            }
            else -> null
        }
        if (text != null) {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }
    }
}
