package net.xwdoor.ethos

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.alignParentBottom
import org.jetbrains.anko.alignParentLeft
import org.jetbrains.anko.alignParentRight
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.button
import org.jetbrains.anko.editText
import org.jetbrains.anko.info
import org.jetbrains.anko.inputMethodManager
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.sdk25.listeners.onClick
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

class MainActivity : Activity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        info("ttt, 测试 log 数据")
    }

    private lateinit var editView: EditText
    private lateinit var editMask: LinearLayout
    private lateinit var textView: TextView
    private lateinit var editToolBar: LinearLayout

    private fun init() {
        relativeLayout {
            editToolBar = linearLayout {
                button("编辑").onClick {
                    openEditMask()
                }
            }.lparams(matchParent, wrapContent) {
                alignParentBottom()
            }

            // todo: 之后采用 constraintLayout
            editMask = verticalLayout {
                relativeLayout {
                    textView("取消").lparams(wrapContent, wrapContent) {
                        alignParentLeft()
                    }.onClick { closeEditMask() }

                    textView("完成").lparams {
                        alignParentRight()
                    }.onClick { closeEditMask() }
                }

                editView = editText {
                    padding = 15
                    gravity = Gravity.TOP
                    backgroundColor = Color.GREEN
                    // 刚进入界面时不获取焦点
                    isFocusable = false
                }.lparams(matchParent, 0, 1f)

                // scrollView 是为 view 漂浮在软键盘上做准备
                scrollView {
                    linearLayout {
                        textView("tool bar")
                    }
                }

            }.lparams(matchParent, 0) {
                alignParentBottom()
            }
        }
    }

    private fun openEditMask() {
        openAnimator.start()
        editToolBar.visibility = View.GONE

        editView.isFocusable = true
        editView.isFocusableInTouchMode = true
        editView.requestFocus()
        showSoftInput(editView)
    }

    private fun closeEditMask() {
        closeAnimator.apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    editToolBar.visibility = View.VISIBLE
                }
            })
        }.start()
        hideSoftInput(editView)
    }

    private val openAnimator by lazy {
        ValueAnimator
                .ofInt(0, window.decorView.height)
                .setDuration(100)
                .apply {
                    addUpdateListener {
                        val h = it.animatedValue as Int
                        editMask.layoutParams = editMask.layoutParams.apply {
                            height = h
                        }
                    }
                }
    }

    private val closeAnimator by lazy {
        ValueAnimator
                .ofInt(window.decorView.height, 0)
                .setDuration(100)
                .apply {
                    addUpdateListener {
                        val h = it.animatedValue as Int
                        editMask.layoutParams = editMask.layoutParams.apply {
                            height = h
                        }
                    }
                }
    }

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
}
