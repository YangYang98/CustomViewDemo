package com.yang.customviewdemo.ui.activity.customer.drawable

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.yang.customviewdemo.R
import com.yang.customviewdemo.databinding.ActivityRoundImageDrawableBinding


/**
 * Create by Yang Yang on 2023/7/20
 */
class RoundImageDrawableActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityRoundImageDrawableBinding = DataBindingUtil.setContentView(this, R.layout.activity_round_image_drawable)

        binding.apply {

            val roadBitmap = BitmapFactory.decodeResource(resources,
                R.drawable.ic_woniu
            )
            val roundImageDrawable = RoundImageDrawable(roadBitmap)
            imgRoundImageMax.setImageDrawable(roundImageDrawable)
            imgRoundImage.setImageDrawable(roundImageDrawable)

            textRoundImage.background = roundImageDrawable
        }
    }
}