package net.xwdoor.basemodule.extension

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import org.jetbrains.anko.inputMethodManager

/**
 * 开关软键盘
 */
fun Context.toggleSoftInput() {
    // 开关软键盘
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
            InputMethodManager.HIDE_NOT_ALWAYS)
}

/**
 * 显示软键盘
 *
 * @param view 能够获取焦点的且已经获取焦点的当前布局中的 view
 */
fun Context.showSoftInput(view: View) {
    inputMethodManager.showSoftInput(view, 0)
}

/**
 * 隐藏软键盘
 *
 * @param view 当前布局中的任意 view
 */
fun Context.hideSoftInput(view: View) {
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}