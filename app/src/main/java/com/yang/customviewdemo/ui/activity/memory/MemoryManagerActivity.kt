package com.yang.customviewdemo.ui.activity.memory

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.yang.customviewdemo.R


/**
 * Create by Yang Yang on 2023/12/26
 */
class MemoryManagerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activtiy_memory_manager)

        findViewById<Button>(R.id.btn_memory_practice).setOnClickListener {
            startActivity(Intent(this, MemoryPractice1Activity::class.java))
        }
    }
}