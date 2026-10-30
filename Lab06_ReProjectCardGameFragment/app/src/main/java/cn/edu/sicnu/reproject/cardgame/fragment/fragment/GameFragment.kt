package cn.edu.sicnu.reproject.cardgame.fragment.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.reproject.cardgame.fragment.CardGameApp
import cn.edu.sicnu.reproject.cardgame.fragment.R
import cn.edu.sicnu.reproject.cardgame.fragment.controller.CardGameController
import cn.edu.sicnu.reproject.cardgame.fragment.model.PlayerInfo
import cn.edu.sicnu.reproject.cardgame.fragment.store.GameStore
import cn.edu.sicnu.reproject.cardgame.fragment.util.Insets
import cn.edu.sicnu.reproject.cardgame.fragment.view.CardGameView

/**
 * 实验六 · 第 2、3 个 Fragment：翻牌游戏
 *
 * 同一个类通过参数 [ARG_CARDS_TO_MATCH] 复用两次：
 *  - 传 2 就是“两张牌的翻牌游戏”；
 *  - 传 3 就是“三张牌的翻牌游戏”。
 *
 * 持久化：Fragment 每次被替换都会走 onPause，此时把 [CardGameController.snapshot]
 * 得到的 [cn.edu.sicnu.reproject.cardgame.fragment.model.GameState] 序列化进文件；
 * 下一次 onViewCreated 再读回来交给 Controller 还原。退出进程后重新打开同样能恢复。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class GameFragment : Fragment() {

    private val cardsToMatch: Int
        get() = requireArguments().getInt(ARG_CARDS_TO_MATCH, 2)

    private val playerInfo: PlayerInfo
        get() = (requireActivity().application as CardGameApp).playerInfo

    private lateinit var controller: CardGameController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_game, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Insets.applyTopPaddingToHeader(view.findViewById(R.id.header_game))
        Insets.applyBottomInset(view.findViewById(R.id.board_container))

        val title = view.findViewById<TextView>(R.id.tv_game_title)
        val mode = view.findViewById<TextView>(R.id.tv_game_mode)
        val scoreView = view.findViewById<TextView>(R.id.tv_score)
        val btnReset = view.findViewById<Button>(R.id.btn_reset)
        val recycler = view.findViewById<RecyclerView>(R.id.rv_cards)

        title.text = getString(
            if (cardsToMatch >= 3) R.string.game3_title else R.string.game2_title
        )

        val gameView = CardGameView(requireContext(), recycler) { index ->
            controller.onCardClicked(index)
        }
        gameView.setup()

        controller = CardGameController(
            context = requireContext(),
            gameView = gameView,
            playerInfo = playerInfo,
            cardsToMatch = cardsToMatch,
            onScoreChanged = { oldScore, newScore, matched, compared ->
                showScoreToast(oldScore, newScore, matched, compared)
            },
            onAllCleared = { finalScore, isNewRecord ->
                Toast.makeText(
                    requireContext(),
                    getString(R.string.game_toast_all_cleared, finalScore),
                    Toast.LENGTH_LONG
                ).show()
                if (isNewRecord) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.game_toast_new_record, playerInfo.bestScore),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )

        // 有存档就还原，没有就新开一局
        val saved = GameStore.loadGame(requireContext(), cardsToMatch)
        controller.attach(scoreView, btnReset, saved)

        mode.text = getString(R.string.game_mode_format, cardsToMatch, controller.game.cardCount)
        logDeal()
        if (saved != null && saved.cardCount == controller.game.cardCount) {
            Toast.makeText(requireContext(), R.string.game_toast_restored, Toast.LENGTH_SHORT).show()
        }
    }

    /** 视图被替换 / 页面切走时保存这一局的快照 */
    override fun onPause() {
        super.onPause()
        if (::controller.isInitialized) {
            GameStore.saveGame(requireContext(), controller.snapshot())
        }
    }

    /**
     * 调试用：把本局牌序打印到 Logcat，便于核对发牌与自动化截图时定位可配对的牌。
     */
    private fun logDeal() {
        val order = (0 until controller.game.cardCount)
            .joinToString(" ") { controller.game.cardAtIndex(it).toString() }
        Log.d("CardGameFragment$cardsToMatch", "deal(${controller.game.cardCount}): $order")
    }

    /** 把“这次翻牌发生了什么”翻译成提示文字 */
    private fun showScoreToast(oldScore: Int, newScore: Int, matched: Boolean, compared: Boolean) {
        val text = when {
            matched -> getString(R.string.game_toast_matched, newScore - oldScore)
            compared && newScore < oldScore -> getString(R.string.game_toast_mismatch, oldScore - newScore)
            else -> null
        }
        if (text != null) {
            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val ARG_CARDS_TO_MATCH = "cards_to_match"

        /** 创建指定玩法的游戏 Fragment */
        fun newInstance(cardsToMatch: Int): GameFragment = GameFragment().apply {
            arguments = Bundle().apply { putInt(ARG_CARDS_TO_MATCH, cardsToMatch) }
        }
    }
}
