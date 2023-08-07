package com.yang.customviewdemo.ui.widget

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.yang.customviewdemo.R
import com.yang.customviewdemo.databinding.DialogHeightRulerBinding


/**
 * Create by Yang Yang on 2023/8/3
 */
class HeightDialog(context: Context): Dialog(context, R.style.UIAlertViewStyle) {

    private lateinit var mBinding: DialogHeightRulerBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DialogHeightRulerBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        setCanceledOnTouchOutside(true)
        setCancelable(true)

        val windowManager = window?.windowManager
        val lp = window?.attributes
        lp?.let {
            //所有在这个window之后的会变暗,使用dimAmount属性来控制变暗的程度(1.0不透明,0.0完全透明)
            it.alpha = 1f
            it.dimAmount = .5f
        }
        window?.let {
            it.attributes = lp
            it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
        //设置窗口的占比
        val display = windowManager?.defaultDisplay
        (display?.height)?.div(2.2)?.let {
            window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, it.toInt())
        }
        window?.attributes?.gravity = Gravity.BOTTOM

        mBinding.rulerHeight.setTextChangedListener {
            mBinding.tvRegisterInfoHeightValue.text = it
        }

        mBinding.closeImage.setOnClickListener {
            this.dismiss()
        }
        mBinding.required.setOnClickListener {
            this.dismiss()
        }

    }
}