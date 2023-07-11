package com.yang.customviewdemo.services

import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.yang.customviewdemo.ui.widget.CustomTextClockView
import java.util.Timer
import kotlin.concurrent.timer


/**
 * Create by Yang Yang on 2023/7/11
 */
class TextClockWallpaperService  : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return TextClockEngine()
    }

    inner class TextClockEngine : Engine() {

        private val mClockView = CustomTextClockView(this@TextClockWallpaperService.baseContext)
        private val mHandler = Handler(Looper.getMainLooper())
        private var mTimer: Timer? = null

        private val mPaint = Paint().apply {
            this.color = Color.RED
            this.isAntiAlias = true
            this.textSize = 60f
            this.textAlign = Paint.Align.CENTER
        }

        /**
         * 当壁纸显示或隐藏时会回调该方法
         * 要只在壁纸显示的时候做绘制操作（防止占用CPU）
         */
        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (visible) {
                startClock()
            } else {
                stopClock()
            }
        }

        private fun startClock() {
            if (mTimer != null) return

            mTimer = timer(period = 1000) {
                mHandler.post {
                    mClockView.doInvalidate {
                        surfaceHolder.lockCanvas()?.let { canvas ->
                            mClockView.initWidthHeight(canvas.width.toFloat(), canvas.height.toFloat())
                            mClockView.draw(canvas)
                            surfaceHolder.unlockCanvasAndPost(canvas)
                        }
                    }
                }
            }
        }

        private fun stopClock() {
            mTimer?.cancel()
            mTimer = null
            mClockView.stopInvalidate()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            startClock()
        }
    }
}