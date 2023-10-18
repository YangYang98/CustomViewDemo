package com.yang.customviewdemo.ui.widget.timeDisk

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sqrt


/**
 * Create by Yang Yang on 2023/10/17
 */
open class DiskView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    var mRadius = 0f
    var startX = 0f
    var startY = 0f
    var curX = 0f
    var curY = 0f

    /**
     * 第一次手指按下的点与初始位置形成的夹角
     */
    var startDegree = 0

    /**
     * 手指按下的点与初始位置形成的夹角
     */
    var curDegree = 0

    /**
     * 圆盘当前位置相对初始位置的角度，初始位置角度为0度
     */
    var degree = 0
        set(value) {
            field = if (abs(value) > 360) {
                value % 360
            } else {
                value
            }
        }

    /**
     * 手指抬起后是否需要回归原来的状态
     */
    var isNeedReturn = true

    protected val animator: ValueAnimator by lazy {
        ValueAnimator.ofInt(0, 1)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.rotate((degree + curDegree - startDegree).toFloat(), mRadius, mRadius)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        curX = event.x
        curY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y

                startDegree = computeCurrentAngle(curX, curY)
                //起始落点不能超过圆盘界限
                if (sqrt(
                    (startX - mRadius) * (startX - mRadius) + (startY - mRadius) * (startY - mRadius)
                ) > mRadius) {
                    startDegree = 0
                }
            }

            MotionEvent.ACTION_MOVE -> {
                //起始落点不能超过圆盘界限
                if (sqrt(
                        (startX - mRadius) * (startX - mRadius) + (startY - mRadius) * (startY - mRadius)
                    ) > mRadius) {
                    return false
                }
                curDegree = computeCurrentAngle(curX, curY)
                postInvalidate()
            }

            MotionEvent.ACTION_UP -> {
                if (sqrt(
                        (startX - mRadius) * (startX - mRadius) + (startY - mRadius) * (startY - mRadius)
                    ) > mRadius) {
                    return false
                }
                val tempDegree = degree
                degree = degree + curDegree - startDegree
                startDegree = 0
                curDegree = 0
                startX = 0f
                startY = 0f

                if (isNeedReturn) {
                    animator.apply {
                        setIntValues(degree, tempDegree)
                        addUpdateListener {
                            degree = this.animatedValue as Int
                            postInvalidate()
                        }
                        duration = 200
                        interpolator = DecelerateInterpolator()
                        start()
                    }
                }
            }
        }

        return true
    }

    private fun computeCurrentAngle(x: Float, y: Float): Int {
        val distance: Float = sqrt(
            (x - mRadius) * (x - mRadius) + (y - mRadius) * (y - mRadius)
        )
        var degree: Int = (acos((x - mRadius) / distance) * 180 / Math.PI).toInt()
        if (y < mRadius) {
            degree = -degree
        }

        return degree
    }
}