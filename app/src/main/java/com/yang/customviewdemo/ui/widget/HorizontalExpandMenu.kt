package com.yang.customviewdemo.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.Transformation
import android.widget.RelativeLayout
import com.yang.customviewdemo.R
import com.yang.customviewdemo.utils.dp
import com.yang.customviewdemo.utils.px
import kotlin.math.min


/**
 * Create by Yang Yang on 2023/8/17
 */
class HorizontalExpandMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): RelativeLayout(context, attrs, defStyleAttr) {

    private val defaultWidth = 200.dp
    private val defaultHeight = 40.dp
    private var viewWidth = defaultWidth
    private var viewHeight = defaultHeight

    private var menuBackColor: Int
    private var menuStrokeWidth: Float
    private var menuStrokeColor: Int
    private var menuCornerRadius: Float

    private var buttonIconDegrees = 0f//按钮icon符号竖线的旋转角度
    private var buttonIconSize: Float //按钮icon符号的大小
    private var buttonIconStrokeWidth: Float //按钮icon符号的粗细
    private var buttonIconColor: Int //按钮icon颜色
    private val buttonIconPaint: Paint by lazy { Paint() }

    private val buttonStyle: Int //按钮类型
    private var buttonRadius: Int = defaultHeight / 2 //按钮矩形区域内圆半径
    private var buttonTop: Float = 0f //按钮矩形区域top值
    private var buttonBottom: Float = defaultHeight.toFloat() //按钮矩形区域bottom值

    private val rightButtonCenter: PointF by lazy { PointF() } //右按钮中点
    private var rightButtonLeft: Float = 0f  //右按钮矩形区域left值
    private var rightButtonRight: Float = 0f  //右按钮矩形区域right值


    private val leftButtonCenter: PointF by lazy { PointF() } //左按钮中点
    private var leftButtonLeft: Float = 0f  //左按钮矩形区域left值
    private var leftButtonRight: Float = 0f  //左按钮矩形区域right值

    private val path: Path by lazy { Path() }

