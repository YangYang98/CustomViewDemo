package com.yang.customviewdemo.ui.activity.customer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yang.customviewdemo.databinding.ActivityIrisDiaphragmBinding


/**
 * 光圈效果
 *
 * Create by Yang Yang on 2023/8/11
 */
class IrisDiaphragmActivity : AppCompatActivity(){

    private lateinit var mBinding: ActivityIrisDiaphragmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityIrisDiaphragmBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


    }
}