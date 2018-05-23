package net.xwdoor.ethos

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import net.xwdoor.basemodule.extension.hideSoftInput
import net.xwdoor.basemodule.extension.showSoftInput
import net.xwdoor.basemodule.extension.withAlpha
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.alignParentBottom
import org.jetbrains.anko.alignParentLeft
import org.jetbrains.anko.alignParentRight
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.button
import org.jetbrains.anko.editText
import org.jetbrains.anko.info
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.sdk25.listeners.onClick
import org.jetbrains.anko.textColor
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
                backgroundColor = Color.DKGRAY.withAlpha(0.6f)
                padding = 40
                relativeLayout {
                    textView("取消").buttonCancelStyle().lparams(wrapContent, wrapContent) {
                        alignParentLeft()
                    }.onClick { closeEditMask() }

                    textView("完成").buttonOkStyle().lparams {
                        alignParentRight()
                    }.onClick { closeEditMask() }
                }

                editView = editText {
                    gravity = Gravity.TOP
                    backgroundColor = Color.TRANSPARENT
                    // 刚进入界面时不获取焦点
                    isFocusable = false
                    textSize = 20f
                }.lparams(matchParent, 0, 1f) {
                    topMargin = 20
                }

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

    fun TextView.buttonCancelStyle(): TextView {
        textColor = Color.WHITE
        return textStyle()
    }

    fun TextView.buttonOkStyle(): TextView {
        textColor = Color.GREEN
        return textStyle()
    }

    fun TextView.textStyle(): TextView {
        textSize = 17f
        gravity = Gravity.CENTER
        return this
    }
}
