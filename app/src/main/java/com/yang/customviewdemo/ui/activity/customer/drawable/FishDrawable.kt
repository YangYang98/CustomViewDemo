package com.yang.customviewdemo.ui.activity.customer.drawable

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.view.animation.LinearInterpolator
import kotlin.math.cos
import kotlin.math.sin


/**
 * Create by Yang Yang on 2023/7/21
 */
class FishDrawable : Drawable() {

    private val mPath: Path by lazy { Path() }
    private val mPaint: Paint by lazy { Paint() }

    //要想不抽搐，要让下面的sin(xx)走完完整的周期-1～1
    private val valueAnimator: ValueAnimator by lazy { ValueAnimator.ofFloat(0f, 3600f) }

    private var middlePoint: PointF

    private var fishMainAngle = 90F

    private var currentAnimValue = 0f

    companion object {
        private const val OTHER_ALPHA = 110
        private const val BODY_ALPHA = 160

        /**
         * 鱼的长度值
         */
        // 绘制鱼头的半径
        private const val HEAD_RADIUS = 50f
        // 鱼身长度
        private const val BODY_LENGTH = HEAD_RADIUS * 3.2f
        // 寻找鱼鳍起始点坐标的线长
        private const val FIND_FINS_LENGTH = 0.9f * HEAD_RADIUS
        // 鱼鳍的长度
        private const val FINS_LENGTH = 1.3f * HEAD_RADIUS
        // 大圆的半径
        private const val BIG_CIRCLE_RADIUS = 0.7f * HEAD_RADIUS
        // 中圆的半径
        private const val MIDDLE_CIRCLE_RADIUS = 0.6f * BIG_CIRCLE_RADIUS
        // 小圆半径
        private const val SMALL_CIRCLE_RADIUS = 0.4f * MIDDLE_CIRCLE_RADIUS
        // --寻找尾部中圆圆心的线长
        private final const val FIND_MIDDLE_CIRCLE_LENGTH = BIG_CIRCLE_RADIUS * (0.6f + 1)
        // --寻找尾部小圆圆心的线长
        private final const val FIND_SMALL_CIRCLE_LENGTH = MIDDLE_CIRCLE_RADIUS * (0.4f + 2.7f)
        // --寻找大三角形底边中心点的线长
        private final const val FIND_TRIANGLE_LENGTH = MIDDLE_CIRCLE_RADIUS * 2.7f
    }

    init {

        mPaint.apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            isDither = true
            setARGB(OTHER_ALPHA, 244, 92, 71)
        }

        middlePoint = PointF(4.19f * HEAD_RADIUS, 4.19f * HEAD_RADIUS)

