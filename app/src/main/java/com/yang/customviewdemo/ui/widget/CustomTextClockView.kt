package com.yang.customviewdemo.ui.widget

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.yang.customviewdemo.utils.getBottomedY
import com.yang.customviewdemo.utils.getCenterY
import com.yang.customviewdemo.utils.getToppedY
import com.yang.customviewdemo.utils.toText
import java.util.Calendar
import java.util.Timer
import kotlin.concurrent.timer
import kotlin.properties.Delegates


/**
 * Create by Yang Yang on 2023/7/11
 */
class CustomTextClockView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val mPaint = createPaint()
    private val mHelperPaint = createPaint(color = Color.RED)

    private var mWidth: Float = -1f
    private var mHeight: Float by Delegates.notNull()

    private var mHourR: Float by Delegates.notNull()
    private var mMinuteR: Float by Delegates.notNull()
    private var mSecondR: Float by Delegates.notNull()

    //度数
    private var mHourDeg: Float by Delegates.notNull()
    private var mMinuteDeg: Float by Delegates.notNull()
    private var mSecondDeg: Float by Delegates.notNull()

    private var mAnimator: ValueAnimator by Delegates.notNull()

    private lateinit var mTimer: Timer

    /**
     * 绘制方法的回调，供动态壁纸使用
     */
    private var mBlock: (() -> Unit)? = null

    init {
        mAnimator = ValueAnimator.ofFloat(6f, 0f)
        mAnimator.duration = 150
        mAnimator.interpolator = LinearInterpolator()
        doInvalidate()
    }

    //可以在要开始绘制新的一秒的时候，在前150ms线性的旋转6°,达到线性转动的效果
    fun doInvalidate(block: (() -> Unit)? = null) {
        this.mBlock = block

        Calendar.getInstance().run {
            val hour = get(Calendar.HOUR)
            val minute = get(Calendar.MINUTE)
            val second = get(Calendar.SECOND)

            mHourDeg = -360 / 12f * (hour - 1)
            mMinuteDeg = -360 / 60f * (minute - 1)
            mSecondDeg = -360 / 60f * (second - 1)

            //记录当前角度，然后让秒圈线性的旋转6°
            val hd = mHourDeg
            val md = mMinuteDeg
            val sd = mSecondDeg

            mAnimator.removeAllUpdateListeners()
            mAnimator.addUpdateListener {
                val value = it.animatedValue as Float

                if (minute == 0 && second == 0) {
                    mHourDeg = hd + value * 5 //时圈旋转角度是分秒的5倍，线性的旋转30°
                }
                if (second == 0) {
                    mMinuteDeg = md + value//线性的旋转6°
                }
                mSecondDeg = sd + value

                if (this@CustomTextClockView.mBlock != null) {
                    this@CustomTextClockView.mBlock?.invoke()
                } else {
                    invalidate()
                }
            }
            mAnimator.start()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        mWidth = (measuredWidth - paddingStart - paddingEnd).toFloat()
        mHeight = (measuredHeight - paddingTop - paddingBottom).toFloat()

        //统一用View宽度*系数来处理大小，这样可以联动适配样式
        mHourR = mWidth * 0.143f
        mMinuteR = mWidth * 0.35f
        mSecondR = mWidth * 0.35f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return

        canvas.drawColor(Color.BLACK)//背景
        canvas.save()
        canvas.translate(mWidth / 2, mHeight / 2)

        //绘制各元件
        drawCenterInfo(canvas)
        drawHour(canvas, mHourDeg)
        drawMinute(canvas, mMinuteDeg)
        drawSecond(canvas, mSecondDeg)


        //从原点处向右画一条辅助线，之后要处理文字与x轴的对齐问题
        //canvas.drawLine(0f, 0f, mWidth, 0f, mHelperPaint)

        canvas.restore()
    }

    private fun drawCenterInfo(canvas: Canvas) {
        Calendar.getInstance().run {

            //绘制数字时间
            val hour = get(Calendar.HOUR_OF_DAY)
            val minute = get(Calendar.MINUTE)

            mPaint.textSize = mHourR * 0.4f //字体大小根据「时圈」半径来计算
            mPaint.alpha = 225

            mPaint.textAlign = Paint.Align.CENTER
            canvas.drawText("$hour:$minute", 0f, mPaint.getBottomedY(), mPaint)

            //绘制月份、星期
            val month = (this.get(Calendar.MONTH) + 1).let {
                if (it < 10) "0$it" else "$it"
            }
            val day = this.get(Calendar.DAY_OF_MONTH)
            val dayOfWeek = (get(Calendar.DAY_OF_WEEK) - 1).toText()//将Int数字转换为 一、十一、二十等

            mPaint.textSize = mHourR * 0.16f
            mPaint.alpha= 255
            mPaint.textAlign = Paint.Align.CENTER
            canvas.drawText("$month.$day 星期$dayOfWeek", 0f, mPaint.getToppedY(), mPaint)
        }
    }

    /**
     * 绘制小时
     *
     * for循环12次，每次将画布旋转30°乘以i，然后在指定位置绘制文字，12次后刚好一个圆圈
     *
     * @param degrees 初始旋转度数
     */
    private fun drawHour(canvas: Canvas, degrees: Float) {
        mPaint.textSize = mHourR * 0.16f

        //处理整体旋转
        canvas.save()
        canvas.rotate(degrees)

        //从x轴开始旋转，每30°绘制一下「几点」，12次就画完了「时圈」
        for (i in 0 until 12) {
            canvas.save()
            val iDeg = 360 / 12f * i
            canvas.rotate(iDeg)

            //这里处理当前时间点的透明度，因为degrees控制整体逆时针旋转
            //iDeg控制绘制时顺时针，所以两者和为0时，刚好在x正半轴上，也就是起始绘制位置。
            mPaint.alpha = if (iDeg + degrees == 0f) 255 else (0.6f * 255).toInt()
            mPaint.textAlign = Paint.Align.LEFT

            canvas.drawText("${(i+1).toText()}点", mHourR, mPaint.getCenterY(), mPaint)
            canvas.restore()
        }
        canvas.restore()
    }

    private fun drawMinute(canvas: Canvas, degrees: Float) {
        mPaint.textSize = mHourR * 0.16f

        //处理整体旋转
        canvas.save()
        canvas.rotate(degrees)

        for (i in 0 until 60) {
            canvas.save()
            val iDeg = 360 / 60f * i
            canvas.rotate(iDeg)

            mPaint.alpha = if (iDeg + degrees == 0f) 255 else (0.6f * 255).toInt()
            mPaint.textAlign = Paint.Align.RIGHT

            /*if (i < 59) {
                canvas.drawText("${(i+1).toText()}分", mMinuteR, mPaint.getCenterY(), mPaint)
            }*/
            canvas.drawText("${if (i < 59) (i+1).toText() else '零'}分", mMinuteR, mPaint.getCenterY(), mPaint)

            canvas.restore()
        }
        canvas.restore()
    }

    private fun drawSecond(canvas: Canvas, degrees: Float) {
        mPaint.textSize = mHourR * 0.16f

        //处理整体旋转
        canvas.save()
        canvas.rotate(degrees)

        for (i in 0 until 60) {
            canvas.save()
            val iDeg = 360 / 60f * i
            canvas.rotate(iDeg)

            mPaint.alpha = if (iDeg + degrees == 0f) 255 else (0.6f * 255).toInt()
            mPaint.textAlign = Paint.Align.LEFT

            /*if (i < 59) {
                canvas.drawText("${(i+1).toText()}秒", mSecondR, mPaint.getCenterY(), mPaint)
            }*/
            canvas.drawText("${if (i < 59) (i+1).toText() else '零'}秒", mSecondR, mPaint.getCenterY(), mPaint)

            canvas.restore()
        }
        canvas.restore()
    }

    private fun createPaint(colorString: String? = null, color: Int = Color.WHITE): Paint {
        return Paint().apply {
            this.color = if (colorString != null) Color.parseColor(colorString) else color
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }

    fun startTextClock(context: Activity) {
        mTimer = timer(period = 1000) {
            context.runOnUiThread {
                doInvalidate()
            }
        }
    }

    fun closeTextClock() {
        mTimer.cancel()
    }

    /**
     * 停止后续绘制，供动态壁纸使用
     */
    fun stopInvalidate() {
        mAnimator.removeAllUpdateListeners()
    }


    /**
     * 初始化宽高，供动态壁纸使用
     */
    fun initWidthHeight(width: Float, height: Float) {
        if (this.mWidth < 0) {
            this.mWidth = width
            this.mHeight = height

            mHourR = mWidth * 0.143f
            mMinuteR = mWidth * 0.35f
            mSecondR = mWidth * 0.35f
        }
    }

}