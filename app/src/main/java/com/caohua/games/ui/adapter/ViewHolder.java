package com.caohua.games.ui.adapter;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhouzhou on 2017/5/23.
 */

public class ViewHolder extends RecyclerView.ViewHolder {
    private View convertView;
    private SparseArrayCompat<View> mViews;
    public ViewHolder(View itemView) {
        super(itemView);
        convertView = itemView;
        mViews = new SparseArrayCompat<>();
    }

    public static ViewHolder create(LayoutInflater inflater,ViewGroup parent, int viewType) {
        View inflate = inflater.inflate(viewType, parent, false);
        ViewHolder viewHolder = new ViewHolder(inflate);
        return viewHolder;
    }

    /**
     *
     * @param resId
     * @return
     */
    public <T extends View> T getView(int resId) {
        View view = mViews.get(resId);
        if (view == null) {
            view = convertView.findViewById(resId);
            mViews.put(resId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return convertView;
    }
}
