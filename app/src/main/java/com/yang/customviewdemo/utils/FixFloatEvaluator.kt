package com.yang.customviewdemo.utils

import android.animation.FloatEvaluator


/**
 * 修复startValue和endValue值太小精确不到目标值的问题
 *
 *
 *
 * Create by Yang Yang on 2023/8/3
 */
class FixFloatEvaluator: FloatEvaluator() {

    override fun evaluate(fraction: Float, startValue: Number, endValue: Number): Float {
        if (fraction == 0f) {
            return startValue.toFloat()
        }
        if (fraction == 1f) {
            return endValue.toFloat()
        }
        return super.evaluate(fraction, startValue, endValue)
    }
}