package com.yang.customviewdemo.ui.activity.memory

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yang.customviewdemo.R


/**
 * Create by Yang Yang on 2023/12/26
 */
class MemoryPractice1Activity : AppCompatActivity() {

    private lateinit var textView: TextView

    private var count = 0

    private val array = IntArray(1000)

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            count++
            textView.text = count.toString()

            //模拟一些复杂的业务逻辑，创建大量的数组对象
            for (i in 0 until 100) {
                array[0] = i
            }

            sendEmptyMessage(0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_memory_practice_1)

        textView = findViewById(R.id.tv_counter)

        handler.sendEmptyMessage(0)
    }
}