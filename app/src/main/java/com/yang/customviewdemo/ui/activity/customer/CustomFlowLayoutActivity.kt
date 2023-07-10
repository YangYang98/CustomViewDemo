package com.yang.customviewdemo.ui.activity.customer

import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.yang.customviewdemo.R
import com.yang.customviewdemo.databinding.ActivityCustomFlowLayoutBinding
import com.yang.customviewdemo.ui.widget.CustomFlowLayout
import com.yang.customviewdemo.utils.dp


/**
 * Create by Yang Yang on 2023/7/4
 */
class CustomFlowLayoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomFlowLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_custom_flow_layout)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_custom_flow_layout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_item -> {
                binding.flowLayout.addView(TextView(this).apply {
                    text = "添加的"
                    setBackgroundResource(R.drawable.shape_button_circular)
                    setPadding(12.dp, 2.dp, 12.dp, 2.dp)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                })
            }
            R.id.delete_item -> {

            }
            R.id.action_line_vertical_gravity_top -> {
                binding.flowLayout.lineVerticalGravity = CustomFlowLayout.LINE_VERTICAL_GRAVITY_TOP
                true
            }
            R.id.action_line_vertical_gravity_center_vertical -> {
                binding.flowLayout.lineVerticalGravity = CustomFlowLayout.LINE_VERTICAL_GRAVITY_CENTER_VERTICAL
                true
            }
            R.id.action_line_vertical_gravity_bottom -> {
                binding.flowLayout.lineVerticalGravity = CustomFlowLayout.LINE_VERTICAL_GRAVITY_BOTTOM
                true
            }
        }

        return true
    }
}