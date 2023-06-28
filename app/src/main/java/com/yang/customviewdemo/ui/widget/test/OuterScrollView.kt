package com.yang.customviewdemo.ui.widget.test

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ScrollView
import java.lang.reflect.Array.getInt
import kotlin.math.abs


/**
 * Create by Yang Yang on 2023/6/26
 */
class OuterScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    private var isFirstIntercept: Boolean = true

    private var isNeedRequestDisallowIntercept: Boolean? = null

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.e("YANGYANG", "OuterScrollView,  dispatchTouchEvent:${MotionEvent.actionToString(ev.action)}")
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Log.e("YANGYANG", "OuterScrollView,  onInterceptTouchEvent:${MotionEvent.actionToString(ev.action)}")
        /*if (ev.action == MotionEvent.ACTION_DOWN) {
            isFirstIntercept = true
        }

        val result = super.onInterceptTouchEvent(ev)

        if (result && isFirstIntercept) {
            isFirstIntercept = false
            return false
        }

        return result*/

        if (ev.actionMasked == MotionEvent.ACTION_DOWN) parent.requestDisallowInterceptTouchEvent(true)
        if (ev.actionMasked == MotionEvent.ACTION_MOVE) {
            val offsetY = ev.y.toInt() - getInt("mLastMotionY", 0)

            if (abs(offsetY) > getInt("mTouchSlop", 0)) {
                if ((offsetY > 0 && isScrollToTop()) || (offsetY < 0 && isScrollToBottom())) {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    private fun isScrollToTop() = scrollY == 0

    private fun isScrollToBottom(): Boolean {
        return scrollY + height - paddingTop - paddingBottom == getChildAt(0).height
    }
}