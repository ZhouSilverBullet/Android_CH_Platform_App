package com.caohua.games.ui.search;

import android.graphics.Color;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.bbs.ForumShareEntry;
import com.caohua.games.biz.search.SearchPostEntry;
import com.caohua.games.biz.search.SearchPostLogic;
import com.caohua.games.ui.account.AccountHeadView;
import com.caohua.games.ui.account.AccountHomePageActivity;
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
 */

public class SearchPostFragment extends SearchSuperFragment<SearchPostEntry> {

    @Override
    protected void onFragmentVisibleChange(boolean b) {
        super.onFragmentVisibleChange(b);
        if (b) {
            loadData(true, true);
        }
    }

    @Override
    public int getWidthDivider() {
        return 8;
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
        new SearchPostLogic().searchPost(gameName, size, new BaseLogic.DataLogicListner<List<SearchPostEntry>>() {
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
            public void success(List<SearchPostEntry> entryList) {
                showNoNetworkView(false);
                if (isRefreshLayout) {
                    refreshLayout.finishRefresh();
                } else {
                    refreshLayout.finishLoadmore();
                }
                showLoadProgress(false);
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
        if (adapter != null) {
            onLogic(adapter.getItemCount(), false);
        }
    }

    @Override
    protected int itemLayoutId() {
        return R.layout.ch_search_post_recycler_item;
    }

    @Override
    protected void covertItem(ViewHolder holder, final SearchPostEntry searchPostEntry, int position) {
        AccountHeadView photo = holder.getView(R.id.ch_post_content_item_user_photo);
        TextView name = holder.getView(R.id.ch_post_content_item_nick_name);
        TextView time = holder.getView(R.id.ch_post_content_item_time);
        TextView content = holder.getView(R.id.ch_post_content_item_content);
        LinearLayout container = holder.getView(R.id.ch_post_content_item_image_container);
        ImageView image1 = holder.getView(R.id.ch_post_content_item_image_1);
        ImageView image2 = holder.getView(R.id.ch_post_content_item_image_2);
        ImageView image3 = holder.getView(R.id.ch_post_content_item_image_3);
        TextView imageText = holder.getView(R.id.ch_post_content_item_image_text);
        TextView recommend = holder.getView(R.id.ch_post_content_item_recommend);
        TextView upvote = holder.getView(R.id.ch_post_content_item_upvote_total);
        TextView comment = holder.getView(R.id.ch_post_content_item_comment_total);

        photo.setAccountImage(searchPostEntry.user_icon, false);
        photo.setAccountWidthHeadBg(searchPostEntry.img_mask, 10);  //image_mask后台先未给，我填空
        clickEventUserMsg(photo.getAccountImage(), searchPostEntry.userid, searchPostEntry.nickname);
        int is_vip = searchPostEntry.is_vip;
        if (is_vip > 0) {
            name.setTextColor(Color.RED);
        } else {
            name.setTextColor(Color.BLACK);
        }
        name.setText(searchPostEntry.nickname);
        clickEventUserMsg(name, searchPostEntry.userid, searchPostEntry.nickname);
        time.setText(searchPostEntry.time);
        Spannable spannable = changeContentColor(searchPostEntry.title);
        if (spannable != null) {
            content.setText(spannable);
        } else {
            content.setText(searchPostEntry.title);
        }

        recommend.setText(searchPostEntry.tag_name);
        upvote.setText(searchPostEntry.upvote_total);
        comment.setText(searchPostEntry.comment_total);

        List<String> images = searchPostEntry.image;
        int size = images.size();
        container.setVisibility(View.VISIBLE);
        if (size == 1) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.GONE);
            image3.setVisibility(View.GONE);
            imageText.setVisibility(View.GONE);
        } else if (size == 2) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.GONE);
            imageText.setVisibility(View.GONE);
        } else if (size >= 3) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
            imageText.setVisibility(View.GONE);
            if (size > 3) {
                imageText.setVisibility(View.VISIBLE);
                imageText.setText("共" + size + "张图");
            }
        } else {
            image1.setVisibility(View.GONE);
            image2.setVisibility(View.GONE);
            image3.setVisibility(View.GONE);
            imageText.setVisibility(View.GONE);
            container.setVisibility(View.GONE);
        }

        if (size > 0) {
            for (int i = 0; i < size; i++) {
                String url = images.get(i);
                if (i == 0) {
                    doImageIcon(image1, url, R.drawable.ch_default_apk_icon);
                } else if (i == 1) {
                    doImageIcon(image2, url, R.drawable.ch_default_apk_icon);
                } else if (i == 2) {
                    doImageIcon(image3, url, R.drawable.ch_default_apk_icon);
                }
            }
        }

        final String detail_url = searchPostEntry.url;
        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(detail_url)) {
                    ForumShareEntry forumShareEntry = new ForumShareEntry();
                    forumShareEntry.setTitle(searchPostEntry.title);
                    forumShareEntry.setGameIcon(searchPostEntry.game_icon);
                    forumShareEntry.setGameName(searchPostEntry.forum_name);
                    WebActivity.startForForumPage(activity, detail_url, searchPostEntry.article_id, forumShareEntry, -1);
                }
            }
        });
    }

    public void doImageIcon(ImageView imageView, String url, int defaultPic) {
        imageView.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(url)) {
            PicUtil.displayImg(activity, imageView, url, defaultPic);
        } else {
            imageView.setImageResource(defaultPic);
        }
    }

    private void clickEventUserMsg(View view, final String userId, final String userName) {
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AccountHomePageActivity.start(activity, userId, userName);
                }
            });
        }
    }
}
