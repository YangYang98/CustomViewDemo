package com.yang.customviewdemo.ui.activity.customer

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.yang.customviewdemo.R
import com.yang.customviewdemo.databinding.ActivityParallaxAnimatorBinding


/**
 * Create by Yang Yang on 2023/8/9
 */
class ParallaxAnimatorActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityParallaxAnimatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityParallaxAnimatorBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        mBinding.parallaxViewPager.setLayout(
            supportFragmentManager,
            arrayListOf(
                R.layout.layout_parallax_1_item,
                R.layout.layout_parallax_2_item,
                R.layout.layout_parallax_3_item,
                R.layout.layout_parallax_4_item
            )
        )
    }
}

class ParallaxAnimatorViewPager(context: Context, attrs: AttributeSet?): ViewPager(context, attrs) {

    fun setLayout(fragmentManager: FragmentManager, @LayoutRes list: ArrayList<Int>) {
        val listFragment = arrayListOf<CustomInflateFragment>()
        list.map {
            CustomInflateFragment.instance(it)
        }.forEach {
            listFragment.add(it)
        }

        adapter = ParallaxAnimatorAdapter(listFragment, fragmentManager)
        
        addOnPageChangeListener(object : OnPageChangeListener {

            /**
             * @param position: 当前页面下标
             * @param positionOffset:当前页面滑动百分比
             * @param positionOffsetPixels: 当前页面滑动的距离
             *
             * 从左到右：
             *      positionOffset = [0-1]
             *      positionOffsetPixels = [0 - 屏幕宽度]
             *
             * 从右到左：
             *      positionOffset = [1-0]
             *      positionOffsetPixels = [屏幕宽度 - 0]
             */
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                Log.e("yyParallaxViewPager", "onPageScrolled:position:$position\tpositionOffset:${positionOffset}\tpositionOffsetPixels:${positionOffsetPixels}")

                //当前fragment
                val currentFragment = listFragment[position].apply {
                    parallaxViewList.forEach { view ->
                        val tag = view.getTag(view.id)

                        (tag as? CustomInflateFragment.ParallaxBean)?.also {
                            view.translationX = -it.parallaxTransformInX * positionOffsetPixels
                            view.translationY = -it.parallaxTransformInY * positionOffsetPixels
                            view.rotation = -it.parallaxRotate * 360 * positionOffset

                            view.scaleX = 1 + it.parallaxZoom - (it.parallaxZoom * positionOffset)
                            view.scaleY = 1 + it.parallaxZoom - (it.parallaxZoom * positionOffset)
                        }
                    }
                }

                if (position + 1 < listFragment.size) {
                    val nextFragment = listFragment[position + 1].apply {
                        parallaxViewList.forEach { view ->
                            val tag = view.getTag(view.id)

                            (tag as? CustomInflateFragment.ParallaxBean)?.also {
                                view.translationX = it.parallaxTransformInX * (width - positionOffsetPixels)
                                view.translationY = it.parallaxTransformInY * (height - positionOffsetPixels)
                                view.rotation = it.parallaxRotate * 360 * positionOffset

                                view.scaleX = 1 + (it.parallaxZoom * positionOffset)
                                view.scaleY = 1 + (it.parallaxZoom * positionOffset)
                            }
                        }
                    }
                }
            }

            /**
             * @param position: 但页面切换完成的时候调用
             */
            override fun onPageSelected(position: Int) {
                //Log.e("yyParallaxViewPager", "onPageSelected:$position")
            }

            /**
             * @param state: 但页面发生变化时候调用,一共有3种状体
             *      SCROLL_STATE_IDLE 空闲状态
             *      SCROLL_STATE_DRAGGING 拖动状态
             *      SCROLL_STATE_SETTLING 拖动停止状态
             */
            override fun onPageScrollStateChanged(state: Int) {
                /*when (state) {
                    SCROLL_STATE_IDLE -> {
                        Log.e("yyParallaxViewPager", "onPageScrollStateChanged:页面空闲中..")
                    }
                    SCROLL_STATE_DRAGGING -> {
                        Log.e("yyParallaxViewPager", "onPageScrollStateChanged:拖动中..")
                    }
                    SCROLL_STATE_SETTLING -> {
                        Log.e("yyParallaxViewPager", "onPageScrollStateChanged:拖动停止了..")
                    }
                }*/
            }

        })
    }
    

    private inner class ParallaxAnimatorAdapter(
        private val list: List<Fragment>,
        fm: FragmentManager
    ): FragmentPagerAdapter(fm) {
        override fun getCount(): Int = list.size

        override fun getItem(position: Int): Fragment = list[position]

    }
}