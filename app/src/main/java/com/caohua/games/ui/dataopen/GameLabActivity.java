package com.caohua.games.ui.dataopen;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.dataopen.GameLabLogic;
import com.caohua.games.biz.dataopen.entry.GameLabEntry;
import com.caohua.games.ui.adapter.ItemDivider;
import com.caohua.games.ui.adapter.MultiRecyclerViewAdapter;
import com.caohua.games.ui.adapter.ViewHolder;
import com.caohua.games.ui.vip.CommonActivity;
import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/9/21.
 */

public class GameLabActivity extends CommonActivity {
    private RecyclerView recyclerView;
    private GameLabAdapter adapter;

    @Override
    protected String subTitle() {
        return "游戏研究室";
    }

    @Override
    protected int childViewId() {
        return R.layout.ch_activity_game_lab;
    }

    @Override
    protected void initView() {
        recyclerView = getView(R.id.ch_activity_game_lab_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.addItemDecoration(new ItemDivider().setDividerWidth(ViewUtil.dp2px(activity, 8)).setDividerColor(0xfff4f4f4));
        adapter = new GameLabAdapter(activity, new ArrayList<GameLabEntry>(), new int[]{R.layout.ch_game_lab_item, R.layout.ch_game_lab_item_1});
        recyclerView.setAdapter(adapter);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                refreshLayout.setEnabled(topRowVerticalPosition >= 0);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                loadData();
            }
        });
        showLoadProgress(true);
    }

    @Override
    public boolean childIsRefresh() {
        return true;
    }

    public void childLoadData() {
        new GameLabLogic().gameLab(new BaseLogic.DataLogicListner<List<GameLabEntry>>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                showLoadProgress(false);
                refreshLayout.finishRefresh();
                if (activity.isFinishing()) {
                    return;
                }
                CHToast.show(activity, errorMsg);
                if (adapter == null || adapter.getItemCount() == 0) {
                    if (HttpConsts.ERROR_NO_NETWORK.equals(errorMsg)) {
                        showNoNetworkView(true);
                        return;
                    }
                    showEmptyView(true);
                }
            }

            @Override
            public void success(List<GameLabEntry> entryList) {
                showNoNetworkView(false);
                showLoadProgress(false);
                refreshLayout.finishRefresh();
                if (activity.isFinishing()) {
                    return;
                }
                //如果结果回来要加一个空的item进去，否则会少一个item的数据
                if (entryList != null && entryList.size() > 0) {
                    entryList.add(new GameLabEntry());
                    adapter.addAll(entryList);
                }

                if (adapter == null || adapter.getItemCount() == 0) {
                    showEmptyView(true);
                }
            }
        });
    }

    @Override
    protected void loadData() {
        childLoadData();
    }

    private class GameLabAdapter extends MultiRecyclerViewAdapter<GameLabEntry> {

        public GameLabAdapter(Context context, List<GameLabEntry> list, int[] layoutIds) {
            super(context, list, layoutIds);
        }

        @Override
        protected void covert(ViewHolder holder, int position) {
            if (position == list.size() - 1) {
            } else {
                GameLabEntry entry = list.get(position);
                ImageView bg = holder.getView(R.id.ch_game_lab_item_bg);
                ImageView icon = holder.getView(R.id.ch_game_lab_item_game_icon);
                TextView name = holder.getView(R.id.ch_game_lab_item_game_name);
                TextView des = holder.getView(R.id.ch_game_lab_item_game_des);
                des.setText(entry.desc);
                final String game_name = entry.game_name;
                name.setText(game_name);
                PicUtil.displayImg(context, icon, entry.icon, R.drawable.ch_default_apk_icon);
                PicUtil.displayImg(context, bg, entry.img, R.drawable.ch_default_pic);
                final String game_id = entry.game_id;
                bg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(game_id)) {
                            DataOpenActivity.startOpen(context, game_id, game_name, DataOpenActivity.class);
                        }
                    }
                });
            }
        }

        @Override
        protected int getMultiItemViewType(int position) {
            if (position == list.size() - 1) {
                return layoutIds[1];
            }
            return layoutIds[0];
        }
    }
}
