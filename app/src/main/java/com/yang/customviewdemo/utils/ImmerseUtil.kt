package com.yang.customviewdemo.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log


/**
 * 状态栏沉浸工具类
 *
 * Create by Yang Yang on 2023/7/28
 */
class ImmerseUtil {

    companion object {
        private const val STATUS_BAR_HEIGHT = "status_bar_height"
        private const val NAVIGATION_BAT_HEIGHT = "navigation_bar_height"
        private const val DIMEN = "dimen"
        private const val ANDROID = "android"

        @SuppressLint("DiscouragedApi", "PrivateApi")
        @JvmStatic
        fun isHasNavigationBar(context: Context): Boolean {
            var isHasNavigationBar = false
            val rs = context.resources
            val id = rs.getIdentifier("config_showNavigationBar", "bool", ANDROID)
            if (id > 0) isHasNavigationBar = rs.getBoolean(id)
            try {
                val systemPropertiesClass = Class.forName("android.os.SystemProperties")
                val m = systemPropertiesClass.getMethod("get", String::class.java)
                val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
                if ("1" == navBarOverride) {
                    isHasNavigationBar = false
                } else if ("0" == navBarOverride) {
                    isHasNavigationBar = true
                }
            } catch (e: Exception) {
                Log.w(ImmerseUtil::class.java.simpleName, e.toString(), e)
            }
            return isHasNavigationBar
        }

        @SuppressLint("InternalInsetResource", "DiscouragedApi")
        fun getNavigationBarHeight(context: Context): Int {
            var navigationBarHeight = 0
            val rs = context.resources
            val id = rs.getIdentifier(NAVIGATION_BAT_HEIGHT, DIMEN, ANDROID)
            if (id > 0 && isHasNavigationBar(context)) navigationBarHeight =
                rs.getDimensionPixelSize(id)
            return navigationBarHeight
        }

        @SuppressLint("InternalInsetResource", "DiscouragedApi")
        fun getStatusBarHeight(context: Context): Int {
            var result = 0
            try {
                val resourceId = context.resources.getIdentifier(STATUS_BAR_HEIGHT, DIMEN, ANDROID)
                if (resourceId > 0) {
                    result = context.resources.getDimensionPixelSize(resourceId)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return result
        }



    }
}