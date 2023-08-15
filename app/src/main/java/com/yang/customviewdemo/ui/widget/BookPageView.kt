package com.yang.customviewdemo.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.View
import kotlin.math.min


/**
 * Create by Yang Yang on 2023/8/14
 */
class BookPageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    private val pointPaint: Paint by lazy { Paint() }
    private val bgPaint: Paint by lazy { Paint() }

    private val a = PointF()
    private val f = PointF()
    private val g = PointF()
    private val e = PointF()
    private val h = PointF()
    private val c = PointF()
    private val j = PointF()
    private var b = PointF()
    private var k = PointF()
    private val d = PointF()
    private val i = PointF()

    private val defaultWidth = 600f
    private val defaultHeight = 1000f
    var viewWidth = defaultWidth
    var viewHeight = defaultHeight

    private val pathAPaint: Paint by lazy { Paint() }
    private val pathA: Path by lazy { Path() }
    private lateinit var cacheBitmap: Bitmap
    private lateinit var bitmapCanvas: Canvas

    private val pathBPaint: Paint by lazy { Paint() }
    private val pathB: Path by lazy { Path() }

    private val pathCPaint: Paint by lazy { Paint() }
    private val pathC: Path by lazy { Path() }

    companion object {
        const val STYLE_TOP_RIGHT = "STYLE_TOP_RIGHT"
        const val STYLE_BOTTOM_RIGHT = "STYLE_BOTTOM_RIGHT"
    }

    init {

        pointPaint.apply {
            color = Color.RED
            textSize = 25f
        }

        bgPaint.apply {
            color = Color.GREEN
        }

        pathAPaint.apply {
            color = Color.GREEN
            isAntiAlias = true
        }

        pathBPaint.apply {
            color = Color.BLUE
            isAntiAlias = true

            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_ATOP)
        }

        pathCPaint.apply {
            color = Color.YELLOW
            isAntiAlias = true

            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_ATOP)
        }

        //cacheBitmap = Bitmap.createBitmap(viewWidth.toInt(), viewHeight.toInt(), Bitmap.Config.ARGB_8888)
        //bitmapCanvas = Canvas(cacheBitmap)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val height = measureSize(defaultHeight, heightMeasureSpec)
        val width = measureSize(defaultWidth, widthMeasureSpec)
        setMeasuredDimension(width, height)

        viewWidth = width.toFloat()
        viewHeight = height.toFloat()

        a.set(-1f, -1f)
    }

    private fun measureSize(defaultSize: Float, measureSpec: Int): Int {
        var result = defaultSize.toInt()
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = min(result, specSize)
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //TODO 待优化
        cacheBitmap = Bitmap.createBitmap(viewWidth.toInt(), viewHeight.toInt(), Bitmap.Config.ARGB_8888)
        bitmapCanvas = Canvas(cacheBitmap)

        //canvas.drawRect(0f, 0f, viewWidth, viewHeight, bgPaint)
        bitmapCanvas.apply {

            if (a.x == -1f && a.y == -1f) {
                drawPath(getDefaultPath(), pathAPaint)
            } else {
                if (f.x == viewWidth && f.y == 0f) {
                    drawPath(getPathAFromRightTop(), pathAPaint)
                } else if (f.x == viewWidth && f.y == viewHeight) {
                    drawPath(getPathAFromRightBottom(), pathAPaint)
                }
                drawPath(getRealPathC(), pathCPaint)
                drawPath(getRealPathB(), pathBPaint)
            }
        }

        canvas.drawBitmap(cacheBitmap, 0f, 0f, null)

        canvas.drawText("a",a.x,a.y,pointPaint)
        canvas.drawText("f",f.x,f.y,pointPaint)
        canvas.drawText("g",g.x,g.y,pointPaint)

        canvas.drawText("e",e.x,e.y,pointPaint)
        canvas.drawText("h",h.x,h.y,pointPaint)

        canvas.drawText("c",c.x,c.y,pointPaint)
        canvas.drawText("j",j.x,j.y,pointPaint)

        canvas.drawText("b",b.x,b.y,pointPaint)
        canvas.drawText("k",k.x,k.y,pointPaint)

        canvas.drawText("d",d.x,d.y,pointPaint)
        canvas.drawText("i",i.x,i.y,pointPaint)

    }

    private fun calcPointsXY(a: PointF, f: PointF) {
        g.x = (a.x + f.x) / 2
        g.y = (a.y + f.y) / 2

        e.x = g.x - (f.y - g.y) * (f.y - g.y) / (f.x - g.x)
        e.y = f.y

        h.x = f.x
        h.y = g.y - (f.x - g.x) * (f.x - g.x) / (f.y - g.y)

        c.x = e.x - (f.x - e.x) / 2
        c.y = f.y

        j.x = f.x
        j.y = h.y - (f.y - h.y) / 2

        b = getIntersectionPoint(a,e,c,j)
        k = getIntersectionPoint(a,h,c,j)

        //bc的中点与e的中点
        d.x = ((c.x + b.x) / 2 + e.x) / 2
        d.y = ((c.y + b.y) / 2 + e.y) / 2

        //jk的中点与h的中点
        i.x = ((j.x + k.x) / 2 + h.x) / 2
        i.y = ((j.y + k.y) / 2 + h.y) / 2
    }

    private fun getIntersectionPoint(
        lineOne_pointOne: PointF, lineOne_pointTwo: PointF,
        lineTwo_pointOne: PointF, lineTwo_pointTwo: PointF
    ): PointF {
        val x1 = lineOne_pointOne.x
        val y1 = lineOne_pointOne.y
        val x2 = lineOne_pointTwo.x
        val y2 = lineOne_pointTwo.y
        val x3 = lineTwo_pointOne.x
        val y3 = lineTwo_pointOne.y
        val x4 = lineTwo_pointTwo.x
        val y4 = lineTwo_pointTwo.y

        val pointX = ((x1 - x2) * (x3 * y4 - x4 * y3) - (x3 - x4) * (x1 * y2 - x2 * y1)) / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4))

        val pointY = ((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1) * (y3 - y4)) / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4))

        return PointF(pointX, pointY)
    }

    private fun getDefaultPath(): Path {
        pathA.apply {
            reset()
            lineTo(0f, viewHeight)
            lineTo(viewWidth, viewHeight)
            lineTo(viewWidth, 0f)
            close()
        }

        return pathA
    }

    /**
     * 获取f点在右下角的pathA
     */
    private fun getPathAFromRightBottom(): Path {
        pathA.apply {
            reset()
            lineTo(0f, viewHeight)
            lineTo(c.x, c.y)
            quadTo(e.x, e.y, b.x, b.y)
            lineTo(a.x, a.y)
            lineTo(k.x, k.y)
            quadTo(h.x, h.y, j.x, j.y)
            lineTo(viewWidth, 0f)
            close()
        }

        return pathA
    }

    /**
     * 获取f点在右上角的pathA
     */
    private fun getPathAFromRightTop(): Path {
        pathA.apply {
            reset()
            lineTo(c.x, c.y)
            quadTo(e.x, e.y, b.x, b.y)
            lineTo(a.x, a.y)
            lineTo(k.x, k.y)
            quadTo(h.x, h.y, j.x, j.y)
            lineTo(viewWidth, viewHeight)
            lineTo(0f, viewHeight)
            close()
        }

        return pathA
    }

    private fun getRealPathB(): Path {
        pathB.apply {
            reset()
            lineTo(0f, viewHeight)
            lineTo(viewWidth, viewHeight)
            lineTo(viewWidth, 0f)
            close()
        }

        return pathB
    }

    private fun getRealPathC(): Path {
        pathC.apply {
            reset()
            moveTo(i.x, i.y)
            lineTo(d.x, d.y)
            lineTo(b.x, b.y)
            lineTo(a.x, a.y)
            lineTo(k.x, k.y)
            close()
        }

        return pathC
    }

    fun setTouchPoint(x: Float, y: Float, style: String? = null) {
        when (style) {
            STYLE_TOP_RIGHT -> {
                f.x = viewWidth
                f.y = 0f
            }
            STYLE_BOTTOM_RIGHT -> {
                f.x = viewWidth
                f.y = viewHeight
            }
        }
        a.x = x
        a.y = y
        calcPointsXY(a, f)
        postInvalidate()
    }

    fun reset() {
        a.x = -1f
        a.y = -1f
        postInvalidate()
    }


}