package com.example.mpchartdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.mpchartdemo.databinding.ActivityMainBinding
import com.example.mpchartdemo.ui.activity.CandleChartActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.btnCandleChart.setOnClickListener {
            startActivity(Intent(this, CandleChartActivity::class.java))
        }
    }
}