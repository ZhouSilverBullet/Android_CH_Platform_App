package com.caohua.games.ui.giftcenter.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.gift.GiftCenterHotEntry;
import com.caohua.games.ui.adapter.CommonRecyclerViewAdapter;
import com.caohua.games.ui.adapter.ViewHolder;
import com.caohua.games.ui.giftcenter.GameGiftActivity;
import com.caohua.games.ui.giftcenter.GiftDetailActivity;
import com.chsdk.utils.PicUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/10/27.
 */

public class GiftCenterHScrollView extends RelativeLayout {
    private Context context;
    private RecyclerView recyclerView;
    private MyAdapter adapter;

    public GiftCenterHScrollView(Context context) {
        this(context, null);
    }

    public GiftCenterHScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GiftCenterHScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.ch_gift_center_h_scroll_view, this, true);
        setVisibility(GONE);
        recyclerView = ((RecyclerView) findViewById(R.id.ch_gift_center_h_scroll_recycler));
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        adapter = new MyAdapter(context, new ArrayList<GiftCenterHotEntry.MyListBean>(), R.layout.ch_gift_center_h_scroll_item);
        recyclerView.setAdapter(adapter);
    }

    public void setData(List<GiftCenterHotEntry.MyListBean> list) {
        if (list == null || list.size() == 0) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        if (adapter != null) {
            adapter.addAll(list);
        }
    }

    private class MyAdapter extends CommonRecyclerViewAdapter<GiftCenterHotEntry.MyListBean> {

        public MyAdapter(Context context, List<GiftCenterHotEntry.MyListBean> list, int layoutId) {
            super(context, list, layoutId);
        }

        @Override
        protected void covert(ViewHolder holder, GiftCenterHotEntry.MyListBean entry) {
            TextView name = holder.getView(R.id.ch_gift_center_h_scroll_item_name);
            TextView count = holder.getView(R.id.ch_gift_center_h_scroll_item_gift_count);
            ImageView image = holder.getView(R.id.ch_gift_center_h_scroll_item_image);
            String gameIcon = entry.getGame_icon();
            name.setText(entry.getGame_name());
            PicUtil.displayImg(context, image, gameIcon, R.drawable.ch_default_apk_icon);
            count.setText("" + entry.getGift_num());
            final String gameId = entry.getGame_id();
            holder.getConvertView().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(gameId)) {
                        GameGiftActivity.start(context, gameId);
                    }
                }
            });
        }
    }

}
