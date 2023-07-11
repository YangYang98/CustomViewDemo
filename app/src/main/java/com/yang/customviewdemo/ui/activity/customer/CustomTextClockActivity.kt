package com.yang.customviewdemo.ui.activity.customer

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.yang.customviewdemo.R
import com.yang.customviewdemo.databinding.ActivityCustomTextClockBinding
import com.yang.customviewdemo.services.TextClockWallpaperService


/**
 * Create by Yang Yang on 2023/7/11
 */
class CustomTextClockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomTextClockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_custom_text_clock)

        binding.textClock.startTextClock(this)

        binding.btnSet.setOnClickListener {
            startActivity(Intent().apply {
                action = WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER
                putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, ComponentName(
                    this@CustomTextClockActivity.baseContext, TextClockWallpaperService::class.java
                ))
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.textClock.closeTextClock()
    }
}