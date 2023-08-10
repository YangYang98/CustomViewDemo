package com.yang.customviewdemo.ui.activity.viewpager

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager.widget.ViewPager.PageTransformer
import com.yang.customviewdemo.R
import com.yang.customviewdemo.databinding.ActivityPaletteBinding
import java.util.Timer
import java.util.TimerTask
import kotlin.math.abs


/**
 * Create by Yang Yang on 2023/8/10
 */
class PaletteActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityPaletteBinding

    private val mDrawables = intArrayOf(R.drawable.ic_woniu, R.drawable.ic_tiger_run_eat, R.drawable.ic_road, R.mipmap.introductory_page_0)

    //按压时不自己滚动
    var isDown: Boolean = false
    val timer: Timer by lazy { Timer() }

    val timerTask: TimerTask by lazy {
        object : TimerTask() {
            override fun run() {
                val page = mBinding.viewPager.currentItem + 1
                runOnUiThread {
                    mBinding.viewPager.currentItem = page
                }
            }
        }
    }

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
}