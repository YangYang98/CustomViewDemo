package com.yang.customviewdemo.ui.activity.customer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.yang.customviewdemo.databinding.ActivityBookPageViewBinding
import com.yang.customviewdemo.ui.widget.BookPageView


/**
 * Create by Yang Yang on 2023/8/14
 */
class BookPageViewActivity: AppCompatActivity() {

    private lateinit var mBinding: ActivityBookPageViewBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityBookPageViewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.viewBookPage.apply {
            setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (event.y < this.viewHeight / 2) {
                            this.setTouchPoint(event.x, event.y, BookPageView.STYLE_TOP_RIGHT)
                        } else if (event.y >= this.viewHeight / 2) {
                            this.setTouchPoint(event.x, event.y, BookPageView.STYLE_BOTTOM_RIGHT)
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        this.setTouchPoint(event.x, event.y)
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