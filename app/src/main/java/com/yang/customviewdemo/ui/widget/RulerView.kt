package com.yang.customviewdemo.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.Scroller
import androidx.annotation.RequiresApi
import com.yang.customviewdemo.R
import kotlin.math.abs
import kotlin.math.round


/**
 * Create by Yang Yang on 2023/8/3
 */

@RequiresApi(Build.VERSION_CODES.M)
class RulerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    private var call:((String)->Unit)? = null

    private var mLinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)    //刻度画笔
    private var mTextPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)    //文字画笔

    private var mWidth: Int = 0
    private var mHeight: Int = 0

    //标尺
    private var mMaxValue = 250f           //最大值
    private var mMinValue = 80f            //最小值
    private var mPerValue = 1f             //最小刻度值，最小单位
    private var mLineSpace = 5f            //两条刻度之间的间隔距离

    private var mTotalLine = 0             //计算mMaxValue-mMinValue之间一共有多少条刻度线
    private var mMaxOffset = 0             //所有刻度共有多长 （mTotalLine-1）* mLineSpaceWidth
    private var mOffset = 0f               // 默认状态下，mSelectorValue所在的位置  位于尺子总刻度的位置


    //刻度线
    private var mLineMaxLength = 40f       //三种不同长度(如刻度80cm-250cm)，最长的那根线（80,90,100,...）时的线高度
    private var mLineMidLength = 30f       //中等长度（85,95,105,...）时的线高度
    private var mLineMinLength = 20f       //最短长度（81,82,83,...）时的线高度
    private var mLineWidth = 1f            //刻度线的粗细

    private var mLineColor = context.getColor(R.color.white6)    //刻度线颜色

    private var mSelectorValue = 100.0f                          // 未选择时 默认的值 指针指向的默认值

    //标尺下方文字
    private var mTextColor = context.getColor(R.color.black)       //文字颜色
    private var mTextSize = 35f                                    //文字大小
    private var mTextMarginTop = 10f                               //文字与上方的距离
    private var mTextHeight = 40f                                  //尺子刻度下方数字的高度

    private var mLastX: Int = 0
    private var mMove: Int = 0

    private var mMinVelocity = 0
    private var mScroller: Scroller? = null
    private var mVelocityTracker: VelocityTracker? = null


    fun myFloat(paramFloat: Float) = 0.5f + paramFloat * 1.0f

    init {
        this.mLineSpace = myFloat(mLineSpace)
        this.mLineWidth = myFloat(mLineWidth)
        this.mLineMidLength = myFloat(mLineMidLength)
        this.mLineMinLength = myFloat(mLineMinLength)
        this.mTextHeight = myFloat(mTextHeight)

        val styleable = context.obtainStyledAttributes(attrs, R.styleable.RulerView).apply {

            mMaxValue = getFloat(R.styleable.RulerView_maxValue, mMaxValue)
            mMinValue = getFloat(R.styleable.RulerView_minValue, mMinValue)
            mPerValue = getFloat(R.styleable.RulerView_perValue, mPerValue)
            mLineSpace = getDimension(R.styleable.RulerView_lineSpaceWidth, mLineSpace)
            mSelectorValue = getFloat(R.styleable.RulerView_selectorValue, mSelectorValue)

            mLineMaxLength = getDimension(R.styleable.RulerView_lineMaxHeight, mLineMaxLength)
            mLineMidLength = getDimension(R.styleable.RulerView_lineMidHeight, mLineMidLength)
            mLineMinLength = getDimension(R.styleable.RulerView_lineMinHeight, mLineMinLength)
            mLineWidth = getDimension(R.styleable.RulerView_lineWidth, mLineWidth)

            mLineColor = getColor(R.styleable.RulerView_lineColor, mLineColor)
            mTextColor = getColor(R.styleable.RulerView_textColor, mTextColor)
            mTextSize = getDimension(R.styleable.RulerView_textSize, mTextSize)
            mTextMarginTop = getDimension(R.styleable.RulerView_textMarginTop, mTextMarginTop)

            recycle()
        }

        mScroller = Scroller(context)
        mMinVelocity = ViewConfiguration.get(context).scaledMinimumFlingVelocity / 2
        initPaint()
        setRulerValue(mSelectorValue, mMaxValue, mMinValue, mPerValue)
    }

    private fun initPaint() {
        mTextPaint.color = mTextColor
        mTextPaint.textSize = mTextSize
        mTextPaint.typeface = Typeface.DEFAULT_BOLD

        mLinePaint.color = mLineColor
        mLinePaint.strokeWidth = mLineWidth
    }

    private fun setRulerValue(
        selectorValue: Float,
        maxValue: Float,
        minValue: Float,
        preValue: Float
    ) {

        mSelectorValue = selectorValue
        mMaxValue = maxValue
        mMinValue = minValue
        mPerValue = preValue * 10f
        mTotalLine = ((mMaxValue * 10 - mMinValue * 10) / mPerValue).toInt() + 1
        mMaxOffset = (-(mTotalLine - 1) * mLineSpace).toInt()
        mOffset = (mMinValue - mSelectorValue) / mPerValue * mLineSpace * 10

        invalidate()
        visibility = VISIBLE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            mWidth = w
            mHeight = h
        }
    }

    override fun onDraw(canvas: Canvas?) {
        var left: Float
        var value: String
        var lineHeight: Float
        val srcPointX = mWidth / 2
        super.onDraw(canvas)
        for (i in 0 until mTotalLine) {
            left = srcPointX + mOffset + i * mLineSpace
            if (left < 0 || left > width) {
                continue
            }

            if (i % 10 == 0) {
                lineHeight = mLineMaxLength
                value = (mMinValue + i * mPerValue / 10).toInt().toString()
                mLinePaint.color = context.getColor(R.color.white6)
                canvas?.drawText(value, left - mTextPaint.measureText(value) / 2, lineHeight + mTextMarginTop + mTextHeight, mTextPaint)
            } else if (i % 5 == 0) {
                lineHeight = mLineMidLength
                mLinePaint.color = context.getColor(R.color.white5)
            } else {
                lineHeight = mLineMinLength
                mLinePaint.color = context.getColor(R.color.white5)
            }

            canvas?.drawLine(left, 0f, left, lineHeight, mLinePaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            val xPosition = event.x.toInt()
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain()
            }
            mVelocityTracker?.addMovement(event)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mScroller?.forceFinished(true)
                    mLastX = xPosition
                }
                MotionEvent.ACTION_MOVE -> {
                    mMove = mLastX - xPosition
                    changeMoveAndValue()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                    countMoveEnd()
                    countVelocityTracker()
                    return false
                }
            }
            mLastX = xPosition

            return true
        }

        return super.onTouchEvent(event)
    }

    private fun changeMoveAndValue() {
        mOffset -= mMove
        if (mOffset <= mMaxOffset) {
            mOffset = mMaxOffset.toFloat()
            mMove = 0
            mScroller?.forceFinished(true)
        } else if (mOffset > 0) {
            mOffset = 0f
            mMove = 0
            mScroller?.forceFinished(true)
        }

        mSelectorValue = mMinValue + round(abs(mOffset) * 1f / mLineSpace) * mPerValue / 10f
        call?.invoke(mSelectorValue.toInt().toString())
        postInvalidate()
    }

    /**
     * 动完成后如果指针落在两条刻度中间，则指向靠近的那条指针
     */
    private fun countMoveEnd() {
        mOffset -= mMove
        if (mOffset <= mMaxOffset) {
            mOffset = mMaxOffset.toFloat()
        } else if (mOffset >= 0) {
            mOffset = 0f
        }

        mLastX = 0
        mMove = 0
        mSelectorValue = mMinValue + round(abs(mOffset) * 1f / mLineSpace) * mPerValue / 10f
        mOffset = (mMinValue - mSelectorValue) * 10f / mPerValue * mLineSpace
        call?.invoke(mSelectorValue.toInt().toString())
        postInvalidate()
    }

    private fun countVelocityTracker() {
        mVelocityTracker?.let {
            it.computeCurrentVelocity(1000)
            val velocityX = it.yVelocity
            if (abs(velocityX) > mMinVelocity) {
                mScroller?.fling(0, 0, velocityX.toInt(), 0, Int.MIN_VALUE, Int.MAX_VALUE, 0, 0)
            }
        }
    }

    override fun computeScroll() {
        super.computeScroll()
        mScroller?.let {
            if (it.computeScrollOffset()) {
                if (it.currX == it.finalX) {
                    countMoveEnd()
                } else {
                    val xPosition = it.currX
                    mMove = mLastX - xPosition
                    changeMoveAndValue()
                    mLastX = xPosition
                }
            }
        }
    }

    fun setTextChangedListener(call: (String) -> Unit) {
        this.call = call
    }
}