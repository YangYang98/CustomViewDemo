package com.yang.customviewdemo.ui.coordinatorLayout.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.yang.customviewdemo.ui.widget.DodoMoveView


/**
 * Create by Yang Yang on 2023/6/29
 */
class DodoViewButtonBehavior1(val context: Context, attrs: AttributeSet?) : CoordinatorLayout.Behavior<Button>() {

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: Button,
        dependency: View
    ): Boolean {
        return dependency is DodoMoveView
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: Button,
        dependency: View
    ): Boolean {

        setPosition(child, 0f, dependency.y)
        return true
    }

    private fun setPosition(v: View, x: Float, y: Float) {
        val lp = v.layoutParams as CoordinatorLayout.LayoutParams

        lp.apply {
            leftMargin = x.toInt()
            topMargin = y.toInt()
        }
        v.layoutParams = lp

    }
}

/**
 * 可以跟着DodoView 旋转， 对着
 */
class DodoViewButtonBehavior2(val context: Context, attrs: AttributeSet?) : CoordinatorLayout.Behavior<Button>() {

    var width = 0
    var height = 0
    init {
        val display = context.resources.displayMetrics
        width = display.widthPixels
        height = display.heightPixels
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: Button,
        dependency: View
    ): Boolean {
        return dependency is DodoMoveView
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: Button,
        dependency: View
    ): Boolean {
        val top = dependency.top
        val left = dependency.left

        val x = width - left - child.width
        val y = height - top - child.height
        setPosition(child, x, y)

        return true
    }

    private fun setPosition(v: View, x: Int, y: Int) {
        val lp = v.layoutParams as CoordinatorLayout.LayoutParams

        lp.apply {
            leftMargin = x
            topMargin = y
            width = y / 2
        }
        v.layoutParams = lp

    }
}