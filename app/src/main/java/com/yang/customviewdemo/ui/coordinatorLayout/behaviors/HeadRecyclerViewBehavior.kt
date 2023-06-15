package com.yang.customviewdemo.ui.coordinatorLayout.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView


/**
 * Create by Yang Yang on 2023/6/15
 */
class HeadRecyclerViewBehavior(val context: Context, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<TextView>(context, attrs) {

    companion object {
        private const val TAG = "HeadRVBehavior"
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: TextView,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return target is RecyclerView
    }

    // 最后一次滑动距离
    private var lastScrollY = 0
    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: TextView,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        val maxScroll = -child.height

        val minScroll = 0

        var currentOffset = lastScrollY - dy

        currentOffset = when {
            currentOffset < maxScroll -> {
                maxScroll
            }
            currentOffset > minScroll -> {
                minScroll
            }
            else -> currentOffset
        }

        ViewCompat.offsetTopAndBottom(child, currentOffset - child.top)

        val consumedY: Int = lastScrollY - currentOffset
        lastScrollY = currentOffset

        consumed[1] = consumedY
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: TextView,
        layoutDirection: Int
    ): Boolean {
        parent.onLayoutChild(child, layoutDirection)
        return super.onLayoutChild(parent, child, layoutDirection)
    }
}