    private var isExpand = true
    private var downX = -1f
    private var downY = -1f
    private var expandAnimTime = 400
    private val anim: ExpandMenuAnim by lazy { ExpandMenuAnim().apply { 
        setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                
            }

            override fun onAnimationEnd(animation: Animation?) {
                isAnimEnd = true
            }

            override fun onAnimationRepeat(animation: Animation?) {
                
            }

        })
    } }
    private var isAnimEnd = false

    private var maxChildPathWidth: Float = defaultWidth.toFloat()
    private var childPathWidth: Float = 0f
    private var menuLeft = 0f
    private var menuRight = defaultWidth.toFloat()
    private var childView: View? = null
    private val childViewLayoutParams: LayoutParams by lazy { LayoutParams(viewWidth, viewHeight) }

    private var isFirstLayout = true

    class ButtonStyle {
        companion object {
            const val STYLE_RIGHT = 0
            const val STYLE_LEFT = 1
        }
    }

    init {

        context.obtainStyledAttributes(attrs, R.styleable.HorizontalExpandMenu).apply {
            menuBackColor = this.getColor(R.styleable.HorizontalExpandMenu_back_color, Color.WHITE)
            menuStrokeWidth = this.getDimension(R.styleable.HorizontalExpandMenu_stroke_width, 1f)
            menuStrokeColor = this.getColor(R.styleable.HorizontalExpandMenu_corner_radius, Color.GRAY)
            menuCornerRadius = this.getDimension(R.styleable.HorizontalExpandMenu_corner_radius, 20f.px)

            buttonStyle = this.getInteger(R.styleable.HorizontalExpandMenu_button_style, ButtonStyle.STYLE_RIGHT)
            buttonIconSize = this.getDimension(R.styleable.HorizontalExpandMenu_button_icon_size, 8f.px)
            buttonIconStrokeWidth = this.getDimension(R.styleable.HorizontalExpandMenu_button_icon_stroke_width, 8f)
            buttonIconColor = this.getColor(R.styleable.HorizontalExpandMenu_button_icon_color, Color.GRAY)
            expandAnimTime = this.getInteger(R.styleable.HorizontalExpandMenu_expand_time, 400)

            recycle()
        }

        buttonIconPaint.apply {
            color = buttonIconColor
            strokeWidth = buttonIconStrokeWidth
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        buttonIconDegrees = 90f

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = measureSize(defaultHeight, heightMeasureSpec)
        val width = measureSize(defaultWidth, widthMeasureSpec)
        viewHeight = height
        viewWidth = width
        setMeasuredDimension(viewWidth, viewHeight)
        Log.e("HorizontalExpandMenu", "viewWidth: $viewWidth, viewHeight: $viewHeight")

        buttonRadius = viewHeight / 2
        layoutRootButton()
        maxChildPathWidth = (viewWidth - buttonRadius * 2).toFloat()
        childPathWidth = maxChildPathWidth

        if (background == null) {
            setMenuBackground()
        }

    }

    private fun measureSize(defaultSize: Int, measureSpec: Int): Int {
        val specSize = MeasureSpec.getSize(measureSpec)
        val specMode = MeasureSpec.getMode(measureSpec)

        val result = when (specMode) {
            MeasureSpec.EXACTLY -> {
                specSize
            }
            MeasureSpec.AT_MOST -> {
                min(defaultSize, specSize)
            }
            else -> {
                defaultSize
            }
        }

        return result
    }

    /**
     * 设置菜单背景，如果要显示阴影，需在onLayout之前调用
     */
    private fun setMenuBackground() {
        val gd = GradientDrawable().apply {
            setColor(menuBackColor)
            setStroke(menuStrokeWidth.toInt(), menuStrokeColor)
            cornerRadius = menuCornerRadius
        }

        background = gd
    }

    private fun layoutRootButton() {
        buttonTop = 0f
        buttonBottom = viewHeight.toFloat()

        rightButtonCenter.set((viewWidth - buttonRadius).toFloat(), viewHeight / 2f)
        rightButtonLeft = (rightButtonCenter.x - buttonRadius).toFloat()
        rightButtonRight = (rightButtonCenter.x + buttonRadius).toFloat()

        leftButtonCenter.set(buttonRadius.toFloat(), viewHeight / 2f)
        leftButtonLeft = (leftButtonCenter.x - buttonRadius).toFloat()
        leftButtonRight = (leftButtonCenter.x + buttonRadius).toFloat()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        if (isFirstLayout) {
            menuLeft = left.toFloat()
            menuRight = right.toFloat()
            isFirstLayout = false
        }

        if (childCount > 0) {
            childView = getChildAt(0)
            if (isExpand) {
                when (buttonStyle) {
                    ButtonStyle.STYLE_RIGHT -> {
                        childView?.layout(leftButtonCenter.x.toInt(),
                            buttonTop.toInt(), rightButtonLeft.toInt(), buttonBottom.toInt()
                        )
                    }
                    ButtonStyle.STYLE_LEFT -> {
                        childView?.layout(leftButtonRight.toInt(), buttonTop.toInt(), rightButtonCenter.x.toInt(), buttonBottom.toInt())
                    }
                }

                childViewLayoutParams.apply {
                    width = viewWidth
                    height = viewHeight
                    setMargins(0, 0, buttonRadius * 3, 0)
                }
                childView?.layoutParams = childViewLayoutParams
            } else {
                childView?.visibility = View.GONE
            }
        }

        if (childCount > 1) {
            throw IllegalStateException("HorizontalExpandMenu can host only one direct child")
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w //当menu的宽度改变时，重新给viewWidth赋值

        if (isAnimEnd) {//防止出现动画结束后菜单栏位置大小测量错误的bug
            when (buttonStyle) {
                ButtonStyle.STYLE_RIGHT -> {
                    if (!isExpand) {
                        layout((menuRight - buttonRadius * 2 - childPathWidth).toInt(), top, menuRight.toInt(), bottom)
                    }
                }
                ButtonStyle.STYLE_LEFT -> {
                    if (!isExpand) {
                        layout(menuLeft.toInt(), top, (menuLeft + buttonRadius * 2 + childPathWidth).toInt(), bottom)
                    }
                }
            }
        }

    }

    override fun onDraw(canvas: Canvas) {
        layoutRootButton()
        if (buttonStyle == ButtonStyle.STYLE_RIGHT) {
            drawRightIcon(canvas)
        } else {
            drawLeftIcon(canvas)
        }
        super.onDraw(canvas)//注意父方法在最后调用，以免icon被遮盖
    }

    private fun drawLeftIcon(canvas: Canvas) {
        path.apply {
            reset()

            moveTo(leftButtonCenter.x - buttonIconSize, leftButtonCenter.y)
            lineTo(leftButtonCenter.x + buttonIconSize, leftButtonCenter.y)
        }

        canvas.apply {
            drawPath(path, buttonIconPaint)

            save()
            rotate(-buttonIconDegrees, leftButtonCenter.x, leftButtonCenter.y)
            path.apply {
                reset()
                moveTo(leftButtonCenter.x, leftButtonCenter.y - buttonIconSize)
                lineTo(leftButtonCenter.x, leftButtonCenter.y + buttonIconSize)
            }
            drawPath(path, buttonIconPaint)

            restore()
        }
    }

    private fun drawRightIcon(canvas: Canvas) {
        path.apply {
            reset()

            moveTo(rightButtonCenter.x - buttonIconSize, rightButtonCenter.y)
            lineTo(rightButtonCenter.x + buttonIconSize, rightButtonCenter.y)
        }

        canvas.apply {
            drawPath(path, buttonIconPaint)

            save()
            rotate(-buttonIconDegrees, rightButtonCenter.x, rightButtonCenter.y)
            path.apply {
                reset()
                moveTo(rightButtonCenter.x, rightButtonCenter.y - buttonIconSize)
                lineTo(rightButtonCenter.x, rightButtonCenter.y + buttonIconSize)
            }
            drawPath(path, buttonIconPaint)

            restore()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
            }
            MotionEvent.ACTION_MOVE -> {

            }
            MotionEvent.ACTION_UP -> {
                if (maxChildPathWidth == childPathWidth || childPathWidth == 0f) {
                    when (buttonStyle) {
                        ButtonStyle.STYLE_RIGHT -> {
                            if (x == downX && y == downY && y >= buttonTop && y <= buttonBottom && x >= rightButtonLeft && x <= rightButtonRight) {
                                changeMenuStatus(expandAnimTime)
                            }
                        }
                        ButtonStyle.STYLE_LEFT -> {
                            if (x == downX && y == downY && y >= buttonTop && y <= buttonBottom && x >= leftButtonLeft && x <= leftButtonRight) {
                                changeMenuStatus(expandAnimTime)
                            }
                        }
                    }
                }
            }
        }

        return true
    }

    private fun changeMenuStatus(time: Int) {
        anim.duration = time.toLong()
        isExpand = !isExpand
        this.startAnimation(anim)
        isAnimEnd = false
    }

    inner class ExpandMenuAnim: Animation() {

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            super.applyTransformation(interpolatedTime, t)
            val left = menuRight - buttonRadius * 2 //按钮在右边，菜单收起时按钮区域left值
            val right = menuLeft + buttonRadius * 2 //按钮在左边，菜单收起时按钮区域right值

            childView?.visibility = View.GONE

            if (isExpand) {
                buttonIconDegrees = 90 * interpolatedTime
                childPathWidth = maxChildPathWidth * interpolatedTime

                if (childPathWidth == maxChildPathWidth) {
                    childView?.visibility = View.VISIBLE
                }
            } else {
                buttonIconDegrees = 90 - 90 * interpolatedTime
                childPathWidth = maxChildPathWidth * (1 - interpolatedTime)
            }

            when (buttonStyle) {
                ButtonStyle.STYLE_RIGHT -> {
                    layout((left - childPathWidth).toInt(), top, menuRight.toInt(), bottom)
                }
                ButtonStyle.STYLE_LEFT -> {
                    layout(menuLeft.toInt(), top, (right + childPathWidth).toInt(), bottom)
                }
            }
            postInvalidate()
        }
    }
}