        valueAnimator.apply {
            duration = 6000
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                currentAnimValue = it.animatedValue as Float
                invalidateSelf()
            }
            start()
        }
    }

    override fun draw(canvas: Canvas) {
        val fishAngle = (fishMainAngle + sin(Math.toRadians(currentAnimValue * 1.2)) * 10).toFloat()

        //鱼头
        val headPoint = calculatePoint(middlePoint, BODY_LENGTH / 2, fishAngle)
        canvas.drawCircle(headPoint.x, headPoint.y, HEAD_RADIUS, mPaint)

        //右鱼鳍
        val rightFinsPoint = calculatePoint(headPoint, FIND_FINS_LENGTH, fishAngle - 110)
        makeFins(canvas, rightFinsPoint, fishAngle, false)

        //左鱼鳍
        val leftFinsPoint = calculatePoint(headPoint, FIND_FINS_LENGTH, fishAngle + 110)
        makeFins(canvas, leftFinsPoint, fishAngle, true)

        val bodyBottomCenterPoint = calculatePoint(headPoint, BODY_LENGTH, fishAngle - 180)
        // 画节肢1
        val middleCenterPoint = makSegment(canvas, bodyBottomCenterPoint, BIG_CIRCLE_RADIUS, MIDDLE_CIRCLE_RADIUS, FIND_MIDDLE_CIRCLE_LENGTH, fishAngle, true)

        // 画节肢2
        makSegment(canvas, middleCenterPoint, MIDDLE_CIRCLE_RADIUS, SMALL_CIRCLE_RADIUS, FIND_SMALL_CIRCLE_LENGTH, fishAngle, false)

        //尾巴
        makeTail(canvas, middleCenterPoint, FIND_TRIANGLE_LENGTH, BIG_CIRCLE_RADIUS, fishAngle)
        makeTail(canvas, middleCenterPoint, FIND_TRIANGLE_LENGTH - 10, BIG_CIRCLE_RADIUS - 20, fishAngle)

        //身体
        makeBody(canvas, headPoint, bodyBottomCenterPoint, fishAngle)
    }

    private fun makeBody(
        canvas: Canvas,
        headPoint: PointF,
        bodyBottomCenterPoint: PointF,
        fishAngle: Float
    ) {

        val topLeftPoint = calculatePoint(headPoint, HEAD_RADIUS, fishAngle + 90)
        val topRightPoint = calculatePoint(headPoint, HEAD_RADIUS, fishAngle - 90)
        val bottomLeftPoint = calculatePoint(bodyBottomCenterPoint, BIG_CIRCLE_RADIUS, fishAngle + 90)
        val bottomRightPoint = calculatePoint(bodyBottomCenterPoint, BIG_CIRCLE_RADIUS, fishAngle - 90)

        val leftControlPoint = calculatePoint(headPoint, BODY_LENGTH * 0.56f, fishAngle + 130)
        val rightControlPoint =calculatePoint(headPoint, BODY_LENGTH * 0.56f, fishAngle - 130)

        mPath.reset()
        mPath.moveTo(topLeftPoint.x, topLeftPoint.y)
        mPath.quadTo(leftControlPoint.x, leftControlPoint.y, bottomLeftPoint.x, bottomLeftPoint.y)
        mPath.lineTo(bottomRightPoint.x, bottomRightPoint.y)
        mPath.quadTo(rightControlPoint.x, rightControlPoint.y, topRightPoint.x, topRightPoint.y)
        mPaint.alpha = BODY_ALPHA

        canvas.drawPath(mPath, mPaint)
    }

    private fun makeTail(canvas: Canvas, startPont: PointF, findCenterLength: Float, findEdgeLength: Float, fishAngle: Float) {
        val angle = (fishAngle + sin(Math.toRadians(currentAnimValue * 1.5)) * 20).toFloat()

        val centerPoint = calculatePoint(startPont, findCenterLength, angle - 180)

        val leftPoint = calculatePoint(centerPoint, findEdgeLength, angle + 90)
        val rightPoint = calculatePoint(centerPoint, findEdgeLength, angle - 90)

        mPath.reset()
        mPath.moveTo(startPont.x, startPont.y)
        mPath.lineTo(leftPoint.x, leftPoint.y)
        mPath.lineTo(rightPoint.x, rightPoint.y)
        canvas.drawPath(mPath, mPaint)

    }

    private fun makSegment(
        canvas: Canvas, bottomCenterPoint: PointF, bigRadius: Float,
        smallRadius: Float, findSmallCircleLength: Float, fishAngle: Float,
        hasBigCircle: Boolean
    ): PointF {
        val segmentAngle = if (hasBigCircle) {
            (fishAngle + cos(Math.toRadians(currentAnimValue * 1.5)) * 15).toFloat()
        } else {
            (fishAngle + sin(Math.toRadians(currentAnimValue * 1.5)) * 20).toFloat()
        }

        //val segmentAngle = (fishAngle + sin(Math.toRadians(currentAnimValue * 1.3)) * 20).toFloat()

        val upperCenterPoint = calculatePoint(bottomCenterPoint, findSmallCircleLength, segmentAngle - 180)

        val bottomLeftPoint = calculatePoint(bottomCenterPoint, bigRadius, segmentAngle + 90)
        val bottomRightPoint = calculatePoint(bottomCenterPoint, bigRadius, segmentAngle - 90)
        val topLeftPoint = calculatePoint(upperCenterPoint, smallRadius, segmentAngle + 90)
        val topRightPoint = calculatePoint(upperCenterPoint, smallRadius, segmentAngle - 90)

        if (hasBigCircle) {
            canvas.drawCircle(bottomCenterPoint.x, bottomCenterPoint.y, bigRadius, mPaint)
        }
        canvas.drawCircle(upperCenterPoint.x, upperCenterPoint.y, smallRadius, mPaint)

        mPath.reset()
        mPath.moveTo(topLeftPoint.x, topLeftPoint.y)
        mPath.lineTo(topRightPoint.x, topRightPoint.y)
        mPath.lineTo(bottomRightPoint.x, bottomRightPoint.y)
        mPath.lineTo(bottomLeftPoint.x, bottomLeftPoint.y)
        canvas.drawPath(mPath, mPaint)

        return upperCenterPoint
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.setColorFilter(colorFilter)
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }


    /**
     * 鱼要可以围绕重心旋转,8.38计算过程:身长6.79减去头顶到中部位置的长度2.6再乘以2
     *
     */
    override fun getIntrinsicHeight(): Int {
        return (8.38f * HEAD_RADIUS).toInt()
    }

    override fun getIntrinsicWidth(): Int {
        return (8.38f * HEAD_RADIUS).toInt()
    }

    private fun calculatePoint(startPoint: PointF, length: Float, angle: Float): PointF {
        val deltaX: Float = (cos(Math.toRadians(angle.toDouble())) * length).toFloat()
        //angle.toDouble() - 180是因为Android坐标系和数学坐标系不同
        val deltaY: Float = (sin(Math.toRadians((angle - 180).toDouble())) * length).toFloat()

        return PointF(startPoint.x + deltaX, startPoint.y + deltaY)
    }

    private fun makeFins(canvas: Canvas, startPoint: PointF, fishAngle: Float, isLeft: Boolean) {
        val controlAngle = 115
        val endPoint = calculatePoint(startPoint, FINS_LENGTH, fishAngle - 180)

        val controlPoint = calculatePoint(startPoint, FINS_LENGTH * 1.8f, fishAngle + if (isLeft) controlAngle else (-controlAngle))

        mPath.reset()
        mPath.moveTo(startPoint.x, startPoint.y)
        mPath.quadTo(controlPoint.x, controlPoint.y, endPoint.x, endPoint.y)

        canvas.drawPath(mPath, mPaint)
    }

}