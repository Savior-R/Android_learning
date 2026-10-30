package cn.edu.sicnu.reproject.cardgame.rv

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.reproject.cardgame.rv.controller.CardGameController
import cn.edu.sicnu.reproject.cardgame.rv.util.Insets
import cn.edu.sicnu.reproject.cardgame.rv.view.CardGameView

/**
 * 实验五 · 翻牌游戏 Activity（二级 Activity）
 *
 * 三个角色：
 *   Model      -> CardMatchingGame / Card / Deck（纯 Kotlin）
 *   View       -> CardGameView + CardAdapter + RecyclerView
 *   Controller -> CardGameController
 *
 * 本类只做装配，另外负责一件与屏幕方向有关的事：
 * 布局里 RecyclerView 的列数、本局发牌张数都来自资源限定符
 * （values/integers.xml 与 values-land/integers.xml），
 * 因此不拦截方向变化，让 Activity 正常重建，重建后自然读到另一套数值，
 * 也就实现了“横屏竖屏不同牌数”。
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

        val recycler = findViewById<RecyclerView>(R.id.rv_cards)
        val scoreView = findViewById<TextView>(R.id.tv_score)
        val modeView = findViewById<TextView>(R.id.tv_game_mode)
        val btnReset = findViewById<Button>(R.id.btn_reset)
        val btnExit = findViewById<Button>(R.id.btn_exit)

        // 让顶部栏避开状态栏、牌桌避开导航条
        Insets.applyTopPaddingToHeader(findViewById(R.id.header_game))
        Insets.applyBottomInset(findViewById(R.id.board_container))

        val gameView = CardGameView(this, recycler) { index ->
            controller.onCardClicked(index)
        }
        gameView.setup()

        controller = CardGameController(
            context = this,
            gameView = gameView,
            playerInfo = playerInfo,
            onExitRequested = { finish() },
            onScoreChanged = { oldScore, newScore, matched, compared ->
                showScoreToast(oldScore, newScore, matched, compared)
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
        controller.attach(scoreView, btnReset, btnExit)

        logDeal()

        // 顶部提示当前玩法与本局牌数，横竖屏切换后这里会跟着变
        modeView.text = getString(
            R.string.game_mode_format,
            controller.cardsToMatch,
            controller.game.cardCount
        )
    }

    /**
     * 调试用：把本局牌序打印到 Logcat。
     * 仅用于开发阶段核对发牌与自动化截图时定位可配对的牌，不影响游戏逻辑。
     */
    private fun logDeal() {
        val order = (0 until controller.game.cardCount)
            .joinToString(" ") { controller.game.cardAtIndex(it).toString() }
        Log.d("CardGameRv", "deal(${controller.game.cardCount}): $order")
    }

    /** 把“这次翻牌发生了什么”翻译成提示文字 */
    private fun showScoreToast(oldScore: Int, newScore: Int, matched: Boolean, compared: Boolean) {
        val text = when {
            matched -> getString(R.string.game_toast_matched, newScore - oldScore)
            compared && newScore < oldScore -> getString(R.string.game_toast_mismatch, oldScore - newScore)
            else -> null
        }
        if (text != null) {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }
    }
}
