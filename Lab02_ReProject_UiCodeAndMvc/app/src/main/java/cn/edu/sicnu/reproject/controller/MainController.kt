package cn.edu.sicnu.reproject.controller

import android.content.Context
import android.view.View
import cn.edu.sicnu.reproject.R
import cn.edu.sicnu.reproject.model.ProgramAdviserModel
import cn.edu.sicnu.reproject.view.MainView

/**
 * 实验二 · Controller 层（控制器层）
 *
 * MVC 中的 C。它是 View 与 Model 之间的**唯一桥梁**：
 *
 *   ① 启动时：让 View 用代码生成界面，并把 Model 里的语言列表交给 View 渲染；
 *   ② 区 1：监听「ADD TEXTVIEW」按钮 → 计数 +1 → 通知 View 追加一个 TextView；
 *   ③ 区 2：监听「确定」按钮 → 读取用户所选语言 → **查询模型层** → 把结果回填给 View。
 *
 * 这样 View 不知道 Model 的存在，Model 也不知道 View 的存在，
 * 所有「谁调谁」的逻辑都收敛在 Controller 里，这正是 MVC 分层的价值。
 *
 * @author 犹锐 2024110544 计算机科学学院 5 班
 */
class MainController(
    private val context: Context,
    /** 注入 View 层：Controller 操作界面，但界面不反向依赖 Controller */
    private val view: MainView,
    /** 注入 Model 层：Controller 依赖 Model，但 Model 完全不知道 Controller 的存在 */
    private val model: ProgramAdviserModel = ProgramAdviserModel()
) {

    /** 区 1 中动态添加的 TextView 计数（当前是第几个） */
    private var addedTextViewCount = 0

    /**
     * 【启动流程】装配 MVC：生成界面 -> 灌数据 -> 绑定事件 -> 显示默认结果。
     */
    fun start() {
        // ① 让 View 用代码把区 1 / 区 2 全部控件生成出来
        view.buildUi()

        // ② 把模型层的候选语言交给 View 渲染成下拉菜单
        view.setLanguageItems(model.availableLanguages.map { context.getString(it.titleRes) })

        // ③ 绑定两个功能区的交互事件
        view.setOnAddClickListener(View.OnClickListener { onAddTextViewClicked() })
        view.setOnConfirmClickListener(View.OnClickListener { onConfirmClicked() })

        // ④ 首屏显示模型的默认咨询结果（kotlin）
        showAdvice(model.defaultAdvice())
    }

    // ---------------------------------------------------------------
    // 区 1 的交互：动态添加 TextView
    // ---------------------------------------------------------------

    /**
     * 每点一次「ADD TEXTVIEW」：序号 +1，然后让 View 追加一个 TextView。
     *
     * 文案由「字符串资源前缀 + 序号」拼成，前缀取自 res/values/strings.xml，
     * 满足实验要求「禁止在布局文件和代码中直接出现字符串」。
     */
    private fun onAddTextViewClicked() {
        addedTextViewCount++
        val text = context.getString(R.string.new_textview_prefix) + addedTextViewCount
        view.addTextView(text)
    }

    // ---------------------------------------------------------------
    // 区 2 的交互：查询模型层并回填结果
    // ---------------------------------------------------------------

    /**
     * 点击「确定」后：取用户所选下标 -> 查询 Model -> 把文案写回 View。
     * 这三步正好对应 MVC 的「View 产生事件 → Controller 处理 → Model 返回数据 → View 刷新」。
     */
    private fun onConfirmClicked() {
        // ★ 查询模型层：把用户选中的下标交给 Model，取回对应的咨询记录
        showAdvice(model.adviceAt(view.selectedLanguageIndex()))
    }

    /** 把一条咨询记录（两个字符串资源 id）解析成文案后交给 View 显示 */
    private fun showAdvice(advice: ProgramAdviserModel.Advice) {
        view.showResult(
            context.getString(advice.titleRes),
            context.getString(advice.descriptionRes)
        )
    }
}
