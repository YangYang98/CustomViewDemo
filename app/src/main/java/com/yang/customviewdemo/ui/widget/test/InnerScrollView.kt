package com.yang.customviewdemo.ui.widget.test

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ScrollView


/**
 * Create by Yang Yang on 2023/6/26
 */
class InnerScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    private var isFirstIntercept: Boolean = true

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.e("YANGYANG", "InnerScrollView,  dispatchTouchEvent:${MotionEvent.actionToString(ev.action)}")
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Log.e("YANGYANG", "InnerScrollView,  onInterceptTouchEvent:${MotionEvent.actionToString(ev.action)}")
        /*if (ev.action == MotionEvent.ACTION_DOWN) {
            isFirstIntercept = true
        }

        val result = super.onInterceptTouchEvent(ev)

        if (result && isFirstIntercept) {
            isFirstIntercept = false
            return false
        }

        return result*/
        return super.onInterceptTouchEvent(ev)
    }
}