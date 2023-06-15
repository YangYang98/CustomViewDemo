package com.yang.customviewdemo.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yang.customviewdemo.R


/**
 * Create by Yang Yang on 2023/6/15
 */
class TextAdapter(
    private val context: Context,
    private val dataList: List<String>
) : RecyclerView.Adapter<TextAdapter.TextViewHolder>() {


    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_text, null)
        return TextViewHolder(view)
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        holder.tvItem.text = dataList[position]
    }

    override fun getItemCount(): Int = dataList.size

    inner class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvItem: TextView by lazy {
            itemView.findViewById(R.id.tvItem)
        }
    }

}