package com.caohua.games.ui.dataopen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.dataopen.DataOpenActLogic;
import com.caohua.games.biz.dataopen.entry.DataOpenActEntry;
import com.caohua.games.ui.adapter.CommonRecyclerViewAdapter;
import com.caohua.games.ui.adapter.ViewHolder;
import com.caohua.games.ui.vip.CommonActivity;
import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.Base64PicUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/9/27.
 */

public class DataOpenActSecondActivity extends CommonActivity {
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private String managerGameId;

    @Override
    protected void initVariables() {
        Intent intent = getIntent();
        if (intent != null) {
            managerGameId = intent.getStringExtra(DataOpenActivity.MANAGER_GAME_ID);
        }
    }

    @Override
    protected String subTitle() {
        return "活动中心";
    }

    @Override
    protected int childViewId() {
        return R.layout.ch_activity_data_open_act_second;
    }

    @Override
    protected void initView() {
        recyclerView = getView(R.id.ch_activity_data_open_act_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new RecyclerAdapter(activity,
                new ArrayList<DataOpenActEntry>(), R.layout.ch_view_data_open_act_item);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void loadData() {
        showLoadProgress(true);
        new DataOpenActLogic().dataOpenAct(managerGameId, "all", new BaseLogic.DataLogicListner<List<DataOpenActEntry>>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                showLoadProgress(false);
                if (activity.isFinishing()) {
                    return;
                }
                CHToast.show(activity, errorMsg);
                if (HttpConsts.ERROR_NO_NETWORK.equals(errorMsg)) {
                    showNoNetworkView(true);
                    return;
                }
                showEmptyView(true);
            }

            @Override
            public void success(List<DataOpenActEntry> entryResult) {
                if (activity.isFinishing()) {
                    return;
                }
                if (entryResult != null && entryResult.size() > 0) {
                    adapter.addAll(entryResult);
                }
                showLoadProgress(false);
                showNoNetworkView(false);
                if (adapter.getItemCount() == 0) {
                    showEmptyView(true);
                }
            }
        });
    }

    private class RecyclerAdapter extends CommonRecyclerViewAdapter<DataOpenActEntry> {

        public RecyclerAdapter(Context context, List<DataOpenActEntry> list, int layoutId) {
            super(context, list, layoutId);
        }

        @Override
        protected void covert(ViewHolder holder, DataOpenActEntry dataOpenActEntry) {
            ImageView icon = holder.getView(R.id.ch_view_open_data_act_item_image);
            TextView title = holder.getView(R.id.ch_view_open_data_act_item_title);
            TextView time = holder.getView(R.id.ch_view_open_data_act_item_time);
            TextView des = holder.getView(R.id.ch_view_open_data_act_item_des);
            String timeValue = dataOpenActEntry.time;
            if (!TextUtils.isEmpty(timeValue)) {
                time.setText(timeValue);
                time.setVisibility(View.VISIBLE);
                des.setVisibility(View.INVISIBLE);
            } else {
                des.setText("火爆进行中...");
                des.setVisibility(View.VISIBLE);
                time.setVisibility(View.INVISIBLE);
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

    public static void startOpen(Context context, String managerGameId, Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.putExtra(DataOpenActivity.MANAGER_GAME_ID, managerGameId);
        context.startActivity(intent);
    }

}
