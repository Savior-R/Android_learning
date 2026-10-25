package cn.edu.sicnu.reproject.model

import androidx.annotation.StringRes
import cn.edu.sicnu.reproject.R

/**
 * 实验二 · Model 层（模型层）
 *
 * MVC 中的 M。它只负责「数据 + 业务逻辑」，既不持有任何 View 引用，
 * 也不知道界面长什么样——即使 View 整体被替换，Model 一行都不用改。
 *
 * [ProgramAdviserModel] 是本次实验的「编程语言咨询顾问」模型：
 *  1. 内部维护一份语言知识库（[Advice] 列表）；
 *  2. 对外提供 [availableLanguages] 供 View 构建下拉菜单；
 *  3. 对外提供 [adviceAt] / [defaultAdvice] 作为唯一查询入口。
 *
 * 注意：本实验禁止在代码中直接出现字符串，因此模型里存放的不是文案本身，
 * 而是字符串资源 id（@StringRes）。真正的文案由 Controller 调用
 * `getString()` 从 res/values/strings.xml 取出——Model 因此保持「无 Context」的纯数据层。
 *
 * @author 犹锐 2024110544 计算机科学学院 5 班
 */
class ProgramAdviserModel {

    /**
     * 一条咨询记录：语言名（标题资源 id）+ 该语言的说明（描述资源 id）。
     */
    data class Advice(
        @StringRes val titleRes: Int,
        @StringRes val descriptionRes: Int
    )

    /**
     * 语言知识库。用 List 保证顺序，下拉菜单的顺序就是这里的顺序。
     */
    private val knowledgeBase: List<Advice> = listOf(
        Advice(R.string.model_lang_kotlin_title, R.string.model_lang_kotlin_desc),
        Advice(R.string.model_lang_java_title, R.string.model_lang_java_desc),
        Advice(R.string.model_lang_swift_title, R.string.model_lang_swift_desc),
        Advice(R.string.model_lang_dart_title, R.string.model_lang_dart_desc),
        Advice(R.string.model_lang_python_title, R.string.model_lang_python_desc),
        Advice(R.string.model_lang_cpp_title, R.string.model_lang_cpp_desc)
    )

    /**
     * 供 View 构建下拉菜单用的候选语言列表（只读）。
     */
    val availableLanguages: List<Advice>
        get() = knowledgeBase

    /**
     * 【核心查询接口】按下标查询对应的咨询记录。
     *
     * 这就是 MVC 里 Controller 调用的「查询模型层」入口：
     * 界面只把用户选中的位置丢进来，具体返回什么完全由模型决定。
     *
     * @param index 用户在下拉菜单中选中的条目下标
     * @return 对应的咨询记录；下标越界时兜底返回第一条
     */
    fun adviceAt(index: Int): Advice =
        knowledgeBase.getOrElse(index) { knowledgeBase.first() }

    /**
     * 默认咨询结果：界面首次进入时展示的内容（与参考界面默认显示 kotlin 一致）。
     */
    fun defaultAdvice(): Advice = knowledgeBase.first()
}
