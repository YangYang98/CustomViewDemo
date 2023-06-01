package com.yang.customviewdemo.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.yang.customviewdemo.R
import com.yang.customviewdemo.data.FundTestDataHelper
import com.yang.customviewdemo.data.FundTestDataHelper.Companion.LATEST_ONE_MONTH_DATA
import com.yang.customviewdemo.data.FundTestDataHelper.Companion.LATEST_ONE_YEAR_DATA
import com.yang.customviewdemo.data.FundTestDataHelper.Companion.LATEST_SIX_MONTH_DATA
import com.yang.customviewdemo.data.FundTestDataHelper.Companion.LATEST_THREE_MONTH_DATA
import com.yang.customviewdemo.data.FundTestDataHelper.Companion.LATEST_THREE_YEARS_DATA
import com.yang.customviewdemo.data.TotalRate
import com.yang.customviewdemo.databinding.ActivityAntFundBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Create by Yang Yang on 2023/5/31
 */
class AntFundActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAntFundBinding
    private val fundTestDataHelper by lazy {
        FundTestDataHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ant_fund)

        setRateData()

        binding.apply {
            latestOneYear.isSelected = true

            latestOneMonth.setOnClickListener(dateRangeClickListener)
            latestThreeMonths.setOnClickListener(dateRangeClickListener)
            latestSixMonths.setOnClickListener(dateRangeClickListener)
            latestOneYear.setOnClickListener(dateRangeClickListener)
            latestThreeYears.setOnClickListener(dateRangeClickListener)
        }
    }

    private fun setRateData(
        localJsonFileName: String = LATEST_ONE_YEAR_DATA, isFirstInit: Boolean = false
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (isFirstInit) {
                delay(200)
            }

            fundTestDataHelper.getFundReturnRateData(localJsonFileName).apply {

                withContext(Dispatchers.Main) {
                    binding.returnOnFoundsChart.setData(dayRateList, isShowWithAnimator = true)
                    setTotalRateData(totalReturnRate)
                }
            }
        }
    }

    private fun setTotalRateData(totalRate: TotalRate) {
        binding.apply {
            totalYieldTv.setTotalReturnRate(totalRate.totalYield)
            totalIndexYieldTv.setTotalReturnRate(totalRate.totalIndexYield)
            totalFundTypeTv.setTotalReturnRate(totalRate.totalFundTypeYield)
        }
    }

    private fun TextView.setTotalReturnRate(totalReturn: String) {
        this.text = if (totalReturn.contains("-")) "$totalReturn%" else "+$totalReturn%"
        this.setTextColor(
            ContextCompat.getColor(
                this@AntFundActivity, if (totalReturn.contains("-")) {
                    R.color.fund_total_rate_down_text_color
                } else {
                    R.color.fund_total_rate_raise_text_color
                }
            )
        )
    }

    private val dateRangeClickListener by lazy {
        View.OnClickListener { v ->
            binding.apply {
                latestOneMonth.isSelected = false
                latestThreeMonths.isSelected = false
                latestSixMonths.isSelected = false
                latestOneYear.isSelected = false
                latestThreeYears.isSelected = false

                val localJsonFileName = when (v.id) {
                    R.id.latestOneMonth -> {
                        latestOneMonth.isSelected = true
                        LATEST_ONE_MONTH_DATA
                    }
                    R.id.latestThreeMonths -> {
                        latestThreeMonths.isSelected = true
                        LATEST_THREE_MONTH_DATA
                    }
                    R.id.latestSixMonths -> {
                        latestSixMonths.isSelected = true
                        LATEST_SIX_MONTH_DATA
                    }
                    R.id.latestOneYear -> {
                        latestOneYear.isSelected = true
                        LATEST_ONE_YEAR_DATA
                    }
                    R.id.latestThreeYears -> {
                        latestThreeYears.isSelected = true
                        LATEST_THREE_YEARS_DATA
                    }
                    else -> LATEST_ONE_YEAR_DATA
                }
                setRateData(localJsonFileName)
            }
        }
    }
}