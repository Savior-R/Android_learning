package cn.edu.sicnu.reproject.cardgame.fragment.util

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * 实验五 · 系统栏适配工具
 *
 * targetSdk 35 起 Android 强制 edge-to-edge：即使不调用任何 API，
 * 内容也会铺到状态栏与导航栏下面。本实验的几个界面顶部都是深色标题栏，
 * 如果不处理，标题与菜单按钮会被状态栏的时间、电量盖住。
 *
 * 这里用 systemBars 的内边距做补偿：
 *  - [applyTopInsetToBar]    ：固定高度的标题栏，paddingTop 与高度一起增加，深色背景仍铺满状态栏；
 *  - [applyTopPaddingToHeader]：高度随内容变化的标题栏，只增加 paddingTop；
 *  - [applyBottomInset]      ：底部可滚动容器补 paddingBottom，避免最后一行被导航条压住。
 *
 * 首次调用时记录原始内边距 / 高度，后续每次都用“原始值 + 当前 inset”计算，
 * 避免重复回调导致数值累加。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
object Insets {

    /** 给固定高度的标题栏补状态栏高度 */
    fun applyTopInsetToBar(bar: View) {
        val baseHeight = bar.layoutParams?.height ?: 0
        val basePaddingTop = bar.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(bar) { view, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            view.setPadding(view.paddingLeft, basePaddingTop + top, view.paddingRight, view.paddingBottom)
            val params = view.layoutParams
            if (params != null && baseHeight > 0 && params.height != baseHeight + top) {
                params.height = baseHeight + top
                view.layoutParams = params
            }
            insets
        }
        ViewCompat.requestApplyInsets(bar)
    }

    /** 给高度随内容变化的标题栏补状态栏高度 */
    fun applyTopPaddingToHeader(header: View) {
        val basePaddingTop = header.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(header) { view, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            view.setPadding(view.paddingLeft, basePaddingTop + top, view.paddingRight, view.paddingBottom)
            insets
        }
        ViewCompat.requestApplyInsets(header)
    }

    /** 给底部容器补导航栏高度 */
    fun applyBottomInset(view: View) {
        val basePaddingBottom = view.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, basePaddingBottom + bottom)
            insets
        }
        ViewCompat.requestApplyInsets(view)
    }

    /** 把 ?attr/actionBarSize 转成像素 */
    fun actionBarSize(context: Context): Int {
        val value = TypedValue()
        val resolved = context.theme.resolveAttribute(
            androidx.appcompat.R.attr.actionBarSize, value, true
        )
        return if (resolved) {
            TypedValue.complexToDimensionPixelSize(value.data, context.resources.displayMetrics)
        } else {
            (56 * context.resources.displayMetrics.density).toInt()
        }
    }
}
