package com.yang.customviewdemo.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import kotlin.math.max


/**
 * Create by Yang Yang on 2023/7/4
 */
class CustomFlowLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ViewGroup(context, attrs, defStyleAttr) {

    private var itemHorizontalSpacing = 20

    private var itemVerticalSpacing = 20

    private val allLineItems = ArrayList<ArrayList<View>>()

    private val lineHeights = ArrayList<Int>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        allLineItems.clear()
        lineHeights.clear()
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var lineWidth = 0 // 行宽
        var maxLineWidth = 0 // 最大行宽
        var lineHeight = 0 // 行高
        var totalHeight = 0 // 总高度
        var lineCount = 0

        var lineViews = ArrayList<View>()

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            if (child.visibility != View.GONE) {
                val lp = child.layoutParams
                val childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, paddingStart + paddingEnd, lp.width)
                val childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom, lp.height)
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec)

                val childMeasureWidth = child.measuredWidth
                val childMeasureHeight = child.measuredHeight
                val itemHorizontalSpacing = if (lineWidth == 0) 0 else itemHorizontalSpacing

                if (lineWidth + itemHorizontalSpacing + childMeasureWidth <= widthSize) {
                    lineWidth += itemHorizontalSpacing + childMeasureWidth
                    lineHeight = max(lineHeight, childMeasureHeight)
                    lineViews.add(child)
                } else {
                    maxLineWidth = max(lineWidth, maxLineWidth)
                    lineCount++
                    totalHeight += lineHeight + if (lineCount == 1) 0 else itemVerticalSpacing
                    lineHeights.add(lineHeight)
                    allLineItems.add(lineViews)

                    //初始化下一行
                    lineWidth = childMeasureWidth
                    lineHeight = childMeasureHeight
                    lineViews = ArrayList()
                    lineViews.add(child)
                }

                if (i == childCount - 1) {
                    maxLineWidth = max(lineWidth, maxLineWidth)
                    lineCount++
                    totalHeight += lineHeight + if (lineCount == 1) 0 else itemVerticalSpacing
                    lineHeights.add(lineHeight)
                    allLineItems.add(lineViews)
                }
            }
        }

        val measureWidth = if (widthMode == MeasureSpec.EXACTLY) widthSize else maxLineWidth
        val measureHeight = if (heightMode == MeasureSpec.EXACTLY) heightSize else totalHeight
        setMeasuredDimension(measureWidth, measureHeight)

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        var childLeft = 0
        var childTop = 0

        for (i in 0 until allLineItems.size) {
            val lineView = allLineItems[i]
            val lineHeight = lineHeights[i]

            for (j in 0 until lineView.size) {

                val child = lineView[j]
                val measuredWidth = child.measuredWidth
                val measuredHeight = child.measuredHeight

                child.layout(childLeft, childTop, childLeft + measuredWidth, childTop + measuredHeight)

                childLeft += measuredWidth + itemHorizontalSpacing
            }

            childTop += lineHeight + itemVerticalSpacing
            childLeft = 0
        }
    }
}