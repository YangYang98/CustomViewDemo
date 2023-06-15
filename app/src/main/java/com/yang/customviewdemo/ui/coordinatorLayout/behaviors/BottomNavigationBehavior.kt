package com.yang.customviewdemo.ui.coordinatorLayout.behaviors

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView


/**
 * Create by Yang Yang on 2023/6/15
 */
class BottomNavigationBehavior(val context: Context, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<View>(context, attrs) {

    // 原始bottomTop位置
    private var originChildTop = 0

    // 记录 原始 3 / 2 的高度
    private var originChildHeight = 0

    companion object {
        private const val TAG = "BNBehavior"
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency is RecyclerView
    }

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

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {
        parent.onLayoutChild(child, layoutDirection)
        originChildTop = child.top
        originChildHeight = child.height * 3 / 2

        return true
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        // 最大滑动距离
        val maxScrollY = originChildTop + child.height + child.marginBottom + child.marginTop

        // 最小滑动距离
        val minScrollY = originChildTop

        Log.i(
            TAG,
            "maxScrollY${maxScrollY}\tminScrollY:$minScrollY\tdy:$dy"
        )
        // 当前滑动距离
        val currentScrollY = child.top
//
        val up = dy > 0 && currentScrollY < maxScrollY
        val down = dy < 0 && currentScrollY > minScrollY - originChildHeight
        if (up || down) {
            // 偏移 bottomNavigationView
            ViewCompat.offsetTopAndBottom(child, dy)
        }

        // 避免快速滑动冲突
        if (currentScrollY > maxScrollY) {
            child.y = maxScrollY.toFloat()
        }

        if (currentScrollY < minScrollY) {
            child.y = minScrollY.toFloat()
        }
    }

}