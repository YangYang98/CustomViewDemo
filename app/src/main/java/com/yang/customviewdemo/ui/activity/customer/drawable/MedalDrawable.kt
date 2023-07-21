package com.yang.customviewdemo.ui.activity.customer.drawable

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Paint.FontMetrics
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import com.yang.customviewdemo.R


/**
 * Create by Yang Yang on 2023/7/21
 */
class MedalDrawable(
    private val mContext: Context, resId: Int, private val mText: String
) : Drawable() {

    private val mBitmap: Bitmap by lazy { BitmapFactory.decodeResource(mContext.resources, resId) }
    private lateinit var mPaint: Paint
    private lateinit var bounds: Rect
    private lateinit var fontMetrics: FontMetrics

    var textSize = 20
    var marginLeft = 62 //文字距离左侧边距
    var strokeMarginLeft = 0 //描边文字距离左侧边距

    var mLevelText = "211"
    private lateinit var mLevelPaint: Paint //勋章等级数字
    private lateinit var mStrokePaint: Paint //文字描边
    private lateinit var mLevelFontMetrics: FontMetrics
    private var strokeBaseline = 0F

    init {
        //设置drawable的绘制区域
        setBounds(0, 0, intrinsicWidth, intrinsicHeight)

        initPaint()
        initThirdPaint()
    }

    private fun initPaint() {
        mPaint = Paint()
        bounds = Rect()
        mPaint.apply {
            color = Color.parseColor("#FFFFFF")
            isFakeBoldText = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            getTextBounds(mText, 0, mText.length, bounds)
            textSize = this@MedalDrawable.textSize.toFloat()
        }
        fontMetrics = mPaint.fontMetrics

    }

    private fun initThirdPaint() {
        mLevelPaint = Paint().apply {
            color = Color.parseColor("#FFFFFF")
            isFakeBoldText = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            getTextBounds(mLevelText, 0, mLevelText.length, Rect())
        }

        mStrokePaint = Paint().apply {
            color = Color.parseColor("#bf11e6")
            isFakeBoldText = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            getTextBounds(mLevelText, 0, mLevelText.length, Rect())
            strokeWidth = 4f
            style = Paint.Style.STROKE
        }

        if (mLevelText.length >= 2) {
            mLevelPaint.textSize = mContext.resources.getDimensionPixelSize(R.dimen.sp_6).toFloat()
            mStrokePaint.textSize = mContext.resources.getDimensionPixelSize(R.dimen.sp_6).toFloat()
        } else {
            mLevelPaint.textSize = mContext.resources.getDimensionPixelSize(R.dimen.sp_9).toFloat()
            mStrokePaint.textSize = mContext.resources.getDimensionPixelSize(R.dimen.sp_9).toFloat()
        }

        mLevelFontMetrics = mLevelPaint.fontMetrics
    }

    override fun draw(canvas: Canvas) {
        val baseLine = (intrinsicHeight - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top
        canvas.drawBitmap(mBitmap, 0f, 0f, null)
        canvas.drawText(mText, marginLeft.toFloat(), baseLine, mPaint)

        strokeBaseline = (intrinsicHeight - mLevelFontMetrics.bottom + mLevelFontMetrics.top) / 2 - mLevelFontMetrics.top
        strokeMarginLeft = mContext.resources.getDimensionPixelOffset(R.dimen.dp_5)

        if (mLevelText.length == 1) {
            strokeMarginLeft = mContext.resources.getDimensionPixelOffset(R.dimen.dp_8)
        } else if (mLevelText.length == 2) {
            strokeMarginLeft = mContext.resources.getDimensionPixelOffset(R.dimen.dp_7)
        } else if (mLevelText.length == 3) {
            strokeMarginLeft = mContext.resources.getDimensionPixelOffset(R.dimen.dp_5)
        }
        canvas.drawText(mLevelText, strokeMarginLeft.toFloat(), strokeBaseline, mStrokePaint)
        canvas.drawText(mLevelText, strokeMarginLeft.toFloat(), strokeBaseline, mLevelPaint)
    }

    override fun setAlpha(alpha: Int) {
        
    }

    /**
     * 颜色过滤器，在绘制出来之前，被绘制内容的每一个像素都会被颜色过滤器改变
     */
    override fun setColorFilter(colorFilter: ColorFilter?) {
        
    }

    /**
     * 获得不透明度
     * PixelFormat.OPAQUE：便是完全不透明，遮盖在他下面的所有内容
     *
     * PixelFormat.TRANSPARENT：透明，完全不显示任何东西
     *
     * PixelFormat.TRANSLUCENT：只有绘制的地方才覆盖底下的内容
     */
    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

    /**
     * 获取内部宽度和高度，主要是为了在View使用wrap_content的时候，使用这两个方法返回的尺寸
     */
    override fun getIntrinsicHeight(): Int {
        return mBitmap.height
    }

    override fun getIntrinsicWidth(): Int {
        return mBitmap.width
    }
}