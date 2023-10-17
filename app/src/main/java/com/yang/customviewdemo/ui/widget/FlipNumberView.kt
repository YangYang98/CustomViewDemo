package com.yang.customviewdemo.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.yang.customviewdemo.utils.sp2px


/**
 * Create by Yang Yang on 2023/10/16
 */
class FlipNumberView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    /**
     * 每片card的宽高
     */
    private var cardWidth: Float = 0F
    private var cardHeight: Float = 0F

    /**
     * padding大小，为阴影绘制留出空间
     */
    var paddingSize: Float = 250F

    var mPaint: Paint = Paint()
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val textBounds = Rect()

    init {
        initPaint()
    }

    private fun initPaint() {
        mPaint.apply {
            style = Paint.Style.FILL
            color = Color.GRAY
        }

        textPaint.apply {
            textSize = 200f.sp2px
            color = Color.RED
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        //设置card 大小
        cardWidth = width - paddingSize * 2
        cardHeight = height / 2 - paddingSize
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val space = 10f
        val bgBorderR = 10f

        val upperHalfBottom = height.toFloat() / 2 - space / 2
        canvas.drawRoundRect(
            0f, 0f, width.toFloat(), upperHalfBottom, bgBorderR, bgBorderR, mPaint
        )

        val lowerHalfTop = height.toFloat() / 2 + space / 2
        canvas.drawRoundRect(
            0f, lowerHalfTop, width.toFloat(), height.toFloat(), bgBorderR, bgBorderR, mPaint
        )

        val number4 = "4"
        textPaint.getTextBounds(number4, 0, number4.length, textBounds)
        val x = (width / 2 - textBounds.width() / 2 - textBounds.left).toFloat()
        val y = (height / 2 + textBounds.height() / 2 - textBounds.bottom).toFloat()

        canvas.save()
        canvas.clipRect(
            0f, 0f, width.toFloat(), upperHalfBottom
        )
        canvas.drawText(number4, x, y, textPaint)
        canvas.restore()

        canvas.save()
        canvas.clipRect(
            0f, lowerHalfTop,
            width.toFloat(),
            height.toFloat()
        )
        canvas.drawText(number4, x, y, textPaint)
        canvas.restore()
    }
}