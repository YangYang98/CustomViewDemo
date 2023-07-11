package com.yang.customviewdemo.ui.activity.customer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.yang.customviewdemo.R
import com.yang.customviewdemo.databinding.ActivityCustomTextClockBinding


/**
 * Create by Yang Yang on 2023/7/11
 */
class CustomTextClockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomTextClockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_custom_text_clock)

        binding.textClock.startTextClock(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.textClock.closeTextClock()
    }
}