package cn.edu.sicnu.reproject.cardgame

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.reproject.cardgame.controller.MainController
import cn.edu.sicnu.reproject.cardgame.util.Insets

/**
 * 实验四 · 主 Activity
 *
 * 只做"装配"：
 *   1. 从 Application 单例里取全局的 [cn.edu.sicnu.reproject.cardgame.model.PlayerInfo]；
 *   2. 让 [MainController] 找到控件并把模型渲染上去；
 *   3. 两个按钮分别启动"信息修改"与"翻牌游戏"两个二级 Activity。
 *
 * 因为玩家信息存在 Application 级单例里，从信息修改界面返回后，
 * 在 onResume 里再渲染一次就能看到最新内容，不需要 startActivityForResult。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class MainActivity : AppCompatActivity() {

    private lateinit var controller: MainController

    private val playerInfo
        get() = (application as CardGameApp).playerInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Android 15+ 强制 edge-to-edge：标题栏与列表要避开状态栏 / 导航栏
        Insets.applyTopPaddingToHeader(findViewById(R.id.header_main))
        Insets.applyBottomInset(findViewById(R.id.scroll_root))

        controller = MainController(this)
        controller.findViews()

        // 进入"信息修改"界面
        controller.btnEditInfo.setOnClickListener {
            startActivity(Intent(this, EditInfoActivity::class.java))
        }

        // 进入"翻牌游戏"界面
        controller.btnPlayGame.setOnClickListener {
            startActivity(Intent(this, CardGameActivity::class.java))
        }
    }

    /**
     * 每次回到主界面都重新渲染：
     * 这样在信息修改界面点"保存"、或在游戏里刷新了最高分，返回后立刻可见。
     */
    override fun onResume() {
        super.onResume()
        controller.render(playerInfo)
    }
}
