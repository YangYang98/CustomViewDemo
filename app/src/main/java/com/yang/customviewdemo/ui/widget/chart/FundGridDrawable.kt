package com.yang.customviewdemo.ui.widget.chart

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import com.yang.customviewdemo.data.DayRateDetail
import com.yang.customviewdemo.utils.px
import com.yang.customviewdemo.utils.sp2px
import com.yang.customviewdemo.utils.toStringAsFixed
import java.util.concurrent.CopyOnWriteArrayList


/**
 * Create by Yang Yang on 2023/5/31
 */
class FundGridDrawable : Drawable() {

    internal var lineChartRect = Rect()
    private val dashPathEffect by lazy {
        DashPathEffect(floatArrayOf(3f.px, 3f.px, 3f.px, 3f.px), 1f)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 10f.sp2px
        color = Color.parseColor("#CBCBCB")
    }
    private val bottomLinePaint =  Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 0.8f.px
        color = Color.parseColor("#CBCBCB")
    }
    private val rateLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 0.8f.px
        pathEffect = dashPathEffect
        color = Color.parseColor("#CBCBCB")
    }

    private var minDateTime: String = "2022-03-31"
    private var middleDateTime: String = "2022-03-31"
    private var maxDateTime: String = "2022-03-31"

    private val paddingBottom = 10f.px

    private var maxRate: Double = 0.0
    private var minRate: Double = 0.0
    private var xPxSpec: Double = 0.0
    //yPxSpec代表一个高度代表多少收益率
    private var yPxSpec: Double = 0.0
    private val rateAbscissaLines = CopyOnWriteArrayList<Int>()

    internal fun setMaxMinRate(maxRate: Double, minRate: Double) {
        this.maxRate = maxRate
        this.minRate = minRate
    }

    internal fun setPxSpec(xPxSpec: Double, yPxSpec: Double) {
        this.xPxSpec = xPxSpec
        this.yPxSpec = yPxSpec
    }

    internal fun setRateAbscissaLines(newRateAbscissaLines: List<Int>) {
        this.rateAbscissaLines.clear()
        this.rateAbscissaLines.addAll(newRateAbscissaLines)
    }

    internal fun setDayDataList(dayRateDetailList: List<DayRateDetail>) {
        minDateTime = dayRateDetailList.first().date
        middleDateTime = dayRateDetailList[dayRateDetailList.size / 2].date
        maxDateTime = dayRateDetailList.last().date
    }


    private val textBoundsRect = Rect()
    private var dateTimeTextPxY = 0f
    private fun drawDateTimeText(canvas: Canvas) {
        textPaint.getTextBounds(minDateTime, 0, minDateTime.length, textBoundsRect)

        dateTimeTextPxY = lineChartRect.bottom + paddingBottom + textBoundsRect.height()

        textPaint.textAlign = Paint.Align.LEFT
        //绘制初始日期
        canvas.drawText(
            minDateTime,
            lineChartRect.left.toFloat(),
            dateTimeTextPxY,
            textPaint
        )

        //绘制最左边竖线
        canvas.drawLine(
            lineChartRect.left.toFloat(),
            lineChartRect.bottom.toFloat(),
            lineChartRect.left.toFloat(),
            lineChartRect.bottom.toFloat() + paddingBottom / 2,
            bottomLinePaint
        )

        textPaint.textAlign = Paint.Align.CENTER
        //绘制中间日期
        canvas.drawText(
            middleDateTime,
            ((lineChartRect.left + lineChartRect.right) / 2).toFloat(),
            dateTimeTextPxY,
            textPaint
        )

        //绘制中间竖线
        canvas.drawLine(
            ((lineChartRect.left + lineChartRect.right) / 2).toFloat(),
            lineChartRect.bottom.toFloat(),
            ((lineChartRect.left + lineChartRect.right) / 2).toFloat(),
            lineChartRect.bottom.toFloat() + paddingBottom / 2,
            bottomLinePaint
        )

        textPaint.textAlign = Paint.Align.RIGHT
        //绘制最后日期
        canvas.drawText(
            maxDateTime,
            lineChartRect.right.toFloat(),
            dateTimeTextPxY,
            textPaint
        )

        //绘制最右边竖线
        canvas.drawLine(
            lineChartRect.right.toFloat(),
            lineChartRect.bottom.toFloat(),
            lineChartRect.right.toFloat(),
            lineChartRect.bottom.toFloat() + paddingBottom / 2,
            bottomLinePaint
        )

        //绘制横线
        canvas.drawLine(
            lineChartRect.left.toFloat(),
            lineChartRect.bottom.toFloat(),
            lineChartRect.right.toFloat(),
            lineChartRect.bottom.toFloat(),
            bottomLinePaint
        )
    }

    private var yRatePx = 0f
    private fun drawRateTextAndLines(canvas: Canvas) {
        rateAbscissaLines.forEach {
            yRatePx = lineChartRect.top + (maxRate - it).div(yPxSpec).toFloat()

            canvas.drawLine(
                lineChartRect.left.toFloat(),
                yRatePx,
                lineChartRect.right.toFloat(),
                yRatePx,
                rateLinePaint
            )

            textPaint.textAlign = Paint.Align.RIGHT
            canvas.drawText(
                "${it.toStringAsFixed(2)}%",
                lineChartRect.left.toFloat() - 10f.px,
                yRatePx + 5f.px,
                textPaint
            )
        }
    }

    override fun draw(canvas: Canvas) {
        drawDateTimeText(canvas)

        drawRateTextAndLines(canvas)
    }

    override fun setAlpha(alpha: Int) {
        rateLinePaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        rateLinePaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return when (rateLinePaint.alpha) {
            0xff -> {
                PixelFormat.OPAQUE
            }
            0x00 -> {
                PixelFormat.TRANSPARENT
            }
            else -> {
                PixelFormat.TRANSLUCENT
            }
        }
    }
}