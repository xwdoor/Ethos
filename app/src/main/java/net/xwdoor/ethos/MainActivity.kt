package net.xwdoor.ethos

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ScaleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import net.xwdoor.basemodule.extension.hideSoftInput
import net.xwdoor.basemodule.extension.showSoftInput
import net.xwdoor.basemodule.extension.withAlpha
import net.xwdoor.basemodule.view.MultiLineRadioGroup
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.alignParentBottom
import org.jetbrains.anko.alignParentLeft
import org.jetbrains.anko.alignParentRight
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.button
import org.jetbrains.anko.checkBox
import org.jetbrains.anko.custom.customView
import org.jetbrains.anko.editText
import org.jetbrains.anko.info
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.radioButton
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
                setPadding(40, 40, 40, 0)
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
                    customView<MultiLineRadioGroup> {
                        checkBox {
                            background = backgroundDrawable()
                            width = 70
                            height = 70
                            padding = 0
                            buttonDrawable = null
                            gravity = Gravity.CENTER
                            textSize = 18f
                            text = "T"
                            setTextColor(foregroundDrawable())
                            setOnCheckedChangeListener { button, checked ->
                                info("ttt, 选中啦：${button.text}")
                            }
                            // 如果用 lparams {}, 会使用 FrameLayout.LayoutParams 的布局类型，很奇怪
                            // 是 customView 的问题
                            // 所以要用布局参数的话，需要指明类型。但是下边的宽高设置无效
                            layoutParams = RadioGroup.LayoutParams(70, 70).apply {
                                leftMargin = 40
                            }
                        }
                        arrayOf(Color.WHITE, Color.BLACK, Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.parseColor("#800080")).forEach { color ->
                            radioButton().colorSelectStyle(color).also {
                                // 默认选择白色
                                if (color == Color.WHITE) {
                                    check(it.id)
                                    editView.textColor = color
                                }
                            }
                        }

                        lparams {
                            bottomMargin = 30
                        }
                    }.setOnCheckedChangeListener { group, checkedId ->
                        // 每次选择不同的颜色时，将文本设置为该颜色
                        editView.textColor = group.findViewById<RadioButton>(checkedId).tag as Int
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

    private fun RadioButton.colorSelectStyle(color: Int = Color.WHITE): RadioButton {
        // 将颜色值存储到 tag 属性中，方便读取
        tag = color
        buttonDrawable = null
        background = radioSelector(35, color, 8)
        // 由于我们修改 buttonDrawable、background 的值，导致宽高发生变化
        // 所以这里指定宽高，不写在布局参数 lparams 中，是由于 anko 的 bug:
        // RadioGroup 布局下，默认使用的是 LinearLayout.LayoutParams，而不是 RadioGroup.LayoutParams
        width = 70
        height = 70
        layoutParams = LinearLayout.LayoutParams(wrapContent, wrapContent).apply {
            leftMargin = 40
        }
        return this
    }

    private fun radioSelector(radius: Int, bgColor: Int, borderWidth: Int): StateListDrawable {
        return StateListDrawable().apply {
            // 如果选中（state_checked 属性为 true），就显示 gradientDrawable 效果
            addState(intArrayOf(android.R.attr.state_checked), gradientDrawable(radius, bgColor, borderWidth, Color.WHITE))
            // else，就显示默认效果
            addState(intArrayOf(),
                    ScaleDrawable(
                            gradientDrawable(radius, bgColor, borderWidth, Color.WHITE),
                            Gravity.CENTER, 0.18f, 0.18f
                    )
            )
        }
    }

    private fun gradientDrawable(radius: Int, bgColor: Int, width: Int, strokeColor: Int): GradientDrawable {
        return GradientDrawable().apply {
            // 设置半径
            cornerRadius = radius.toFloat()
            // 设置背景颜色
            setColor(bgColor)
            // 设置画笔宽度和画笔颜色
            setStroke(width, strokeColor)
            // 设置 level，缩放效果才有效
            level = 100
        }
    }

    private fun foregroundDrawable(): ColorStateList {
//        val states = Array(2) { IntArray(2) }
        val states = arrayOfNulls<IntArray>(2)
        states[0] = intArrayOf(android.R.attr.state_checked)
        states[1] = intArrayOf()
        return ColorStateList(states, intArrayOf(Color.BLACK, Color.WHITE))
    }

    private fun backgroundDrawable(): StateListDrawable {
        return StateListDrawable().apply {
            // 如果选中（state_checked 属性为 true），就显示 gradientDrawable 效果
            addState(intArrayOf(android.R.attr.state_checked),
                    gradientDrawable(8, Color.WHITE, 0, 0))
            // else，就显示默认效果
            addState(intArrayOf(),
                    gradientDrawable(8, Color.TRANSPARENT, 5, Color.WHITE)
            )
        }
    }
}
