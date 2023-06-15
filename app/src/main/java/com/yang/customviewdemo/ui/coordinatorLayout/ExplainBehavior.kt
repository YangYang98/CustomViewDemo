package com.yang.customviewdemo.ui.coordinatorLayout

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout


/**
 * Create by Yang Yang on 2023/6/15
 */
class ExplainBehavior(val context: Context, attrs: AttributeSet?) : CoordinatorLayout.Behavior<AppCompatImageView>(context, attrs) {

    /**
     * TODO 当解析layout完成时候调用 View#onAttachedToWindow() 然后紧接着调用该方法
     */
    override fun onAttachedToLayoutParams(params: CoordinatorLayout.LayoutParams) {
        super.onAttachedToLayoutParams(params)
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
        return super.onDependentViewChanged(parent, child, dependency)
    }

    /*
     * TODO 当被依赖的view移除view的时候调用
     */
    override fun onDependentViewRemoved(
        parent: CoordinatorLayout,
        child: AppCompatImageView,
        dependency: View
    ) {
        super.onDependentViewRemoved(parent, child, dependency)
    }

    /**
     * TODO: 当 CoordinatorLayout#onInterceptTouchEvent() 事件的时候调用
     */
    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: AppCompatImageView,
        ev: MotionEvent
    ): Boolean {
        return super.onInterceptTouchEvent(parent, child, ev)
    }

    /**
     * TODO 调用 CoordinatorLayout#onMeasureChild() 的时候调用
     */
    override fun onMeasureChild(
        parent: CoordinatorLayout,
        child: AppCompatImageView,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int
    ): Boolean {
        return super.onMeasureChild(
            parent,
            child,
            parentWidthMeasureSpec,
            widthUsed,
            parentHeightMeasureSpec,
            heightUsed
        )
    }

    /**
     * TODO 调用CoordinatorLayout$onLayout() 的时候调用
     */
    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: AppCompatImageView,
        layoutDirection: Int
    ): Boolean {
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    /**
     * TODO  设置背景色 CoordinatorLayout.drawChild中使用
     *
     * 需要配合 getScrimOpacity() 使用 因为 getScrimOpacity() 默认 = 0f
     */
    override fun getScrimColor(parent: CoordinatorLayout, child: AppCompatImageView): Int {
        return super.getScrimColor(parent, child)
    }

    /**
     * TODO 设置不透明度 CoordinatorLayout.drawChild中使用
     */
    override fun getScrimOpacity(parent: CoordinatorLayout, child: AppCompatImageView): Float {
        return super.getScrimOpacity(parent, child)
    }
}