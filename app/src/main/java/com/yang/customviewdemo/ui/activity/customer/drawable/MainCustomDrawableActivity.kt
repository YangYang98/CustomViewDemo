package com.yang.customviewdemo.ui.activity.customer.drawable

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.yang.customviewdemo.R
import com.yang.customviewdemo.databinding.ActivityMainCustomDrawableBinding


/**
 * Create by Yang Yang on 2023/7/20
 */
class MainCustomDrawableActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainCustomDrawableBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_custom_drawable)

        binding.apply {

            btnRoundImage.setOnClickListener {
                startActivity(Intent(this@MainCustomDrawableActivity, RoundImageDrawableActivity::class.java))
            }

            btnCircleImage.setOnClickListener {
                startActivity(Intent(this@MainCustomDrawableActivity, CircleImageDrawableActivity::class.java))
            }

            btnStateDrawable.setOnClickListener {
                startActivity(Intent(this@MainCustomDrawableActivity, StateDrawableActivity::class.java))
            }
        }
    }
}