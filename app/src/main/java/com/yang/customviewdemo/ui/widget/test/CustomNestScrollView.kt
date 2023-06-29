package com.yang.customviewdemo.ui.widget.test

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.LinearLayout
import kotlin.math.abs


/**
 * Create by Yang Yang on 2023/6/29
 */
class CustomNestScrollView@JvmOverloads constructor(
    context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var mTouchSlop = 0
    private var mLastY = 0f
    private var mTopViewHeight = 0

    init {
        //识别滑动手势为翻页式滑动所需的最小移动距离
        //TODO 为什么拿到的scaledPagingTouchSlop太大了
        mTouchSlop = ViewConfiguration.get(context).scaledPagingTouchSlop / 6
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {

        /**
         * 上滑：当头部还没有消失的时候拦截事件
         * 下滑：当头部还没有完全显示的时候拦截事件
         */

        var intercepted = false
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastY = ev.rawY
            }
            MotionEvent.ACTION_MOVE -> {

                if (abs(mLastY - ev.rawY) > mTouchSlop) {
                    if (hasViewPartHidden(mLastY - ev.rawY) || hasViewPartShown(mLastY - ev.rawY)) {
                        intercepted = true
                    }
                }
                mLastY = ev.rawY
            }
        }
        return intercepted
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                if (abs(event.y - mLastY) > mTouchSlop) {
                    scrollBy(0, (mLastY - event.y).toInt())
                }
                mLastY = event.y
            }
        }

        return super.onTouchEvent(event)
    }

    override fun scrollTo(x: Int, y: Int) {
        var finalY = if (y < 0) {
            0
        } else y

        if (y > mTopViewHeight) {
            finalY  = mTopViewHeight
        }

        super.scrollTo(x, finalY)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mTopViewHeight = getChildAt(0).measuredHeight
    }

    /**
     * @param dy 手指滑动的相对距离 dy >0 上滑 dy < 0 下滑
     */
    private fun hasViewPartHidden(dy: Float): Boolean {
        return dy > 0 && scrollY < mTopViewHeight
    }

    private fun hasViewPartShown(dy: Float): Boolean {
        return dy < 0 && scrollY > 0 && !canScrollVertically(-1)
    }
}