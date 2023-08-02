package com.yang.customviewdemo.ui.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator


/**
 * Create by Yang Yang on 2023/8/2
 */
class ExperienceBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    //整个View的宽度
    private var mViewWidth = 0F
    //整个View的高度
    private var mViewHeight = 0F
    //内部经验条的宽度
    private var mLineWidth = 0F
    //内部经验条的高度
    private var mLineHeight = 0F
    //内部经验条的左边距
    private var mLineLeft = 0F
    //内部经验条的上边距
    private var mLineTop = 0F
    //经验条的圆角
    private var mRadius = 0F


    //经验条百分比（相对于总进度）
    private var mExperiencePercent = 0F
    //等级圆点的间隔
    private var mPointInterval = 0F

    //当前经验值
    private var mExperience = 0
    //每一等级占总长的百分比
    private var mLevelPercent = 1F
    //当前等级
    private var mCurrentLevel = 0
    //升级所需要的经验列表
    private val mLevelList = mutableListOf<Int>()



    //各种颜色值
    private val mPointColor = Color.parseColor("#E1E1E1")
    private val mLineColor = Color.parseColor("#666666")
    private val mShaderStartColor = Color.parseColor("#18EFE2")
    private val mShaderEndColor = Color.parseColor("#0CF191")
    private val mStrokeColor = Color.parseColor("#323232")

    private val mStrokePaint by lazy {
        Paint().apply {
            color = mStrokeColor
        }
    }
    private val mShaderPaint by lazy {
        Paint().apply {
            color = mShaderStartColor
        }
    }
    private val mLinePaint by lazy {
        Paint().apply {
            color = mLineColor
        }
    }
    private val mLevelAchievedPaint by lazy {
        Paint().apply {
            color = mShaderEndColor
        }
    }
    private val mLevelNotAchievedPaint by lazy {
        Paint().apply {
            color = mPointColor
        }
    }

    //动画相关
    private var mAnimator : ValueAnimator? = null
    //动画时长
    private val mAnimatorDuration = 500L
    //插值器
    private val mInterpolator by lazy { DecelerateInterpolator() }
    //动画值回调
    private val mAnimatorListener by lazy {
        ValueAnimator.AnimatorUpdateListener {
            mExperiencePercent = it.animatedValue as Float
            invalidate()
        }
    }

    /**
     * 经验条宽高比为20：1, 所以View的最终高度就是宽度的1/20加上上下的内边距
     * 经验条内部轨道的高度为边框高度的1/3
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        mViewWidth = (width - paddingStart - paddingEnd).toFloat()
        val height = mViewWidth / 20 + paddingTop + paddingBottom
        mViewHeight = mViewWidth / 20
        mRadius = mViewHeight

        mLineHeight =  mViewHeight / 3
        mLineTop = (mViewHeight - mLineHeight) / 2
        mLineWidth = mViewWidth - mLineTop * 2
        mLineLeft = mLineTop

        setShaderColor()
        computerPointInterval()


        setMeasuredDimension(width, height.toInt())

    }

    /**
     * 设置经验条的渐变色
     */
    private fun setShaderColor() {
        mShaderPaint.shader = LinearGradient(0f, 0f, mLineWidth, 0f, mShaderStartColor, mShaderEndColor, Shader.TileMode.CLAMP)
    }

    /**
     * 计算各个等级点之间的间隔
     */
    private fun computerPointInterval() {
        if (mLineWidth > 0 || mLevelList.isNotEmpty()) {
            mPointInterval = mLineWidth / mLevelList.size
        }
    }

    override fun onDraw(canvas: Canvas) {

        canvas.apply {
            save()

            translate(paddingStart.toFloat(), paddingTop.toFloat())
            drawBackground(canvas)
            drawExperienceBar(canvas)
            drawLevelPoint(canvas)

            restore()
        }

    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawRoundRect(0f, 0f, mViewWidth, mViewHeight, mRadius, mRadius, mStrokePaint)
    }

    private fun drawExperienceBar(canvas: Canvas) {
        canvas.apply {
            save()

            translate(mLineLeft, mLineTop)
            //分两部分绘制，减少过度绘制
            //绘制经验条底部背景
            drawRoundRect((mLineWidth * mExperiencePercent - mLineHeight).coerceAtLeast(0f), 0f, mLineWidth, mLineHeight, mRadius, mRadius, mLinePaint)
            //绘制渐变的经验条
            drawRoundRect(0f, 0f, mLineWidth * mExperiencePercent, mLineHeight, mRadius, mRadius, mShaderPaint)

            restore()
        }
    }

    private fun drawLevelPoint(canvas: Canvas) {
        if (mLevelList.size > 1) {
            canvas.apply {
                save()

                translate(mLineLeft, 0f)
                val cy = mViewHeight / 2
                for (level in 1 until mLevelList.size) {
                    val achieved = mExperiencePercent >= mLevelPercent * level
                    drawCircle(
                        mPointInterval * level, cy,
                        if (achieved) mLineHeight else mLineHeight / 2,
                        if (achieved) mLevelAchievedPaint else mLevelNotAchievedPaint
                    )
                }

                restore()
            }
        }
    }

    private fun computeLevelInfo(): Float {
        if (mLevelList.isNotEmpty()) {
            mCurrentLevel = 0
            for (value in mLevelList) {
                if (mExperience >= value) mCurrentLevel++
                else break
            }
            if (mCurrentLevel < mLevelList.size) {
                mLevelPercent = 1f / mLevelList.size
                val nearestAchieveLevelExperience = if (mCurrentLevel > 0) mLevelList[mCurrentLevel - 1] else 0
                //当前经验与最近已完成等级的经验差占这个阶段的百分比
                val currentExperiencePercent = (mExperience - nearestAchieveLevelExperience) / (mLevelList[mCurrentLevel] - nearestAchieveLevelExperience).toFloat()
                return (mCurrentLevel + currentExperiencePercent) * mLevelPercent
            }
        }
        return 1f
    }

    private fun startAnimator(start: Float, end: Float) {
        mAnimator?.cancel()
        mAnimator = ValueAnimator.ofFloat(start, end).apply {
            duration = mAnimatorDuration
            interpolator = mInterpolator
            addUpdateListener(mAnimatorListener)
            start()
        }
    }

    fun updateExperience(experience: Int) {
        if (mLevelList.isEmpty() || experience == mExperience) {
            return
        }
        mExperience = experience
        startAnimator(mExperiencePercent, computeLevelInfo())
    }

    fun setLevelInfo(experience : Int, list : List<Int>) {
        mExperience = experience
        mLevelList.clear()
        mLevelList.addAll(list)
        computerPointInterval()
        startAnimator(0f, computeLevelInfo())
    }
}