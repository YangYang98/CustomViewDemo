package com.yang.customviewdemo.ui.activity.customer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yang.customviewdemo.databinding.ActivityWaveProgressBinding


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
            waveProgress.setProgressNum(80f,3000)
        }

    }
}