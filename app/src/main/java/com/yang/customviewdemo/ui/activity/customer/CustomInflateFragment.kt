package com.yang.customviewdemo.ui.activity.customer

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.LayoutInflater.Factory2
import android.view.View
import android.view.ViewGroup
import androidx.annotation.FloatRange
import androidx.annotation.LayoutRes
import androidx.core.os.bundleOf
import androidx.core.view.LayoutInflaterCompat
import androidx.fragment.app.Fragment
import com.yang.customviewdemo.utils.SystemAppCompatViewInflater
import java.lang.RuntimeException


/**
 * Create by Yang Yang on 2023/8/9
 */
class CustomInflateFragment private constructor(): Fragment(), Factory2 {

    companion object {
        private const val LAYOUT_ID = "layout_id"

        fun instance(@LayoutRes layoutId: Int) = let {
            CustomInflateFragment().apply {
                arguments = bundleOf(LAYOUT_ID to layoutId)
            }
        }
    }

    private val layoutId by lazy {
        arguments?.getInt(LAYOUT_ID) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val newInflater = inflater.cloneInContext(activity)
        LayoutInflaterCompat.setFactory2(newInflater, this)
        return newInflater.inflate(layoutId, container, false)
    }

    val parallaxViewList = arrayListOf<View>()
    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {

        val data = ParallaxBean()
        (0 until attrs.attributeCount).forEach {
            Log.e("YANGYANG", "key:${attrs.getAttributeName(it)}\t" +
                    "value:${attrs.getAttributeValue(it)}")
            when (attrs.getAttributeName(it)) {
                "parallaxTransformInX" -> {
                    data.parallaxTransformInX = attrs.getAttributeValue(it).toFloat()
                }
                "parallaxTransformInY" -> {
                    data.parallaxTransformInY = attrs.getAttributeValue(it).toFloat()
                }
                "parallaxTransformOutX" -> {
                    data.parallaxTransformOutX = attrs.getAttributeValue(it).toFloat()
                }
                "parallaxTransformOutY" -> {
                    data.parallaxTransformOutY = attrs.getAttributeValue(it).toFloat()
                }
                "parallaxRotate" -> {
                    data.parallaxRotate = attrs.getAttributeValue(it).toFloat()
                }
                "parallaxZoom" -> {
                    data.parallaxZoom = attrs.getAttributeValue(it).toFloat()
                }
            }
        }

        val createView = createView(parent, name, context, attrs)
        if (createView != null && data.isNotEmpty()) {
            if (createView.id != View.NO_ID) {
                createView.setTag(createView.id, data)
                parallaxViewList.add(createView)
            } else {
                throw RuntimeException("需要移动的View必须设置id来保证数据不会丢失")
            }
        }

        return createView
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return onCreateView(null, name, context, attrs)
    }

    private var mAppCompatViewInflater = SystemAppCompatViewInflater()
    private fun createView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        val is21 = Build.VERSION.SDK_INT < 21
        return mAppCompatViewInflater.createView(parent, name, context, attrs, false, is21, true, false)
    }

    inner class ParallaxBean(
        @FloatRange(from = 0.0, to = 1.0) var parallaxTransformInX:Float=0.0f, //入场时候X的坐标
        @FloatRange(from = 0.0, to = 1.0) var parallaxTransformInY:Float=0.0f, //入场时候Y的坐标
        @FloatRange(from = 0.0, to = 1.0) var parallaxTransformOutX:Float=0.0f,//出场时候X的坐标
        @FloatRange(from = 0.0, to = 1.0) var parallaxTransformOutY:Float=0.0f,//出场时候Y的坐标
        @FloatRange(from = 0.0) var parallaxRotate: Float =0f,//入场旋转
        @FloatRange(from = 0.0) var parallaxZoom: Float=0f,//缩放倍数
    ) {

        fun isNotEmpty() = let {
            parallaxTransformInX != 0f ||
                    parallaxTransformInY != 0f ||
                    parallaxTransformOutX != 0f ||
                    parallaxTransformOutY != 0f ||
                    parallaxRotate != 0f ||
                    parallaxZoom != 0f

        }
    }
}