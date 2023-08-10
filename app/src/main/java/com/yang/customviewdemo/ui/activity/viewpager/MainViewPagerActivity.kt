package com.yang.customviewdemo.ui.activity.viewpager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yang.customviewdemo.databinding.ActivityMainViewPagerBinding


/**
 * Create by Yang Yang on 2023/8/10
 */
class MainViewPagerActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainViewPagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainViewPagerBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        mBinding.btnPalette.setOnClickListener {
            startActivity(Intent(this, PaletteActivity::class.java))
        }
    }
}