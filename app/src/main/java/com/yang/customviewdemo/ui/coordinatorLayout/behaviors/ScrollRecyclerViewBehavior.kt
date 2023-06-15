package com.yang.customviewdemo.ui.coordinatorLayout.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2


/**
 * Create by Yang Yang on 2023/6/15
 */
class ScrollRecyclerViewBehavior(val context: Context, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<ViewPager2>(context, attrs) {

    companion object {
        private const val TAG = "ScrollRVBehavior"
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: ViewPager2,
        layoutDirection: Int
    ): Boolean {
        parent.onLayoutChild(child, layoutDirection)
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: ViewPager2,
        dependency: View
    ): Boolean {
        return dependency is AppCompatTextView
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: ViewPager2,
        dependency: View
    ): Boolean {

        ViewCompat.offsetTopAndBottom(child, -(child.top - dependency.bottom))
        return false
    }
}