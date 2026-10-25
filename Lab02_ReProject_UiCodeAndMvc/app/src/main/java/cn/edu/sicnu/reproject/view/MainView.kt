package cn.edu.sicnu.reproject.view

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import cn.edu.sicnu.reproject.R

/**
 * 实验二 · View 层（视图层）
 *
 * MVC 中的 V。**整个界面完全由代码生成，项目中不存在 res/layout 目录**。
 * 它只关心「界面长什么样」，不写业务逻辑、不查询数据：
 *
 *  - [buildUi]       用代码把区 1 / 区 2 的全部控件 new 出来并组装成视图树；
 *  - [addTextView]   在区 1 的容器里追加一个 TextView（由 Controller 触发）；
 *  - [showResult]    把查询结果显示到区 2（由 Controller 回填）；
 *  - setOnXxxListener 把「按钮被点击」这件事交给 Controller 去处理。
 *
 * 界面分两个功能区（与实验要求一致）：
 *   区 1：ScrollView 嵌套 LinearLayout，点按钮动态添加 TextView，可滚动查看；
 *   区 2：MVC 模式的 ProgramAdviser —— 下拉选择语言 + 确定按钮 + 结果显示。
 *
 * @author 犹锐 2024110544 计算机科学学院 5 班
 */
class MainView(val context: Context) {

    /** 根容器：Activity 最终 setContentView 的就是这棵视图树 */
    private val root = LinearLayout(context)

    /** 顶部标题栏（紫色条 + 姓名学号） */
    private lateinit var titleBar: LinearLayout

    /** 区 1 中承载动态 TextView 的滚动容器与外层 ScrollView */
    private lateinit var scrollView: ScrollView
    private lateinit var textViewContainer: LinearLayout

    /** 区 1 的「添加」按钮、区 2 的下拉菜单 / 确定按钮 / 结果文本 */
    private lateinit var addButton: Button
    private lateinit var languageSpinner: Spinner
    private lateinit var confirmButton: Button
    private lateinit var resultView: TextView
    private lateinit var descView: TextView

    fun getRootContainer(): LinearLayout = root

    // ==================================================================
    // 一、纯代码构建整个界面（区 1 + 区 2）
    // ==================================================================

