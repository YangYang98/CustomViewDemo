package com.yang.customviewdemo.ui.activity.customer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yang.customviewdemo.R
import com.yang.customviewdemo.ui.widget.timeDisk.TimeDiskView


/**
 * Create by Yang Yang on 2023/10/17
 */
class TimeDiskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_time_disk_view)

        findViewById<TimeDiskView>(R.id.tdv).apply {
            //showCurrentTime()
            start()
        }
    }
}