package com.example.mpchartdemo.ui.activity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.MotionEvent
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mpchartdemo.R
import com.example.mpchartdemo.data.SimpleLineChartData
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate


/**
 * Create by Yang Yang on 2023/6/1
 */
class SimpleChartActivity : AppCompatActivity() {

    private val simpleLineChart: LineChart by lazy { findViewById(R.id.simple_line_chart) }

    private val simplePieChart: PieChart by lazy { findViewById(R.id.simple_pie_chart) }
    private val simplePieChartCountSeekbar: SeekBar by lazy { findViewById(R.id.seekbar_simple_pie_chart_count) }
    private val simplePieChartRangeSeekbar: SeekBar by lazy { findViewById(R.id.seekbar_simple_pie_chart_range) }

    private val simpleBarChart: BarChart by lazy { findViewById(R.id.simple_bar_chart) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_simple_chart)


        initSimpleLineChart()
        initSimplePieChart()

        initSimpleBarChart()
    }

    private fun initSimpleBarChart() {

    }

    private fun initSimplePieChart() {
        simplePieChart.apply {
            setUsePercentValues(true) //使用百分比显示
            setExtraOffsets(5f, 10f, 5f, 5f) //设置图表上下左右的偏移，类似于外边距
            dragDecelerationFrictionCoef = 0.95f //饼图拖动减速摩擦系数的方法.当用户手指在饼图上滑动时，滑动到放开手指后，饼图会自动减速并渐渐停止移动。这个减速过程的速度就是通过设置该方法中的参数来控制的
            setCenterTextTypeface(Typeface.createFromAsset(assets, "din_medium.otf")) //设置PieChart中间文字的字体
            centerText = "PieChart" //设置PieChart中间文字的内容
            setCenterTextColor(Color.parseColor("#000000")) //设置PieChart中间文字的颜色
            setCenterTextSize(16f) //设置PieChart中间文字的大小
            setDrawCenterText(true)

            //isDrawHoleEnabled = false // 是否要将PieChart设为一个圆环状
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE) //设置中间圆环的颜色
            holeRadius = 58f //设置中间圆环的半径

            setTransparentCircleColor(Color.BLACK) //设置中间圆环边缘的透明度
            setTransparentCircleAlpha(50) //设置中间圆环边缘的透明度
            transparentCircleRadius = 61f //设置中间圆的半透明圆环的半径

            rotationAngle = 0f //设置PieChart旋转的角度，即设置图表初始化时第一块数据显示的位置
            isRotationEnabled = true //是否可以旋转
            isHighlightPerTapEnabled = true //是否可以选中高亮显示

            //当Item被选中的时候的监听器
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    Toast.makeText(this@SimpleChartActivity, "${e?.data ?: "aaa"}", Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected() {

                }
            })

            //监听手势的监听器
            onChartGestureListener = object : OnChartGestureListener {
                override fun onChartGestureStart(
                    me: MotionEvent?,
                    lastPerformedGesture: ChartTouchListener.ChartGesture?
                ) {

                }

                override fun onChartGestureEnd(
                    me: MotionEvent?,
                    lastPerformedGesture: ChartTouchListener.ChartGesture?
                ) {

                }

                override fun onChartLongPressed(me: MotionEvent?) {

                }

                override fun onChartDoubleTapped(me: MotionEvent?) {

                }

                override fun onChartSingleTapped(me: MotionEvent?) {

                }

                override fun onChartFling(
                    me1: MotionEvent?,
                    me2: MotionEvent?,
                    velocityX: Float,
                    velocityY: Float
                ) {

                }

                override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {

                }

                override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {

                }

            }
        }

        var simplePieChartCount: Int = 5
        var simplePieChartRange: Float = 10f
        simplePieChartCountSeekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                simplePieChartCount = progress
                setPieChartData(simplePieChartCount, simplePieChartRange)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        simplePieChartRangeSeekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                simplePieChartRange = progress.toFloat()
                setPieChartData(simplePieChartCount, simplePieChartRange)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        setPieChartData(simplePieChartCount, simplePieChartRange)
    }

    private fun setPieChartData(count: Int, range: Float) {
        val mult = range
        val yVals1 = mutableListOf<PieEntry>()
        for (i in 0 until count) {
            yVals1.add(PieEntry((Math.random() * mult).toFloat(), i.toFloat()))
        }

        val xVals1 = ArrayList<String>()
        for (i in 0 until count) {
            xVals1.add("" + i)
        }

        val pieDataSet = PieDataSet(yVals1, "SimplePieChart")
        pieDataSet.apply {
            sliceSpace = 3f // 设置不同DataSet之间的间距
            selectionShift = 7f // 设置Item被选中时变化的距离
        }

        val colors = ArrayList<Int>()
        ColorTemplate.VORDIPLOM_COLORS.forEach {
            colors.add(it)
        }
        ColorTemplate.JOYFUL_COLORS.forEach {
            colors.add(it)
        }
        ColorTemplate.COLORFUL_COLORS.forEach {
            colors.add(it)
        }
        ColorTemplate.LIBERTY_COLORS.forEach {
            colors.add(it)
        }
        ColorTemplate.PASTEL_COLORS.forEach {
            colors.add(it)
        }
        colors.add(ColorTemplate.getHoloBlue())
        pieDataSet.colors = colors //为DataSet中的数据匹配上颜色

        val pieData = PieData(pieDataSet)
        pieData.apply {
            setValueFormatter(PercentFormatter())
            setValueTextSize(11f)
            setValueTextColor(Color.WHITE)
        }
        simplePieChart.data = pieData
        simplePieChart.highlightValue(null)
        simplePieChart.invalidate()
    }

    private fun initSimpleLineChart() {
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
        simpleLineChart.data = lineData
        simpleLineChart.setDrawBorders(false)

        simpleLineChart.invalidate()
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