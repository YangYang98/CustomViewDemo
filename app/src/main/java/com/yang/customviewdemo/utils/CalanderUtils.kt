package com.yang.customviewdemo.utils


/**
 * Create by Yang Yang on 2023/7/11
 */

private val NUMBER_TEXT_LIST = listOf(
    "日",
    "一",
    "二",
    "三",
    "四",
    "五",
    "六",
    "七",
    "八",
    "九",
    "十"
)

fun Int.toText(): String {
    var result = ""
    val iArr = "$this".toCharArray().map { it.toString().toInt() }

    //处理 10，11，12.. 20，21，22.. 等情况
    if (iArr.size > 1) {
        if (iArr[0] != 1) {
            result += NUMBER_TEXT_LIST[iArr[0]]
        }
        result += "十"
        if (iArr[1] > 0) {
            result += NUMBER_TEXT_LIST[iArr[1]]
        }
    } else {
        result = NUMBER_TEXT_LIST[iArr[0]]
    }

    return result
}