package com.yang.customviewdemo.ui.activity.customer.drawable

import android.os.Bundle
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.yang.customviewdemo.R
import com.yang.customviewdemo.databinding.ActivityArrowDrawableBinding


/**
 * Create by Yang Yang on 2023/7/25
 */
class ArrowDrawableActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityArrowDrawableBinding = DataBindingUtil.setContentView(this, R.layout.activity_arrow_drawable)

        binding.apply {

            val arrowDrawable = ArrowDrawable.create(btnArrow)
            btnArrow.background = arrowDrawable

            durationBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    arrowDrawable.progress = (progress).toFloat() / seekBar.max
                    arrowDrawable.fire()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    
                }

            })
        }
    }
}