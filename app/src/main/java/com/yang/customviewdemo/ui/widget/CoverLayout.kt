package com.yang.customviewdemo.ui.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Scroller
import com.yang.customviewdemo.R
import kotlin.math.abs


/**
 * Create by Yang Yang on 2023/7/28
 *
 * View的顺序：
 *     TopBar
 *         HeaderView
 *             BottomView
 *           ResidualView
 *       TopView
 *     BottomBar
 */
class CoverLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ViewGroup(context, attrs, defStyleAttr) {

    private var mBottomView: View? = null
    private val mTransitionView: View by lazy { View(context) }
    private var mTransitionColor: Int = Color.RED

    private var mTopView: View? = null
    private val mElevationView: View by lazy { View(context) }
    private var mResidualView: View? = null

    private var mBottomBar: View? = null
    var mTopBar: View? = null
    private var mHeaderView: View? = null

    private var mHeaderViewOffset = 0
    private var mTopOffset = 0
    private var mTopViewOffset = 0
    private var mBottomViewOffset = 0
    private var mTopElevation = 0
    private var mTriggerOffset = mTopOffset / 2

    private var isBeingDragged: Boolean = false
    private var mLastY: Int = 0
    private var mTouchSlop: Int = 0
    private var mCurrentStatus: Int = STATE_COVER

    private val mVelocityTracker: VelocityTracker by lazy { VelocityTracker.obtain() }
    private val mScroll: Scroller by lazy { Scroller(context) }

    private var isNewScroll = true
    private var mScrollOffset = 0

    var mOnStateChangedListener: OnStateChangedListener? = null


