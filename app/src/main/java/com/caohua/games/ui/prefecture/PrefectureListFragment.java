package com.caohua.games.ui.prefecture;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.prefecture.GameCenterLogic;
import com.caohua.games.biz.prefecture.PrefectureListEntry;
import com.caohua.games.biz.prefecture.PrefectureListLogic;
import com.caohua.games.ui.adapter.ViewHolder;
import com.caohua.games.ui.search.SearchSuperFragment;
import com.chsdk.http.HttpConsts;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.PicUtil;

import java.util.List;

/**
 * Created by zhousaito on 2017/11/10.
 */

public class PrefectureListFragment extends SearchSuperFragment<PrefectureListEntry.ContentBean> {

    private String classifyId;
    private int listId;

    public static PrefectureListFragment newInstance(String id, int listId) {
        Bundle args = new Bundle();
        args.putString("key", id);
        args.putInt("listId", listId);
        PrefectureListFragment fragment = new PrefectureListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initChildView() {
        super.initChildView();
        classifyId = getArguments().getString("key");
        listId = getArguments().getInt("listId",-1);
    }

    @Override
    protected void onFragmentVisibleChange(boolean b) {
        super.onFragmentVisibleChange(b);
        if (b) {
            loadData(true, true);
        }
    }

    @Override
    protected void loadMore() {
        if (adapter != null) {
            onLogic(adapter.getItemCount(), false);
        }
    }

    @Override
    protected void loadData(boolean isRefreshLayout, boolean showProgress) {
        if (showProgress) {
            showLoadProgress(true);
        }
        isFirst = false;
        onLogic(0, isRefreshLayout);
    }

    private void onLogic(int size, final boolean isRefreshLayout) {
        final PrefectureListLogic logic = new PrefectureListLogic(size, listId);
        logic.prefectureList(classifyId, new GameCenterLogic.AppGameCenterListener() {

            @Override
            public void failed(String errorMsg) {
                if (isRefreshLayout) {
                    refreshLayout.finishRefresh();
                } else {
                    refreshLayout.finishLoadmore();
                }
                if (!HttpConsts.ERROR_CODE_PARAMS_VALID.equals(errorMsg)) {
                    CHToast.show(activity, errorMsg);
                }
                showLoadProgress(false);
                if (adapter != null && adapter.getItemCount() > 0) {
                    showEmptyView(false);
                    showNoNetworkView(false);
                    refreshLayout.setLoadmoreFinished(true);
                    return;
                }
                if (!AppContext.getAppContext().isNetworkConnected()) {
                    showNoNetworkView(true);
                    return;
                }
                showEmptyView(true);
            }

            @Override
            public void success(Object entryResult, int currentPage) {
                showNoNetworkView(false);
                if (isRefreshLayout) {
                    refreshLayout.finishRefresh();
                } else {
                    refreshLayout.finishLoadmore();
                }
                showLoadProgress(false);
                if (entryResult instanceof PrefectureListEntry) {
                    List<PrefectureListEntry.ContentBean> entryList = ((PrefectureListEntry) entryResult).getContent();
                    if (entryList != null && entryList.size() > 0) {
                        showEmptyView(false);
                        if (adapter == null) {
                            adapter = new MyAdapter(activity, entryList, new int[]{itemLayoutId(), R.layout.ch_game_center_recycler_item_loading});
                            recyclerView.setAdapter(adapter);
                        } else {
                            if (isRefreshLayout) {
                                adapter.addAll(entryList);
                            } else {
                                adapter.addAllNotClear(entryList);
                            }
                        }
                    } else {
                        if (adapter != null && adapter.getItemCount() > 0) {
                            return;
                        }
                        showEmptyView(true);
                    }
                }
            }
        });
    }
    @Override
    protected int itemLayoutId() {
        return R.layout.ch_prefecture_item_strategy;
    }

    @Override
    protected void covertItem(ViewHolder holder,final PrefectureListEntry.ContentBean contentBean, int position) {
        ImageView image = holder.getView(R.id.ch_prefecture_strategy_image);
        TextView title = holder.getView(R.id.ch_prefecture_strategy_title);
        TextView type = holder.getView(R.id.ch_prefecture_strategy_type);
        TextView date = holder.getView(R.id.ch_prefecture_strategy_date);

        title.setText(contentBean.getTitle());
        if (!TextUtils.isEmpty(contentBean.getImage())) {
            image.setVisibility(View.VISIBLE);
            PicUtil.displayImg(getContext(), image, contentBean.getImage(), R.drawable.ch_default_pic);
        } else {
            image.setVisibility(View.GONE);
        }
        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.startWebPage(getContext(), contentBean.getUrl());
            }
        });
        date.setText(contentBean.getTime());
        type.setText(contentBean.getClassify_name());
    }
}
