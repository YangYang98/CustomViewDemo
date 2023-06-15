package com.yang.customviewdemo.ui.coordinatorLayout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.yang.customviewdemo.R


/**
 * Create by Yang Yang on 2023/6/15
 */
class CustomerBehaviorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_customer_behavior_layout)

        findViewById<Button>(R.id.btn_simple_demo).setOnClickListener {
            startActivity(Intent(this, SimpleDemoActivity::class.java))
        }

        findViewById<Button>(R.id.btn_use_behavior).setOnClickListener {
            startActivity(Intent(this, UseBehaviorActivity::class.java))
        }
    }
}