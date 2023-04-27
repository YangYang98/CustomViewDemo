package com.yang.customviewdemo.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.yang.customviewdemo.R
import kotlin.math.min


/**
 * Create by Yang Yang on 2023/4/26
 */
class CircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    private var paint1: Paint = Paint()
    private var color: Int = Color.BLUE

    init {

        val a = context.obtainStyledAttributes(attrs, R.styleable.CircleView)
        color = a.getColor(R.styleable.CircleView_circle_color, Color.BLUE)
        a.recycle()

        paint1.color = color
        paint1.strokeWidth = 5f
        paint1.style = Paint.Style.STROKE

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthModel = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightModel = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(widthMeasureSpec)

        val width = 300
        val height = 300

        if (layoutParams.width == WRAP_CONTENT && layoutParams.height == WRAP_CONTENT) {
            setMeasuredDimension(width, width)
        } else if (layoutParams.width == WRAP_CONTENT) {
            setMeasuredDimension(width, heightSize)
        } else if (layoutParams.height == WRAP_CONTENT) {
            setMeasuredDimension(widthSize, height)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val width = width - paddingLeft - paddingRight
        val height = height - paddingTop - paddingBottom

        val r = min(width, height) / 2

        canvas?.drawCircle((width / 2 + paddingLeft).toFloat(), (height / 2 + paddingTop).toFloat(), r.toFloat(), paint1)

        /*canvas?.translate(paddingLeft.toFloat(), paddingTop.toFloat())
        canvas?.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), r.toFloat(), paint1)
        canvas?.translate(-paddingLeft.toFloat(), -paddingTop.toFloat())*/
    }
}