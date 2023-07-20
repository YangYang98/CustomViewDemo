package com.yang.customviewdemo.ui.activity.customer.drawable

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.yang.customviewdemo.R
import com.yang.customviewdemo.databinding.ActivityStateDrawableBinding


/**
 * Create by Yang Yang on 2023/7/20
 */
class StateDrawableActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityStateDrawableBinding = DataBindingUtil.setContentView(this, R.layout.activity_state_drawable)

        binding.apply {
            btnSwitch.setOnClickListener {
                stateDrawableView.mMessageReaded = !stateDrawableView.mMessageReaded
            }
        }
    }
}