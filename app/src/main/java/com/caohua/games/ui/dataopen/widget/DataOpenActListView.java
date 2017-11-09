package com.caohua.games.ui.dataopen.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.dataopen.entry.DataOpenActEntry;
import com.caohua.games.ui.adapter.ViewHolder;
import com.caohua.games.ui.dataopen.DataOpenActSecondActivity;
import com.chsdk.utils.Base64PicUtil;

/**
 * Created by admin on 2017/9/21.
 */

public class DataOpenActListView extends DataOpenCommonListView<DataOpenActEntry> {

    public DataOpenActListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        titleView.getNameText().setText("最新活动");
        titleView.getMoreText().setText("查看全部");
        titleView.getMoreText().setVisibility(VISIBLE);
        titleView.getIcon().setVisibility(GONE);
        titleView.getMoreText().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DataOpenActSecondActivity.startOpen(context, managerGameId, DataOpenActSecondActivity.class);
            }
        });
    }

    @Override
    protected int recyclerItemLayoutId() {
        return R.layout.ch_view_data_open_act_item;
    }

    @Override
    protected void onCommonCovert(ViewHolder holder, DataOpenActEntry dataOpenActEntry) {
        ImageView icon = holder.getView(R.id.ch_view_open_data_act_item_image);
        TextView title = holder.getView(R.id.ch_view_open_data_act_item_title);
        TextView time = holder.getView(R.id.ch_view_open_data_act_item_time);
        TextView des = holder.getView(R.id.ch_view_open_data_act_item_des);
        String timeValue = dataOpenActEntry.time;
        if (!TextUtils.isEmpty(timeValue)) {
            time.setText(timeValue);
            time.setVisibility(VISIBLE);
            des.setVisibility(INVISIBLE);
        } else {
            des.setText("火爆进行中...");
            des.setVisibility(VISIBLE);
            time.setVisibility(INVISIBLE);
        }
        title.setText(dataOpenActEntry.activityName);
        if (!TextUtils.isEmpty(dataOpenActEntry.icon)) {
            Bitmap bitmap = Base64PicUtil.stringToBitmap(dataOpenActEntry.icon);
            if (bitmap != null) {
                icon.setImageBitmap(bitmap);
            }
        }
    }
}
