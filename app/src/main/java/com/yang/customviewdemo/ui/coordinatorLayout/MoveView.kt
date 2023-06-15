package com.yang.customviewdemo.ui.coordinatorLayout

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.ViewCompat


/**
 * Create by Yang Yang on 2023/6/15
 */
class MoveView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var lastOffset = OffSet(0f, 0f)
    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastOffset = OffSet(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - lastOffset.x
                val dy = event.y - lastOffset.y

                ViewCompat.offsetLeftAndRight(this, dx.toInt())
                ViewCompat.offsetTopAndBottom(this, dy.toInt())
            }
        }

        return true
    }
}

data class OffSet(
    var x: Float, var y: Float
)