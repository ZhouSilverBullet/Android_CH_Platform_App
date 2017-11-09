package com.caohua.games.ui.search;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.download.ViewDownloadMgr;
import com.caohua.games.biz.search.SearchGameEntry;
import com.caohua.games.biz.search.SearchNewGameLogic;
import com.caohua.games.ui.adapter.ViewHolder;
import com.caohua.games.ui.download.DownloadButton;
import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.PicUtil;

import java.util.List;

/**
 * Created by admin on 2017/10/25.
 */

public class SearchGameFragment extends SearchSuperFragment<SearchGameEntry> {
    private ViewDownloadMgr downloadMgr;

    @Override
    protected void onFragmentVisibleChange(boolean b) {
        super.onFragmentVisibleChange(b);
        if (b) {
            loadData(true, true);
        }
    }

    @Override
    protected void loadData(final boolean isRefreshLayout, boolean showProgress) {
        if (showProgress) {
            showLoadProgress(true);
        }
        isFirst = false;
        onLogic(0, isRefreshLayout);
    }

    private void onLogic(int size, final boolean isRefreshLayout) {
        new SearchNewGameLogic().newGame(gameName, size, new BaseLogic.DataLogicListner<List<SearchGameEntry>>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
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
            public void success(List<SearchGameEntry> entryList) {
                showLoadProgress(false);
                showNoNetworkView(false);
                if (isRefreshLayout) {
                    refreshLayout.finishRefresh();
                } else {
                    refreshLayout.finishLoadmore();
                }
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
        });
    }

    @Override
    public void onDestroyView() {
        LogUtil.errorLog("SearchGameFragment onDestroyView");
        adapter = null;
        super.onDestroyView();
    }

    @Override
    protected void loadMore() {
        onLogic(adapter.getItemCount(), false);
    }

    @Override
    protected int itemLayoutId() {
        return R.layout.ch_search_game_recycler_item;
    }

    @Override
    protected void covertItem(ViewHolder holder, final SearchGameEntry entry, int position) {
        if (entry == null) {
            return;
        }
        ImageView icon = holder.getView(R.id.ch_view_fragment_item_icon);
        TextView title = holder.getView(R.id.ch_view_fragment_item_title);
//        TextView type = holder.getView(R.id.ch_view_fragment_item_type_and_size);
        TextView des = holder.getView(R.id.ch_view_fragment_item_des);
        downloadMgr = new ViewDownloadMgr((DownloadButton) holder.getView(R.id.ch_view_fragment_search_item_btn));
        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.startGameDetail(v.getContext(), entry.getDownloadEntry());
            }
        });
        PicUtil.displayImg(getContext(), icon, entry.getGame_icon(), R.drawable.ch_default_apk_icon);
        downloadMgr.setData(entry.getDownloadEntry());
        title.setText(entry.getGame_name());
//        type.setText(entry.getClassify_name() + " | " + entry.getGame_size() + "MB");
        des.setText(entry.getShort_desc());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (downloadMgr != null) {
            downloadMgr.release();
        }
    }
}
