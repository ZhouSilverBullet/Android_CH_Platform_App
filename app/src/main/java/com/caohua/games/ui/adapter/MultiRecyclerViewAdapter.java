package com.caohua.games.ui.adapter;

import android.content.Context;

import java.util.List;

/**
 * Created by zhouzhou on 2017/5/23.
 */

public abstract class MultiRecyclerViewAdapter<D> extends CommonRecyclerViewAdapter<D> {

    protected int[] layoutIds;

    public MultiRecyclerViewAdapter(Context context, List<D> list) {
        super(context, list, -1);
    }

    public MultiRecyclerViewAdapter(Context context, List<D> list, int[] layoutIds) {
        super(context, list, -1);
        this.layoutIds = layoutIds;
    }


    @Override
    public int getItemViewType(int position) {
        return getMultiItemViewType(position);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        covert(holder, position);
    }

    protected abstract void covert(ViewHolder holder, int position);

    protected abstract int getMultiItemViewType(int position);

    @Override
    protected void covert(ViewHolder holder, D d) {

    }
}
