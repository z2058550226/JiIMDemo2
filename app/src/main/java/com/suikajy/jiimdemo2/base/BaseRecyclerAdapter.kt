package com.suikajy.jiimdemo2.base

import android.support.v7.widget.RecyclerView

/**
 * Created by zjy on 2017/11/2.
 */
abstract class BaseRecyclerAdapter<VH : RecyclerView.ViewHolder?, T> : RecyclerView.Adapter<VH>() {
    val dataSet: MutableList<T> = ArrayList()

    fun refreshData(items: List<T>) {
        dataSet.clear()
        dataSet.addAll(items)
        notifyDataSetChanged()
    }

    fun loadMore(items: List<T>) {
        val oldSize = dataSet.size
        dataSet.addAll(items)
        notifyItemRangeChanged(oldSize - 1, items.size)
    }

    fun clearData() {
        dataSet.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = dataSet.size
}