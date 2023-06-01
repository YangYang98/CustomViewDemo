package com.example.mpchartdemo.ui.activity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mpchartdemo.R
import com.example.mpchartdemo.data.SimpleLineChartData
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet


/**
 * Create by Yang Yang on 2023/6/1
 */
class SimpleChartActivity : AppCompatActivity() {

    private val lineChart: LineChart by lazy { findViewById(R.id.line_chart) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_simple_chart)


        val entries = ArrayList<Entry>()
        generateLineChartData().forEach {
            entries.add(Entry(it.valueX.toFloat(), it.valueY.toFloat()))
        }
        val lineDataSet = LineDataSet(entries, "LineChart")
        lineDataSet.apply {
            color = Color.CYAN
            valueTextColor = Color.BLACK
        }
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData
        lineChart.setDrawBorders(false)

        lineChart.invalidate()
    }

    private fun generateLineChartData(): List<SimpleLineChartData> {

        val data = mutableListOf<SimpleLineChartData>()
        val firstData = SimpleLineChartData()
        firstData.valueX = 0.0
        firstData.valueY = (0..10).random().toDouble()
        data.add(firstData)
        for (i in 1..50) {
            val lineChartData = SimpleLineChartData()
            lineChartData.valueX = i.toDouble()
            lineChartData.valueY = data[i - 1].valueY + (-30..30).random()

            data.add(lineChartData)
        }

        return data
    }
}