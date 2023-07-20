package com.yang.customviewdemo.ui.activity.customer.drawable

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.yang.customviewdemo.R
import com.yang.customviewdemo.databinding.ActivityCircleImageDrawableBinding


/**
 * Create by Yang Yang on 2023/7/20
 */
class CircleImageDrawableActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityCircleImageDrawableBinding = DataBindingUtil.setContentView(this, R.layout.activity_circle_image_drawable)

        binding.apply {

            val roadBitmap = BitmapFactory.decodeResource(resources,
                R.drawable.ic_woniu
            )
            val roundImageDrawable = CircleImageDrawable(roadBitmap)
            imgCircleImageMax.setImageDrawable(roundImageDrawable)
            imgCircleImage.apply {
                scaleType = ImageView.ScaleType.FIT_XY
                setImageDrawable(roundImageDrawable)
            }
        }
    }
}