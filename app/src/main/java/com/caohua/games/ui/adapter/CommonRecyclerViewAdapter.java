package com.caohua.games.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.List;

/**
 * Created by zhouzhou on 2017/5/23.
 */

public abstract class CommonRecyclerViewAdapter<D> extends RecyclerView.Adapter<ViewHolder> {
    protected LayoutInflater inflater;
    protected Context context;
    protected List<D> list;
    protected int layoutId;

    protected OnClickItemListener onClickItemListener;

    public interface OnClickItemListener {
        void onClickItem(CommonRecyclerViewAdapter adapter, ViewHolder holder, int position);
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    public CommonRecyclerViewAdapter(Context context, List<D> list, int layoutId) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.layoutId = layoutId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ViewHolder.create(inflater, parent, viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickItemListener != null) {
                    onClickItemListener.onClickItem(CommonRecyclerViewAdapter.this, holder, position);
                }
            }
        });
        covert(holder, list.get(position));
        covert(holder, list.get(position), position);
    }

    public void addAll(Collection<D> collection) {
        if (list != null) {
            list.clear();
            list.addAll(collection);
            notifyDataSetChanged();
        }
    }

    public void addAllNotClear(Collection<D> collection) {
        if (list != null) {
            list.addAll(collection);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return layoutId;
    }

    protected abstract void covert(ViewHolder holder, D d);

    /**
     * 实现了这个就不要实现2个参数的那个
     *
     * @param holder
     * @param d
     * @param position
     */
    protected void covert(ViewHolder holder, D d, int position) {

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}
