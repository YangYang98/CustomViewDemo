package com.yang.customviewdemo.ui.activity.customer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.yang.customviewdemo.R


/**
 * Create by Yang Yang on 2023/7/4
 */
class MainCustomViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_custom_view)

        findViewById<Button>(R.id.btn_custom_flow_layout).setOnClickListener {
            startActivity(Intent(this, CustomFlowLayoutActivity::class.java))
        }

        findViewById<Button>(R.id.btn_custom_text_clock).setOnClickListener {
            startActivity(Intent(this, CustomTextClockActivity::class.java))
        }

        findViewById<Button>(R.id.btn_cover_layout).setOnClickListener {
            startActivity(Intent(this, CoverLayoutActivity::class.java))
        }

        findViewById<Button>(R.id.btn_experience_bar).setOnClickListener {
            startActivity(Intent(this, ExperienceBarActivity::class.java))
        }

        findViewById<Button>(R.id.btn_ruler_view).setOnClickListener {
            startActivity(Intent(this, RulerViewActivity::class.java))
        }

        findViewById<Button>(R.id.btn_parallax_animator).setOnClickListener {
            startActivity(Intent(this, ParallaxAnimatorActivity::class.java))
        }

        findViewById<Button>(R.id.btn_iris_diaphragm).setOnClickListener {
            startActivity(Intent(this, IrisDiaphragmActivity::class.java))
        }

        findViewById<Button>(R.id.btn_wave_progress).setOnClickListener {
            startActivity(Intent(this, WaveProgressViewActivity::class.java))
        }
    }
}