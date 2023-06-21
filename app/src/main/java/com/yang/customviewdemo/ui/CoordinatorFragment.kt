package com.yang.customviewdemo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yang.customviewdemo.R
import com.yang.customviewdemo.ui.adapter.TextAdapter
import com.yang.customviewdemo.ui.widget.StartEndMargin20ItemDecoration
import com.yang.customviewdemo.utils.ResourceUtil


/**
 * Create by Yang Yang on 2023/6/15
 */
class CoordinatorFragment : Fragment() {

    companion object {
        const val KET = "key"
    }

    private val key by lazy {
        requireArguments().getString(KET) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_coordinator, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemDecoration = StartEndMargin20ItemDecoration(
            ResourceUtil.getDrawable(context, ResourceUtil.getDrawableResId(context, android.R.attr.listDivider))
        )
        itemDecoration.setShowFooterDivider(true)

        view.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter =
                TextAdapter(requireContext(), (0..1000).map { "${key}-数据:${it}" }.toList())
            recycledViewPool.setMaxRecycledViews(1, 20)
            addItemDecoration(itemDecoration)
        }
    }
}