package com.yang.customviewdemo.ui.activity.customer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yang.customviewdemo.databinding.ActivityRulerViewBinding
import com.yang.customviewdemo.ui.widget.HeightDialog


/**
 * Create by Yang Yang on 2023/8/3
 */
class RulerViewActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityRulerViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityRulerViewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnRulerDialog.setOnClickListener {
            HeightDialog(this).show()
        }
    }
}