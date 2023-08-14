package com.yang.customviewdemo.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import com.yang.customviewdemo.R
import com.yang.customviewdemo.utils.dp
import com.yang.customviewdemo.utils.px
import kotlin.math.ceil


/**
 * Create by Yang Yang on 2023/8/11
 */
class WaveProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    private val mWavePaint: Paint by lazy { Paint() }
    private val mWavePath: Path by lazy { Path() }
    private val mSecondWavePaint: Paint by lazy { Paint() }

    private var waveWidth = 25f.px
    private var waveHeight = 10f.px
    private var waveColor = Color.BLUE
    private var secondWaveColor = Color.BLUE
    private var bgColor = Color.GRAY
    private val defaultSize = 200.dp
    private val maxHeight = 250f.px
    private var waveNum: Int = (ceil(defaultSize / waveWidth / 2.0)).toInt()


    private val waveProgressAnim: WaveProgressAnim by lazy { WaveProgressAnim() }
    private var percent: Float = 0f
    var maxNum = 100
    private var progressNum = 0f

    private var waveStartPositionX = 0f

    private var cacheBitmap: Bitmap? = null
    private var bitmapCanvas: Canvas? = null

    private val circlePaint: Paint by lazy { Paint().apply {
        color = Color.GRAY
        isAntiAlias = true
    } }

    private var viewRealSize = defaultSize
        set(value) {
            field = value

            cacheBitmap = Bitmap.createBitmap(viewRealSize, viewRealSize, Bitmap.Config.ARGB_8888)
            if (cacheBitmap != null) {
                bitmapCanvas = Canvas(cacheBitmap!!)
            }
        }

    var onAnimationListener: OnAnimationListener? = null

    init {

        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.WaveProgressView).apply {
            waveWidth = getDimension(R.styleable.WaveProgressView_wave_width, 25f.px)
            waveHeight = getDimension(R.styleable.WaveProgressView_wave_height, 10f.px)
            waveColor = getColor(R.styleable.WaveProgressView_wave_color, Color.BLUE)
            secondWaveColor = getColor(R.styleable.WaveProgressView_wave_second_color, Color.WHITE)
            bgColor = getColor(R.styleable.WaveProgressView_bg_color, Color.GRAY)

            recycle()
        }

        mWavePaint.apply {
            color = waveColor
            isAntiAlias = true
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        }

        mSecondWavePaint.apply {
            color = secondWaveColor
            isAntiAlias = true
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
        }

        circlePaint.color = bgColor

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val height = measureSize(defaultSize, heightMeasureSpec)
        val width = measureSize(defaultSize, widthMeasureSpec)
        val minLength = height.coerceAtMost(width)
        setMeasuredDimension(height, width)
        viewRealSize = minLength
        waveNum = (ceil(viewRealSize / waveWidth / 2.0)).toInt()

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //使用缓存
        bitmapCanvas?.let {
            it.drawCircle(viewRealSize / 2f, viewRealSize / 2f, viewRealSize / 2f, circlePaint)
            it.drawPath(getWavePath(), mWavePaint)
            it.drawPath(getSecondWavePath(), mSecondWavePaint)
        }

        if (cacheBitmap != null) {
            canvas.drawBitmap(cacheBitmap!!, 0f, 0f, null)
        }
    }

    private fun getWavePath(): Path {
        mWavePath.apply {
            reset()
            val pathHeight = (1 - percent) * viewRealSize
            val changeWaveHeight = onAnimationListener?.howToChangeWaveHeight(percent, waveHeight)
            val realWaveHeight = if (changeWaveHeight != null && changeWaveHeight != 0f) {
                changeWaveHeight
            } else {
                waveHeight
            }

            moveTo(-waveStartPositionX, pathHeight)//起始点移动至(0,waveHeight)

            for (i in 0 until waveNum * 2) {
                rQuadTo(waveWidth / 2, realWaveHeight, waveWidth, 0f)
                rQuadTo(waveWidth / 2, -realWaveHeight, waveWidth, 0f)
            }

            lineTo(viewRealSize.toFloat(), viewRealSize.toFloat())
            lineTo(0f, viewRealSize.toFloat())
            lineTo(0f, pathHeight)

            close()
        }

        return mWavePath
    }

    private fun getSecondWavePath(): Path {
        mWavePath.apply {
            reset()
            val pathHeight = (1 - percent) * viewRealSize
            val changeWaveHeight = onAnimationListener?.howToChangeWaveHeight(percent, waveHeight)
            val realWaveHeight = if (changeWaveHeight != null && changeWaveHeight != 0f) {
                changeWaveHeight
            } else {
                waveHeight
            }

            moveTo(0f, pathHeight)
            lineTo(0f, viewRealSize.toFloat())
            lineTo(viewRealSize.toFloat(), viewRealSize.toFloat())
            lineTo(viewRealSize + waveStartPositionX, pathHeight)

            for (i in 0 until waveNum * 2) {
                rQuadTo(-waveWidth / 2, realWaveHeight, -waveWidth, 0f)
                rQuadTo(-waveWidth / 2, -realWaveHeight, -waveWidth, 0f)
            }

            close()
        }

        return mWavePath
    }

    private fun measureSize(defaultSize: Int, measureSpec: Int): Int {
        var result = defaultSize

        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = result.coerceAtMost(specSize)
        }

        return result
    }

    fun setProgressNum(progressNum: Float, duration: Int) {
        this.progressNum = progressNum

        percent = 0f
        waveProgressAnim.apply {
            this.duration = duration.toLong()
            repeatCount = Animation.INFINITE
            interpolator = LinearInterpolator()
        }
        this.startAnimation(waveProgressAnim)
    }

    inner class WaveProgressAnim: Animation() {

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            super.applyTransformation(interpolatedTime, t)
            if (percent < progressNum / maxNum) {
                percent = interpolatedTime * progressNum / maxNum
            }
            waveStartPositionX = interpolatedTime * viewRealSize
            postInvalidate()
        }
    }

    interface OnAnimationListener {

        fun howToChangeWaveHeight(percent: Float, defaultHeight: Float): Float
    }
}