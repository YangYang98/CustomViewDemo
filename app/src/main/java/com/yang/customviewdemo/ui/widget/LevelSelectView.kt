package com.yang.customviewdemo.ui.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import kotlin.math.abs


/**
 * Create by Yang Yang on 2023/10/23
 */
@RequiresApi(Build.VERSION_CODES.Q)
class LevelSelectView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    var mWidth: Int = 0
    var mHeight: Int = 0

    //指示器半径
    var indicatorRadius = 30f
    //初始指示器半径
    var indicatorInitRadius = 30f

    var indicatorX: Float = 0f
    var indicatorY: Float = 0f
    var indicatorOffset: Float = 0f

    @ColorInt
    var indicatorColor = Color.WHITE
    @ColorInt
    var lineSegmentColor = Color.BLACK

    var leftLineStartColor = Color.parseColor("#ff7FFFD4")
    var leftLineEndColor = Color.parseColor("#ffFF4500")

    @ColorInt
    var shadowColor = Color.parseColor("#ffe0e0e0")
    //指示器中心颜色
    @ColorInt
    var indicatorDotColor = Color.parseColor("#330000FF")

    @ColorInt
    var dotColor = Color.GRAY

    var dotRadius = 10f

    var levelCount = 3
    var lineStrokeWidth = 4f
    var lineSegmentWidth = 0f
    var lineBgHeight = 0

    private val linePaint: Paint by lazy { Paint() }
    private val indicatorPaint: Paint by lazy { Paint() }
    private val dotPaint: Paint by lazy { Paint() }

    private val indicatorTransAnimator: ValueAnimator by lazy {
        ValueAnimator.ofFloat()
    }
    private val indicatorScaleAnimator: ValueAnimator by lazy {
        ValueAnimator.ofFloat()
    }

    init {

        linePaint.apply {
            color = lineSegmentColor
            style = Paint.Style.STROKE
            strokeWidth = lineStrokeWidth
            isAntiAlias = true
        }

        indicatorPaint.apply {
            color = indicatorColor
            style = Paint.Style.FILL
            strokeWidth = lineStrokeWidth
            isAntiAlias = true
        }

        dotPaint.apply {
            color = dotColor
            style = Paint.Style.FILL
            strokeWidth = lineStrokeWidth
            isAntiAlias = true
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        this.mWidth = w
        this.mHeight = h

        indicatorInitRadius = mHeight / 6f
        indicatorRadius = indicatorInitRadius
        dotRadius = indicatorRadius / 6

        indicatorX = 2 * indicatorInitRadius + indicatorOffset
        indicatorY = mHeight / 2f

        lineSegmentWidth = (mWidth - 4 * indicatorInitRadius) / levelCount
        lineBgHeight = indicatorInitRadius.toInt()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        setLayerType(LAYER_TYPE_SOFTWARE, null)

        for (i in 0 ..levelCount) {
            canvas.drawCircle(lineSegmentWidth * i + 2 * indicatorInitRadius, indicatorY, dotRadius, dotPaint)
        }

        drawLineBg(canvas)

        drawIndicator(canvas)

    }

    private fun drawLineBg(canvas: Canvas) {
        canvas.save()

        val rectF = RectF(2 * indicatorInitRadius - lineBgHeight / 2, indicatorY - lineBgHeight / 2, mWidth - 2 * indicatorInitRadius + lineBgHeight / 2, indicatorY + lineBgHeight / 2)
        val path = Path().apply {
            addRoundRect(rectF, lineBgHeight / 2f, lineBgHeight / 2f, Path.Direction.CW)
        }

        linePaint.apply {
            style = Paint.Style.FILL
            clearShadowLayer()
            color = Color.parseColor("#88e0e0e0")
        }
        canvas.drawPath(path, linePaint)

        drawIndicatorLeftLineBg(canvas)

        linePaint.apply {
            setShadowLayer(3f, 0f, 2f, shadowColor)
            style = Paint.Style.STROKE
            color = Color.parseColor("#ffe0e0e0")
        }
        canvas.apply {
            clipPath(path)
            drawPath(path, linePaint)
        }

        canvas.restore()
    }


    private fun drawIndicatorLeftLineBg(canvas: Canvas) {
        val linearGradient = LinearGradient(0f, 0f, mWidth.toFloat(), 0f, leftLineStartColor, leftLineEndColor, Shader.TileMode.CLAMP)
        with(linePaint) {
            shader = linearGradient
            style = Paint.Style.FILL
            clearShadowLayer()
        }

        val rectF = RectF(2 * indicatorInitRadius - lineBgHeight / 2, indicatorY - lineBgHeight / 2, indicatorX + indicatorOffset + lineBgHeight / 2, indicatorY + lineBgHeight / 2)
        val path = Path().apply {
            addRoundRect(rectF, lineBgHeight.toFloat(), lineBgHeight.toFloat(), Path.Direction.CW)
        }
        canvas.drawPath(path, linePaint)

        linePaint.clearShadowLayer()
        linePaint.shader = null
    }

    private fun drawIndicator(canvas: Canvas) {
        with(indicatorPaint) {
            color = Color.WHITE
            style = Paint.Style.FILL
            setShadowLayer(3f, 0f, 2f, Color.GRAY)
        }
        canvas.drawCircle(indicatorX + indicatorOffset, indicatorY, indicatorRadius, indicatorPaint)

        with(indicatorPaint) {
            color = indicatorDotColor
            style = Paint.Style.FILL
            clearShadowLayer()
        }
        canvas.drawCircle(indicatorX + indicatorOffset, indicatorY, indicatorRadius / 2f, indicatorPaint)

        canvas.save()
        val path = Path().apply {
            addCircle(indicatorX + indicatorOffset, indicatorY, indicatorRadius / 2f, Path.Direction.CW)
        }
        with(indicatorPaint) {
            color = Color.WHITE
            setShadowLayer(3f, 0f, 2f, Color.GRAY)
            style = Paint.Style.STROKE
        }
        canvas.clipPath(path)
        canvas.drawPath(path, indicatorPaint)
        canvas.restore()
    }

    private var downX = 0f
    private var downY = 0f

    private var isDrag = false
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                if (abs(downX - indicatorX) < indicatorInitRadius) {
                    isDrag = true
                    scaleOutIndicator()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDrag) {
                    indicatorOffset = event.x - downX
                    if (indicatorOffset + indicatorX <= 2 * indicatorInitRadius) {
                        indicatorOffset = 2 * indicatorInitRadius - indicatorX
                    } else if (indicatorOffset + indicatorX >= mWidth - 2 * indicatorInitRadius) {
                        indicatorOffset = mWidth - 2 * indicatorInitRadius - indicatorX
                    }
                    postInvalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                isDrag = false
                indicatorTransAnimator.cancel()

                val overLineSegmentCount = (abs(indicatorX + indicatorOffset) / lineSegmentWidth).toInt()
                var indexSegmentCount = overLineSegmentCount
                if (abs((indicatorX + indicatorOffset) % lineSegmentWidth) > lineSegmentWidth / 2) {
                    indexSegmentCount = overLineSegmentCount + 1
                }
                indicatorTransAnimator.apply {
                    setFloatValues(indicatorX + indicatorOffset, indexSegmentCount * lineSegmentWidth + 2 * indicatorInitRadius)
                    duration = 150
                    addUpdateListener {
                        indicatorX = it.animatedValue as Float
                        postInvalidate()
                    }
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            scaleInIndicator()
                        }
                    })
                }
                indicatorX += indicatorOffset
                indicatorOffset = 0f
                downX = 0f
                downY = 0f
                indicatorTransAnimator.start()
            }
        }
        return true
    }

    private fun scaleInIndicator() {
        with(indicatorScaleAnimator) {
            cancel()
            setFloatValues(indicatorRadius, indicatorInitRadius)
            duration = 300
            addUpdateListener {
                indicatorRadius = it.animatedValue as Float
                postInvalidate()
            }
            start()
        }
    }

    private fun scaleOutIndicator() {
        with(indicatorScaleAnimator) {
            cancel()
            setFloatValues(indicatorInitRadius, indicatorInitRadius * 2)
            duration = 150
            addUpdateListener {
                indicatorRadius = it.animatedValue as Float
                postInvalidate()
            }
            start()
        }
    }
}