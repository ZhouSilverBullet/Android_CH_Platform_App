package com.caohua.games.ui.search;

import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.search.SearchArticleEntry;
import com.caohua.games.biz.search.SearchArticleLogic;
import com.caohua.games.ui.adapter.ViewHolder;
import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.PicUtil;

import java.util.List;

/**
 * Created by admin on 2017/10/25.
 * 文章
 */

public class SearchArticleFragment extends SearchSuperFragment<SearchArticleEntry> {

    public void loadData(boolean isRefreshLayout, boolean showProgress) {
        if (showProgress) {
            showLoadProgress(true);
        }
        isFirst = false;
        onLogic(0, isRefreshLayout);
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

    private void onLogic(int size, final boolean isRefreshLayout) {
        new SearchArticleLogic().searchArticle(gameName, size, new BaseLogic.DataLogicListner<List<SearchArticleEntry>>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                if (isRefreshLayout) {
                    refreshLayout.finishRefresh();
                } else {
                    refreshLayout.finishLoadmore();
                }
                showLoadProgress(false);
                if (!HttpConsts.ERROR_CODE_PARAMS_VALID.equals(errorMsg)) {
                    CHToast.show(activity, errorMsg);
                }
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
            public void success(List<SearchArticleEntry> entryList) {
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
    protected void onFragmentVisibleChange(boolean b) {
        super.onFragmentVisibleChange(b);
        if (b) {
            loadData(true, true);
        }
    }

    @Override
    protected int itemLayoutId() {
        return R.layout.ch_search_article_recycler_item;
    }

    @Override
    protected void covertItem(ViewHolder holder, SearchArticleEntry entry, int position) {
        ImageView image = holder.getView(R.id.ch_search_article_recycler_item_image);
        TextView content = holder.getView(R.id.ch_search_article_recycler_item_content);
//        TextView title = holder.getView(R.id.ch_search_article_recycler_item_title);
        TextView typeText = holder.getView(R.id.ch_search_article_recycler_item_type);
        TextView upvoteText = holder.getView(R.id.ch_search_article_recycler_item_upvote_total);
        TextView commentNumberText = holder.getView(R.id.ch_search_article_recycler_item_comment_total);
        PicUtil.displayImg(activity, image, entry.image, R.drawable.ch_default_pic);
        Spannable spannable = changeContentColor(entry.title);
        if (spannable != null) {
//            title.setText(spannable);
            content.setText(spannable);
        } else {
//            title.setText(entry.title);
            content.setText(entry.title);
        }
        upvoteText.setText(entry.praise_times);
        typeText.setText(entry.game_name);
//        content.setText(entry.content);
        commentNumberText.setText(entry.comment_times);
        final String url = entry.url;
        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(url)) {
                    WebActivity.startWebPage(activity, url);
                }
            }
        });
    }

}
