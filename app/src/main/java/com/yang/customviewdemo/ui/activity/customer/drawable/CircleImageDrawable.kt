package com.yang.customviewdemo.ui.activity.customer.drawable

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Shader
import android.graphics.drawable.Drawable


/**
 * Create by Yang Yang on 2023/7/20
 */
class CircleImageDrawable(private val bitmap: Bitmap) : Drawable() {

    private var mPaint: Paint = Paint()
    private var mWidth = 0

    init {

        val bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mPaint.isAntiAlias = true
        mPaint.setShader(bitmapShader)
        mWidth = bitmap.width.coerceAtMost(bitmap.height)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawCircle((mWidth / 2).toFloat(), (mWidth / 2).toFloat(), (mWidth / 2).toFloat(), mPaint)
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        mWidth = right.coerceAtMost(bottom)
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


}