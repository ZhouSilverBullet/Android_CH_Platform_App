package com.caohua.games.ui.giftcenter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.caohua.games.R;
import com.caohua.games.ui.adapter.ItemDivider;
import com.caohua.games.ui.fragment.NormalFragment;
import com.caohua.games.ui.widget.BlankLoginView;
import com.caohua.games.ui.widget.EmptyView;
import com.caohua.games.ui.widget.NoNetworkView;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.utils.ViewUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

/**
 * Created by admin on 2017/10/27.
 */

public abstract class GiftCenterListFragment extends NormalFragment {

    protected SmartRefreshLayout refreshLayout;
    protected RecyclerView recyclerView;
    protected EmptyView emptyView;
    protected LinearLayoutManager layoutManager;
    protected boolean isFragmentVisible;
    protected boolean isFirst = true;
    protected BlankLoginView blankLoginView;
    private NoNetworkView noNetworkView;

    @Override
    protected void initChildView() {
        refreshLayout = findView(R.id.ch_fragment_gift_center_list_refresh);
        recyclerView = findView(R.id.ch_fragment_gift_center_list_recycler);
        recyclerView.addItemDecoration(new ItemDivider().setDividerColor(0xfff4f4f4).setDividerWidth(ViewUtil.dp2px(activity, 1)));
        emptyView = findView(R.id.ch_fragment_gift_center_list_empty);
        layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadMore(refreshlayout);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                onLogic(0, true);
            }
        });
        blankLoginView = findView(R.id.ch_fragment_gift_center_list_blank_login);
        if (hasLogin()) {
            blankLoginView.show(new BlankLoginView.BlankLoginListener() {
                @Override
                public void onBlankLogin(LoginUserInfo info) {
                    refreshLayout.autoRefresh();
                }
            });
        }

        noNetworkView = findView(R.id.ch_fragment_gift_center_list_no_network);
    }

    /**
     * 需要登录的用这个重写这个方法
     * @return
     */
    protected boolean hasLogin() {
        return false;
    }

    protected abstract void loadMore(RefreshLayout refreshlayout);

    protected void showEmptyView(boolean showEmpty) {
        if (showEmpty) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    emptyView.setVisibility(View.GONE);
                    onLogic(0, true);
                }
            });
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    protected void showNoNetworkView(boolean showNoNetwork) {
        if (showNoNetwork) {
            noNetworkView.setVisibility(View.VISIBLE);
            noNetworkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noNetworkView.setVisibility(View.GONE);
                    onLogic(0, true);
                }
            });
        } else {
            noNetworkView.setVisibility(View.GONE);
        }
    }

    protected void onLogic(int pageCount, boolean isRefresh) {
        isFirst = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isFragmentVisible = true;
        }

        if (mRoot == null) {
            return;
        }
        //可见，并且没有加载过
        if (isFirst && isFragmentVisible) {
            if(blankLoginView.isLogin()) {
                refreshLayout.autoRefresh();
            }
            return;
        }
        //由可见——>不可见 已经加载过
        if (isFragmentVisible) {
            isFragmentVisible = false;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ch_fragment_gift_center_super_list;
    }

}
