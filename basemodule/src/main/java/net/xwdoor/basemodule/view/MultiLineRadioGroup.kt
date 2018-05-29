package net.xwdoor.basemodule.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup

/**
 * 支持多行多列排列的 RadioGroup
 */
class MultiLineRadioGroup : RadioGroup {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    /**
     * 重写测量方法，测量多行布局需要的高度和宽度
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 获取宽度模式和最大宽度（父控件给的期望宽度）
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val maxWidth = MeasureSpec.getSize(widthMeasureSpec)

        // 下边的逻辑主要用于测量子 View 需要排列成多少行，并测量 GroupView 需要的真实高度
        // 子 View 排列的行数
        var rowCount = 1
        // 真实高度，由行数计算确定
        var realHeight = 0
        // 当前行的占用宽度，如果子 View 的宽度超过最大宽度，则换行
        var rowWidth = 0
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            // 隐藏的子 View 不进行测量
            if (child.visibility == View.GONE) continue
            // 计算 child 的宽高
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            // 获取测量后的 child 的宽高，以及四边边距
            val width = child.measuredWidth
            val height = child.measuredHeight
            val leftMargin = (child.layoutParams as LinearLayout.LayoutParams).leftMargin
            val rightMargin = (child.layoutParams as LinearLayout.LayoutParams).rightMargin
            val topMargin = (child.layoutParams as LinearLayout.LayoutParams).topMargin
            val bottomMargin = (child.layoutParams as LinearLayout.LayoutParams).bottomMargin

            rowWidth += width + leftMargin + rightMargin
            // 这句话是必不可少的，如果只有一行的话，就靠这个赋值了
            realHeight = rowCount * (topMargin + height + bottomMargin)
            // 如果当先行的宽度超出父控件，则换行
            if (rowWidth > maxWidth) {
                // 第一个 child 不进行换行，即使它的宽度超出了父控件
                if (index != 0) rowCount++
                // 重新设置当前行的占用宽度
                rowWidth = width + leftMargin + rightMargin
                // 重新设置当前父控件高度
                // todo: 考虑每个 RadioButton 间距或高度不一样的情况
                realHeight = rowCount * (topMargin + height + bottomMargin)
            }
        }

        // 保存测量值
        setMeasuredDimension(maxWidth, realHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val maxWidth = right - left
        var rowCount = 1
        // 当前行宽
        var rowWidth = 0
        // 当前布局高度
        var layoutHeight: Int
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            if (child.visibility == View.GONE) continue

            // 获取测量后的 child 的宽高，以及四边边距
            val width = child.measuredWidth
            val height = child.measuredHeight
            val leftMargin = (child.layoutParams as LinearLayout.LayoutParams).leftMargin
            val rightMargin = (child.layoutParams as LinearLayout.LayoutParams).rightMargin
            val topMargin = (child.layoutParams as LinearLayout.LayoutParams).topMargin
            val bottomMargin = (child.layoutParams as LinearLayout.LayoutParams).bottomMargin

            rowWidth += width + leftMargin + rightMargin
            // 这句话是必不可少的，如果只有一行的话，就靠这个赋值了
            layoutHeight = rowCount * (height + topMargin + bottomMargin)
            // 如果当先行的宽度超出父控件，则换行
            if (rowWidth > maxWidth) {
                // 第一个 child 不进行换行，即使它的宽度超出了父控件
                if (index != 0) rowCount++
                // 重新设置当前行的占用宽度
                rowWidth = width + leftMargin + rightMargin
                layoutHeight = rowCount * (height + topMargin + bottomMargin)
            }

            // todo: 考虑每个 RadioButton 间距或高度不一样的情况
            child.layout(
                    rowWidth - width - rightMargin,
                    layoutHeight - height - bottomMargin,
                    Math.min(rowWidth, maxWidth),
                    layoutHeight
            )
        }
    }
}