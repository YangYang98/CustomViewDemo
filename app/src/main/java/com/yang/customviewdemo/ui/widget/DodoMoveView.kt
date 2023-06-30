package com.yang.customviewdemo.ui.widget
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup.MarginLayoutParams
import androidx.appcompat.widget.AppCompatTextView


/**
 * Create by Yang Yang on 2023/6/29
 */
class DodoMoveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var lastX = 0
    private var lastY = 0
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.rawX.toInt()
        val y = event.rawY.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {}
            MotionEvent.ACTION_MOVE -> {
                val layoutParams = layoutParams as MarginLayoutParams
                val left = layoutParams.leftMargin + x - lastX
                val top = layoutParams.topMargin + y - lastY
                layoutParams.leftMargin = left
                layoutParams.topMargin = top.coerceAtLeast(0)
                setLayoutParams(layoutParams)
                requestLayout()
            }

            MotionEvent.ACTION_UP -> {}
        }
        lastX = x
        lastY = y
        return true
    }
}