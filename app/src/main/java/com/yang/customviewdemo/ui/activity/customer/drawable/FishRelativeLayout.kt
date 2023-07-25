package com.yang.customviewdemo.ui.activity.customer.drawable

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.RelativeLayout
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.sqrt


/**
 * Create by Yang Yang on 2023/7/22
 */
class FishRelativeLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val mPaint: Paint by lazy { Paint() }
    private val fishImageView: ImageView by lazy { ImageView(context) }
    private val layoutParams: RelativeLayout.LayoutParams by lazy { RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT) }
    private val fishDrawable: FishDrawable by lazy { FishDrawable() }

    private var touchX: Float = 0f
    private var touchY: Float = 0f
    private var paintAlpha = 0
    private var ripper: Float = 0f
        set(value) {
            paintAlpha = (100 * (1- value)).toInt()
            field = value
            invalidate()
        }
        get() {
            return field
        }

    init {

        setWillNotDraw(false)

        mPaint.apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }

        fishImageView.apply {
            layoutParams = this@FishRelativeLayout.layoutParams
            setImageDrawable(fishDrawable)
        }

        addView(fishImageView)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        touchX = event.x
        touchY = event.y

        val objectAnimator = ObjectAnimator.ofFloat(this, "ripper", 0f, 1f).setDuration(1000)
        objectAnimator.start()

        makeTrail()

        return super.onTouchEvent(event)
    }

    private fun makeTrail() {
        val fishRelativeMiddle = fishDrawable.middlePoint
        //起始点
        val fishRealMiddle = PointF(fishRelativeMiddle.x + fishImageView.x, fishRelativeMiddle.y + fishImageView.y)
        val fishRelativeHead = fishDrawable.headPoint
        //控制点1
        val fishRealHead = PointF(fishRelativeHead.x + fishImageView.x, fishRelativeHead.y + fishImageView.y)
        //结束点
        val endPoint = PointF(touchX - fishRelativeMiddle.x, touchY - fishRelativeMiddle.y)

        val touchPoint = PointF(touchX, touchY)

        // 控制点2角度
        val angle  = includeAngle(fishRealMiddle, fishRealHead, touchPoint) / 2
        val delta = includeAngle(fishRealMiddle, PointF(fishRealMiddle.x + 1, fishRealMiddle.y), fishRealHead)
        // 控制点2
        val controlPoint = fishDrawable.calculatePoint(fishRealMiddle, (FishDrawable.HEAD_RADIUS * 1.6).toFloat(), angle + delta)
        Log.e("YANGYANG1", "angle:$angle,  delta:$delta,   angle + delta=${angle + delta})")

        val path = Path()
        path.moveTo(fishRealMiddle.x - fishRelativeMiddle.x, fishRealMiddle.y - fishRelativeMiddle.y)
        path.cubicTo(fishRealHead.x - fishRelativeMiddle.x, fishRealHead.y - fishRelativeMiddle.y, controlPoint.x - fishRelativeMiddle.x, controlPoint.y - fishRelativeMiddle.y, endPoint.x, endPoint.y)
        val objectAnimator = ObjectAnimator.ofFloat(fishImageView, "x", "y", path).apply {
            duration = 2000
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    fishDrawable.frequencySwing = 2f
                }

                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    fishDrawable.frequencySwing = 1f
                }
            })
        }
        objectAnimator.start()


        //鱼转身
        val pathMeasure = PathMeasure(path, false)
        val tan = FloatArray(2)
        objectAnimator.addUpdateListener {
            val fraction = it.animatedFraction
            //拿到切点角度
            pathMeasure.getPosTan(pathMeasure.length * fraction, null, tan)
            val fishAngle = Math.toDegrees(atan2((-tan[1]).toDouble(), tan[0].toDouble()))
            fishDrawable.fishMainAngle = fishAngle.toFloat()
        }

    }

    private fun includeAngle(pointO: PointF, pointA: PointF, pointB: PointF): Float {
        val AOB = (pointA.x - pointO.x) * (pointB.x - pointO.x) + (pointA.y - pointO.y) * (pointB.y - pointO.y)
        val OALength = sqrt((pointA.x - pointO.x) * (pointA.x - pointO.x) + (pointA.y - pointO.y) * (pointA.y - pointO.y))
        val OBLength = sqrt((pointB.x - pointO.x) * (pointB.x - pointO.x) + (pointB.y - pointO.y) * (pointB.y - pointO.y))
        val cosAOB = AOB / (OALength * OBLength)

        //反三角函数拿到度数
        val angleAOB = (Math.toDegrees(acos(cosAOB.toDouble()))).toFloat()

        //AB连线与X的夹角的tan值 - OB与x轴的夹角的tan值
        val direction = ((pointA.y - pointB.y) / (pointA.x - pointB.x) - (pointO.y - pointB.y) / (pointO.x - pointB.x)).toInt()
        Log.e("YANGYANG1", "angleAOB:$angleAOB,  direction:$direction")

        return if (direction == 0) {
            if (AOB >= 0) {
                0f
            } else {
                180f
            }
        } else {
            if (direction > 0) {
                -angleAOB
            } else {
                angleAOB
            }
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mPaint.alpha = paintAlpha
        canvas.drawCircle(touchX, touchY, ripper * 150, mPaint)
    }
}