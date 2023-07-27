package com.yang.customviewdemo.ui.activity.customer.drawable

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.CornerPathEffect
import android.graphics.MaskFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PixelFormat
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.View.OnLayoutChangeListener
import com.yang.customviewdemo.utils.ScaleHelper
import java.util.Random
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin


/**
 * Create by Yang Yang on 2023/7/25
 *
 * 拉弓：通过Path的offset实现，
 *
 * 移动的元素定义三样东西：--已经开始了的时间--、--动画时长--、--要移动的距离--。偏移的距离 = （开始了的时间/动画时长）* 要移动的距离
 */
class ArrowDrawable(
    private var width: Int,
    private var height: Int,
    private var bowLength: Float
) : Drawable() {

    private val mPaint: Paint by lazy { Paint() }
    private val mPaintHelper: Paint by lazy { Paint() }


    private val mTempPoint = PointF()

    //弓
    private val mBowPath: Path by lazy { Path() }
    private val mBowPathMeasure: PathMeasure by lazy { PathMeasure() }
    private var mBowPathPoints: FloatArray = floatArrayOf()
    private val mScaleHelper: ScaleHelper by lazy { ScaleHelper(
        .2F, 0F,//起点处缩至20%
        1F, .05F,//5%处恢复正常
        2F, .5F,//50%处放大到200%
        1F, .95F,//95%处又恢复正常
        .2F, 1F//最后缩放到20%
    ) }

    //弓上得把手
    private val mHandlePath: Path by lazy { Path() }

    //弦
    private val mStringStartPoint: PointF by lazy { PointF() }
    private val mStringMiddlePoint: PointF by lazy { PointF() }
    private val mStringEndPoint: PointF by lazy { PointF() }
    private var mStringWidth: Float = 5f
    private val mStringColor: Int = Color.WHITE


    var mBowLength = BOW_LENGTH
    private var mBowWidth = 10f
    private var mHandleWidth = 20f

    private var mCenterX = 0f
    private var mCenterY = 0f

    //箭
    private val mArrowPath: Path by lazy { Path() }
    private var mArrowBodyLength = mBowLength / 2
    private var mArrowBodyWidth = mArrowBodyLength / 70
    private var mFinHeight = mArrowBodyLength / 6
    private var mFinWidth = mFinHeight / 3
    //箭羽倾斜高度 = 箭羽宽度
    private var mFinSlopeHeight = mFinWidth
    //箭嘴宽度 = 箭羽宽度
    private var mArrowWidth = mFinWidth
    //箭嘴高度 取 箭杆长度的 1/8
    private var mArrowHeight = mArrowBodyLength / 8

    var progress: Float = 0f
        set(value) {
            if (mState == STATE_NORMAL || mState == STATE_DRAGGING) {
                mState = STATE_DRAGGING
                field = if (progress > 1) 1F else if (progress < 0) 0F else value
                //每一次进度的更新，draw方法都会被回调
                invalidateSelf()
            }

        }
    var mBaseAngle: Float = 25F
    //弓可以从mBaseAngle最大弯曲多少度
    var mUsableAngle: Float = 20F

    private var mBaseBowOffset: Float = 0F
    private var mMaxBowOffset: Float = 0F
    private var mStringOffset: Float = 0F
    private var mMaxStringOffset: Float = 0F
    private var mArrowOffset: Float = 0F

    private var mState = STATE_NORMAL

    private var mFireTime: Long = 0L
    var mFiringBowFallDuration = 100L
    private var mFiringBowOffsetDistance = 0f
    private var mFiredArrowShrinkStartTime = 0L
    var mFiredArrowShrinkDuration = 200L
    private var mFiredArrowShrinkDistance = 0f
    private var mFiredArrowMoveStartTime = 0L
    var mFiredArrowMoveDuration = 200L
    private var mFiredArrowMoveDistance = 0f
    private var mFiredArrowLastMoveDistance = 0f

    private val mArrowTail: RectF by lazy { RectF() }
    private var mTailMaskFilter: MaskFilter? = null

    //发射中坠落的线条
    private var mLines = MutableList(6) { Line() }
    private var mBaseLinesFallDuration = 200
    private val mRandom: Random by lazy { Random() }

    //hit
    private var mHitStartTime = 0L
    private var mHitDuration = 200L
    private var mHitDistance = 0f

    private var mSkewStartTime = 0L
    private var mSkewDuration = 50L
    private var mCurrentSkewCount = 0
    private var mSkewTan = .035F //命中后左右摆动的幅度(正切值)(.035F约等于2度)
    var mMaxSkewCount = 8
        set(value) {
            if (value >= 0) {
                field = value
            }
        }

    init {
        mPaint.apply {
            isAntiAlias = true
            isDither = true
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            color = Color.YELLOW
            strokeWidth = 10f

        }
        mPaintHelper.apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            isDither = true
            color = Color.GREEN
            strokeWidth = 15f
        }

        updateSize(width, height, bowLength)
    }

    companion object {
        private const val BOW_LENGTH = 1000f
        private const val STATE_NORMAL = 0
        private const val STATE_DRAGGING = 1
        private const val STATE_FIRING = 2 //射箭中
        private const val STATE_HITTING = 3

        @JvmStatic
        fun create(targetView: View): ArrowDrawable {
            targetView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

            val bowLength = targetView.width * 0.4f

            val drawable: ArrowDrawable
            targetView.run {
                drawable = ArrowDrawable(width, height, bowLength)
                //无效宽高，等待布局完成后更新一次尺寸
                if (width == 0 || height == 0) {
                    addOnLayoutChangeListener(object : OnLayoutChangeListener {
                        override fun onLayoutChange(
                            v: View?,
                            left: Int,
                            top: Int,
                            right: Int,
                            bottom: Int,
                            oldLeft: Int,
                            oldTop: Int,
                            oldRight: Int,
                            oldBottom: Int
                        ) {
                            if (width > 0 || height == 0) {
                                val bowRealLength = targetView.width * 0.4f
                                drawable.updateSize(width, height, bowRealLength)
                                removeOnLayoutChangeListener(this)
                            }
                        }
                    })
                }
            }

            return drawable
        }
    }

    override fun draw(canvas: Canvas) {

        //testDrawSkewLine(canvas)

        when (mState) {
            STATE_FIRING -> {
                handleFiringState(canvas)
            }
            STATE_HITTING -> {
                handleHittingState(canvas)
            }
            else -> {
                updateBowPath(getAngleByProgress())
                drawBowPath(canvas)

                updateHandlePath()
                drawHandlePath(canvas)

                updateStringPoints(true)
                drawString(canvas)


                updateArrowOffset()
                drawArrow(canvas)
            }
        }
    }

    fun fire() {
        if (progress >= .95F && mState == STATE_DRAGGING) {
            mState = STATE_FIRING
            mFiredArrowShrinkStartTime = 0

            mFiredArrowMoveStartTime = 0
            mFiredArrowLastMoveDistance = 0f
            //第一次要向上移动，所以是负数
            mFiredArrowMoveDistance = -abs(mFiredArrowMoveDistance)
            mFireTime = SystemClock.uptimeMillis()

            mLines.forEach { initLines(it) }
            invalidateSelf()
        }
    }

    fun hit() {
        if (mState == STATE_FIRING && mFiredArrowMoveStartTime > 0) {
            mState = STATE_HITTING

            mHitStartTime = SystemClock.uptimeMillis()
            var currentArrowOffset = mArrowOffset + mFiredArrowLastMoveDistance
            if (mFiredArrowMoveDistance > 0) {
                //如果距离是正数，证明上一次是向上偏移的，这次该向下偏移了，因为向上偏移是负数，所以应该减去这个距离
                currentArrowOffset -= mFiredArrowMoveDistance
            }

            val arrowBodyHeight = mFinHeight + mFinSlopeHeight + mArrowBodyLength
            mHitDistance = -(currentArrowOffset - arrowBodyHeight)
            mFiredArrowLastMoveDistance = 0f
            invalidateSelf()
        }
    }

    private fun updateSize(width: Int, height: Int, bowLength: Float) {
        this.width = width
        this.height = height

        mBowLength = bowLength
        mCenterX = width / 2f
        mStringMiddlePoint.x = mCenterX
        mBowWidth = mBowLength / 50
        mHandleWidth = mBowWidth * 2.5f
        mStringWidth = mBowWidth / 3
        mArrowBodyLength = mBowLength / 2
        mArrowBodyWidth = mArrowBodyLength / 70
        mFinHeight = mArrowBodyLength / 6
        mFinWidth = mFinHeight / 3
        //箭羽倾斜高度 = 箭羽宽度
        mFinSlopeHeight = mFinWidth
        //箭嘴宽度 = 箭羽宽度
        mArrowWidth = mFinWidth
        //箭嘴高度 取 箭杆长度的 1/8
        mArrowHeight = mArrowBodyLength / 8

        mBaseBowOffset = getPointByAngle(mBaseAngle).y + mBowWidth
        //最大偏移量 = 弓高 + Drawable总高度-箭杆长度的一半
        mMaxBowOffset = mBaseBowOffset + (height - mArrowBodyLength) / 2
        //弦最大偏移量 = 箭杆长度 - 弓的高度
        mMaxStringOffset = mArrowBodyLength - mBaseBowOffset
        mFiringBowOffsetDistance = height - mMaxBowOffset + mBaseBowOffset
        mFiredArrowShrinkDistance = mArrowBodyLength * .3F
        //发射后的箭每次上下移动的距离 取 箭羽的高度
        mFiredArrowMoveDistance = mFinHeight

        mPaint.pathEffect = CornerPathEffect(mBowWidth)
        initArrowPath(mArrowBodyLength)
        initArrowTail()

        invalidateSelf()
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

    override fun getIntrinsicHeight(): Int {
        return height
    }

    override fun getIntrinsicWidth(): Int {
        return width
    }

    /**
     * 分解Path
     * @return Path上的全部坐标点
     */
    private fun decomposePath(pathMeasure: PathMeasure): FloatArray {
        if (pathMeasure.length == 0f) {
            return floatArrayOf()
        }

        val pathLength = pathMeasure.length
        val precision = 1
        val numPoints: Int = ((pathLength / precision) + 1).toInt()
        val points = FloatArray(numPoints * 2)
        val position = FloatArray(2)
        var index = 0
        var distance: Float
        for (i in 0 until numPoints) {
            distance = (i * 1f / numPoints) * pathLength
            pathMeasure.getPosTan(distance, position, null)
            points[index] = position[0]
            points[index + 1] = position[1]
            index += 2
        }

        return points
    }

    private fun updateBowPath(currentAngle: Float) {
        val endPoint = getPointByAngle(currentAngle)
        //起始点的x坐标，直接镜像 结束点的x轴坐标
        val startX = mCenterX * 2 - endPoint.x
        val startY = endPoint.y

        val controlX = mCenterX
        //控制点的y坐标，刚好跟两端的y坐标相反，这样的话，线条的中点位置就能保持不变
        val controlY = -endPoint.y

        //初始化偏移量
        var offsetY = -mBaseBowOffset
        //根据滑动进度偏移
        //如果当前进度>25%，表示已经到了终点，所以总是返回1
        //如果<=25%，因为总距离也是只有25%，所以要用4倍速度赶上
        offsetY += mMaxBowOffset * if (progress <= .25f) progress * 4f else 1f

        mBowPath.apply {
            reset()
            moveTo(startX, startY)
            quadTo(controlX, controlY, endPoint.x, endPoint.y)
            offset(0f, offsetY)
        }

    }

    private fun getPointByAngle(angle: Float): PointF {
        //Math.toRadians()
        //角度转成弧度
        val radian = angle * Math.PI / 180
        val radius = mBowLength / 2
        val x: Float = (mCenterX + radius * cos(radian)).toFloat()
        val y: Float = (radius * sin(radian)).toFloat()

        mTempPoint.set(x, y)

        return mTempPoint
    }

    private fun drawBowPath(canvas: Canvas) {
        mPaint.apply {
            style = Paint.Style.FILL
            color = Color.YELLOW
        }

        mBowPathMeasure.setPath(mBowPath, false)
        mBowPathPoints = decomposePath(mBowPathMeasure)

        var fraction: Float
        var radius: Float
        for (i in mBowPathPoints.indices step 2) {
            fraction = i.toFloat() / mBowPathPoints.size
            radius = mBowWidth * mScaleHelper.getScale(fraction) / 2
            canvas.drawCircle(mBowPathPoints[i], mBowPathPoints[i + 1], radius, mPaint)
        }
    }

    private fun updateHandlePath() {
        val bowPathLength = mBowPathMeasure.length
        //握柄长度取弓长度的1/5
        val handlePathLength = bowPathLength / 5
        val bowCenter = bowPathLength / 2
        val start = bowCenter - handlePathLength / 2

        mHandlePath.reset()
        mBowPathMeasure.getSegment(start, start + handlePathLength, mHandlePath, true)
    }

    private fun drawHandlePath(canvas: Canvas) {
        mPaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = mHandleWidth
        }

        canvas.drawPath(mHandlePath, mPaint)
    }

    /**
     * 拿弓Path上5%和95%位置上的坐标当作弦的起始坐标
     *
     * mBowPathPoints中[x,y]成对地存放的，所以拿x坐标的时候必须是偶数，y坐标则必须是奇数索引
     *
     * @param updateMiddlePointY 是否更新中间的y轴坐标
     */
    private fun updateStringPoints(updateMiddlePointY: Boolean) {

        val length = mBowPathPoints.size

        var stringStartIndex = (length * 0.05F).toInt()
        if (stringStartIndex % 2 != 0) {
            stringStartIndex -= 1
        }

        var stringEndIndex = (length * .95F).toInt()
        if (stringEndIndex % 2 != 0) {
            stringEndIndex -= 1
        }

        mStringStartPoint.apply {
            x = mBowPathPoints[stringStartIndex]
            y = mBowPathPoints[stringStartIndex + 1]
        }
        mStringEndPoint.apply {
            x = mBowPathPoints[stringEndIndex]
            y = mBowPathPoints[stringEndIndex + 1]
        }

        if (updateMiddlePointY) {
            mStringOffset = mStringStartPoint.y + if (progress <= .5F) 0F else (progress - .5F) * mMaxStringOffset * 2F
            mStringMiddlePoint.apply {
                x = mCenterX
                //暂时
                y = mStringOffset
            }
        }
    }

    private fun drawString(canvas: Canvas) {
        mPaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = mStringWidth
            color = Color.WHITE
        }

        canvas.drawLine(mStringStartPoint.x, mStringStartPoint.y, mStringMiddlePoint.x, mStringMiddlePoint.y, mPaint)
        canvas.drawLine(mStringMiddlePoint.x, mStringMiddlePoint.y, mStringEndPoint.x, mStringEndPoint.y, mPaint)
    }

    private fun initArrowPath(arrowBodyLength: Float) {
        mArrowPath.reset()

        //一开始定位到箭杆的底部偏向右边的位置
        /**
         * 为什么是从底部开始向上画，而不是从顶部开始向下画呢？
         * 要照顾后面的动态效果，到时候箭是从Drawable的顶部慢慢向下移动的，所以就干脆把它画在Drawable可见范围的外面。
         */
        mArrowPath.apply {
            moveTo(mCenterX + mArrowBodyWidth, -mFinSlopeHeight)
            rLineTo(mFinWidth, mFinSlopeHeight)
            rLineTo(0f, -mFinHeight)
            rLineTo(-mFinWidth, -mFinSlopeHeight)
            rLineTo(0f, -arrowBodyLength)
            rLineTo(mArrowWidth, 0f)
            rLineTo(-mArrowWidth - mArrowBodyWidth, -mArrowHeight)
            rLineTo(-mArrowWidth - mArrowBodyWidth, mArrowHeight)
            rLineTo(mArrowWidth, 0f)
            rLineTo(0f, arrowBodyLength)
            rLineTo(-mFinWidth, mFinSlopeHeight)
            rLineTo(0f, mFinHeight)
            rLineTo(mFinWidth, -mFinSlopeHeight)
            close()
        }
    }

    private fun drawArrow(canvas: Canvas) {
        mPaint.apply {
            style = Paint.Style.FILL
        }
        canvas.drawPath(mArrowPath, mPaint)
    }

    private fun updateArrowOffset() {
        var newOffset = 0f

        //进度超过一半开始拉弓
        if (progress > .5f) {
            newOffset = mStringOffset
        } else if (progress >= .25f) {
            //如果进度大于1/4，证明弓已经到达目的地，要开始箭的偏移了
            //这时候要用4倍速度去偏移，因为箭偏移的动作只分配了25%。
            newOffset = (progress - .25f) * mStringOffset * 4f
        }

        mArrowPath.offset(0f, -mArrowOffset)
        mArrowPath.offset(0f, newOffset)
        mArrowOffset = newOffset
    }

    /**
     * 根据当前拖动的进度计算出弓的弯曲角度
     *
     * 前50% 是用来偏移弓和箭的，所以在50%之前，弓的弯曲角度是不变的
     * 过了50%之后，角度才开始变化，但这时候，进度已经被消费了一半，如果按照原速度来弯曲，肯定是来不及了，所以要用2倍速度弯曲。
     */
    private fun getAngleByProgress() =
        mBaseAngle + if (progress <= .5f) 0f else mUsableAngle * (progress - .5f/*对齐(从0%开始)*/) * 2f/*两倍速度追赶*/

    /**
     * 发射时动画的流程和细节
     *
     * 1.弓先向下偏移，直至超出可见范围。偏移过程中弓会慢慢张开，张开的动作占用总进度的30%(即弯曲角度在弓偏移到总距离的30%处会恢复到初始的角度)；
     * 2.箭杆在离弦(弦的中点y值>箭的偏移量)之后，开始缩短(改变箭杆长度后重画)，并且箭的小尾巴(一个外发光的矩形)慢慢出现；
     * 3.在箭杆缩短了30%之后，箭开始上下反复移动(移动的幅度为一个箭羽的高度)；
     * 4.箭上下移动时，会出现一条条的竖线快速地往下掉；
     */
    private fun handleFiringState(canvas: Canvas) {
        //弓坠落动画已播放的时长
        val firedTime = (SystemClock.uptimeMillis() - mFireTime)

        if (firedTime <= mFiringBowFallDuration) {
            drawBowFalling(canvas, firedTime.toFloat())
        }
        drawLinesAndArrow(canvas)

        invalidateSelf()
    }

    private fun drawBowFalling(canvas: Canvas, firedTime: Float) {
        //得出动画已播放的百分比
        var percent = (firedTime) / mFiringBowFallDuration
        if (percent > 1) {
            percent = 1F
        }

        var angle = getAngleByProgress() - percent * 3f * mUsableAngle
        if (angle < mBaseAngle) {
            angle = mBaseAngle
        }
        updateBowPath(angle)
        mBowPath.offset(0f, percent * mFiringBowOffsetDistance)
        drawBowPath(canvas)

        updateHandlePath()
        drawHandlePath(canvas)

        updateStringPoints(false)
        if (mStringMiddlePoint.y < mStringStartPoint.y) {
            //弦中点y值小于两边端点y值的时候，证明箭已经离弦了
            //弦绷紧（即三个点的y值都一样）
            mStringMiddlePoint.y = mStringStartPoint.y

            //箭杆的缩放动画是时候播放了，记录开始时间
            if (mFiredArrowShrinkStartTime == 0L) {
                mFiredArrowShrinkStartTime = SystemClock.uptimeMillis()
            }
        }
        drawString(canvas)

        drawArrow(canvas)
    }

    private fun initArrowTail() {
        //箭尾尺寸暂定为箭羽宽高的两倍
        val tailHeight = mFinHeight * 2
        mArrowTail.set(mCenterX - mFinWidth, 0F, mCenterX + mFinWidth, tailHeight)
        mTailMaskFilter = BlurMaskFilter(if (mFinWidth == 0f) 1F else mFinWidth, BlurMaskFilter.Blur.NORMAL)
    }

    private fun drawArrowTail(canvas: Canvas) {
        mPaint.apply {
            style = Paint.Style.FILL
            maskFilter = mTailMaskFilter
        }
        canvas.drawRect(mArrowTail, mPaint)
        mPaint.maskFilter = null
    }

    private fun drawLinesAndArrow(canvas: Canvas) {
        if (mFiredArrowMoveStartTime > 0) {

            drawLines(canvas)
            updateLinesY()
            drawDancingArrow(canvas)
        } else if (mFiredArrowShrinkStartTime > 0) {
            drawShrinkingArrow(canvas)
        }
    }

    private fun drawShrinkingArrow(canvas: Canvas) {
        //先算出已播放的时长
        val startedTime = (SystemClock.uptimeMillis() - mFiredArrowShrinkStartTime).toFloat()
        var percent = startedTime / mFiredArrowShrinkDuration
        if (percent > 1) {
            percent = 1F
        }

        //当前进度 * 要缩短的总长度 = 当前要缩短的长度
        val needSubtractLength = percent * mFiredArrowShrinkDistance
        //新的箭杆长度（原始长度 - 要缩短的长度）
        val arrowLength = mArrowBodyLength - needSubtractLength
        initArrowPath(arrowLength)

        //因为现在的箭是新画的，还没有偏移量，所以还要偏移一下
        //箭新的偏移量（缩短了多少就向下偏移多少，以保持箭头位置不变）
        val newArrowOffset = mArrowOffset - needSubtractLength
        mArrowPath.offset(0f, newArrowOffset)
        //更新箭尾的位置：x坐标不变（在Drawable的中间），y坐标，在箭的底部往上偏移一半的箭羽高度
        mArrowTail.offsetTo(mArrowTail.left, newArrowOffset - mFinHeight / 2)

        mPaint.apply {
            color = Color.WHITE
            alpha = (255 * percent).toInt()
        }
        drawArrowTail(canvas)
        mPaint.alpha = 255
        drawArrow(canvas)

        if (percent == 1F) {
            //缩短动画播放完毕，开始上下移动的动画
            mFiredArrowShrinkStartTime = 0
            mFiredArrowMoveStartTime = SystemClock.uptimeMillis()
        }
    }

    private fun drawDancingArrow(canvas: Canvas) {
        val startedTime = (SystemClock.uptimeMillis() - mFiredArrowMoveStartTime).toFloat()
        var percent = startedTime / mFiredArrowMoveDuration
        if (percent > 1) {
            percent = 1F

            hit()
        }

        val distance = percent * mFiredArrowMoveDistance
        val offset = distance - mFiredArrowLastMoveDistance

        mArrowPath.offset(0f, offset)
        mArrowTail.offset(0f, offset)
        mFiredArrowLastMoveDistance = distance

        drawArrow(canvas)
        drawArrowTail(canvas)

        if (percent == 1f) {
            mFiredArrowMoveStartTime = SystemClock.uptimeMillis()
            mFiredArrowMoveDistance = -mFiredArrowMoveDistance
            mFiredArrowLastMoveDistance = 0f
        }
    }

    private fun drawLines(canvas: Canvas) {
        mPaint.style = Paint.Style.STROKE

        mLines.forEach {
            Log.e("YANGYANG", it.toString())
            canvas.drawLine(it.startX, it.startY, it.endX, it.startY + it.height, mPaint)
        }
    }

    /**
     * 更新每一条线的y坐标,使其坠落
     */
    private fun updateLinesY() {
        mLines.forEach {
            val startedTime = (SystemClock.uptimeMillis() - it.startTime).toFloat()
            val percent = startedTime / it.duration

            it.startY = percent * it.distance - it.height
            if (it.startY >= height) {
                initLines(it)
            }
        }
    }

    private fun initLines(line: Line) {
        line.apply {
            startTime = SystemClock.uptimeMillis()
            //随机时长：最小不会小于给定时长的1/4，最大时长是给定时长的1.25倍
            duration = (mBaseLinesFallDuration / 4 + mRandom.nextInt(mBaseLinesFallDuration)).toLong()
            //线条起始点的y坐标值，是一个负的随机数，最大不超过Drawable的高度
            val ran = mRandom.nextFloat()
            startY = -this@ArrowDrawable.height + ran * this@ArrowDrawable.height
            height = if (-startY > 10) -startY else 10F
            startX = mRandom.nextFloat() * this@ArrowDrawable.width
            endX = startX
            distance = height - startY
        }

    }

    private fun handleHittingState(canvas: Canvas) {
        if (mHitStartTime > 0) {
            drawArrowHitting(canvas)
            invalidateSelf()
        } else {
            if (mSkewStartTime > 0) {
                //命中后甩尾
                drawArrowSkewing(canvas)
                invalidateSelf()
            } else {
                //命中后甩尾结束
                drawArrow(canvas)
            }
        }
    }

    private fun drawArrowHitting(canvas: Canvas) {
        val startedTime = (SystemClock.uptimeMillis() - mHitStartTime).toFloat()
        var percent = startedTime / mHitDuration
        if (percent > 1f) {
            percent = 1f
            //偏移动画结束，开始左右摆动的动画
            mHitStartTime = 0
            mSkewStartTime = SystemClock.uptimeMillis()
            mCurrentSkewCount = 1
        }
        val distance = percent * mHitDistance
        val offset = distance - mFiredArrowLastMoveDistance
        mFiredArrowLastMoveDistance = distance

        mArrowPath.offset(0f, offset)
        mArrowTail.offset(0f, offset)

        drawLines(canvas)
        updateLinesY()

        drawArrow(canvas)
        //箭尾渐渐变得透明起来，直至完全透明
        mPaint.alpha = (255 * (1 - percent)).toInt()
        drawArrowTail(canvas)
        mPaint.alpha = 255
    }

    /**
     * 摆动策略
     * ·一共要摆动的次数为8次(mMaxSkewCount = 8)，左右各2次来回；
     * ·每次摆动的幅度大概为2度(mSkewTan = .035F)(这个是正切值)；
     * ·第一次向右偏移(刚刚的drawArrowHitting方法标记了mCurrentSkewCount为1)，偏移结束后，
     *  mCurrentSkewCount会+1，+1后就是偶数了，继续往下执行，因为检测到是偶数还会把目的地取反，也就是要往相反方向走了。
     *  所以当它重新开始动画时，所偏移的方向是反的，这样一来一回，看上去就像是箭尾在摆动的样子。
     */
    private fun drawArrowSkewing(canvas: Canvas) {
        val startedTime = SystemClock.uptimeMillis() - mSkewStartTime
        var percent = (startedTime).toFloat() / mSkewDuration
        if (percent > 1f) {
            percent = 1f
        }

        var tan = percent * mSkewTan
        //如果是偶数，则向左摆动，否则向右
        if (mCurrentSkewCount % 2 == 0) {
            //目的地取反
            tan -= mSkewTan
        }

        canvas.skew(tan, 0f)
        drawArrow(canvas)
        if (percent == 1f) {
            if (mCurrentSkewCount == mMaxSkewCount) {
                mSkewStartTime = 0
                return
            } else {
                mSkewStartTime = SystemClock.uptimeMillis()
                mCurrentSkewCount++
            }

            //如果次数为偶数就要切换方法(一次来一次回，所以是偶数)
            if (mCurrentSkewCount % 2 == 0) {
                mSkewTan = -mSkewTan
            }
        }

    }


    private class Line {
        var duration = 0L
        var startTime = 0L
        var distance = 0F

        var startX = 0F
        var startY = 0F
        var height = 0F
        var endX = 0F

        override fun toString(): String {
            return "startX:${startX}, startY:${startY}, endX:${endX}, height:${height}, distance:${distance}, startTime:${startTime}, duration:${duration}"
        }
    }

    /**
     * 画一条路径上粗细不一样的斜线
     */
    private fun testDrawSkewLine(canvas: Canvas) {
        val path = Path().apply {
            moveTo(100f, 100f)
            lineTo(1000f, 1100f)
        }
        //分解Path
        val points = decomposePath(PathMeasure(path, false))
        val scaleHelper = ScaleHelper(
            .5F, 0F,
            .1F, .3F,
            1.8F, .8F,
            1F, 1F
        )

        val baseLineWidth = 50
        var fraction: Float
        var radius: Float

        for (i in points.indices step 2) {
            fraction = i * 1f / points.size
            radius = baseLineWidth * scaleHelper.getScale(fraction) / 2
            canvas.drawCircle(points[i], points[i + 1], radius, mPaint)
        }
    }
}