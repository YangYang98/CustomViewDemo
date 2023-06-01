package com.yang.customviewdemo.data

import com.google.gson.annotations.SerializedName


/**
 * Create by Yang Yang on 2023/5/31
 */
class FundReturnRateBean (
    @SerializedName("data")
    var dayRateList: List<DayRateDetail>,
    @SerializedName("total")
    var totalReturnRate: TotalRate,
    var name: String,
    var success: Boolean,
    var totalCount: Int
)

class DayRateDetail (
    var benchQuote: String,
    var fundTypeYield: String,//同类平均收益率
    var indexYield: String,//沪深300收益率
    var date: String,//日期
    var yield: String//本基金收益率
)

class TotalRate (
    var totalYield: String,//本基金总收益率
    var totalIndexYield: String,//沪深300总收益率
    var totalFundTypeYield: String,//同类平均总收益率
    var totalBenchQuote: String
)