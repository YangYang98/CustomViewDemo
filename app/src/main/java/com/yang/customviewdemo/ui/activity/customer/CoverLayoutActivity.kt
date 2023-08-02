package com.yang.customviewdemo.ui.activity.customer

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.yang.customviewdemo.R
import com.yang.customviewdemo.ui.adapter.ImageAdapter
import com.yang.customviewdemo.ui.widget.CoverLayout
import com.yang.customviewdemo.utils.ImmerseUtil


/**
 * Create by Yang Yang on 2023/7/28
 */
class CoverLayoutActivity : AppCompatActivity() {

    private val mCoverLayout: CoverLayout by lazy { findViewById(R.id.cover_layout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initStatusBar()

        setContentView(R.layout.activity_cover_layout)

        val topBar: View? = mCoverLayout.mTopBar
        if (topBar != null) {
            val statusHeight = ImmerseUtil.getStatusBarHeight(this)
            topBar.apply {
                setPadding(0 ,statusHeight, 0, 0)
                val lp = layoutParams
                lp.height += statusHeight
                layoutParams = lp
                mCoverLayout.requestLayout()
            }
        }

        val topRecyclerView: RecyclerView = findViewById<RecyclerView>(R.id.top_recycler_view).apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = ImageAdapter(this@CoverLayoutActivity, getData(), R.layout.adapter_item_view)
        }

        val bottomRecyclerView = findViewById<RecyclerView>(R.id.bottom_recycler_view).apply {
            layoutManager = LinearLayoutManager(this@CoverLayoutActivity)
            adapter = ImageAdapter(this@CoverLayoutActivity, getData(), R.layout.adapter_item_view)
        }

        val horizontalView = findViewById<RecyclerView>(R.id.horizontal_recycler_view).apply {
            layoutManager = LinearLayoutManager(this@CoverLayoutActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = ImageAdapter(this@CoverLayoutActivity, getData(), R.layout.adapter_item_view2)
        }
    }

    private fun initStatusBar() {
        window.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                clearFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                            or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                )
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = Color.TRANSPARENT
                navigationBarColor = Color.TRANSPARENT
            }
        }
    }

    private fun getData(): List<Int> {
        val result: MutableList<Int> = ArrayList()
        for (i in 0..18) {
            if (i % 4 == 0) {
                result.add(R.drawable.ic_woniu)
            }
            if (i % 4 == 1) {
                result.add(R.drawable.ic_tiger_run_eat)
            }
            if (i % 4 == 2) {
                result.add(R.drawable.ic_launcher_background)
            }
            if (i % 4 == 3) {
                result.add(R.drawable.ic_road)
            }
        }
        return result
    }
}