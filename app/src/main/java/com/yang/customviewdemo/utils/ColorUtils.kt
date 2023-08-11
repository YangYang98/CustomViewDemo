package com.yang.customviewdemo.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.palette.graphics.Palette
import kotlin.math.roundToInt


/**
 * Create by Yang Yang on 2023/8/11
 */
class ColorUtils {

    companion object {

        @JvmStatic
        fun initPalette(
            bitmap: Bitmap,
            callback: (
                    palette: Palette, hotColor: Int, darkMutedColor: Int,
                    lightMutedColor: Int, darkVibrantColor: Int, lightVibrantColor: Int,
                    mutedColor: Int, vibrantColor: Int
                    ) -> Unit
        ) {
            Palette.from(bitmap).generate { palette ->
                palette?.let {
                    //获取图片中的主色调
                    val dominantColor = it.getDominantColor(Color.TRANSPARENT)
                    //获取图片中柔和的暗色
                    val darkMutedColor = it.getDarkMutedColor(Color.TRANSPARENT)
                    //获取图片中柔和的亮色
                    val lightMutedColor = it.getLightMutedColor(Color.TRANSPARENT)
                    //获取图片中有活力的暗色
                    val darkVibrantColor = it.getDarkVibrantColor(Color.TRANSPARENT)
                    //获取图片中有活力的亮色
                    val lightVibrantColor = it.getLightVibrantColor(Color.TRANSPARENT)
                    //获取图片中柔和的颜色
                    val mutedColor = it.getMutedColor(Color.TRANSPARENT)
                    //获取图片中有活力的颜色
                    val vibrantColor = it.getVibrantColor(Color.TRANSPARENT)
                    //从调色板中返回一个明亮且充满活力的样例。可能为空。
                    val lightVibrantSwatch = it.lightVibrantSwatch
                    var hotColor = Color.TRANSPARENT
                    if (lightVibrantSwatch != null) {
                        //谷歌推荐的:图片的整体的颜色rgb的混合---主色调
                        val rgb = lightVibrantSwatch.rgb
                        hotColor = getTranslucentColor(0.7f, rgb)
                    }

                    callback(
                        it,
                        hotColor,
                        darkMutedColor,
                        lightMutedColor,
                        darkVibrantColor,
                        lightVibrantColor,
                        mutedColor,
                        vibrantColor
                    )

                }
            }
        }

        /**
         * rgb & 0xff运算如下:
         * a           r       g          b
         * 11011010  01111010  10001010  10111010
         * 11111111 做运算
         * 10111010这样就取出了blue了
         * int green = rgb>> 8 & 0xff运算如下:
         * rgb>>8运算:
         * 11011010  01111010  10001010  这里砍掉10111010
         * 和0xff运算
         * 11111111 做运算
         * 100001010  这样就取出了g了
         *
         * @param percent 透明度
         * @param rgb     主题颜色
         * @return 颜色
         */
        private fun getTranslucentColor(percent: Float, rgb: Int): Int {
            //10101011110001111
            val blue = rgb and 0xff //源码就是这样玩的
            val green = rgb shr 8 and 0xff
            val red = rgb shr 16 and 0xff
            var alpha = rgb ushr 24
            alpha = (alpha * percent).roundToInt()
            //int blue=Color.blue(rgb); //会自动给你分析出颜色
            return Color.argb(alpha, red, green, blue)
        }

        @JvmStatic
        fun setGradualChange(
            view: View,
            colors: IntArray,
            type: GradientDrawable.Orientation?,
            radius: Int
        ) {
            val drawable = GradientDrawable(type, colors)
            drawable.cornerRadius = radius.toFloat()
            view.background = drawable
        }
    }
}