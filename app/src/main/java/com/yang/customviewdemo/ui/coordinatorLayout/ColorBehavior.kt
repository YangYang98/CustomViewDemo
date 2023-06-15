package com.yang.customviewdemo.ui.coordinatorLayout

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import kotlin.random.Random


/**
 * Create by Yang Yang on 2023/6/15
 */
class ColorBehavior(val context: Context, attrs: AttributeSet?) : CoordinatorLayout.Behavior<AppCompatImageView>(context, attrs) {

    companion object {
        const val TAG = "ColorBehavior"
    }

    /*
     * TODO 判断跟随变化的 View
     * @param parent: CoordinatorLayout
     * @param child: 当前的 view
     * @param dependency: 需要依赖的 View
     */
    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: AppCompatImageView,
        dependency: View
    ): Boolean {
        return dependency is MoveView
    }

    // TODO 当依赖的view发生变化的时候调用
    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: AppCompatImageView,
        dependency: View
    ): Boolean {
        child.setBackgroundColor(context.randomColor())
        return super.onDependentViewChanged(parent, child, dependency)
    }

    override fun getScrimColor(parent: CoordinatorLayout, child: AppCompatImageView): Int {
        return Color.RED
    }

    override fun getScrimOpacity(parent: CoordinatorLayout, child: AppCompatImageView): Float {
        return 0.5f
    }
}

fun Context.randomColor(): Int {

    val colors = arrayOf(Color.BLACK, Color.BLUE, Color.RED, Color.YELLOW, Color.GRAY, Color.GREEN, Color.MAGENTA)
    return colors[Random.nextInt(101) % colors.size]
}