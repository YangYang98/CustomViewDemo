package com.yang.customviewdemo.ui.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.yang.customviewdemo.R
import kotlin.math.min


/**
 * Create by Yang Yang on 2023/4/27
 */
class ArcView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {


    private var arcColor = DEFAULT_ARC_COLOR
    private var bgColor = DEFAULT_BG_COLOR
    private var textColor = DEFAULT_TEXT_COLOR
    private var textSize = DEFAULT_TEXT_SIZE

    private val circlePaint = Paint()
    private val bgPaint = Paint()
    private val textPaint = Paint()

    var process = 0
        set(value) {
            if (value < 0 || process > maxProgress) {
                return
            }
            field = value
            invalidate()
        }
    var maxProgress = DEFAULT_MAX_PROGRESS

    private val textBound = Rect()

    companion object {
        private const val MAX_SWEEP_ANGLE = 240f
        private const val START_SWEEP_ANGLE = 150f
        private const val DEFAULT_MAX_PROGRESS = 100f

        private const val DEFAULT_ARC_COLOR: Int = Color.RED
        private const val DEFAULT_BG_COLOR: Int = Color.DKGRAY
        private const val DEFAULT_TEXT_COLOR: Int = Color.BLACK
        private const val DEFAULT_TEXT_SIZE = 40
    }

    init {

        val a = context.obtainStyledAttributes(attrs, R.styleable.ArcView)
        arcColor = a.getColor(R.styleable.ArcView_arcColor, DEFAULT_ARC_COLOR)
        bgColor = a.getColor(R.styleable.ArcView_bgColor, DEFAULT_BG_COLOR)
        textColor = a.getColor(R.styleable.ArcView_arc_textColor, DEFAULT_TEXT_COLOR)
        textSize = a.getColor(R.styleable.ArcView_arc_textSize, DEFAULT_TEXT_SIZE)

        a.recycle()

        circlePaint.color = arcColor
        circlePaint.strokeWidth = 8f
        circlePaint.style = Paint.Style.STROKE
        circlePaint.isDither = true
        circlePaint.isAntiAlias = true

        bgPaint.color = bgColor
        bgPaint.strokeWidth = 20f
        bgPaint.style = Paint.Style.STROKE
        bgPaint.isDither = true
        bgPaint.isAntiAlias = true

        textPaint.strokeWidth = 4f
        val size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            textSize.toFloat(), resources.displayMetrics)
        textPaint.textSize = size
        textPaint.isAntiAlias = true
        textPaint.color = textColor
        textPaint.textAlign = Paint.Align.LEFT

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val circleWidth = width - paddingLeft - paddingRight
        val circleHeight = height - paddingTop - paddingBottom

        val radius = min(circleWidth, circleHeight) / 2

        val left = (left + paddingLeft).toFloat()
        val top = (top + paddingTop).toFloat()
        val right = left + 2 * radius
        val bottom = top + 2 * radius
        canvas?.drawArc(left, top, right, bottom, START_SWEEP_ANGLE, MAX_SWEEP_ANGLE, false, bgPaint)

        val sweepArc = MAX_SWEEP_ANGLE * process / maxProgress
        canvas?.drawArc(left, top, right, bottom, START_SWEEP_ANGLE, sweepArc, false, circlePaint)

        val text = "$process%"
        textPaint.getTextBounds(text, 0, text.length, textBound)
        canvas?.drawText(text, (left + right) / 2 - textBound.width() / 2, (top + bottom) / 2 + textBound.height() / 2, textPaint)
        //canvas?.drawText(text, (left + right) / 2, (top + bottom) / 2, textPaint)
    }

}