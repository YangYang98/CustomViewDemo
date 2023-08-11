package com.yang.customviewdemo.ui.activity.viewpager

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager.widget.ViewPager.PageTransformer
import com.yang.customviewdemo.R
import com.yang.customviewdemo.databinding.ActivityPaletteBinding
import com.yang.customviewdemo.utils.ColorUtils
import java.util.Random
import java.util.Timer
import java.util.TimerTask
import kotlin.math.abs
import kotlin.math.min


/**
 * 当不滑动状态下:
 *      position = -1 左侧View
 *      position = 0 当前View
 *      position = 1 右侧View
 *
 * 当滑动状态下:
 *  向左滑动: [ position < 0 && position > -1]
 *    左侧View      position < -1
 *    当前View    0 ~ -1
 *    右侧View   1 ~ 0
 *
 *  向右滑动:[position > 0 && position < 1 ]
 *   左侧View  -1 < position < 0
 *   当前View  0 ~ 1
 *   右侧View  position > 1
 *
 * Create by Yang Yang on 2023/8/10
 */
class PaletteActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityPaletteBinding

    private val mDrawables = intArrayOf(R.drawable.ic_woniu, R.drawable.ic_road, R.drawable.ic_launcher_background)

    //按压时不自己滚动
    var isDown: Boolean = false
    val timer: Timer by lazy { Timer() }

    val timerTask: TimerTask by lazy {
        object : TimerTask() {
            override fun run() {
                if (!isDown) {
                    val page = mBinding.viewPager.currentItem + 1
                    runOnUiThread {
                        mBinding.viewPager.currentItem = page
                    }
                }
            }
        }
    }

    private val mRandom: Random by lazy { Random() }
    var mHotColor = Color.TRANSPARENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityPaletteBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        mBinding.viewPager.apply {
            adapter = BannerAdapter(context, mDrawables)
            pageMargin = 20
            offscreenPageLimit = 3
            currentItem = 1

            setPageTransformer(true, ScaleTransformer())
            
            addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    isDown = true
                }

                override fun onPageSelected(position: Int) {
                    
                }

                override fun onPageScrollStateChanged(state: Int) {
                    isDown = false
                }

            })
        }

        mBinding.viewStackPage.apply {
            adapter = BannerAdapter(context, mDrawables)
            pageMargin = 20
            offscreenPageLimit = 3
            currentItem = 1

            setPageTransformer(true, StackPageTransformer(this))
        }

        initBitMap(mBinding.ivId.drawable.toBitmap())
    }

    override fun onResume() {
        super.onResume()

        timer.schedule(timerTask, 0, 2500)
    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }

    inner class BannerAdapter(private val context: Context, private val data: IntArray): PagerAdapter() {

        override fun getCount(): Int {
            //return data.size
            return Int.MAX_VALUE
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = View.inflate(container.context, R.layout.banner_item_layout, null)
            val imageView: ImageView = view.findViewById<ImageView?>(R.id.iv_icon).apply {
                setImageResource(data[position % data.size])
                setOnClickListener {
                    Toast.makeText(context, "当前：${position % data.size}", Toast.LENGTH_SHORT).show()
                }
            }

            container.addView(view)
            return view
        }


        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object` //过滤和缓存的作用
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    /**
     * 画廊效果
     */
    class ScaleTransformer: PageTransformer {

        companion object {
            private const val MAX_SCALE = 1.0f
            private const val MIN_SCALE = 0.80f
        }

        override fun transformPage(page: View, position: Float) {
            Log.e("ScaleTransformer", "position:$position")

            if (position != 0f) {
                val scaleFactor = MIN_SCALE + (1 - abs(position)) * (MAX_SCALE - MIN_SCALE)
                page.apply {
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                }
            } else {
                page.apply {
                    scaleX = MAX_SCALE
                    scaleY = MAX_SCALE
                }
            }
        }

    }

    /**
     * 翻书效果
     */
    class StackPageTransformer(private val viewPager: ViewPager): PageTransformer {

        companion object {
            //View 之间的偏移量
            private const val DEVIATION = 60f

            private const val ROTATION = 60f

            private const val SCALE_VALUE = 1f

        }

        var isStack = false

        override fun transformPage(page: View, position: Float) {

            val pageWidth = viewPager.width

            //隐藏左侧侧的view
            page.visibility = if (position <= -1f) View.GONE else View.VISIBLE

            //当前View和右侧的View [让右侧View和当前View叠加起来]
            if (position >= 0f) {

                val translationX: Float = if (isStack) {
                    DEVIATION - pageWidth * position
                } else {
                    (DEVIATION - pageWidth) * position
                }
                page.translationX = translationX
            }

            if (position == 0f) {
                page.apply {
                    scaleX = SCALE_VALUE
                    scaleY = SCALE_VALUE
                }
            } else {
                //左侧已经隐藏了，所以这里值的是右侧View的scale
                val scaleFactor = min(SCALE_VALUE - position * 0.1f, SCALE_VALUE)
                page.apply {
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                }
            }

            if (position < 0 && position > -1) {
                page.apply {
                    rotation = ROTATION * position
                    alpha = 1f - abs(position)
                }
            } else {
                page.alpha = 1f
            }

            if (position > 0 && position < 1) {
                page.rotation = 0f
            }
        }

    }

    fun onAnimatorClick(view: View?) {
        val colorAnim = ObjectAnimator.ofInt(mBinding.titleTv7, "backgroundColor", Color.RED, Color.BLUE, Color.YELLOW).apply {
            duration = 3000
            setEvaluator(ArgbEvaluator())
            start()
        }
    }

    fun onRandomClick(view: View?) {
        val drawable = mDrawables[mRandom.nextInt(mDrawables.size)]
        val bitmap = BitmapFactory.decodeResource(resources, drawable)
        if (bitmap != null) {
            mBinding.ivId.setImageBitmap(bitmap)
            initBitMap(bitmap)
        }
    }

    private fun initBitMap(bitmap: Bitmap) {
        ColorUtils.initPalette(bitmap) { palette, hotColor, darkMutedColor, lightMutedColor,
                                         darkVibrantColor, lightVibrantColor,
                                         mutedColor, vibrantColor ->

            Toast.makeText(this, "处理完了", Toast.LENGTH_SHORT).show()
            runOnUiThread {
                mBinding.apply {
                    titleTv.apply {
                        text = "hotColor"
                        setTextColor(palette.dominantSwatch?.bodyTextColor ?: Color.WHITE)
                        setBackgroundColor(hotColor)
                    }
                    titleTv1.apply {
                        text = "darkMutedColor"
                        setTextColor(palette.darkMutedSwatch?.bodyTextColor ?: Color.WHITE)
                        setBackgroundColor(darkMutedColor)
                    }
                    titleTv2.apply {
                        text = "lightMutedColor"
                        setTextColor(palette.lightMutedSwatch?.bodyTextColor ?: Color.WHITE)
                        setBackgroundColor(lightMutedColor)
                    }
                    titleTv3.apply {
                        text = "darkVibrantColor"
                        setTextColor(palette.darkVibrantSwatch?.bodyTextColor ?: Color.WHITE)
                        setBackgroundColor(darkVibrantColor)
                    }
                    titleTv4.apply {
                        text = "lightVibrantColor"
                        setTextColor(palette.lightVibrantSwatch?.bodyTextColor ?: Color.WHITE)
                        setBackgroundColor(lightVibrantColor)
                    }
                    titleTv5.apply {
                        text = "mutedColor"
                        setTextColor(palette.mutedSwatch?.bodyTextColor ?: Color.WHITE)
                        setBackgroundColor(mutedColor)
                    }
                    titleTv6.apply {
                        text = "vibrantColor"
                        setTextColor(palette.vibrantSwatch?.bodyTextColor ?: Color.WHITE)
                        setBackgroundColor(vibrantColor)
                    }

                    val color = intArrayOf(mutedColor, lightVibrantColor, vibrantColor)
                    ColorUtils.setGradualChange(titleTv7, color, GradientDrawable.Orientation.TL_BR, 55)

                }
            }
        }
    }
}