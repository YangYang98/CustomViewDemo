package com.yang.customviewdemo.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Create by Yang Yang on 2023/6/20
 */
class StartEndMargin20ItemDecoration : RecyclerView.ItemDecoration {
    companion object {
        const val HORIZONTAL_ONLY = LinearLayoutManager.HORIZONTAL
        const val VERTICAL_ONLY = LinearLayoutManager.VERTICAL
        const val GRID = 2
    }

    private var mDivider: Drawable? = null
    private var mOrientation = LinearLayoutManager.VERTICAL
    private var mShowHeaderDivider: Boolean = false
    private var mShowFooterDivider: Boolean = false

    constructor(drawable: Drawable) {
        mDivider = drawable
    }

    constructor(context: Context, @DrawableRes drawableRes: Int) {
        mDivider = ContextCompat.getDrawable(context, drawableRes)
    }

    fun setOrientation(orientation: Int) {
        if (mOrientation != HORIZONTAL_ONLY && mOrientation != VERTICAL_ONLY && mOrientation != GRID) {
            throw IllegalArgumentException("Invalid orientation")
        }
        this.mOrientation = orientation
    }

    fun setShowFooterDivider(show: Boolean) {
        mShowFooterDivider = show
    }

    fun setShowHeaderDivider(show: Boolean) {
        mShowHeaderDivider = show
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) == 0) {
            return
        }
        if (mOrientation == VERTICAL_ONLY) {
            outRect.top = mDivider?.intrinsicHeight ?: 0
        } else if (mOrientation == HORIZONTAL_ONLY) {
            outRect.left = mDivider?.intrinsicWidth ?: 0
        } else if (mOrientation == GRID) {
            outRect.top = mDivider?.intrinsicHeight ?: 0
            outRect.left = mDivider?.intrinsicWidth ?: 0
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        if (mOrientation == VERTICAL_ONLY) {
            drawVertical(c, parent)
        } else if (mOrientation == HORIZONTAL_ONLY) {
            drawHorizontal(c, parent)
        } else if (mOrientation == GRID) {
            drawHorizontal(c, parent)
            drawVertical(c, parent)
        }
    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val dividerLeft = 60
        val dividerRight = parent.width - 60
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val dividerTop = child.bottom + params.bottomMargin
            val dividerBottom = dividerTop + (mDivider?.intrinsicHeight ?: 0)
            if (i == parent.childCount - 1 && !mShowFooterDivider) {
                continue
            }
            mDivider?.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
            mDivider?.draw(c)
        }
    }

    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val dividerTop = parent.paddingTop
        val dividerBottom = parent.height - parent.paddingBottom
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val dividerLeft = child.right + params.leftMargin
            val dividerRight = dividerLeft + (mDivider?.intrinsicWidth ?: 0)
            if (i == parent.childCount - 1 && !mShowFooterDivider) {
                continue
            }
            mDivider?.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
            mDivider?.draw(c)
        }
    }
}