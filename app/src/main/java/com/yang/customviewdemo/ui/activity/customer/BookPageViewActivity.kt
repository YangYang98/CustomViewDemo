package com.yang.customviewdemo.ui.activity.customer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yang.customviewdemo.databinding.ActivityBookPageViewBinding


/**
 * Create by Yang Yang on 2023/8/14
 */
class BookPageViewActivity: AppCompatActivity() {

    private lateinit var mBinding: ActivityBookPageViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityBookPageViewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }
}