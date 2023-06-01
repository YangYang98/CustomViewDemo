package com.example.mpchartdemo.ui.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mpchartdemo.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.LineDataSet
import java.text.DecimalFormat


/**
 * Create by Yang Yang on 2023/6/1
 */
class CandleChartActivity : AppCompatActivity() {

    private val cc: CombinedChart by lazy { findViewById(R.id.cc_kl) }
    private val bc: BarChart by lazy { findViewById(R.id.bc_kl) }

    private val xValues = mutableMapOf<Int, String>()
    private lateinit var lineSet5: LineDataSet
    private lateinit var lineSet10: LineDataSet
    private lateinit var candleSet: CandleDataSet
    private lateinit var combinedData: CombinedData
    private lateinit var barSet: BarDataSet

    private val format4p = DecimalFormat("0.0000")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_candle_chart)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initView()
    }

    private fun initView() {

        //initChart()
    }

}