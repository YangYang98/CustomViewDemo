package com.yang.customviewdemo.ui.adapter;


import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


/**
 * Create by Yang Yang on 2023/7/28
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class BaseAdapter<O, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    Context mContext;
    List<O> mData;
    int mLayoutId;
    LayoutInflater mLayoutInflater;
    OnSizeChangedListener mOnSizeChangedListener;

    public BaseAdapter(Context context, List<O> data, int layoutId) {
        mContext = context;
        mData = data == null ? new ArrayList<>() : data;
        mLayoutId = layoutId;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addData(@NonNull O o) {
        mData.add(o);
        notifyItemInserted(mData.size());
        notifyOnSizeChanged();
    }

    public void addData(int index, @NonNull O o) {
        mData.add(index, o);
        notifyItemInserted(index);
        notifyOnSizeChanged();
    }

    public void addData(@NonNull List<O> data) {
        if (!data.isEmpty()) {
            int oldSize = mData.size() - 1;
            mData.addAll(data);
            notifyItemRangeChanged(oldSize, mData.size());
            notifyOnSizeChanged();
        }
    }

    public boolean removeData(@NonNull O o) {
        int pos = mData.indexOf(o);
        if (pos != -1) {
            mData.remove(o);
            notifyItemRemoved(pos);
            notifyOnSizeChanged();
            return true;
        }
        return false;
    }

    public void setData(List<O> data) {
        if (data != null) {
            mData = data;
            notifyDataSetChanged();
            notifyOnSizeChanged();
        }
    }

    public void clearData() {
        mData.clear();
        notifyDataSetChanged();
        notifyOnSizeChanged();
    }

    private void notifyOnSizeChanged() {
        if (mOnSizeChangedListener != null) {
            mOnSizeChangedListener.onSizeChanged(mData.size());
        }
    }

    public void setOnSizeChangedListener(OnSizeChangedListener listener) {
        mOnSizeChangedListener = listener;
    }

    public interface OnSizeChangedListener {
        void onSizeChanged(int currentSize);
    }

}
