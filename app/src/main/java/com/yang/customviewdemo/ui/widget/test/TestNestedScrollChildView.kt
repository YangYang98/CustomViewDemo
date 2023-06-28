package com.yang.customviewdemo.ui.widget.test

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.NestedScrollingChild
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.ViewCompat


/**
 * Create by Yang Yang on 2023/6/27
 */
class TestNestedScrollChildView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), NestedScrollingChild {

    private val helper by lazy { NestedScrollingChildHelper(this) }

    private var lastY = 0f
    private val consume = IntArray(2)
    private val offset = IntArray(2)

    init {
        helper.isNestedScrollingEnabled = true
    }

    /**
     * 主要就是方向不同逻辑不同，
     * 向上的时候先分发给Parent，如果Parent不消耗了（返回false，也就是说到达顶部了），那么自己消耗dy（向上滑动，注意越界情况）；
     * 向下的时候，首先自己向下滑动（自己消耗dy），然后给Parent分发消耗后的dy。
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = event.y
                helper.startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
            }
            MotionEvent.ACTION_MOVE -> {
                var dy = event.y - lastY

                if (dy < 0)  {
                    //向上滑 保证parent消耗，才到自己消费
                    if (!helper.dispatchNestedPreScroll(0, dy.toInt(), consume, offset)) {
                        //space其实是本view顶部距离父View顶部的距离，也就是可滑动距离
                        val space = -y
                        val consumeY = space.coerceAtLeast(dy)
                        y += consumeY
                    }
                } else {
                    // 向下滑动的逻辑，保证自己消耗，才到parent

                    //space其实是本view底部距离父View底部的距离，也就是可滑动距离
                    val space = (parent as ViewGroup).height - y - height
                    val consumeY = space.coerceAtMost(dy)
                    dy -= consumeY
                    y += consumeY
                    helper.dispatchNestedPreScroll(0, dy.toInt(), consume, offset)
                }
             }
        }

        return true
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        helper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return helper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return helper.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        helper.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        return helper.hasNestedScrollingParent()
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?
    ): Boolean {
        return helper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?
    ): Boolean {
        return helper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return helper.dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun dispatchNestedFling(
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return helper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

}