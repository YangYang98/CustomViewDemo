package com.yang.customviewdemo.ui.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.graphics.withSave
import com.yang.customviewdemo.utils.dp


/**
 * Create by Yang Yang on 2023/8/11
 */
class IrisDiaphragmView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val rectF by lazy {
        val left = 0f + RADIUS / 2f
        val top = 0f + RADIUS / 2f
        val right = left + DEF_WIDTH - RADIUS
        val bottom = top + DEF_WIDTH - RADIUS
        RectF(left, top, right, bottom)
    }

    private val animator by lazy {
        val animator = ObjectAnimator.ofFloat(this, "currentSpeed", 0f, 360f).apply {
            repeatCount = -1
            interpolator = null
            duration = 2000
        }

        animator
    }

    var currentSpeed = 0f
        set(value) {
            field = value
            invalidate()
        }

    private val path by lazy {
        Path().also { it.addRoundRect(rectF, RADIUS, RADIUS, Path.Direction.CCW) }
    }

    private val color1 by lazy {
        LinearGradient(
            width * 1f, height / 2f, width * 1f, height * 1f,
            intArrayOf(Color.TRANSPARENT, Color.BLUE), floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    private val color2 by lazy {
        LinearGradient(
            width / 2f, height / 2f, width * 1f, 0f,
            intArrayOf(Color.TRANSPARENT, Color.CYAN), floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    companion object {

        private val DEF_WIDTH = 200.dp
        private val DEF_HEIGHT = DEF_WIDTH
        private val RADIUS = (20.dp).toFloat()
    }

    init {

        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                view?.let {
                    outline?.setRoundRect(0, 0, it.width, it.height, RADIUS)
                }
            }
        }
        clipToOutline = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = resolveSize(DEF_WIDTH, widthMeasureSpec)
        val height = resolveSize(DEF_HEIGHT, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun dispatchDraw(canvas: Canvas) {
        val left = rectF.left + rectF.width() / 2f
        val right = rectF.right + rectF.width() / 2f
        val top = rectF.top + rectF.height() / 2f
        val bottom = rectF.bottom + rectF.height() / 2f

        //canvas.clipOutPath(path)

        canvas.withSave {

            canvas.rotate(currentSpeed, width / 2f, height / 2f)

            paint.shader = color1
            canvas.drawRect(left, top, right, bottom, paint)
            paint.shader = null

            paint.shader = color2
            canvas.drawRect(left, top, -right + rectF.width(), -bottom + rectF.height(), paint)
            paint.shader = null
        }
        canvas.drawRoundRect(rectF, RADIUS, RADIUS, paint)

        //这个super中会绘制children
        super.dispatchDraw(canvas)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        animator.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator.cancel()
    }
}