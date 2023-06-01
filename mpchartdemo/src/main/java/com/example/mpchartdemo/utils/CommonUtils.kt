package com.example.mpchartdemo.utils

import android.content.Context
import android.util.TypedValue
import androidx.core.content.ContextCompat


/**
 * Create by Yang Yang on 2023/6/1
 */
fun getColorById(context: Context, colorId: Int): Int {
    return ContextCompat.getColor(context, colorId)
}

fun sp2px(context: Context, spValue: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, spValue,
        context.resources.displayMetrics
    ).toInt()
}