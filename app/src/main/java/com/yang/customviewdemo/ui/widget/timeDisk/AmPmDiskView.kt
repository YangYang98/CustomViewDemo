package com.yang.customviewdemo.ui.widget.timeDisk

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import com.yang.customviewdemo.utils.sp2px
import kotlin.math.sqrt


/**
 * Create by Yang Yang on 2023/10/17
 */
class AmPmDiskView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): DiskView(context, attrs, defStyleAttr) {

    constructor(context: Context, radius: Int): this(context) {
        this.mRadius = radius.toFloat()
    }

    private val mPaint = Paint()
    private val textBounds = Rect()
    var centerString: String = ""
        set(value) {
            field = value
            postInvalidate()
        }
    var onAPMChangedListener: OnAPMChangedListener? = null

    init {
        mPaint.apply {
            isAntiAlias = true
            color = Color.parseColor("#ff1a1f4a")
            textAlign = Paint.Align.LEFT
            textSize = 24f.sp2px
        }
    }

    companion object {
        const val AM = "AM"
        const val PM = "PM"
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {

            mPaint.color = Color.parseColor("#ff1a1f4a")
            canvas.drawCircle(mRadius, mRadius, mRadius, mPaint)

            if (centerString.isNotEmpty()) {
                mPaint.color = Color.WHITE
                mPaint.getTextBounds(centerString, 0, centerString.length, textBounds)
                canvas.drawText(centerString, mRadius - textBounds.width() / 2, mRadius + textBounds.height() / 2, mPaint)
            } else {
                mPaint.color = Color.WHITE
                mPaint.getTextBounds(AM, 0, AM.length, textBounds)
                canvas.drawText(AM, mRadius - textBounds.width() / 2, mRadius + textBounds.height() / 2, mPaint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_UP -> {
                centerString = if (centerString == AM) PM else AM
                onAPMChangedListener?.onAPMChanged(centerString)
                postInvalidate()
            }
        }

        return sqrt(
            ((startX - mRadius) * (startX - mRadius) + (startY - mRadius) * (startY - mRadius)).toDouble()
        ) <= mRadius
    }

    interface OnAPMChangedListener {
        fun onAPMChanged(str: String)
    }
}