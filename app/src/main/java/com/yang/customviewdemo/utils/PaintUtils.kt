package com.yang.customviewdemo.utils

import android.graphics.Paint


/**
 * Create by Yang Yang on 2023/7/11
 */

/**
 * 获取绘制文字时在x轴上 垂直居中的y坐标
 */
fun Paint.getCenterY(): Float {
    return this.fontSpacing / 2 - this.fontMetrics.bottom
}

/**
 * 获取绘制文字时在x轴上 贴紧x轴的上边缘的y坐标
 */
fun Paint.getBottomedY(): Float {
    return -this.fontMetrics.bottom
}

/**
 * 获取绘制文字时在x轴上 贴近x轴的下边缘的y坐标
 */
fun Paint.getToppedY(): Float {
    return -this.fontMetrics.ascent
}