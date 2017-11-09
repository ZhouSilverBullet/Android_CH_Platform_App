package com.caohua.games.ui.dataopen.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.dataopen.entry.DataOpenHintEntry;
import com.caohua.games.ui.adapter.CommonRecyclerViewAdapter;
import com.caohua.games.ui.adapter.GridDividerItemDecoration;
import com.caohua.games.ui.adapter.ViewHolder;
import com.caohua.games.ui.dataopen.DataExistCallback;
import com.caohua.games.ui.dataopen.DataOpenNotifyActivity;
import com.caohua.games.ui.vip.widget.VipTitleView;
import com.chsdk.utils.Base64PicUtil;
import com.chsdk.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/9/21.
 */

public class DataOpenHintView extends RelativeLayout implements DataExistCallback {
    private Context context;
    private VipTitleView titleView;
    private RecyclerView recyclerView;
    private HintAdapter adapter;
    private String managerGameId;
    private String titleName;

    public DataOpenHintView(Context context) {
        this(context, null);
    }

    public DataOpenHintView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DataOpenHintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.ch_data_open_hint_view, this, true);
        setVisibility(GONE);
        titleView = (VipTitleView) findViewById(R.id.ch_data_open_hint_title_view);
        recyclerView = (RecyclerView) findViewById(R.id.ch_data_open_hint_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new GridDividerItemDecoration(
                ViewUtil.dp2px(context, 10), getResources().getColor(R.color.ch_gray)));
        adapter = new HintAdapter(context, new ArrayList<DataOpenHintEntry>(), R.layout.ch_data_open_hint_item_view);
        recyclerView.setAdapter(adapter);
        titleView.getMoreText().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DataOpenNotifyActivity.startOpen(context, managerGameId, titleName, DataOpenNotifyActivity.class);
            }
        });
    }

    public void setData(List<DataOpenHintEntry> data, String managerGameId, String titleName) {
        if (data == null || data.size() == 0) {
            setVisibility(GONE);
            return;
        }
        this.titleName = titleName;
        this.managerGameId = managerGameId;
        setVisibility(VISIBLE);
        if (adapter != null) {
            adapter.addAll(data);
        }
    }

    @Override
    public boolean existBack() {
        if (adapter != null && adapter.getItemCount() > 0) {
            return true;
        }
        return false;
    }

    private class HintAdapter extends CommonRecyclerViewAdapter<DataOpenHintEntry> {

        public HintAdapter(Context context, List<DataOpenHintEntry> list, int layoutId) {
            super(context, list, layoutId);
        }

        @Override
        protected void covert(ViewHolder holder, DataOpenHintEntry entry) {
            ImageView icon = holder.getView(R.id.ch_data_open_hint_item_icon);
            TextView text = holder.getView(R.id.ch_data_open_hint_item_text);
            TextView limit = holder.getView(R.id.ch_data_open_hint_item_level_limit);
            text.setText(entry.title);
            if (TextUtils.isEmpty(entry.level)) {
                limit.setVisibility(GONE);
            } else {
                limit.setVisibility(VISIBLE);
                limit.setText(entry.level);
            }
            if (!TextUtils.isEmpty(entry.icon)) {
                Bitmap bitmap = Base64PicUtil.stringToBitmap(entry.icon);
                if (bitmap != null) {
                    icon.setImageBitmap(bitmap);
                }
            }
        }
    }
}
