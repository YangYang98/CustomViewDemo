package com.yang.customviewdemo.ui.coordinatorLayout.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.yang.customviewdemo.R


/**
 * Create by Yang Yang on 2023/6/29
 */
class FollowBehavior(val context: Context, attrs: AttributeSet?) : CoordinatorLayout.Behavior<View>() {

    private var targetId= -1
    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.Follow_Behavior)
        for (i in 0..a.indexCount) {
            val attr = a.getIndex(i)
            if (attr == R.styleable.Follow_Behavior_target) {
                targetId = a.getResourceId(attr, -1)
            }
        }

        a.recycle()
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency.id == targetId
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        child.apply {
            y = dependency.y + dependency.height
            x = dependency.x
        }
        return super.onDependentViewChanged(parent, child, dependency)
    }
}