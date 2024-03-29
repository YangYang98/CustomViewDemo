package com.yang.customviewdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.yang.customviewdemo.ui.activity.AntFundActivity
import com.yang.customviewdemo.ui.activity.customer.MainCustomViewActivity
import com.yang.customviewdemo.ui.activity.customer.drawable.MainCustomDrawableActivity
import com.yang.customviewdemo.ui.activity.layoutInflate.TestLayoutInflateActivity
import com.yang.customviewdemo.ui.activity.test.TestJsonRecyclerViewActivity
import com.yang.customviewdemo.ui.activity.viewpager.MainViewPagerActivity
import com.yang.customviewdemo.ui.coordinatorLayout.CustomerBehaviorActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*val arcView = findViewById<ArcView>(R.id.arcView)

        findViewById<SeekBar>(R.id.progressBar).setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                arcView.process = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })*/

        findViewById<Button>(R.id.btn_fund_chart).setOnClickListener {
            startActivity(Intent(this, AntFundActivity::class.java))
        }

        findViewById<Button>(R.id.btn_coordinator_layout).setOnClickListener {
            startActivity(Intent(this, CustomerBehaviorActivity::class.java))
        }

        findViewById<Button>(R.id.btn_test).setOnClickListener {
            startActivity(Intent(this, TestJsonRecyclerViewActivity::class.java))
        }

        findViewById<Button>(R.id.btn_custom_view).setOnClickListener {
            startActivity(Intent(this, MainCustomViewActivity::class.java))
        }

        findViewById<Button>(R.id.btn_layout_inflate).setOnClickListener {
            startActivity(Intent(this, TestLayoutInflateActivity::class.java))
        }

        findViewById<Button>(R.id.btn_custom_drawable).setOnClickListener {
            startActivity(Intent(this, MainCustomDrawableActivity::class.java))
        }

        findViewById<Button>(R.id.btn_view_pager).setOnClickListener {
            startActivity(Intent(this, MainViewPagerActivity::class.java))
        }
    }
}