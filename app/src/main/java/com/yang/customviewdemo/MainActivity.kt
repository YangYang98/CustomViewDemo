package com.yang.customviewdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.yang.customviewdemo.ui.activity.AntFundActivity
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
    }
}