package com.yang.customviewdemo.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout
import com.yang.customviewdemo.R
import com.yang.customviewdemo.utils.ViewOffsetHelper


/**
 * Create by Yang Yang on 2023/6/16
 */
class FloatLinearLayout2 @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(ctx, attrs, defStyle) {
    private var mOnOffsetChangedListener: AppBarLayout.OnOffsetChangedListener? = null

    init {
        isChildrenDrawingOrderEnabled = true
    }

    // 当前吸顶悬浮的View总高度
    private var mTopFloatViewTotalHeight = 0
    // 每个吸顶View的
    private var mTopFloatViewMargins : SparseArray<Int>? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mTopFloatViewTotalHeight = 0
        var minHeightSum = 0
        mTopFloatViewMargins = SparseArray(childCount)
        (0 until childCount).forEach {
            mTopFloatViewTotalHeight += getChildAt(it).measuredHeight
            mTopFloatViewMargins?.put(it, mTopFloatViewTotalHeight)
            if (((getChildAt(it).layoutParams) as? LayoutParams)?.pin == true) {
                minHeightSum += getChildAt(it).measuredHeight
            }
        }
        minimumHeight = minHeightSum
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Add OnOffsetChangedListener
        (parent as? AppBarLayout)?.let {
            ViewCompat.setFitsSystemWindows(this, ViewCompat.getFitsSystemWindows(it))
            if (mOnOffsetChangedListener == null) {
                mOnOffsetChangedListener = this.OffsetUpdateListener()
            }
            it.addOnOffsetChangedListener(mOnOffsetChangedListener)

            ViewCompat.requestApplyInsets(this)
        }
    }

    override fun onDetachedFromWindow() {
        if (mOnOffsetChangedListener != null && parent is AppBarLayout) {
            (parent as AppBarLayout).removeOnOffsetChangedListener(mOnOffsetChangedListener)
        }
        super.onDetachedFromWindow()
    }


    private fun getViewOffsetHelper(view: View): ViewOffsetHelper {
        var offsetHelper = view.getTag(com.google.android.material.R.id.view_offset_helper) as? ViewOffsetHelper
        if (offsetHelper == null) {
            offsetHelper = ViewOffsetHelper(view)
            view.setTag(com.google.android.material.R.id.view_offset_helper, offsetHelper)
        }
        return offsetHelper
    }

    override fun getChildDrawingOrder(childCount: Int, drawingPosition: Int) = childCount - drawingPosition - 1

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?) = p is LayoutParams

    override fun generateDefaultLayoutParams() = LayoutParams(MATCH_PARENT, MATCH_PARENT)

    override fun generateLayoutParams(attrs: AttributeSet?) = LayoutParams(context, attrs)

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?) = LayoutParams(lp)

    /*
     * 增加pin属性
     * 等价于Java的static class
     * 注意不能用inner（会持有外部对象引用）
     */

    class LayoutParams : LinearLayout.LayoutParams {
        var pin = false

        constructor(c: Context?, attrs: AttributeSet?) : super(c, attrs) {
            val ta = c?.obtainStyledAttributes(attrs, R.styleable.FloatLinearLayout)
            pin = ta?.getBoolean(R.styleable.FloatLinearLayout_layout_pin, false) ?: false
            ta?.recycle()
        }

        constructor(width: Int, height: Int) : super(width, height)
        constructor(width: Int, height: Int, weight: Float) : super(width, height, weight)
        constructor(p: ViewGroup.LayoutParams?) : super(p)
        constructor(p: LinearLayout.LayoutParams?) : super(p)
        constructor(p: MarginLayoutParams?) : super(p)
    }

    private inner class OffsetUpdateListener : AppBarLayout.OnOffsetChangedListener {
        override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
            (0 until childCount).forEach { i ->
                val child = getChildAt(i)
                val lp = child.layoutParams as? LayoutParams
                if (lp?.pin == true) {
                    val floatStartHeight = (mTopFloatViewMargins?.get(i) ?: 0) - child.measuredHeight
                    if (floatStartHeight < 0) {
                        //Log.e(TAG,"Impossible!!! floatStartHeight=$floatStartHeight, i=$i")
                        return
                    }
                    // 如果到达了顶部，则累加高度，作为新顶部
                    val offset = -verticalOffset - top
                    /**
                     * TODO floatStartHeight.coerceAtLeast(offset)的作用
                     * 当滑动的时候没轮到需要pin的那个View移动的时候，他的偏移量一直都是距离他的前面的View的距离，
                     * 当滑动超过了需要pin的View的顶部的时候，就需要把垂直滑动的距离当成偏移量了，这样子才能把pin的View固定在顶部
                     */

                    getViewOffsetHelper(child).topAndBottomOffset = floatStartHeight.coerceAtLeast(offset)
                }
            }
        }
    }
}
