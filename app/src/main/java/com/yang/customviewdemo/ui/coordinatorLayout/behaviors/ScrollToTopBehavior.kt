package com.yang.customviewdemo.ui.coordinatorLayout.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout


/**
 * Create by Yang Yang on 2023/6/29
 */
class ScrollToTopBehavior(val context: Context, attrs: AttributeSet?) : CoordinatorLayout.Behavior<View>() {

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return true
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        offsetY(child, dyConsumed)
    }

    private var offsetTotal = 0
    private var scrolling = false
    private fun offsetY(child: View, dy: Int) {
        val old = offsetTotal
        //当前总的滑动距离，不要超过child的高
        //var top = offsetTotal - dy
        var top = offsetTotal - dy
        //控制上滑边界
        top = top.coerceAtLeast(-child.height)
        //控制下滑边界
        top = top.coerceAtMost(0)
        offsetTotal = top
        if (old == offsetTotal) {
            scrolling = false
            return
        }
        val delta = offsetTotal - old
        child.offsetTopAndBottom(delta)
        scrolling = true

    }
}