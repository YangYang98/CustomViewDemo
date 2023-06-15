package com.yang.customviewdemo.ui.coordinatorLayout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yang.customviewdemo.R
import com.yang.customviewdemo.databinding.ActivityUseBehaviorBinding
import com.yang.customviewdemo.ui.CoordinatorFragment


/**
 * Create by Yang Yang on 2023/6/15
 */
class UseBehaviorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityUseBehaviorBinding = DataBindingUtil.setContentView(this, R.layout.activity_use_behavior)

        binding.viewpager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = getFragments().size

            override fun createFragment(position: Int): Fragment = getFragments()[position]
        }

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            binding.viewpager.currentItem =
                group.indexOfChild(group.findViewById(checkedId))
        }

//        binding.bottomNavigationView.setOnItemSelectedListener {
//            "${it.title}" toast this
//
//            binding.bottomNavigationView.menu.forEachIndexed { index, item ->
//                if (it == item) {
//                    binding.viewpager.currentItem = index
//                }
//            }
////            it.isChecked = false
////            binding.bottomNavigationView.menu.get(2).isChecked = true
//            true
//        }

    }

    private fun getFragments() =
        (0..2)
            .map {
                CoordinatorFragment().apply {
                    arguments = bundleOf(CoordinatorFragment.KET to "$it")
                }
            }.toList()

}