    companion object {
        const val STATE_HALF = 0  //半开
        const val STATE_COVER = 1 //盖住
        const val STATE_NAKED = 2 //打开
        const val STATE_OPENING = 3 //正在打开
        const val STATE_CLOSING = 4 //正在关闭

        private const val ANIMATION_DURATION = 500L
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CoverLayout, defStyleAttr, 0).run {
            mTopElevation = getDimensionPixelOffset(R.styleable.CoverLayout_top_elevation, 0)
            mTopOffset = getDimensionPixelOffset(R.styleable.CoverLayout_top_offset, 0)
            var resId: Int = getResourceId(R.styleable.CoverLayout_residual_view, 0)
            if (resId > 0) {
                mResidualView = LayoutInflater.from(context).inflate(resId, this@CoverLayout, false)
            }
            resId = getResourceId(R.styleable.CoverLayout_header_view, 0)
            if (resId > 0) {
                mHeaderView = LayoutInflater.from(context).inflate(resId, this@CoverLayout, false)
            }
            resId = getResourceId(R.styleable.CoverLayout_top_bar, 0)
            if (resId > 0) {
                mTopBar = LayoutInflater.from(context).inflate(resId, this@CoverLayout, false)
            }
            resId = getResourceId(R.styleable.CoverLayout_bottom_bar, 0)
            if (resId > 0) {
                mBottomBar = LayoutInflater.from(context).inflate(resId, this@CoverLayout, false)
            }

            mTransitionColor = getColor(R.styleable.CoverLayout_transition_color, Color.WHITE)
            mTopOffset -= mTopElevation
            mTriggerOffset = getDimensionPixelOffset(R.styleable.CoverLayout_trigger_open_offset, mTopOffset / 2)
            mTriggerOffset += mTopOffset
            recycle()
        }


        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, mTopElevation)
        val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
            intArrayOf(-0x77bbbbbc, 0x00000000))
        mElevationView.apply {
            this.layoutParams = layoutParams
            background = gradientDrawable
        }
        mTransitionView.setBackgroundColor(mTransitionColor)
    }

    //重写全部addView方法，并在最后一个addView方法里面限制两个View
    override fun addView(child: View?) {
        addView(child, -1)
    }

    override fun addView(child: View?, index: Int) {
        if (child == null) {
            throw IllegalArgumentException("Cannot add a null child view to a ViewGroup")
        }
        var params = child.layoutParams
        if (params == null) {
            params = generateDefaultLayoutParams()
            if (params == null) {
                throw IllegalArgumentException("generateDefaultLayoutParams() cannot return null")
            }
        }
        addView(child, index, params)
    }

    override fun addView(child: View?, width: Int, height: Int) {
        val params = generateDefaultLayoutParams().apply {
            this.width = width
            this.height = height
        }
        addView(child, -1, params)
    }

    override fun addView(child: View?, params: LayoutParams?) {
        addView(child, -1, params)
    }

    override fun addView(child: View?, index: Int, params: LayoutParams?) {
        var realChild = child
        when (childCount) {
            0 -> {
                //添加底部View
                mBottomView = packingBottomView(child)
                realChild = mBottomView
            }
            1 -> {
                addResidualView(index)
                mTopView = packingTopView(child)
                realChild = mTopView

                addHeaderView(index)
                if (realChild != null) {
                    super.addView(realChild, index, params)
                }
                addTopBar(index)
                addBottomBar(index)
                return
            }
            6 -> {
                throw IllegalArgumentException("CoverLayout child can't > 2")
            }
        }
        if (realChild != null) {
            super.addView(realChild, index, params)
        }
    }

    private fun addBottomBar(index: Int) {
        mBottomBar?.let {
            val params = it.layoutParams ?: generateDefaultLayoutParams()
            super.addView(it, index, params)
        }
    }

    private fun addTopBar(index: Int) {
        mTopBar?.let {
            val params = it.layoutParams ?: generateDefaultLayoutParams()
            super.addView(it, index, params)
            setTopBarBackgroundAlpha(1f)
        }
    }

    private fun setTopBarBackgroundAlpha(percent: Float) {
        mTopBar?.let {
            val per = 1f - percent
            it.background.alpha = (255f * per).toInt()
        }
    }

    private fun addHeaderView(index: Int) {
        mHeaderView?.let {
            var params = it.layoutParams
            if (params != null) {
                params = generateDefaultLayoutParams()
            }
            super.addView(mHeaderView, index, params)
        }
    }

    private fun packingTopView(view: View?): View? {
        if (view != null) {
            val linearLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                addView(mElevationView)
                addView(view)
            }
            return linearLayout
        }
        return null
    }

    private fun addResidualView(index: Int) {
        mResidualView?.let {
            var params = it.layoutParams
            if (params == null) {
                params = generateDefaultLayoutParams()
            }
            setOnClickListener {
                closeTopView()
            }
            super.addView(mResidualView, index, params)
        }
    }

    private fun packingBottomView(view: View?): View? {
        if (view != null) {
            val frameLayout = FrameLayout(context).apply {
                addView(view)
                addView(mTransitionView)
            }
            return frameLayout
        }
        return null
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        for (i in 0 until childCount) {
            getChildAt(i).apply {
                measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            }
        }

        val view = (mBottomView as ViewGroup).getChildAt(0)
        view?.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
        mTransitionView.measure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount >= 6) {
            mBottomBar?.let {
                it.layout(0, b - it.layoutParams.height, r, b)
            }
        }

        if (childCount >= 5) {
            mTopBar?.let {
                it.layout(0, 0, r, it.layoutParams.height)
            }
        }

        if (childCount >= 4) {
            mHeaderView?.let {
                it.layout(0, mHeaderViewOffset, r, mHeaderViewOffset + it.layoutParams.height)
                translationY = 0f
            }
        }

        if (childCount >= 3 || childCount >= 2) {
            val totalOffset = mTopOffset + mTopViewOffset
            mTopView?.let {
                it.layout(0, totalOffset, r, it.measuredHeight)
            }
            mResidualView?.let {
                it.layout(0, b, r, b+ it.layoutParams.height)
            }
        }

        if (childCount >= 1) {
            mBottomView?.let {
                it.layout(0, mBottomViewOffset, r, mBottomViewOffset + it.measuredHeight)
            }
            //过渡view: 与mBottomView偏移量相反 (因为它要始终显示在屏幕内)
            mTransitionView.let {
                it.layout(0, -mBottomViewOffset, r, -mBottomViewOffset + it.height)
                //it.layout(0, 0, 0, 0)
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        //正在播放开关动画: 拦截
        if (isTopOpeningOrClosing()) {
            return true
        }
        val action = ev.action
        //已经开始拖动: 拦截
        if ((action == MotionEvent.ACTION_MOVE) && isBeingDragged) {
            return true
        }
        if (super.onInterceptTouchEvent(ev)) {
            return true
        }
        //不能拖动: 放行
        if (!canScroll()) {
            return false
        }

        val y = (ev.y).toInt()
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                //停止惯性滚动并刷新y坐标
                if (!isTopOpeningOrClosing()) {
                    abortScrollerAnimation()
                }
                mLastY = y
            }
            MotionEvent.ACTION_MOVE -> {
                val offset = y -mLastY
                //判断是否触发拖动事件
                if (abs(offset) > mTouchSlop) {
                    mLastY = y
                    isBeingDragged = true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                isBeingDragged = false
            }
        }

        return isBeingDragged

    }

    private fun abortScrollerAnimation() {
        mScroll.abortAnimation()
    }

    private fun canScroll(): Boolean {
        mTopView?.let {  topView ->
            mBottomView?.let { bottomView ->
                return (mCurrentStatus != STATE_NAKED && (topView.top < top || topView.bottom > bottom))
                        || (mCurrentStatus == STATE_NAKED && (bottomView.top < top || bottomView.bottom > bottom))
            }
        }
        return true
    }

    private fun isTopOpeningOrClosing(): Boolean {
        return mCurrentStatus === STATE_OPENING || mCurrentStatus === STATE_CLOSING
    }

    /**
     * ·move事件我们就判断当前topView的状态，如果topView未打开，那就滚动topView，反之，则滚动bottomView.
     *
     * ·up事件: 如果topView是半开的状态，则判断是否向下滑动，如果滑动的距离达到我们给定的距离，就触发打开topView，如果距离不够，
     *   就回弹; 如果topView是全开或合上状态，则根据手指速率，开始惯性滚动：
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        mVelocityTracker.addMovement(event)
        val y = (event.y).toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!isTopOpeningOrClosing()) {
                    abortScrollerAnimation()
                } else {
                    return false
                }
                mLastY = y
            }
            MotionEvent.ACTION_MOVE -> {
                if (mCurrentStatus == STATE_NAKED) {
                    offsetBottomView(y)
                } else {
                    offsetTopView(y)
                }

                if (mCurrentStatus != STATE_NAKED) {
                    mVelocityTracker.computeCurrentVelocity(1000)
                    val velocityY = mVelocityTracker.yVelocity
                    //根据手指滑动的速率和方向来判断是否要隐藏或显示TopBar
                    if (abs(velocityY) > 4000) {
                        if (velocityY > 0) {
                            mTopBar?.let {
                                if (it.translationY.toInt() == -it.layoutParams.height) {
                                    showTopBar()
                                }
                            }
                        } else {
                            mTopBar?.let {
                                if (it.translationY.toInt() == 0) {
                                    hideTopBar()
                                }
                            }
                        }
                    }
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE, MotionEvent.ACTION_UP -> {
                var isHandle = false
                if (mCurrentStatus == STATE_HALF) {
                    mTopView?.let {
                        //大于触发距离, 则打开TopView, 反之
                        if (it.top >= mTriggerOffset) {
                            openTopView()
                            isHandle = true
                        } else if (it.top > mTopOffset) {
                            closeTopView()
                            isHandle = true
                        }
                    }
                }

                if (!isHandle) {
                    mVelocityTracker.computeCurrentVelocity(1000)
                    mScroll.fling(
                        0, 0, 0, mVelocityTracker.yVelocity.toInt(),
                        0, 0, Int.MIN_VALUE, Int.MAX_VALUE
                        )
                    invalidate()
                }
                isBeingDragged = false
            }
        }

        return super.onTouchEvent(event)
    }

    /**
     * TopView未打开: 滚动TopView，并做越界处理;
     * TopView已打开: 滚动BottomView，并做越界处理;
     * 本次滚动结束: 更新状态;
     */
    override fun computeScroll() {
        if (mScroll.computeScrollOffset()) {
            if (isNewScroll) {
                isNewScroll = false
                mScrollOffset = mScroll.currY
            }

            //没打开盖子， 滑动TopView
            if (mCurrentStatus != STATE_NAKED) {
                mTopView?.let {
                    //判断是否还可以滚动
                    if (it.bottom >= bottom) {
                        var offset = mScroll.currY - mScrollOffset
                        //判断是否越界: 如果越界,则本次偏移量为可以滑动的最大值
                        if (it.bottom + offset < bottom) {
                            offset = bottom - it.bottom
                        } else if (mScroll.currVelocity > 0 && offset > 0) {
                            //手指滑动, 并且是向下滑
                            if (it.top + offset >= mTopOffset && !isTopOpeningOrClosing()) {
                                offset = mTopOffset - it.top
                            }
                        }
                        offsetChildView(offset)

                    }
                }
            } else {
                //打开了盖子，滑动BottomView
                mBottomView?.let {
                    //判断是否还可以滚动
                    if (it.bottom >= bottom && it.top <= top) {
                        var offset = mScroll.currY - mScrollOffset
                        //判断是否越界: 如果越界,则本次偏移量为可以滑动的最大值
                        if (it.bottom + offset < bottom) {
                            offset = bottom - it.bottom
                        } else if (it.top + offset > top) {
                            offset = top - it.top
                        }
                        mBottomViewOffset += offset
                        it.offsetTopAndBottom(offset)
                        mTransitionView.offsetTopAndBottom(-offset)
                    }
                }
            }
            mScrollOffset = mScroll.currY
            invalidate()
        }

        if (mScroll.isFinished) {
            isNewScroll = true
            //滚动结束, 更新状态
            if (mCurrentStatus == STATE_OPENING) {
                mTransitionView.visibility = View.INVISIBLE
                mHeaderView?.visibility = View.INVISIBLE
                showResidualView()
                mCurrentStatus = STATE_NAKED
                notifyListener()
            } else if (mCurrentStatus == STATE_CLOSING) {
                val offset = top - (mBottomView?.bottom ?: 0)
                mBottomViewOffset += offset
                mBottomView?.offsetTopAndBottom(offset)
                mTransitionView.offsetTopAndBottom(-offset)
                mResidualView?.translationY = 0f
                mCurrentStatus = STATE_HALF
                notifyListener()
            }
        }
    }

    private fun notifyListener() {
        mOnStateChangedListener?.onStateChanged(mCurrentStatus)
    }


    /**
     * 更新TopView和其他需要联动的View的位置
     */
    private fun offsetChildView(offset: Int) {
        mTopView?.let { topView ->
            //不是正在打开或关闭状态,并且TopView当前位置高于默认的偏移量
            if (!isTopOpeningOrClosing() && topView.top < mTopOffset) {
                var bottomViewOffset = offset / 2
                mBottomView?.let { bottomView ->
                    if (bottomView.top > top || bottomView.top + bottomViewOffset > top) {
                        bottomViewOffset = top - bottomView.top
                    }
                    mBottomViewOffset += bottomViewOffset
                    bottomView.offsetTopAndBottom(bottomViewOffset)
                    mHeaderViewOffset += bottomViewOffset
                    mHeaderView?.offsetTopAndBottom(bottomViewOffset)
                    mTransitionView.offsetTopAndBottom(-bottomViewOffset)
                }
            }

            mTopViewOffset += offset
            topView.offsetTopAndBottom(offset)
            //更新TopBar的透明度
            var percent = mTopViewOffset * 1f / (bottom - mTopOffset)
            mTransitionView.alpha = 1f- percent
            percent = ((topView.top - (mTopBar?.height ?: 0)) / (mTopOffset - (mTopBar?.height ?: 0))).toFloat()
            if (percent > 1f) {
                percent = 1f
            }
            if (percent < 0f) {
                percent = 0f
            }
            setTopBarBackgroundAlpha(percent)
        }

    }

    private fun showResidualView() {
        startValueAnimation(mResidualView, 0, -(mResidualView?.layoutParams?.height ?: 0))
    }

    private fun closeTopView() {
        mTopView?.let {
            if (mCurrentStatus == STATE_CLOSING) {
                return
            }
            abortScrollerAnimation()
            isNewScroll = true
            val offset = mTopOffset - it.top
            mScroll.startScroll(0, 0, 0, offset, ANIMATION_DURATION.toInt())
            mTransitionView.visibility = View.VISIBLE
            mHeaderView?.visibility = View.VISIBLE

            if (mCurrentStatus == STATE_NAKED) {
                showHeaderView()
            }
            mCurrentStatus = STATE_CLOSING
            notifyListener()
            invalidate()
            if (mBottomBar != null && mBottomBar!!.translationY > 0) {
                showBottomBar()
            }

            if (mTopBar != null && mTopBar!!.translationY > 0) {
                showTopBar()
            }

        }
    }

    private fun openTopView() {
        mTopView?.let {
            if (mCurrentStatus == STATE_OPENING) {
                return
            }
            abortScrollerAnimation()
            isNewScroll = true
            val offset = bottom - it.top
            mScroll.startScroll(0, 0, 0, offset, ANIMATION_DURATION.toInt())
            mCurrentStatus = STATE_OPENING
            notifyListener()
            invalidate()

            hideBottomBar()

            if (mTopBar?.translationY?.toInt() == 0) {
                hideTopBar()
            } else {
                postDelayed({
                    if (mTopBar?.translationY?.toInt() == 0) {
                        hideTopBar()
                    }
                }, ANIMATION_DURATION)
            }
        }
    }

    private fun hideBottomBar() {
        startValueAnimation(mBottomBar, 0, (mBottomBar?.layoutParams?.height ?: 0))
    }

    private fun showBottomBar() {
        startValueAnimation(mBottomBar, (mBottomBar?.layoutParams?.height ?: 0), 0)
    }

    private fun hideTopBar() {
        startValueAnimation(mTopBar, 0, -(mTopBar?.layoutParams?.height ?: 0))
    }

    private fun showTopBar() {
        startValueAnimation(mTopBar, -(mTopBar?.layoutParams?.height ?: 0), 0)
    }

    private fun showHeaderView() {
        mBottomView?.let { bottomView ->
            mHeaderView?.let {
                startValueAnimation(mHeaderView, if (abs(bottomView.top) > it.height) -it.height else bottomView.top, 0)
            }
        }
    }

    private fun hideHeaderView() {

    }

    private fun offsetTopView(y: Int) {
        mTopView?.let {
            if (it.bottom >= bottom) {
                var offset = y - mLastY
                //判断是否越界
                if (it.bottom + offset < bottom) {
                    offset = bottom - it.bottom
                }
                //如果TopView未打开, 并且是向下滑动, 则加一个阻尼效果
                if (offset > 0 && it.top > mTopOffset) {
                    offset /= 2
                }
                //更新需要联动的view
                offsetChildView(offset)
                val newState = if (it.top <= top) STATE_COVER else STATE_HALF
                if (mCurrentStatus != newState) {
                    mCurrentStatus = newState
                    notifyListener()
                }
            }
        }
        mLastY = y
    }

    private fun offsetBottomView(y: Int) {
        mBottomView?.let {
            if (it.bottom >= bottom && it.top <= top) {
                var offset = y - mLastY
                if (it.bottom + offset < bottom) {
                    offset = bottom - it.bottom
                } else if (it.top + offset > top) {
                    offset = top - it.top
                }

                mBottomViewOffset += offset
                it.offsetTopAndBottom(offset)
                mTransitionView.offsetTopAndBottom(-offset)
            }
        }
        mLastY = y
    }

    private fun startValueAnimation(target: View?, startY: Int, endY: Int) {
        val animator = ValueAnimator.ofInt(startY, endY).apply {
            duration = ANIMATION_DURATION
            addUpdateListener {
                target?.translationY = (it.animatedValue as Int).toFloat()
            }
            start()
        }
    }


    interface OnStateChangedListener {
        fun onStateChanged(newState: Int)
    }

}