package com.yang.customviewdemo.ui.activity.customer.drawable

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.yang.customviewdemo.R
import com.yang.customviewdemo.databinding.ActivityFishDrawableBinding


/**
 * Create by Yang Yang on 2023/7/21
 */
class FishDrawableActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityFishDrawableBinding = DataBindingUtil.setContentView<ActivityFishDrawableBinding?>(this, R.layout.activity_fish_drawable).apply {
            imgFish.setImageDrawable(FishDrawable())
        }


    }
}