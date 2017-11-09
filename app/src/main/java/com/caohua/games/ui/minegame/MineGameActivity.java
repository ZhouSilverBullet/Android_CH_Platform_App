package com.caohua.games.ui.minegame;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.minegame.MineGameListEntry;
import com.caohua.games.biz.minegame.MineGameLogic;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.adapter.ItemDivider;
import com.caohua.games.ui.adapter.MineGameRecyclerAdapter;
import com.caohua.games.ui.widget.EmptyView;
import com.caohua.games.ui.widget.NoNetworkView;
import com.caohua.games.ui.widget.SubActivityTitleView;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.app.AppLogic;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.LogUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhou on 2017/2/22.
 */

public class MineGameActivity extends BaseActivity {
    public static final int IS_REFRESH = 100;
    private RecyclerView recyclerView;
    private SmartRefreshLayout smartRefreshLayout;
    private MineGameRecyclerAdapter mineAdapter;
    private List<MineGameListEntry> localData;
    private EmptyView emptyView;
    private NoNetworkView noNetworkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_mine_game_activity);
        initView();
        initEvent();
        requestGameLogic(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openWelcomeActivity(this, 0);
    }

    private void initView() {
        recyclerView = getView(R.id.ch_activity_mine_recycler);
        emptyView = getView(R.id.ch_mine_game_empty_view);
        noNetworkView = getView(R.id.ch_mine_game_no_network);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new ItemDivider().setDividerColor(0xffeeeeee).setDividerWidth(2));
        smartRefreshLayout = getView(R.id.ch_activity_mine_swipe);
        ArrayList<MineGameListEntry> list = new ArrayList<>();
        mineAdapter = new MineGameRecyclerAdapter(list, this);
        recyclerView.setAdapter(mineAdapter);
//        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                int topRowVerticalPosition =
//                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
//                smartRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
//            }
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//        });
        smartRefreshLayout.autoRefresh();
        localData = AppLogic.getLocalData(this, "mineGame");
        if (localData != null) {
            mineAdapter.addAll(localData);
        }

        ((SubActivityTitleView) getView(R.id.ch_activity_mine_game_title)).getBackImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWelcomeActivity(v.getContext(), 0);
                finish();
            }
        });
    }

    private void initEvent() {
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                requestGameLogic(IS_REFRESH);
            }
        });

        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyView.setVisibility(View.GONE);
                smartRefreshLayout.autoRefresh();
            }
        });
    }

    protected void showNoNetworkView(boolean showNoNetwork) {
        if (showNoNetwork) {
            noNetworkView.setVisibility(View.VISIBLE);
            noNetworkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noNetworkView.setVisibility(View.GONE);
                    smartRefreshLayout.autoRefresh();
                }
            });
        } else {
            noNetworkView.setVisibility(View.GONE);
        }
    }

    private void requestGameLogic(final int refresh) {
        LogUtil.errorLog("MineGameActivity localData: " + localData);

        new MineGameLogic(this).getMineGame(new BaseLogic.AppLogicListner() {
            @Override
            public void failed(String errorMsg) {
                if (smartRefreshLayout != null) {
                    smartRefreshLayout.finishRefresh(false);
                }
                CHToast.show(MineGameActivity.this, errorMsg);
                if (mineAdapter.getItemCount() <= 0 && !AppContext.getAppContext().isNetworkConnected()) {
                    showNoNetworkView(true);
                    mineAdapter.clearAll();
                    return;
                }
                showNoNetworkView(false);
                if (mineAdapter.getItemCount() <= 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    mineAdapter.clearAll();
                    return;
                }
            }

            @Override
            public void success(Object entryResult) {
                showNoNetworkView(false);
                if (smartRefreshLayout != null) {
                    smartRefreshLayout.finishRefresh(true);
                }
                if (entryResult instanceof List && refresh != IS_REFRESH) {
                    if (localData != null) {
                        mineAdapter.clearAll();
                    }
                    emptyView.setVisibility(View.GONE);
                    mineAdapter.addAll(((List<MineGameListEntry>) entryResult));
                } else if (entryResult instanceof List) {
                    mineAdapter.clearAll();
                    mineAdapter.addAll((List<MineGameListEntry>) entryResult);
                    emptyView.setVisibility(View.GONE);
                } else {
                    mineAdapter.clearAll();
                }
            }
        });
    }
}
