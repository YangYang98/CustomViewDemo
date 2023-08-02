package com.yang.customviewdemo.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yang.customviewdemo.R


/**
 * Create by Yang Yang on 2023/7/28
 */
class ImageAdapter(
    context: Context, data: List<Int>, layoutId: Int
) : BaseAdapter<Int, ImageAdapter.ViewHolder>(context, data, layoutId) {
    
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(mLayoutId, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(mContext).load(mData[position]).into(holder.image)
    }
}