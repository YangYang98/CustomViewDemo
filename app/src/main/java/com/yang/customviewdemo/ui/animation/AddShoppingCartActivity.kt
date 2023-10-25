package com.yang.customviewdemo.ui.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationSet
import android.view.animation.CycleInterpolator
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.yang.customviewdemo.R


/**
 * Create by Yang Yang on 2023/10/25
 */
class AddShoppingCartActivity : AppCompatActivity() {

    private lateinit var vAlarm: ImageView
    private lateinit var vMessageButtonClose: ImageView
    private lateinit var vMessageButtonImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_shopping_cart)
        vAlarm = findViewById(R.id.vAlarm)

        vMessageButtonClose = findViewById<ImageView>(R.id.vMessageButtonClose).apply {
            post {
                clickMessageButtonCloseAnimation()
            }
        }
        vMessageButtonImage = findViewById(R.id.vMessageButtonImage)
    }

    private fun clickMessageButtonCloseAnimation() {
        val sc = 44 / 80f

        val startLocation = IntArray(2)
        vMessageButtonImage.getLocationOnScreen(startLocation)
        val startX = startLocation[0] + vMessageButtonImage.width / 2f
        val startY = startLocation[1] + vMessageButtonImage.width / 2f

        val endLocation = IntArray(2)
        vAlarm.getLocationOnScreen(endLocation)
        val endX = endLocation[0] + vAlarm.width * sc
        val endY = endLocation[1] + vAlarm.height * sc

        val deltaX = endX - startX
        val deltaY = endY - startY

        //第一个动画，将 View 移动到目标位置并同时进行缩放
        val moveAnimationX = ObjectAnimator.ofFloat(vMessageButtonImage, "translationX", 0f, deltaX)
        val moveAnimationY = ObjectAnimator.ofFloat(vMessageButtonImage, "translationY", 0f, deltaY)
        val scaleAnimatorX = ObjectAnimator.ofFloat(vMessageButtonImage, "scaleX", sc)
        val scaleAnimatorY = ObjectAnimator.ofFloat(vMessageButtonImage, "scaleY", sc)
        val firstAnimatorSet = AnimatorSet().apply {
            playTogether(moveAnimationX, moveAnimationY, scaleAnimatorX, scaleAnimatorY)
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    //与此同时，alarm图标动画开始： 1.缩小到消失（和悬浮图标同步）2.再次显示，alarm图标进行摇动

                    //1.缩小到消失（和悬浮图标同步）
                    val scale = ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
                        duration = 100
                    }
                    val animSet = AnimationSet(true).apply {
                        addAnimation(scale)
                    }
                    vAlarm.startAnimation(animSet)
                    animSet.setAnimationListener(object : AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {

                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            vMessageButtonImage.visibility = View.GONE

                            //alarm图标动画： 2.再次显示，alarm图标进行摇动
                            val shake = RotateAnimation(-3f, 3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
                                interpolator = CycleInterpolator(3f)
                                duration = 1000
                            }
                            vAlarm.startAnimation(shake)

                        }

                        override fun onAnimationRepeat(animation: Animation?) {

                        }

                    })
                }
            })
        }

        val secondScaleAnimatorX = ObjectAnimator.ofFloat(vMessageButtonImage, "scaleX", 0f).apply {
            duration = 100
            interpolator = AccelerateDecelerateInterpolator()
        }
        val secondScaleAnimatorY = ObjectAnimator.ofFloat(vMessageButtonImage, "scaleY", 0f).apply {
            duration = 100
            interpolator = AccelerateDecelerateInterpolator()
        }
        val animatorSet = AnimatorSet().apply {
            play(firstAnimatorSet).before(secondScaleAnimatorX).before(secondScaleAnimatorY)
        }

        vMessageButtonClose.setOnClickListener {
            vMessageButtonClose.visibility = View.GONE
            animatorSet.start()
        }

    }
}