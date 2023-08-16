package com.yang.customviewdemo.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Region
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Scroller
import com.yang.customviewdemo.R
import kotlin.math.abs
import kotlin.math.hypot
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

    private var style: String = ""

    private val scroller: Scroller by lazy { Scroller(context, LinearInterpolator()) }

    //绘制内容
    private val textPaint: Paint by lazy { Paint() }
    private val pathCContentPaint: Paint by lazy { Paint() }

    companion object {
        const val STYLE_LEFT = "STYLE_LEFT"
        const val STYLE_RIGHT = "STYLE_RIGHT"
        const val STYLE_MIDDLE = "STYLE_MIDDLE"
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
            color = resources.getColor(R.color.softGreen)
            isAntiAlias = true
        }

        pathBPaint.apply {
            color = resources.getColor(R.color.softBlue)
            isAntiAlias = true

            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_ATOP)
        }

        pathCPaint.apply {
            color = resources.getColor(R.color.softYellow)
            isAntiAlias = true

            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_ATOP)
        }

        textPaint.apply {
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
            isSubpixelText = true//设置自像素。如果该项为true，将有助于文本在LCD屏幕上的显示效果。
            textSize = 30f
        }

        pathCContentPaint.apply {
            color = resources.getColor(R.color.softYellow)
            isAntiAlias = true
        }
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
                //drawPath(getDefaultPath(), pathAPaint)
                drawPathAContent(this, getDefaultPath(), pathAPaint)
            } else {
                if (f.x == viewWidth && f.y == 0f) {
                    //drawPath(getPathAFromRightTop(), pathAPaint)
                    drawPathAContent(this, getPathAFromRightTop(), pathAPaint)
                    drawPath(getRealPathC(), pathCPaint)
                    drawPathCContent(this, getPathAFromRightTop(), pathCContentPaint)
                    drawPathBContent(this, getPathAFromRightTop(), pathBPaint)
                } else if (f.x == viewWidth && f.y == viewHeight) {
                    //drawPath(getPathAFromRightBottom(), pathAPaint)
                    drawPathAContent(this, getPathAFromRightBottom(), pathAPaint)
                    drawPath(getRealPathC(), pathCPaint)
                    drawPathCContent(this, getPathAFromRightBottom(), pathCContentPaint)
                    drawPathBContent(this, getPathAFromRightBottom(), pathBPaint)
                }
                //drawPath(getRealPathC(), pathCPaint)
                //drawPath(getRealPathB(), pathBPaint)
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

    private fun drawPathAContent(canvas: Canvas, pathA: Path, pathPaint: Paint) {
        val contentBitmap = Bitmap.createBitmap(viewWidth.toInt(), viewHeight.toInt(), Bitmap.Config.ARGB_8888)
        val contentCanvas = Canvas(contentBitmap)

        contentCanvas.apply {
            drawPath(pathA, pathPaint)
            drawText("AAAAAA区域A内容aaaaaa", viewWidth, viewHeight - 200, textPaint)
        }
        canvas.apply {
            save()
            clipPath(pathA, Region.Op.INTERSECT)
            drawBitmap(contentBitmap, 0f, 0f, null)
            restore()
        }
    }

    /**
     * B区域内容取的是B区域不同于AC区域全集的部分
     */
    private fun drawPathBContent(canvas: Canvas, pathA: Path, pathPaint: Paint) {
        val contentBitmap = Bitmap.createBitmap(viewWidth.toInt(), viewHeight.toInt(), Bitmap.Config.ARGB_8888)
        val contentCanvas = Canvas(contentBitmap)

        contentCanvas.apply {
            drawPath(getRealPathB(), pathPaint)
            drawText("BBBBBB区域B内容BBBBBB", viewWidth - 300, viewHeight - 200, textPaint)
        }

        canvas.apply {
            save()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val pathC = getRealPathC()
                pathC.op(pathA, Path.Op.UNION)//此时pathC就是PathA和PathC的合集
                val pathB = getRealPathB()
                pathB.op(pathC, Path.Op.XOR)//创建一个PathB和(PathA和PathC合集)的Op.XOR,此时pathB就是PathB不同于(PathA和PathC合集)的部分
                clipPath(pathB)
            } else {
                clipPath(pathA)//裁剪出A区域
                clipPath(getRealPathC(), Region.Op.UNION)//裁剪出A和C区域的全集
                clipPath(getRealPathB(), Region.Op.REVERSE_DIFFERENCE) //裁剪出B区域中不同于与AC区域的部分
            }
            drawBitmap(contentBitmap, 0f, 0f, null)
            restore()
        }
    }

    private fun drawPathCContent(canvas: Canvas, pathA: Path, pathPaint: Paint) {
        val contentBitmap = Bitmap.createBitmap(viewWidth.toInt(), viewHeight.toInt(), Bitmap.Config.ARGB_8888)
        val contentCanvas = Canvas(contentBitmap)

        contentCanvas.apply {
            drawPath(getRealPathB(), pathPaint)
            drawText("AAAAAA区域A内容aaaaaa", viewWidth, viewHeight - 200, textPaint)
        }

        canvas.apply {
            save()

            val pathC = getRealPathC()
            //pathC.op(pathA, Path.Op.XOR)//裁剪出C区域不同于A区域的部分
            pathC.op(pathA, Path.Op.UNION)//裁剪出A和C区域的全集
            clipPath(pathC)

            val eh = hypot((f.x - e.x).toDouble(), (f.y - h.y).toDouble())//sqrt(x2+y2)
            val sin0 = ((f.x - e.x) / eh).toFloat()
            val cos0 = ((h.y - f.y) / eh).toFloat()

            //设置翻转和旋转矩阵
            val matrixArray = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1.0f).apply {
                this[0] = -(1 - 2 * sin0 * sin0)
                this[1] = 2 * sin0 * cos0
                this[3] = 2 * sin0 * cos0
                this[4] = 1 - 2 * sin0 * sin0
            }

            val matrix = Matrix().apply {
                reset()
                setValues(matrixArray)
                preTranslate(-e.x, -e.y)//沿当前XY轴负方向位移得到
                postTranslate(e.x, e.y)//沿原XY轴方向位移得到
            }

            drawBitmap(contentBitmap, matrix, null)

            restore()
        }
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

    private fun calcPointCX(tempA: PointF, tempF: PointF): Float {
        val tempG = PointF((tempA.x + tempF.x) / 2, (tempA.y + tempF.y) / 2)
        val tempE = PointF(
            tempG.x - (tempF.y - tempG.y) * (tempF.y - tempG.y) / (tempF.x - tempG.x),
            tempF.y
            )

        return tempE.x - (tempF.x - tempE.x) / 2
    }

    private fun calcPointAByTouchPoint() {
        val w0 = viewWidth - c.x
        val w1 = abs(f.x - a.x)
        val w2 = viewWidth * w1 / w0
        a.x = abs(f.x - w2)

        val h1 = abs(f.y - a.y)
        val h2 = h1 * w2 / w1
        a.y = abs(f.y - h2)
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

    fun setTouchPoint(x: Float, y: Float, style: String = "") {
        a.x = x
        a.y = y
        val touchPoint = PointF()
        this.style = style

        when (style) {
            STYLE_LEFT, STYLE_RIGHT -> {
                a.y = viewHeight - 1
                f.x = viewWidth
                f.y = viewHeight
                calcPointsXY(a, f)
            }
            STYLE_MIDDLE -> {

            }
            STYLE_TOP_RIGHT -> {
                f.x = viewWidth
                f.y = 0f
                calcPointsXY(a, f)
                touchPoint.set(x, y)
                if (calcPointCX(touchPoint, f) < 0) {
                    calcPointAByTouchPoint()
                    calcPointsXY(a, f)
                }
            }
            STYLE_BOTTOM_RIGHT -> {
                f.x = viewWidth
                f.y = viewHeight
                touchPoint.set(x, y)
                calcPointsXY(a, f)
                if (calcPointCX(touchPoint, f) < 0) {
                    calcPointAByTouchPoint()
                    calcPointsXY(a, f)
                }
            }
        }

        postInvalidate()
    }

    fun reset() {
        a.x = -1f
        a.y = -1f
        postInvalidate()
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            val x = (scroller.currX).toFloat()
            val y = (scroller.currY).toFloat()

            if (style == STYLE_TOP_RIGHT) {
                setTouchPoint(x, y, style)
            } else {
                setTouchPoint(x, y, style)
            }

            if (scroller.finalX == x.toInt() && scroller.finalY == y.toInt()) {
                reset()
            }
        }
    }

    fun startCancelAnim() {
        val dx: Int
        val dy: Int

        if (style == STYLE_TOP_RIGHT) {
            dx = (viewWidth - 1 - a.x).toInt()
            dy = (1 - a.y).toInt()
        } else {
            dx = (viewWidth - 1 - a.x).toInt()
            dy = (viewHeight - 1 - a.y).toInt()
        }

        scroller.startScroll(a.x.toInt(), a.y.toInt(), dx, dy, 200)
    }
}