package com.caohua.games.ui.search;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caohua.games.R;
import com.caohua.games.ui.adapter.ItemDivider;
import com.caohua.games.ui.adapter.MultiRecyclerViewAdapter;
import com.caohua.games.ui.adapter.ViewHolder;
import com.caohua.games.ui.fragment.NormalFragment;
import com.caohua.games.ui.widget.CHTwoBallView;
import com.caohua.games.ui.widget.EmptyView;
import com.chsdk.model.BaseEntry;
import com.chsdk.utils.ViewUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.List;

/**
 * Created by admin on 2017/10/25.
 */

public abstract class SearchSuperFragment<T extends BaseEntry> extends NormalFragment {
    protected SmartRefreshLayout refreshLayout;
    protected RecyclerView recyclerView;
    protected MyAdapter adapter;
    protected CHTwoBallView chTwoBallView;
    protected EmptyView emptyView;

    protected boolean isCreate = false;
    protected boolean isFragmentVisible;
    protected boolean isFirst = true;
    protected LinearLayoutManager layoutManager;
    protected String gameName;

    @Override
    protected void initChildView() {
        refreshLayout = findView(R.id.ch_search_super_refresh);
        recyclerView = findView(R.id.ch_search_super_recycler);
        recyclerView.addItemDecoration(new ItemDivider().setDividerColor(0xfff4f4f4).setDividerWidth(ViewUtil.dp2px(activity, getWidthDivider())));
        chTwoBallView = findView(R.id.ch_search_super_progress);
        emptyView = findView(R.id.ch_search_super_empty_view);
        emptyView.setText("没有结果哦，换个关键词搜索试试");
        layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        isCreate = true;
//        if (isFragmentVisible && isFirst) {
//            onFragmentVisibleChange(true);
//        }
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadMore();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                loadData(true,false);
            }
        });
        if (isFragmentVisible && isFirst) {
//            refreshLayout.autoRefresh();
            loadData(true,true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getActivity() != null) {
            gameName = ((SearchActivity) getActivity()).getGameName();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null && !isFirst) {
//            refreshLayout.autoRefresh();
            loadData(true,true);
        }
    }

    protected void loadMore() {

    }

    /**
     * true
     *
     * @param b
     */
    protected void onFragmentVisibleChange(boolean b) {

    }

    protected void showNoNetworkView(boolean showNoNetwork) {
        if (showNoNetwork) {
            noNetworkView.setVisibility(View.VISIBLE);
            noNetworkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noNetworkView.setVisibility(View.GONE);
                    loadData(true,true);
                }
            });
        } else {
            noNetworkView.setVisibility(View.GONE);
        }
    }

    protected void showEmptyView(boolean showEmpty) {
        if (showEmpty) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    emptyView.setVisibility(View.GONE);
                    loadData(true, true);
                }
            });
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    protected void showLoadProgress(boolean showAnim) {
        chTwoBallView.setVisibility(showAnim ? View.VISIBLE : View.GONE);
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
            loadData(true, true);
            return;
        }
        //由可见——>不可见 已经加载过
        if (isFragmentVisible) {
            onFragmentVisibleChange(false);
            isFragmentVisible = false;
        }
    }

    protected void loadData(boolean isRefreshLayout, boolean showProgress) {

    }

    protected abstract int itemLayoutId();

    protected abstract void covertItem(ViewHolder holder, T t, int position);

    @Override
    protected int getLayoutId() {
        return R.layout.ch_fragment_search_super;
    }

    public int getWidthDivider() {
        return 1;
    }

    protected class MyAdapter extends MultiRecyclerViewAdapter<T> {

        public MyAdapter(Context context, List<T> list, int[] layoutId) {
            super(context, list, layoutId);
        }

        @Override
        protected void covert(ViewHolder holder, int position) {
            covertItem(holder, list.get(position), position);
        }

        @Override
        protected int getMultiItemViewType(int position) {
            return layoutIds[0];
        }

    }

    //关键字变颜色
    protected Spannable changeContentColor(String title) {
        if (TextUtils.isEmpty(gameName) || TextUtils.isEmpty(title)) {
            return null;
        }

        if (gameName.length() == 1 && check(gameName)) { //是字母
            String s = gameName.toLowerCase();
            int index = title.indexOf(s);
            Spannable span = new SpannableString(title);
            if (index != -1) {
                span.setSpan(new ForegroundColorSpan(Color.RED), index, index + s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                String upper = gameName.toUpperCase();
                int i = title.indexOf(upper);
                if (i != -1) {
                    span.setSpan(new ForegroundColorSpan(Color.RED), i, i + upper.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            return span;
        } else { //不是字母
            int index = title.indexOf(gameName);
            Spannable span = new SpannableString(title);
            if (index != -1) {
                span.setSpan(new ForegroundColorSpan(Color.RED), index, index + gameName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return span;
        }

    }

    private static boolean check(String fstrData) {
        if (TextUtils.isEmpty(fstrData)) {
            return false;
        }
        char c = fstrData.charAt(0);
        if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
            return true;
        } else {
            return false;
        }
    }
}
