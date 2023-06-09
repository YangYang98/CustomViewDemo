package com.yang.customviewdemo.ui.coordinatorLayout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.yang.customviewdemo.R
import com.yang.customviewdemo.ui.activity.test.SuspendedLayoutActivity
import com.yang.customviewdemo.ui.activity.test.TestDispatchTouchActivity
import com.yang.customviewdemo.ui.activity.test.TestDispatchTouchActivity2
import com.yang.customviewdemo.ui.activity.test.TestDispatchTouchActivity3


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

        findViewById<Button>(R.id.btn_custom_behavior).setOnClickListener {
            startActivity(Intent(this, CustomBehaviorActivity::class.java))
        }

        findViewById<Button>(R.id.btn_custom_behavior_2).setOnClickListener {
            startActivity(Intent(this, CustomBehaviorActivity2::class.java))
        }

        findViewById<Button>(R.id.btn_custom_behavior_3).setOnClickListener {
            startActivity(Intent(this, CustomBehaviorActivity3::class.java))
        }

        findViewById<Button>(R.id.btn_test).setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java))
        }

        findViewById<Button>(R.id.btn_two_stick).setOnClickListener {
            startActivity(Intent(this, TwoStickActivity::class.java))
        }

        findViewById<Button>(R.id.btn_test_dispatch_touch).setOnClickListener {
            startActivity(Intent(this, TestDispatchTouchActivity::class.java))
        }

        findViewById<Button>(R.id.btn_test_dispatch_touch_2).setOnClickListener {
            startActivity(Intent(this, TestDispatchTouchActivity2::class.java))
        }

        findViewById<Button>(R.id.btn_test_dispatch_touch_3).setOnClickListener {
            startActivity(Intent(this, TestDispatchTouchActivity3::class.java))
        }

        findViewById<Button>(R.id.btn_suspended_layout).setOnClickListener {
            startActivity(Intent(this, SuspendedLayoutActivity::class.java))
        }
    }
}