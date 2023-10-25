package com.yang.customviewdemo.ui.animation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.yang.customviewdemo.R


/**
 * Create by Yang Yang on 2023/10/25
 */
class MainAnimationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_animation)

        findViewById<Button>(R.id.btn_add_shopping_cart).setOnClickListener {
            startActivity(Intent(this, AddShoppingCartActivity::class.java))
        }
    }
}