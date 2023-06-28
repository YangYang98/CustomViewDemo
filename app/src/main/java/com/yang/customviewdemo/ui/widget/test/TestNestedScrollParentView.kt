package com.yang.customviewdemo.ui.widget.test

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.NestedScrollingParentHelper


/**
 * Create by Yang Yang on 2023/6/27
 */
class TestNestedScrollParentView@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val helper by lazy { NestedScrollingParentHelper(this) }

    override fun getNestedScrollAxes(): Int {
        return helper.nestedScrollAxes
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        helper.onNestedScrollAccepted(child, target, axes)
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return true
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {

        if (dy < 0) {
            //向上滑
            //space其实是本view顶部距离父View顶部的距离，也就是可滑动距离
            val space = -y
            val consumeY = space.coerceAtLeast(dy.toFloat())
            y += consumeY
            consumed[1] = consumeY.toInt()
        } else {
            //向下滑
            //space其实是本view底部距离父View底部的距离，也就是可滑动距离
            val space = (parent as ViewGroup).height - y - height
            val consumeY = space.coerceAtMost(dy.toFloat())
            y += consumeY
            consumed[1] = consumeY.toInt()
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {

    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return true
    }

    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return true
    }

    override fun onStopNestedScroll(child: View) {
        helper.onStopNestedScroll(child)
    }
}