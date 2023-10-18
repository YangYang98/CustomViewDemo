package com.yang.customviewdemo.ui.widget.timeDisk

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.yang.customviewdemo.utils.sp2px


/**
 * Create by Yang Yang on 2023/10/17
 */
class SecondDiskView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): DiskView(context, attrs, defStyleAttr) {

    constructor(context: Context, radius: Int): this(context) {
        this.mRadius = radius.toFloat()
    }

    private val mPaint = Paint()

    //秒针点颜色
    var dotColor = Color.BLACK

    //秒针指示点颜色
    var indicatorColor = Color.GREEN

    init {
        if (mRadius == 0f) {
            mRadius = 650f
        }

        mPaint.apply {
            isAntiAlias = true
            color = dotColor
            textAlign = Paint.Align.LEFT
            textSize = 20f.sp2px
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mPaint.color = dotColor
        for (i in 0 until 60) {
            if (i == 0) {
                mPaint.color = indicatorColor
                canvas.drawCircle(mRadius, mRadius * 2, 20f.sp2px / 5, mPaint)
                mPaint.color = dotColor
            } else {
                canvas.drawCircle(mRadius, mRadius * 2, 20f.sp2px / 5, mPaint)
            }
            canvas.rotate(-6f, mRadius, mRadius)
        }
    }

    fun setCurrentSecond(second: Int) {
        val tmpDegree = 6 * (second - 1)
        if (tmpDegree == degree) {
            return
        }

        animator.apply {
            setIntValues(degree, tmpDegree)
            duration = 100
            addUpdateListener {
                degree = it.animatedValue as Int
                postInvalidate()
            }
            start()
        }

    }
}