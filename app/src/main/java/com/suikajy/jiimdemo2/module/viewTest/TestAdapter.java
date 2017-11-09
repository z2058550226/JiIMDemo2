package com.suikajy.jiimdemo2.module.viewTest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suikajy.jiimdemo2.base.BaseRecyclerAdapter;

/**
 * @author zjy
 * @date 2017/11/7
 */

public class TestAdapter extends BaseRecyclerAdapter<TestAdapter.ViewHolder, Object> {


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv.setText(String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return 500;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView tv;

        public ViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(android.R.id.text1);
        }
    }

}
