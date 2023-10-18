package com.yang.customviewdemo.ui.widget.timeDisk

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import com.yang.customviewdemo.utils.sp2px
import kotlin.math.abs
import kotlin.math.sqrt


/**
 * Create by Yang Yang on 2023/10/17
 */
class MinuteDiskView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): DiskView(context, attrs, defStyleAttr) {

    constructor(context: Context, radius: Int): this(context) {
        this.mRadius = radius.toFloat()
    }

    private val mPaint = Paint()
    private val textBounds = Rect()

    //圆盘颜色
    var diskColor = -0xd9d296
    //数字颜色
    var numColor = -0x1
    //数字选中颜色
    var selectNumColor = -0xf0100

    var curMinute = 0
    var textHeight = 0

    var onMinuteChangedListener: OnMinuteChangedListener? = null

    init {
        if (mRadius == 0f) {
            mRadius = 600f
        }

        mPaint.apply {
            isAntiAlias = true
            color = diskColor
            textAlign = Paint.Align.LEFT
            textSize = 20f.sp2px
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mPaint.color = diskColor
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint)

        mPaint.color = numColor
        for (i in 0 until 60) {
            mPaint.color = if (i == curMinute) {
                selectNumColor
            } else {
                numColor
            }

            if (i % 10 != 0) {
                if (i % 5  == 0) {
                    canvas.drawCircle(mRadius, mRadius * 2 - textHeight * 3 / 2, 20f.sp2px / 4, mPaint)
                } else {
                    canvas.drawCircle(mRadius, mRadius * 2 - textHeight * 3 / 2, 20f.sp2px / 6, mPaint)
                }
            } else {
                mPaint.getTextBounds(i.toString(), 0, i.toString().length, textBounds)
                textHeight = textBounds.height()
                canvas.drawText(i.toString(), mRadius - textBounds.width() / 2, mRadius * 2 - textBounds.height(), mPaint)
            }
            canvas.rotate(-6f, mRadius, mRadius)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                if (!isNeedReturn) {
                    val tmpDegree = degree + curDegree - startDegree
                    val tempD = tmpDegree % 6
                    var offset = tmpDegree - tempD

                    if (offset < 0) {
                        offset += 360
                    }

                    curMinute = if (tempD == 0 || tempD < 3) {
                        offset / 6
                    } else {
                        offset / 6 + 1
                    }

                    onMinuteChangedListener?.onMinuteChanged(curMinute)
                }
            }

            MotionEvent.ACTION_UP -> {
                if (!isNeedReturn) {
                    //做定位矫正,定位到最近的数字

                    val tmp = degree % 6
                    var offset = degree - tmp
                    if (offset < 0) {
                        offset += 360
                    }

                    if (tmp == 0) {
                        curMinute = offset / 6
                    } else if (tmp < 3) {
                        curMinute = offset / 6
                        animator.apply {
                            setIntValues(degree, degree - tmp)
                            duration = 50
                            addUpdateListener {
                                degree = it.animatedValue as Int
                                postInvalidate()
                            }
                            start()
                        }
                    } else {
                        curMinute = offset / 6 + 1
                        animator.apply {
                            setIntValues(degree, degree - tmp + 6)
                            duration = 50
                            addUpdateListener {
                                degree = it.animatedValue as Int
                                postInvalidate()
                            }
                            start()
                        }
                    }
                    onMinuteChangedListener?.onMinuteChanged(curMinute)
                }
            }
        }

        return sqrt(
            ((startX - mRadius) * (startX - mRadius) + (startY - mRadius) * (startY - mRadius)).toDouble()
        ) <= mRadius
    }

    fun setCurrentMinute(minute: Int) {
        curMinute = minute
        val tmpDegree = 6 * minute
        if (tmpDegree == degree) {
            return
        }
        animator.apply {
            setIntValues(degree, tmpDegree)
            interpolator = DecelerateInterpolator()
            duration = if (abs(tmpDegree - degree) / 6 <= 20) {
                (40 * abs(tmpDegree - degree) / 6).toLong()
            } else {
                800
            }

            addUpdateListener {
                degree = it.animatedValue as Int
                postInvalidate()
            }
            start()
        }
    }


    interface OnMinuteChangedListener {
        fun onMinuteChanged(minute: Int)
    }

}