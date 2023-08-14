package com.yang.customviewdemo.ui.activity.customer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yang.customviewdemo.databinding.ActivityWaveProgressBinding
import com.yang.customviewdemo.ui.widget.WaveProgressView


/**
 * Create by Yang Yang on 2023/8/11
 */
class WaveProgressViewActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityWaveProgressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityWaveProgressBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.apply {
            waveProgress.apply {
                setProgressNum(80f,3000)

                onAnimationListener = object : WaveProgressView.OnAnimationListener {
                    override fun howToChangeWaveHeight(percent: Float, defaultHeight: Float): Float {
                        return (1 - percent) * defaultHeight
                    }
                }
            }
        }

    }
}