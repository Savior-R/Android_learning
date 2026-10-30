package cn.edu.sicnu.reproject.cardgame.rv

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import cn.edu.sicnu.reproject.cardgame.rv.controller.MainController
import cn.edu.sicnu.reproject.cardgame.rv.util.Insets
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * 实验五 · 主 Activity
 *
 * 在实验四的基础上增加了一个选项菜单：
 *  - 用 [Toolbar] 作为 ActionBar（布局里 id=toolbar）；
 *  - 重载 [onCreateOptionsMenu] 加载 res/menu/menu_main.xml；
 *  - 点“关于”时用 MaterialAlertDialogBuilder 弹出关于对话框，对话框文案全部来自字符串资源。
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

        // 用布局里的 Toolbar 充当 ActionBar，菜单按钮才会出现在标题栏右侧
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        // Android 15+ 强制 edge-to-edge：把标题栏让到状态栏下方
        Insets.applyTopInsetToBar(toolbar)
        Insets.applyBottomInset(findViewById(R.id.scroll_root))

        controller = MainController(this)
        controller.findViews()

        controller.btnEditInfo.setOnClickListener {
            startActivity(Intent(this, EditInfoActivity::class.java))
        }
        controller.btnPlayGame.setOnClickListener {
            startActivity(Intent(this, CardGameActivity::class.java))
        }
    }

    /** 加载菜单资源 */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /** 菜单项点击：目前只有“关于”一项 */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                showAboutDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /** 关于对话框：应用名、版本、作者、学号、包名 */
    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.about_title)
            .setMessage(
                getString(
                    R.string.about_message,
                    getString(R.string.app_name),
                    getString(R.string.about_version),
                    getString(R.string.about_author),
                    getString(R.string.about_student_id),
                    packageName
                )
            )
            .setPositiveButton(R.string.about_ok, null)
            .show()
    }

    /** 每次回到主界面都重新渲染，改完信息 / 刷新最高分后立即可见 */
    override fun onResume() {
        super.onResume()
        controller.render(playerInfo)
    }
}
