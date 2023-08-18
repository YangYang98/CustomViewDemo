package com.yang.customviewdemo.ui.activity.customer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yang.customviewdemo.databinding.ActivityHorizontalExpandMenuBinding


/**
 * Create by Yang Yang on 2023/8/17
 */
class HorizontalExpandMenuActivity: AppCompatActivity() {

    private lateinit var mBinding: ActivityHorizontalExpandMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityHorizontalExpandMenuBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

    }
}