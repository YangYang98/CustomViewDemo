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
class HourDiskView @JvmOverloads constructor(
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

    var curHour = 1
        set(value) {
            field = if (value > 12) {
                value - 12
            } else {
                value
            }
        }

    var onHourChangedListener: OnHourChangedListener? = null

    init {
        if (mRadius == 0f) {
            mRadius = 450f
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
        for (i in 1 .. 12) {
            mPaint.getTextBounds(i.toString(), 0, i.toString().length, textBounds)

            mPaint.color = if (i == curHour) {
                selectNumColor
            } else {
                numColor
            }
            canvas.drawText(i.toString(), mRadius - textBounds.width() / 2, mRadius * 2 - textBounds.height() / 2, mPaint)
            canvas.rotate(-30f, mRadius, mRadius)
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                if (!isNeedReturn) {
                    val temDegree = degree + curDegree - startDegree
                    val temD = temDegree % 30
                    var offsetD = temDegree - temD
                    if (offsetD < 0) {
                        offsetD += 360
                    }
                    curHour = if (temD == 0 || temD < 15) {
                        offsetD / 30 + 1
                    } else {
                        offsetD / 30 + 2
                    }
                    onHourChangedListener?.onHourChanged(curHour)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!isNeedReturn) {
                    //做定位矫正,定位到最近的数字
                    val tmp = degree % 30

                    var offset = degree - tmp
                    if (offset < 0) {
                        offset += 360
                    }

                    if (tmp == 0) {
                        curHour = offset / 30 + 1
                    } else if (tmp < 15) {
                        curHour = offset / 30 + 1
                        animator.apply {
                            setIntValues(degree, degree - tmp)
                            duration = 100
                            addUpdateListener {
                                degree = it.animatedValue as Int
                                postInvalidate()
                            }
                            start()
                        }
                    } else {
                        curHour = offset / 30 + 2
                        animator.apply {
                            setIntValues(degree, degree - tmp + 30)
                            duration = 100
                            addUpdateListener {
                                degree = it.animatedValue as Int
                                postInvalidate()
                            }
                            start()
                        }
                        onHourChangedListener?.onHourChanged(curHour)
                    }
                }
            }
        }

        return sqrt(
            ((startX - mRadius) * (startX - mRadius) + (startY - mRadius) * (startY - mRadius)).toDouble()
        ) <= mRadius
    }

    fun setCurrentHour(hour: Int) {
        curHour = hour
        val tmpDegree = (hour - 1) * 30
        if (tmpDegree == degree) {
            return
        }

        animator.apply {
            setIntValues(degree, tmpDegree)
            duration = if (abs(tmpDegree - degree) / 30 <= 5) {
                (80 * abs(tmpDegree - degree) / 30).toLong()
            } else {
                500
            }
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                degree = it.animatedValue as Int
                postInvalidate()
            }
            start()
        }
    }

    interface OnHourChangedListener {
        fun onHourChanged(hour: Int)
    }

}