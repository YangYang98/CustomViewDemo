package com.yang.customviewdemo.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup


/**
 * Create by Yang Yang on 2023/4/26
 */
class TestViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        for (i in 0..childCount) {
            val child = getChildAt(i)
            val width = child.measuredWidth
            val height = child.measuredHeight

            val left = (r - width) / 2
            val top = (b - height) / 2
            val right = left + width
            val bottom = top + height

            child.layout(left, top, right, bottom)
        }
    }
}