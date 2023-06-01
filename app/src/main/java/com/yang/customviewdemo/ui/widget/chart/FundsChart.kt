package com.yang.customviewdemo.ui.widget.chart

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.yang.customviewdemo.data.DayRateDetail
import com.yang.customviewdemo.utils.px
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


/**
 * Create by Yang Yang on 2023/5/31
 */
class FundsChart @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val fundGridDrawable = FundGridDrawable()
    private val fundLabelDrawable = FundLabelDrawable()
    private val rateLineDrawable = RateLineDrawable()
    private var chartRect = Rect()
    private val defaultPadding = 5f.px.toInt()
    private val paddingTop = 15f.px.toInt()
    private val paddingBottom = 25f.px.toInt()
    private val paddingStart = 60f.px.toInt()
    private val paddingEnd = 20f.px.toInt()
    private val labelWidth = 35f.px.toInt()
    private val labelHeight = 100f.px.toInt()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        chartRect.set(0, 0, w, h)
        rateLineDrawable.bounds = Rect(
            paddingStart, chartRect.top + paddingTop,
            chartRect.right - paddingEnd, chartRect.bottom - paddingBottom
        )
        fundGridDrawable.bounds = Rect(
            chartRect.left + defaultPadding,
            rateLineDrawable.bounds.top - defaultPadding,
            rateLineDrawable.bounds.right,
            chartRect.bottom - defaultPadding
        )
        fundGridDrawable.lineChartRect = rateLineDrawable.bounds

        fundLabelDrawable.bounds = Rect(
            rateLineDrawable.bounds.left,
            rateLineDrawable.bounds.bottom - labelHeight,
            rateLineDrawable.bounds.left + labelWidth,
            rateLineDrawable.bounds.bottom
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        fundGridDrawable.draw(canvas)
        rateLineDrawable.draw(canvas)
        //fundLabelDrawable.draw(canvas)
    }


    private var xPxSpec = 0.0
    private var yPxSpec = 0.0
    private var maxRate = Double.NEGATIVE_INFINITY
    private var minRate = Double.POSITIVE_INFINITY
    private var curMaxRate = Double.NEGATIVE_INFINITY
    private var curMinRate = Double.POSITIVE_INFINITY
    private var theRateRangeInterval = (maxRate - minRate).div(3).roundToInt()
    suspend fun setData(
        dayRateDetailList: List<DayRateDetail>,
        isShowWithAnimator: Boolean = false
    ) {
        if (dayRateDetailList.isEmpty()) {
            return
        }

        rateLineDrawable.setDayDataList(dayRateDetailList)
        fundGridDrawable.setDayDataList(dayRateDetailList)

        xPxSpec = 0.0
        yPxSpec = 0.0
        maxRate = Double.NEGATIVE_INFINITY
        minRate = Double.POSITIVE_INFINITY

        xPxSpec = rateLineDrawable.bounds.width().div(dayRateDetailList.size.toDouble())

        dayRateDetailList.forEach {
            maxRate = max(
                max(
                    max(
                        maxRate,
                        it.yield.toDouble()
                    ),
                    it.fundTypeYield.toDouble()
                ),
                it.indexYield.toDouble()
            )

            minRate = min(
                min(
                    min(
                        minRate,
                        it.yield.toDouble()
                    ),
                    it.fundTypeYield.toDouble()
                ),
                it.indexYield.toDouble()
            )


        }

        theRateRangeInterval = (maxRate - minRate).div(3).roundToInt()

        calcRateAbscissa().let { rateAbscissaLines ->
            fundGridDrawable.setRateAbscissaLines(rateAbscissaLines)

            val newMaxRate = rateAbscissaLines.last().toDouble()
            val newMinRate = rateAbscissaLines.first().toDouble()

            if (newMaxRate != curMaxRate || newMinRate != curMinRate) {
                if (curMaxRate == Double.NEGATIVE_INFINITY || curMinRate == Double.POSITIVE_INFINITY) {
                    curMaxRate = 0.0
                    curMinRate = 0.0
                }

                if (isShowWithAnimator) {
                    withContext(Dispatchers.Main) {
                        doMaxMinValueChangeAnimator(curMaxRate, newMaxRate, curMinRate, newMinRate)
                    }
                } else {
                    setDataForDrawableWithoutAnimation(newMaxRate, newMinRate)
                }

            } else {
                setDataForDrawableWithoutAnimation(newMaxRate, newMinRate)
            }
        }
    }

    private fun setDataForDrawableWithoutAnimation(newMaxRate: Double, newMinRate: Double) {
        rateLineDrawable.setMaxMinRate(newMaxRate, newMinRate)
        fundGridDrawable.setMaxMinRate(newMaxRate, newMinRate)

        yPxSpec = (newMaxRate - newMinRate).div(rateLineDrawable.bounds.height())
        rateLineDrawable.setPxSpec(xPxSpec, yPxSpec)
        fundGridDrawable.setPxSpec(xPxSpec, yPxSpec)

        postInvalidate()
    }

    private var startMaxValue: Double = 0.0
    private var finalMaxValue: Double = 0.0
    private var startMinValue: Double = 0.0
    private var finalMinValue: Double = 0.0
    private var maxMinValue: ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 250
        interpolator = DecelerateInterpolator()
        addUpdateListener {
            it.animatedValue.toString().toFloat().let { percent ->
                curMaxRate = startMaxValue + (finalMaxValue - startMaxValue) * percent
                curMinRate = startMinValue + (finalMinValue - startMinValue) * percent

                rateLineDrawable.setMaxMinRate(curMaxRate, curMinRate)
                fundGridDrawable.setMaxMinRate(curMaxRate, curMinRate)

                yPxSpec = (curMaxRate - curMinRate).div(rateLineDrawable.bounds.height())
                rateLineDrawable.setPxSpec(xPxSpec, yPxSpec)
                fundGridDrawable.setPxSpec(xPxSpec, yPxSpec)

                postInvalidate()
            }
        }
    }

    private fun doMaxMinValueChangeAnimator(
        newStartMaxValue: Double,
        newFinalMaxValue: Double,
        newStartMinValue: Double,
        newFinalMinValue: Double
    ) {
        maxMinValue.apply {
            if (isRunning) {
                end()
            }
            startMaxValue = newStartMaxValue
            finalMaxValue = newFinalMaxValue
            startMinValue = newStartMinValue
            finalMinValue = newFinalMinValue

            start()
        }
    }


    private fun calcRateAbscissa(): MutableList<Int> {
        val rateAbscissaLines = mutableListOf<Int>()

        if (theRateRangeInterval == 0) {
            return rateAbscissaLines
        }

        rateAbscissaLines.clear()
        rateAbscissaLines.add(0)

        for (i in 1..5) {
            rateAbscissaLines.add(i * theRateRangeInterval)
            if (theRateRangeInterval * i > maxRate) {
                break
            }
        }

        for (i in 1..5) {
            rateAbscissaLines.add(-i * theRateRangeInterval)
            if (-theRateRangeInterval * i < minRate) {
                break
            }
        }

        rateAbscissaLines.sort()

        return rateAbscissaLines
    }
}