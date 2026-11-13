package cn.edu.sicnu.YouRuiProject.util

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * API 36 强制 edge-to-edge，给根布局补状态栏高度的顶部内边距，避免标题被系统栏遮挡。
 */
object Insets {

    fun applyTopPadding(view: View) {
        val originalTop = view.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            v.setPadding(v.paddingLeft, originalTop + top, v.paddingRight, v.paddingBottom)
            insets
        }
    }
}
