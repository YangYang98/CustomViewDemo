package com.yang.customviewdemo.ui.activity.layoutInflate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.yang.customviewdemo.R


/**
 * LayoutInflater inflate 方法参数的应用，记住下面这个规律：
 *
 * 当传入的 root 不为 null 且 attachToRoot 为 false，此时会给 Xml 布局生成的根 View 设置布局参数
 * 当传入的 root 不为 null 且 attachToRoot 为 true，此时会将 Xml 布局生成的根 View 通过 addView 方法携带布局参数添加到 root 中
 * 当传入的 root 为 null ，此时会将 Xml 布局生成的根 View 对象直接返回
 *
 * Create by Yang Yang on 2023/7/18
 */
class TestLayoutInflateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_test_layout_inflate)

        val consMain = findViewById<ConstraintLayout>(R.id.cons_main)
        //val itemMain = layoutInflater.inflate(R.layout.item_main, null)
        val itemMain = layoutInflater.inflate(R.layout.item_main, consMain, true)
        //val itemMain = layoutInflater.inflate(R.layout.item_main, consMain, false)
        //consMain.addView(itemMain)

    }
}