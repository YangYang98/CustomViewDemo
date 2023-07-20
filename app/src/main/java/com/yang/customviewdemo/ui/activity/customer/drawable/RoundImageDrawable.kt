package com.yang.customviewdemo.ui.activity.customer.drawable

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable


/**
 * Create by Yang Yang on 2023/7/20
 */
class RoundImageDrawable(private val bitmap: Bitmap) : Drawable() {

    private var mPaint: Paint = Paint()
    private lateinit var rectF: RectF

    init {

        val bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mPaint.isAntiAlias = true
        mPaint.setShader(bitmapShader)

    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        rectF = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
    }


    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(rectF, 30F, 30F, mPaint)
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.setColorFilter(colorFilter)
    }


    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    /**
     * getIntrinsicWidth、getIntrinsicHeight主要是为了在View使用wrap_content的时候，提供一下尺寸，默认为-1不是我们希望的。
     */
    override fun getIntrinsicWidth(): Int {
        return bitmap.width
    }

    override fun getIntrinsicHeight(): Int {
        return bitmap.height
    }
}