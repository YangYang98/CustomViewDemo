package com.yang.customviewdemo.utils

import android.content.res.Resources
import android.util.TypedValue
import java.text.DecimalFormat


/**
 * Create by Yang Yang on 2023/5/31
 */
val Float.px: Float
    get() {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            Resources.getSystem().displayMetrics
        )
    }

val Int.dp: Int
    get() {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }

val Float.sp2px: Float
    get() {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            this,
            Resources.getSystem().displayMetrics
        )
    }

fun Number.toStringAsFixed(digits: Int, tailZero: Boolean = true): String {
    if (digits < 0) return this.toString()
    if (digits == 0) return this.toInt().toString()
    val stringBuffer = StringBuffer("0.")
    if (tailZero) {
        for (i in 0 until digits) {
            stringBuffer.append("0")
        }
    } else {
        for (i in 0 until digits) {
            stringBuffer.append("#")
        }
    }
    return DecimalFormat(stringBuffer.toString()).format(this)
}