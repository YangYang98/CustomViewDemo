package com.yang.customviewdemo.ui.widget.timeDisk

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.yang.customviewdemo.utils.getScreenHeight
import com.yang.customviewdemo.utils.getScreenWidth
import com.yang.customviewdemo.utils.sp2px
import java.lang.ref.WeakReference
import java.util.Calendar


/**
 * Create by Yang Yang on 2023/10/17
 */
class TimeDiskView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ViewGroup(context, attrs, defStyleAttr) {

    private var screenWidth = 0
    private var screenHeight = 0

    private var amPmDiskView: AmPmDiskView
    private var hourDiskView: HourDiskView
    private var minuteDiskView: MinuteDiskView
    private var secondDiskView: SecondDiskView
    private val tvTime: TextView = TextView(context)

    var hour24: Int = 1
        set(value) {
            field = if (value == 24) {
                0
            } else {
                value
            }
        }
    var hour: Int = 1
    var minute: Int = 0
    var second: Int = 0
    var isNoon = false

    //是否关闭时间走时
    var isStop = true

    var onTimeChangedListener : OnTimeChangedListener? = null
        set(value) {
            field = value
        }

    private val handler: MyHandler by lazy { MyHandler(Looper.getMainLooper(), this) }

    companion object {
        const val MSG_GET_CUR_TIME = 0x1001
        const val MSG_UPDATE_TV_TIME = 0x1002

        class MyHandler(looper: Looper, timeDiskView: TimeDiskView) : Handler(looper) {
            private val mWeakReference: WeakReference<TimeDiskView>
            init {
                mWeakReference = WeakReference(timeDiskView)
            }

            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                val timeDiskView = mWeakReference.get() ?: return

                when (msg.what) {
                    MSG_UPDATE_TV_TIME -> {
                        if (timeDiskView.isNoon) {
                            timeDiskView.hour24 = timeDiskView.hour + 12
                        } else {
                            timeDiskView.hour24 = timeDiskView.hour
                        }
                        timeDiskView.updateTvTime()
                    }
                    MSG_GET_CUR_TIME -> {
                        timeDiskView.showCurrentTime()
                        if (!timeDiskView.isStop) {
                            sendEmptyMessageDelayed(MSG_GET_CUR_TIME, 1000)
                            timeDiskView.handler.sendEmptyMessage(MSG_UPDATE_TV_TIME)
                        }
                    }
                }
            }
        }
    }


    init {
        screenWidth = getScreenWidth(context)
        screenHeight = getScreenHeight(context)

        amPmDiskView = AmPmDiskView(context, ((screenWidth * 0.8 / 2) / 3).toInt())
        hourDiskView = HourDiskView(context, (screenWidth * 0.8 / 2).toInt())
        val hourLp = LayoutParams((screenWidth * 0.8).toInt(), (screenWidth * 0.8).toInt())
        hourDiskView.layoutParams = hourLp

        minuteDiskView = MinuteDiskView(context, (screenWidth * 1.1 / 2).toInt())
        val minuteLp = LayoutParams((screenWidth * 1.1).toInt(), (screenWidth * 1.1).toInt())
        minuteDiskView.layoutParams = minuteLp

        secondDiskView = SecondDiskView(context, (screenWidth * 1.2 / 2).toInt())
        val secondLp = LayoutParams((screenWidth * 1.2).toInt(), (screenWidth * 1.2).toInt())
        secondDiskView.layoutParams = minuteLp

        val tvTimeLp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            width = 2000
        }
        tvTime.apply {
            textSize = 20f.sp2px
            setTextColor(Color.BLACK)
            text = "00:00:00"
            layoutParams = tvTimeLp
            setBackgroundColor(0x00000000)

            val widthMeasureSpec = MeasureSpec.makeMeasureSpec((1 shl 30) - 1, MeasureSpec.AT_MOST)
            val heightMeasureSpec = MeasureSpec.makeMeasureSpec((1 shl 30) - 1, MeasureSpec.AT_MOST)
            measure(widthMeasureSpec, heightMeasureSpec)
        }

        addView(secondDiskView)
        addView(minuteDiskView)
        addView(hourDiskView)
        addView(amPmDiskView)

        addView(tvTime)

        hourDiskView.isNeedReturn = false
        minuteDiskView.isNeedReturn = false
        secondDiskView.isNeedReturn = false
        secondDiskView.visibility = View.GONE

        initListeners()
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        secondDiskView.layout(
            (screenWidth / 2 - secondDiskView.mRadius).toInt(),
            (hourDiskView.mRadius / 2 - secondDiskView.mRadius).toInt(),
            (screenWidth / 2 + secondDiskView.mRadius).toInt(),
            (secondDiskView.mRadius * 2 - hourDiskView.mRadius / 2 + secondDiskView.mRadius).toInt()
        )

        minuteDiskView.layout(
            (screenWidth / 2 - minuteDiskView.mRadius).toInt(),
            (hourDiskView.mRadius / 2 - minuteDiskView.mRadius).toInt(),
            (screenWidth / 2 + minuteDiskView.mRadius).toInt(),
            (minuteDiskView.mRadius * 2 - hourDiskView.mRadius / 2 + secondDiskView.mRadius).toInt()
        )

        hourDiskView.layout(
            (screenWidth / 2 - hourDiskView.mRadius).toInt(),
            (-hourDiskView.mRadius / 2).toInt(),
            (screenWidth / 2 + hourDiskView.mRadius).toInt(),
            (hourDiskView.mRadius * 2 - hourDiskView.mRadius / 2).toInt()
        )

        amPmDiskView.layout(
            (screenWidth / 2 - amPmDiskView.mRadius).toInt(),
            (hourDiskView.mRadius / 2 - amPmDiskView.mRadius).toInt(),
            (screenWidth / 2 + amPmDiskView.mRadius).toInt(),
            (hourDiskView.mRadius / 2 + amPmDiskView.mRadius).toInt()
        )

        tvTime.layout(
            screenWidth / 2 - tvTime.measuredWidth / 2,
            screenHeight / 2,
            screenWidth / 2 + tvTime.measuredWidth / 2,
            screenHeight / 2 + tvTime.measuredHeight
        )
    }

    private fun initListeners() {
        amPmDiskView.onAPMChangedListener = object : AmPmDiskView.OnAPMChangedListener {
            override fun onAPMChanged(str: String) {
                if (str == AmPmDiskView.AM) {
                    hour24 = hour - 12
                    isNoon = false
                } else {
                    hour24 = hour + 12
                    if (hour24 == 24) {
                        hour24 = 0
                    }
                    isNoon = true
                }
                handler.sendEmptyMessage(MSG_UPDATE_TV_TIME)
            }
        }

        hourDiskView.onHourChangedListener = object : HourDiskView.OnHourChangedListener {
            override fun onHourChanged(hour: Int) {
                this@TimeDiskView.hour = hour
                onTimeChangedListener?.onTimeChanged(isNoon, hour, minute, second)
                handler.sendEmptyMessage(MSG_UPDATE_TV_TIME)
            }
        }
        minuteDiskView.onMinuteChangedListener = object : MinuteDiskView.OnMinuteChangedListener {
            override fun onMinuteChanged(minute: Int) {
                this@TimeDiskView.minute = minute
                onTimeChangedListener?.onTimeChanged(isNoon, hour, minute, second)
                handler.sendEmptyMessage(MSG_UPDATE_TV_TIME)
            }
        }
    }

    interface OnTimeChangedListener {
        fun onTimeChanged(isAm: Boolean, hour: Int, minute: Int, second: Int)
    }

    fun start() {
        hourDiskView.isNeedReturn = true
        minuteDiskView.isNeedReturn = true
        secondDiskView.isNeedReturn = true
        secondDiskView.visibility = View.VISIBLE
        if (isStop) {
            handler.sendEmptyMessageDelayed(MSG_GET_CUR_TIME, 100)
        }
        isStop = false
    }

    fun showCurrentTime() {
        Calendar.getInstance().run {
            isNoon = this.get(Calendar.AM_PM) == 1
            hour24 = this.get(Calendar.HOUR_OF_DAY)
            hour = this.get(Calendar.HOUR)
            if (hour == 0) hour = 12
            minute = this.get(Calendar.MINUTE)
            second = this.get(Calendar.SECOND)

            amPmDiskView.centerString = if (isNoon) AmPmDiskView.PM else AmPmDiskView.AM
            hourDiskView.setCurrentHour(hour)
            minuteDiskView.setCurrentMinute(minute)
            secondDiskView.setCurrentSecond(second + 1)
        }
        updateTvTime()
    }

    fun updateTvTime() {
        tvTime.text = if (hour24 >= 10) {
            if (minute >= 10) {
                if (second >= 10) {
                    "${hour24}:${minute}:${second}"
                } else {
                    "${hour24}:${minute}:0${second}"
                }
            } else {
                if (second >= 10) {
                    "${hour24}:0${minute}:${second}"
                } else {
                    "${hour24}:0${minute}:0${second}"
                }
            }
        } else {
            if (minute >= 10) {
                if (second >= 10) {
                    "0${hour24}:${minute}:${second}"
                } else {
                    "0${hour24}:${minute}:0${second}"
                }
            } else {
                if (second >= 10) {
                    "0${hour24}:0${minute}:${second}"
                } else {
                    "0${hour24}:0${minute}:0${second}"
                }
            }
        }
    }

    fun showTime(hour24: Int, minute: Int, second: Int) {
        this.hour24 = hour24
        this.minute = minute
        this.second = second
        hour = if (this.hour24 > 12) {
            hour24 - 12
        } else {
            hour24
        }
        isNoon = this.hour24 > 12
        amPmDiskView.centerString = if (isNoon) AmPmDiskView.PM else AmPmDiskView.AM
        hourDiskView.setCurrentHour(hour)
        minuteDiskView.setCurrentMinute(minute)
        secondDiskView.setCurrentSecond(second + 1)
        handler.sendEmptyMessage(MSG_UPDATE_TV_TIME)
    }
}