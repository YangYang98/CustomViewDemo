package com.yang.customviewdemo.ui.coordinatorLayout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yang.customviewdemo.R
import com.yang.customviewdemo.databinding.ActivityTwoStickBinding
import com.yang.customviewdemo.ui.CoordinatorFragment


/**
 * Create by Yang Yang on 2023/6/30
 */
class TwoStickActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityTwoStickBinding = DataBindingUtil.setContentView(this, R.layout.activity_two_stick)

        binding.viewpager.isUserInputEnabled = false

        binding.viewpager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = getFragments().size

            override fun createFragment(position: Int): Fragment = getFragments()[position]
        }
    }

    private fun getFragments() =
        (0..2)
            .map {
                CoordinatorFragment().apply {
                    arguments = bundleOf(CoordinatorFragment.KET to "$it")
                }
            }.toList()
}