    /**
     * 构建界面树：标题栏 + 区 1 + 区 2。
     * 区 1 占 3 份高度、区 2 占 2 份，与参考界面上下分区的比例一致。
     */
    fun buildUi() {
        root.orientation = LinearLayout.VERTICAL
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        root.setBackgroundColor(ContextCompat.getColor(context, R.color.screen_bg))
        root.clipToPadding = false

        root.addView(buildTitleBar(), LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        root.addView(buildZone1(), LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 3f
        ))
        root.addView(buildZone2(), LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 2f
        ))
    }

    /**
     * 标题栏（代替系统 ActionBar）：紫色底 + 白色加粗标题，标题文本取自 @string/app_name
     * （即「姓名 + 学号」），与实验提交要求一致。
     *
     * 注意：Android 15（API 35）起应用强制 edge-to-edge，窗口会画到状态栏下面，
     * 因此标题栏顶部要加上状态栏的高度做内边距，否则标题会被状态栏压住。
     */
    private fun buildTitleBar(): View {
        titleBar = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(ContextCompat.getColor(context, R.color.primary))
            setPadding(dp(16), dp(14), dp(16), dp(14))
        }
        val title = TextView(context).apply {
            text = context.getString(R.string.app_name)
            textSize = 20f
            setTextColor(ContextCompat.getColor(context, R.color.title_text))
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
        titleBar.addView(title, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        return titleBar
    }

    /**
     * 应用系统栏内边距：状态栏的高度加到标题栏的上内边距里（紫色一直延伸到状态栏），
     * 导航栏的高度加到根容器的下内边距里，保证内容不被手势条遮挡。
     */
    fun applySystemBarInsets(statusBarTop: Int, navigationBarBottom: Int) {
        titleBar.setPadding(dp(16), dp(14) + statusBarTop, dp(16), dp(14))
        root.setPadding(0, 0, 0, navigationBarBottom)
    }

    // ---------------------------------------------------------------
    // 区 1：点击按钮用代码动态添加 TextView，并支持滚动
    // ---------------------------------------------------------------

    /**
     * 区 1 结构：顶部固定的按钮条 + ScrollView 嵌套 LinearLayout
     *
     *   LinearLayout(竖向)
     *     ├─ LinearLayout(白色按钮条)
     *     │    └─ Button「ADD TEXTVIEW」   ← 固定在顶部，不随列表滚动
     *     └─ ScrollView(青绿色背景)
     *          └─ LinearLayout(竖向)      ← 动态添加的 TextView 全部进这里
     *
     * TextView 会越加越多，只有靠 ScrollView 才能滚动查看，
     * 这正是实验要求「采用 ScrollView 嵌套 LinearLayout」的原因。
     */
    private fun buildZone1(): View {
        val zone = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(ContextCompat.getColor(context, R.color.zone1_list_bg))
        }

        // 1) 顶部按钮条（白色）
        val buttonBar = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setBackgroundColor(ContextCompat.getColor(context, R.color.zone1_header_bg))
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }
        addButton = Button(context).apply {
            text = context.getString(R.string.btn_add_textview)
        }
        buttonBar.addView(addButton, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        zone.addView(buttonBar, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        // 2) ScrollView 嵌套 LinearLayout：动态 TextView 的容器
        textViewContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(4), dp(4), dp(4), dp(4))
        }
        scrollView = ScrollView(context).apply {
            isFillViewport = true
            setBackgroundColor(ContextCompat.getColor(context, R.color.zone1_list_bg))
        }
        scrollView.addView(textViewContainer, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        zone.addView(scrollView, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        return zone
    }

    // ---------------------------------------------------------------
    // 区 2：MVC 模式的 ProgramAdviser 交互区
    // ---------------------------------------------------------------

    /**
     * 区 2 结构：
     *   LinearLayout(竖向, 绿色底)
     *     ├─ LinearLayout(横向)
     *     │    ├─ TextView「新语言」        ← 标签
     *     │    └─ Spinner                  ← 候选语言来自 Model
     *     ├─ Button「确定」                ← 点击 -> Controller 查询 Model
     *     ├─ TextView(大字号)              ← 查询结果：语言名（与参考界面一致）
     *     └─ ScrollView
     *          └─ TextView(小字号)         ← 查询结果：该语言的说明文字
     */
    private fun buildZone2(): View {
        val zone = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(ContextCompat.getColor(context, R.color.zone2_bg))
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }

        // 1) 语言选择行：标签 + 下拉菜单
        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val label = TextView(context).apply {
            text = context.getString(R.string.zone2_label)
            textSize = 20f
            setTextColor(ContextCompat.getColor(context, R.color.text_primary))
        }
        row.addView(label, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        languageSpinner = Spinner(context).apply {
            prompt = context.getString(R.string.zone2_spinner_prompt)
        }
        row.addView(languageSpinner, LinearLayout.LayoutParams(
            0, dp(48), 1f
        ))
        zone.addView(row, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        // 2) 确定按钮：事件响应入口
        confirmButton = Button(context).apply {
            text = context.getString(R.string.btn_confirm)
        }
        zone.addView(confirmButton, LinearLayout.LayoutParams(dp(120), dp(48)).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            topMargin = dp(16)
        })

        // 3) 查询结果：语言名用大字号（与参考界面的 kotlin 一致）
        resultView = TextView(context).apply {
            textSize = 32f
            gravity = Gravity.CENTER
            setTextColor(ContextCompat.getColor(context, R.color.text_primary))
        }
        zone.addView(resultView, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = dp(12)
        })

        // 4) 查询结果：语言说明文字，放进 ScrollView 防止长文本溢出屏幕
        descView = TextView(context).apply {
            textSize = 16f
            setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            setPadding(dp(4), dp(4), dp(4), dp(4))
        }
        val descScroll = ScrollView(context).apply {
            isFillViewport = true
        }
        descScroll.addView(descView, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        zone.addView(descScroll, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        return zone
    }

    // ==================================================================
    // 二、对外暴露的「显示接口」与「事件接口」（由 Controller 调用）
    // ==================================================================

    fun setOnAddClickListener(listener: View.OnClickListener) {
        addButton.setOnClickListener(listener)
    }

    fun setOnConfirmClickListener(listener: View.OnClickListener) {
        confirmButton.setOnClickListener(listener)
    }

    /** 把模型层提供的候选语言灌进下拉菜单 */
    fun setLanguageItems(titles: List<String>) {
        languageSpinner.adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            titles
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    /** 用户当前选中的语言下标（交给 Controller 去查 Model） */
    fun selectedLanguageIndex(): Int = languageSpinner.selectedItemPosition

    /** 在区 1 动态追加一个 TextView，并自动滚动到最新一条 */
    fun addTextView(text: String) {
        val tv = TextView(context).apply {
            this.text = text
            textSize = 22f
            setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            setPadding(dp(4), dp(8), dp(4), dp(8))
        }
        textViewContainer.addView(tv)
        scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
    }

    /** 区 1 中已动态添加的 TextView 个数 */
    fun textViewCount(): Int = textViewContainer.childCount

    /** 把查询结果显示到区 2：语言名用大字号，说明文字放在下方可滚动的区域 */
    fun showResult(title: String, description: String) {
        resultView.text = title
        descView.text = description
    }

    /** dp 转 px */
    private fun dp(value: Int): Int =
        Math.round(context.resources.displayMetrics.density * value)
}
