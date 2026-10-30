package cn.edu.sicnu.reproject.cardgame

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.reproject.cardgame.controller.EditInfoController
import cn.edu.sicnu.reproject.cardgame.util.Insets

/**
 * 实验四 · 信息修改 Activity（二级 Activity）
 *
 * 交互约定：
 *  - 进入时用当前玩家信息填满表单；
 *  - "保存" -> 把表单内容写回全局 PlayerInfo，提示后关闭本界面；
 *  - "取消" -> 什么都不写，直接关闭。
 *
 * 按实验要求，修改后的信息不写入文件，只在本进程内有效。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class EditInfoActivity : AppCompatActivity() {

    private lateinit var controller: EditInfoController

    private val playerInfo
        get() = (application as CardGameApp).playerInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_info)

        Insets.applyTopPaddingToHeader(findViewById(R.id.header_edit))
        Insets.applyBottomInset(findViewById(R.id.scroll_root))

        controller = EditInfoController(this)
        controller.findViews(this)
        controller.loadInto(playerInfo)   // 用现有数据填表单

        // 姓名改动时同步更新头像首字
        controller.etName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                controller.updateAvatarPreview(controller.etName.text.toString())
            }
        }

        controller.btnSave.setOnClickListener {
            // 收集表单 -> 覆盖到全局模型 -> 关闭界面
            val edited = controller.collect(playerInfo)
            playerInfo.copyFrom(edited)
            Toast.makeText(this, R.string.edit_toast_saved, Toast.LENGTH_SHORT).show()
            finish()
        }

        controller.btnCancel.setOnClickListener {
            Toast.makeText(this, R.string.edit_toast_cancelled, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
