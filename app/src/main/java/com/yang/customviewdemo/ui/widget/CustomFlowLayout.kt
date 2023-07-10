package com.yang.customviewdemo.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.yang.customviewdemo.R
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

    var lineVerticalGravity: Int = LINE_VERTICAL_GRAVITY_CENTER_VERTICAL
        set(value) {
            field = value
            requestLayout()
        }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout)
        lineVerticalGravity = ta.getInt(R.styleable.FlowLayout_flowlayout_line_vertical_gravity, LINE_VERTICAL_GRAVITY_CENTER_VERTICAL)
        ta.recycle()
    }

    companion object {
        private const val TAG = "FlowLayout"
        const val LINE_VERTICAL_GRAVITY_TOP = 0
        const val LINE_VERTICAL_GRAVITY_CENTER_VERTICAL = 1
        const val LINE_VERTICAL_GRAVITY_BOTTOM = 2
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        allLineItems.clear()
        lineHeights.clear()
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val maxWidth = widthSize - paddingStart - paddingEnd
        var lineWidth = 0 // 行宽
        var maxLineWidth = 0 // 最大行宽
        var lineHeight = 0 // 行高
        var totalHeight = 0 // 总高度
        var lineCount = 0

        var lineViews = ArrayList<View>()

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            if (child.visibility != View.GONE) {
                val lp = child.layoutParams as MarginLayoutParams
                val childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, paddingStart + paddingEnd, lp.width)
                val childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom, lp.height)
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec)

                val childMeasureWidth = child.measuredWidth
                val childMeasureHeight = child.measuredHeight

                val realChildWidth = childMeasureWidth + lp.marginStart + lp.marginEnd
                val realChildHeight = childMeasureHeight + lp.topMargin + lp.bottomMargin
                val realItemHorizontalSpacing = if (lineWidth == 0) 0 else itemHorizontalSpacing

                if (lineWidth + realItemHorizontalSpacing + realChildWidth <= maxWidth) {
                    lineWidth += realItemHorizontalSpacing + realChildWidth
                    lineHeight = max(lineHeight, realChildHeight)
                    lineViews.add(child)
                } else {
                    maxLineWidth = max(lineWidth, maxLineWidth)
                    lineCount++
                    totalHeight += lineHeight + if (lineCount == 1) 0 else itemVerticalSpacing
                    lineHeights.add(lineHeight)
                    allLineItems.add(lineViews)

                    //初始化下一行
                    lineWidth = realChildWidth
                    lineHeight = realChildHeight
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

        maxLineWidth += paddingStart + paddingEnd
        totalHeight += paddingTop + paddingBottom

        val measureWidth = if (widthMode == MeasureSpec.EXACTLY) widthSize else maxLineWidth
        val measureHeight = if (heightMode == MeasureSpec.EXACTLY) heightSize else totalHeight
        setMeasuredDimension(measureWidth, measureHeight)

    }

    private fun getChildOffsetTop(lineHeight: Int, child: View): Int {
        val lp = child.layoutParams as MarginLayoutParams
        val childMeasuredHeight = child.measuredHeight
        val childMeasuredHeightWithMargin = childMeasuredHeight + lp.topMargin + lp.bottomMargin
        return when(lineVerticalGravity) {
            LINE_VERTICAL_GRAVITY_TOP -> 0
            LINE_VERTICAL_GRAVITY_CENTER_VERTICAL -> (lineHeight - childMeasuredHeightWithMargin) / 2
            LINE_VERTICAL_GRAVITY_BOTTOM -> lineHeight - childMeasuredHeightWithMargin
            else -> {
                throw IllegalArgumentException("unknown lineVerticalGravity value: $lineVerticalGravity")
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        var childLeft = paddingStart
        var childTop = paddingTop

        for (i in 0 until allLineItems.size) {
            val lineView = allLineItems[i]
            val lineHeight = lineHeights[i]

            for (j in 0 until lineView.size) {

                val child = lineView[j]
                val lp = child.layoutParams as MarginLayoutParams
                val measuredWidth = child.measuredWidth
                val measuredHeight = child.measuredHeight

                val verticalOffset = getChildOffsetTop(lineHeight, child)

                child.layout(childLeft + lp.marginStart, childTop + lp.topMargin + verticalOffset, childLeft  + lp.marginStart + measuredWidth, childTop + lp.topMargin + verticalOffset + measuredHeight)

                childLeft += measuredWidth + itemHorizontalSpacing + lp.marginStart
            }

            childTop += lineHeight + itemVerticalSpacing
            childLeft = paddingStart
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    // 当通过 addView(View) 方法添加子元素，并且子元素没有设置布局参数时，会调用此方法来生成默认的布局参数
    // 这里重写返回 MarginLayoutParams 对象，是为了在获取子元素的 LayoutParams 对象时，可以正常强转为 MarginLayoutParams
    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    // 检查传入的布局参数是否符合某个条件
    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        return p is MarginLayoutParams
    }
    // addViewInner 中调用，但是布局参数类型无法通过 checkLayoutParams() 判断时，会走这个方法。
    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }
}