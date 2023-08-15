package com.yang.customviewdemo.ui.activity.customer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yang.customviewdemo.databinding.ActivityBookPageViewBinding
import com.yang.customviewdemo.ui.widget.BookPageView


/**
 * Create by Yang Yang on 2023/8/14
 */
class BookPageViewActivity: AppCompatActivity() {

    private lateinit var mBinding: ActivityBookPageViewBinding

    private var currentStyle = ""

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityBookPageViewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.viewBookPage.apply {
            setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val touchX = event.x
                        val touchY = event.y

                        if (touchX <= this.viewWidth / 3) { // 左
                            currentStyle = BookPageView.STYLE_LEFT
                            this.setTouchPoint(touchX, touchY, currentStyle)
                        } else if (touchX > this.viewWidth / 3 && touchY <= this.viewHeight / 3) { //上
                            currentStyle = BookPageView.STYLE_TOP_RIGHT
                            this.setTouchPoint(touchX, touchY, currentStyle)
                        } else if (touchX > this.viewWidth * 2 / 3 && touchY > this.viewHeight / 3 && touchY <= this.viewHeight * 2 / 3) { //右
                            currentStyle = BookPageView.STYLE_RIGHT
                            this.setTouchPoint(touchX, touchY, currentStyle)
                        } else if (touchX > this.viewWidth / 3 && touchY > this.viewHeight * 2 / 3) {
                            currentStyle = BookPageView.STYLE_BOTTOM_RIGHT
                            this.setTouchPoint(touchX, touchY, currentStyle)
                        } else if (touchX > this.viewWidth / 3 && touchX < this.viewWidth * 2 / 3 && touchY > this.viewHeight / 3 && touchY < this.viewHeight * 2 / 3) {
                            currentStyle = BookPageView.STYLE_MIDDLE
                            Toast.makeText(this@BookPageViewActivity,"点击了中部", Toast.LENGTH_SHORT).show();
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        this.setTouchPoint(event.x, event.y, currentStyle)
                    }
                    MotionEvent.ACTION_UP -> {
                        this.reset()
                    }
                }

                return@setOnTouchListener false
            }
        }
    }
}