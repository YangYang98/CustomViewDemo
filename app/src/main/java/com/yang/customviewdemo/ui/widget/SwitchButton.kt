package com.yang.customviewdemo.ui.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import kotlin.math.abs


/**
 * Create by Yang Yang on 2023/10/23
 */
class SwitchButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    @ColorInt
    val shadowColor = Color.GRAY
    @ColorInt
    val onColor = Color.parseColor("#ff9ab9ff")
    @ColorInt
    val offColor = Color.parseColor("#ffe6e6e6")
    @ColorInt
    val indicatorColor = Color.WHITE
    @ColorInt
    val backgroundAreaColor = Color.WHITE

    var indicatorShadowSize = 6
    var indicatorShadowDistance = 24
    var backgroundAreaShadowDistance = 48
    var backgroundAreaShadowSize = 24
    var shadowOffset = 0

    var backgroundAreaW = 0
    var backgroundAreaH = 0

    var indicatorR = 0

    var indicatorX = 0
    var indicatorXOffset = 0

    val indicatorPaint: Paint by lazy { Paint() }
    val backgroundAreaPaint: Paint by lazy { Paint() }
    val flagPaint: Paint by lazy { Paint() }

    var isChecked = false

    private val shadowAnimator: ValueAnimator by lazy {
        ValueAnimator.ofInt()
    }
    private val translateAnimator: ValueAnimator by lazy {
        ValueAnimator.ofInt()
    }

    init {
        with(indicatorPaint) {
            color = indicatorColor
            isAntiAlias = true
        }
        with(backgroundAreaPaint) {
            color = backgroundAreaColor
            isAntiAlias = true
        }
        with(flagPaint) {
            isAntiAlias = true
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        backgroundAreaW = w * 3 / 4
        backgroundAreaH = h / 2

        indicatorR = backgroundAreaH / 2

        indicatorX = ((width - backgroundAreaW) / 2 + indicatorR)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        setLayerType(LAYER_TYPE_SOFTWARE, null)

        drawBackgroundArea(canvas)

        drawFlag(canvas)

        drawIndicator(canvas)
    }

    private fun drawBackgroundArea(canvas: Canvas) {
        canvas.save()

        backgroundAreaShadowSize = backgroundAreaH / 4
        backgroundAreaShadowDistance = backgroundAreaH / 12
        val strokeW = indicatorR / 2f
        with(backgroundAreaPaint) {
            style = Paint.Style.STROKE
            strokeWidth = strokeW
            color = Color.parseColor("#ffbcbcbc")
            setShadowLayer((backgroundAreaShadowSize + shadowOffset).toFloat(), 0f,
                backgroundAreaShadowDistance.toFloat(), Color.GRAY)
        }
        val strokeRectF = RectF(
            -strokeW + (width - backgroundAreaW) / 2f, -strokeW + (height - backgroundAreaH) / 2f,
            strokeW + (width - backgroundAreaW) / 2f + backgroundAreaW,
            strokeW + (height - backgroundAreaH) / 2f + backgroundAreaH
            )
        val strokePath = Path().apply {
            addRoundRect(strokeRectF, (backgroundAreaH + strokeW) / 2, (backgroundAreaH + strokeW) / 2, Path.Direction.CW)
        }

        val rectF = RectF(
            (width - backgroundAreaW) / 2f, (height - backgroundAreaH) / 2f,
            (width - backgroundAreaW) / 2f + backgroundAreaW,
            (height - backgroundAreaH) / 2f + backgroundAreaH
        )
        val path = Path().apply {
            addRoundRect(rectF, backgroundAreaH / 2f, backgroundAreaH / 2f, Path.Direction.CW)
        }
        canvas.clipPath(path)

        canvas.drawPath(strokePath, backgroundAreaPaint)

        with(backgroundAreaPaint) {
            strokeWidth = 2f
            clearShadowLayer()
        }
        canvas.drawPath(path, backgroundAreaPaint)

        canvas.restore()

    }

    private fun drawFlag(canvas: Canvas) {
        canvas.save()

        val rectF = RectF(
            (width - backgroundAreaW) / 2f, (height - backgroundAreaH) / 2f,
            (width - backgroundAreaW) / 2f + backgroundAreaW,
            (height - backgroundAreaH) / 2f + backgroundAreaH
            )
        val bgAreaPath = Path().apply {
            addRoundRect(rectF, backgroundAreaH / 2f, backgroundAreaH / 2f, Path.Direction.CW)
        }
        canvas.clipPath(bgAreaPath)

        with(flagPaint) {
            style = Paint.Style.FILL
            color = onColor
            clearShadowLayer()
        }
        canvas.drawCircle(
            indicatorX + indicatorXOffset - backgroundAreaW * 3 / 5f,
            height / 2f,
            indicatorR / 4f,
            flagPaint
            )

        val onStrokeW = indicatorR / 4f
        with(flagPaint) {
            style = Paint.Style.STROKE
            strokeWidth = onStrokeW
            setShadowLayer(onStrokeW, -onStrokeW, onStrokeW, onColor)
        }
        val onPath = Path().apply {
            addCircle(
                indicatorX + indicatorXOffset - backgroundAreaW * 3 / 5f,
                height / 2f,
                indicatorR / 4f + onStrokeW / 2,
                Path.Direction.CW
                )
        }
        canvas.save()
        canvas.clipPath(onPath)
        canvas.drawPath(onPath, flagPaint)

        flagPaint.clearShadowLayer()
        canvas.restore()

        //绘制off flag
        with(flagPaint) {
            style = Paint.Style.FILL
            color = offColor
        }
        canvas.drawCircle(
            indicatorX + indicatorXOffset + backgroundAreaW * 3 / 5f,
            height / 2f,
            indicatorR / 4f,
            flagPaint
            )
        val offStrokeW = indicatorR / 4f
        with(flagPaint) {
            style = Paint.Style.STROKE
            strokeWidth = offStrokeW
            setShadowLayer(offStrokeW, - offStrokeW, offStrokeW, offColor)
        }
        val offPath = Path().apply {
            addCircle(
                indicatorX + indicatorXOffset + backgroundAreaW * 3 / 5f,
                height / 2f,
                indicatorR / 4f + offStrokeW / 2f,
                Path.Direction.CW
                )
        }
        canvas.save()
        canvas.clipPath(offPath)
        canvas.drawPath(offPath, flagPaint)
        canvas.restore()
    }

    private fun drawIndicator(canvas: Canvas) {
        indicatorShadowSize = indicatorR / 3
        indicatorShadowDistance = indicatorShadowSize / 2
        //绘制外阴影
        with(indicatorPaint) {
            color = indicatorColor
            style = Paint.Style.FILL
            setShadowLayer(
                (indicatorShadowSize - shadowOffset).toFloat(), 0f,
                indicatorShadowDistance.toFloat(), Color.parseColor("#ffc1c1c1")
            )
        }
        canvas.drawCircle(
            (indicatorX + indicatorXOffset).toFloat(), (height - backgroundAreaH) / 2f + indicatorR,
            indicatorR.toFloat(), indicatorPaint
        )

        //绘制内阴影
        canvas.save()
        val strokeW = indicatorR / 2f
        with(indicatorPaint) {
            color = Color.parseColor("#66bcbcbc")
            strokeWidth = strokeW
            style = Paint.Style.STROKE
            setShadowLayer(indicatorR / 3f, -indicatorR / 6f, -indicatorR / 6f, Color.parseColor("#fff1f1f1"))
        }
        val strokePath = Path().apply {
            addCircle(
                (indicatorX + indicatorXOffset).toFloat(), (height - backgroundAreaH) / 2f + indicatorR,
                indicatorR + strokeW / 2f, Path.Direction.CW
                )
        }
        val path = Path().apply {
            addCircle(
                (indicatorX + indicatorXOffset).toFloat(), (height - backgroundAreaH) / 2f + indicatorR,
                indicatorR.toFloat(), Path.Direction.CW
            )
        }
        canvas.clipPath(path)
        canvas.drawPath(strokePath, indicatorPaint)

        with(indicatorPaint) {
            strokeWidth = 2f
            clearShadowLayer()
        }
        canvas.drawPath(path, indicatorPaint)

        canvas.restore()

        canvas.restore()
    }

    private var downX = 0f
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                shadowAnimator.apply {
                    cancel()
                    setIntValues(0, indicatorR / 4)
                    addUpdateListener {
                        shadowOffset = it.animatedValue as Int
                        postInvalidate()
                    }
                    duration = 200
                    start()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                indicatorXOffset = (event.x - downX).toInt()
                if (indicatorXOffset + indicatorX <= (width - backgroundAreaW) / 2 + indicatorR) {
                    indicatorXOffset = (width - backgroundAreaW) / 2 + indicatorR - indicatorX
                } else if (indicatorXOffset + indicatorX >= width - (width - backgroundAreaW) / 2 - indicatorR) {
                    indicatorXOffset = width - (width - backgroundAreaW) / 2 - indicatorR - indicatorX
                }
            }
            MotionEvent.ACTION_UP -> {
                downX = 0f
                indicatorX += indicatorXOffset
                shadowAnimator.apply {
                    cancel()
                    setIntValues(indicatorR / 4, 0)
                    addUpdateListener {
                        shadowOffset = it.animatedValue as Int
                        postInvalidate()
                    }
                    duration = 200
                    start()
                }
                if (abs(indicatorXOffset) <= 20) {
                    isChecked = !isChecked
                    startTranslateAnim(isChecked)
                } else if (
                    (indicatorXOffset > 0 && indicatorXOffset >= (backgroundAreaW - 2 * indicatorR) / 2) ||
                    (indicatorXOffset < 0 && indicatorXOffset > -(backgroundAreaW - 2 * indicatorR) / 2)
                    ) {
                    indicatorXOffset = 0
                    isChecked = true
                    startTranslateAnim(isChecked)
                } else if (
                    (indicatorXOffset > 0 && indicatorXOffset < (backgroundAreaW - 2 * indicatorR) / 2) ||
                    (indicatorXOffset < 0 && indicatorXOffset <= -(backgroundAreaW - 2 * indicatorR) / 2)
                ) {
                    indicatorXOffset = 0
                    isChecked = false
                    startTranslateAnim(isChecked)
                }
            }
        }
        postInvalidate()

        return true
    }

    private fun startTranslateAnim(checked: Boolean) {
        with(translateAnimator) {
            cancel()
            if (checked) {
                translateAnimator.setIntValues(indicatorX + indicatorXOffset, width - (width - backgroundAreaW) / 2 - indicatorR)
            } else {
                translateAnimator.setIntValues(indicatorX + indicatorXOffset, (width - backgroundAreaW) / 2 + indicatorR)
            }
            Log.e("YANGindicatorXOffset", "indicatorXOffset:$indicatorXOffset")
            duration = 200
            addUpdateListener {
                indicatorX = it.animatedValue as Int
                postInvalidate()
            }
            start()
        }
    }
}