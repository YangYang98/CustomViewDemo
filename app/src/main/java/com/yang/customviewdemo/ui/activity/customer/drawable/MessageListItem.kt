package com.yang.customviewdemo.ui.activity.customer.drawable

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.yang.customviewdemo.R


/**
 * Create by Yang Yang on 2023/7/20
 */
class MessageListItem @JvmOverloads constructor(context: Context,
                      attrs: AttributeSet? = null,
                      defStyleAttr: Int = 0
): RelativeLayout(context, attrs, defStyleAttr) {

    companion object {
        private val STATE_MESSAGE_READED: IntArray = IntArray(1) { R.attr.state_message_readed }
    }

    var mMessageReaded = false
        set(value) {
            if (field != value) {
                field = value
                refreshDrawableState()
            }
        }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        if (mMessageReaded) {
            val drawableState = super.onCreateDrawableState(extraSpace + 1)
            mergeDrawableStates(drawableState, STATE_MESSAGE_READED)
            return drawableState
        }
        return super.onCreateDrawableState(extraSpace)
    }
}