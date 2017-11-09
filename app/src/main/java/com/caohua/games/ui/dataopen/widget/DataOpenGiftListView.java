package com.caohua.games.ui.dataopen.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.dataopen.entry.DataOpenGiftInfoEntry;
import com.caohua.games.ui.adapter.CommonRecyclerViewAdapter;
import com.caohua.games.ui.adapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/9/26.
 */

public class DataOpenGiftListView extends RelativeLayout {
    private Context context;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private int total;
    private int size;
    private GiftAdapter adapter;

    public DataOpenGiftListView(Context context) {
        this(context, null);
    }

    public DataOpenGiftListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DataOpenGiftListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.ch_data_open_gift_list_view, this, true);
        setVisibility(GONE);
        recyclerView = (RecyclerView) findViewById(R.id.ch_data_open_gift_list_recycler);
        recyclerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setFocusable(false);
        recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        adapter = new GiftAdapter(context,
                new ArrayList<DataOpenGiftInfoEntry>(), R.layout.ch_data_open_gift_list_item_view);
        recyclerView.setAdapter(adapter);
    }

    public void setData(List<DataOpenGiftInfoEntry> entryList) {
        if (entryList == null || entryList.size() == 0) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        size = entryList.size();
        total = size % 4;
        layoutManager = new GridLayoutManager(context, 24);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (total) {
                    case 1:
                        if (position == size - 1 || position == size + 1) {
                            return 9;
                        }
                        break;
                    case 2:
                        if (position == size - 2 || position == size + 1) {
                            return 6;
                        }
                        break;
                    case 3:
                        if (position == size - 3 || position == size + 1) {
                            return 3;
                        }
                        break;
                }
                return 6;
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        switch (total) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
        }
        entryList.add(new DataOpenGiftInfoEntry());
        entryList.add(new DataOpenGiftInfoEntry());
        adapter.addAll(entryList);
    }

    private class GiftAdapter extends CommonRecyclerViewAdapter<DataOpenGiftInfoEntry> {

        public GiftAdapter(Context context, List<DataOpenGiftInfoEntry> list, int layoutId) {
            super(context, list, layoutId);
        }

        @Override
        protected void covert(ViewHolder holder, DataOpenGiftInfoEntry dataOpenGiftEntry) {
        }

        @Override
        protected void covert(ViewHolder holder, DataOpenGiftInfoEntry dataOpenGiftEntry, int position) {
            boolean t = false;
            switch (total) {
                case 1:
                    if (position == size - 1 || position == size + 1) {
                        holder.itemView.setVisibility(GONE);
                        t = true;
                    }
                    break;
                case 2:
                    if (position == size - 2 || position == size + 1) {
                        holder.itemView.setVisibility(GONE);
                        t = true;
                    }
                    break;

                case 3:
                    if (position == size - 3 || position == size + 1) {
                        holder.itemView.setVisibility(GONE);
                        t = true;
                    }
                    break;
            }
            if (!t) {
                holder.itemView.setVisibility(VISIBLE);
                ImageView icon = holder.getView(R.id.ch_data_open_gift_list_item_icon);
                TextView text = holder.getView(R.id.ch_data_open_gift_list_item_text);
            }
        }
    }

}
