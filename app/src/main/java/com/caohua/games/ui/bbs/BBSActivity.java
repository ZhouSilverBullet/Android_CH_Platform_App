package com.caohua.games.ui.bbs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.bbs.BBSContentEntry;
import com.caohua.games.biz.bbs.BBSContentLogic;
import com.caohua.games.biz.bbs.BBSContentParam;
import com.caohua.games.biz.bbs.BBSNotifyCommentData;
import com.caohua.games.biz.bbs.BBSNotifyPraiseData;
import com.caohua.games.biz.bbs.BBSTopEntry;
import com.caohua.games.biz.bbs.BBSTopLogic;
import com.caohua.games.biz.bbs.ForumShareEntry;
import com.caohua.games.biz.prefecture.GameCenterLogic;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.account.AccountHeadView;
import com.caohua.games.ui.account.AccountHomePageActivity;
import com.caohua.games.ui.mymsg.MyMsgActivity;
import com.caohua.games.ui.send.SendEditActivity;
import com.caohua.games.ui.widget.NoNetworkView;
import com.chsdk.api.CacheManager;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zhouzhou on 2017/5/10.
 */

public class BBSActivity extends BaseActivity {

    private ListView loadListView;
    private ListAdapter adapter;
    private int headerHeight;
    private TextView bbsTitleBg;
    private ImageView headerGameIcon;
    private TextView headerNameText;
    //    private ImageView headerImageView;
    private BBSSelectorTypeView headerBbsTypeView;
    private LinearLayout headerTopArticle;
    private View backImage;
    private View editImage;
    private String game_icon;
    private String forum_name;
    private View bbsSkin;
    private int forumId;
    private View bbsEmptyView;
    private View downloadProgress;

    private String mTag = "0";
    private boolean sameTag;
    //    private View listErrorLayout;
    private SmartRefreshLayout refreshLayout;
    private View bbsTitleLayout;
    private TextView tieziText;
    private TextView toDayText;
    private TextView desText;
    private TextView changeText;
    private PopupWindow popupWindow;
    private int is_sort = 0;
    private View errorLayout;
    private NoNetworkView noNetworkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_bbs);
        initVariables();
        initView();
        initData();
    }

    private void initVariables() {
        Intent intent = getIntent();
        if (intent != null) {
            forumId = intent.getIntExtra("forumId", 133);
            Uri uri = intent.getData();
            if (uri != null) {
                forumId = stringToInt(uri.getQueryParameter("ch_key"));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openWelcomeActivity(this, 0);
    }

    @Subscribe
    public void notifyPraise(BBSNotifyPraiseData entry) {
        if (entry != null) {
            int position = entry.getPosition();
            BBSContentEntry.DataBean item = (BBSContentEntry.DataBean) adapter.getItem(position);
            String upvote_total = item.getUpvote_total();
            if (!TextUtils.isEmpty(upvote_total)) {
                int parseInt = Integer.parseInt(upvote_total);
                parseInt = parseInt + 1;
                item.setUpvote_total(parseInt + "");
                adapter.dataBeenList.set(position, item);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe
    public void notifyComment(BBSNotifyCommentData entry) {
        if (entry != null) {
            int position = entry.getPosition();
            BBSContentEntry.DataBean item = (BBSContentEntry.DataBean) adapter.getItem(position);
            String comment_total = item.getComment_total();
            if (!TextUtils.isEmpty(comment_total)) {
                int parseInt = Integer.parseInt(comment_total);
                parseInt = parseInt + 1;
                item.setComment_total(parseInt + "");
                adapter.dataBeenList.set(position, item);
                adapter.notifyDataSetChanged();
            }
        }
    }

    protected void showNoNetworkView(boolean showNoNetwork) {
        if (showNoNetwork) {
            noNetworkView.setVisibility(View.VISIBLE);
            noNetworkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noNetworkView.setVisibility(View.GONE);
                    downloadProgress.setVisibility(View.VISIBLE);
                    initData();
                }
            });
        } else {
            noNetworkView.setVisibility(View.GONE);
        }
    }

    private void initData() {
        BBSTopEntry topEntry = (BBSTopEntry) CacheManager.readObject(getApplicationContext(), "BBSTopEntry");
        if (topEntry != null) {
            topInitData(topEntry, true);
        }
        BBSTopLogic logic = new BBSTopLogic(this, forumId, 0);
        logic.getTop(new GameCenterLogic.AppGameCenterListener() {
            @Override
            public void failed(String errorMsg) {
                CHToast.show(BBSActivity.this, errorMsg);
                if (loadListView.getAdapter() != null && loadListView.getAdapter().getCount() > 0) {
                    if (headerBbsTypeView.getData() == null && !AppContext.getAppContext().isNetworkConnected()) {
                        showNoNetworkView(true);
                        return;
                    }
                }
                if (loadListView.getAdapter() != null && loadListView.getAdapter().getCount() > 0) {
                    if (bbsEmptyView != null) {
                        if (headerBbsTypeView.getData() == null) {
                            bbsEmptyView.setVisibility(View.VISIBLE);
                            bbsEmptyView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    bbsEmptyView.setVisibility(View.GONE);
                                    downloadProgress.setVisibility(View.VISIBLE);
                                    initData();
                                }
                            });
                        }
                    }
                }
                contentListLogic(mTag, true, 0, true);
            }

            @Override
            public void success(Object entryResult, int currentPage) {
                showNoNetworkView(false);
                if (entryResult instanceof BBSTopEntry) {
                    topInitData((BBSTopEntry) entryResult);
                }
                contentListLogic(mTag, true, 0, true);
            }
        });
    }

    private void topInitData(BBSTopEntry entryResult, boolean cache) {
        BBSTopEntry entry = entryResult;
        forum_name = entry.getData().getForum_name();
        if (!cache) {
            bbsTitleBg.setText(forum_name);
        }

        tieziText.setText("帖子:" + entry.getData().getForum_count());
        toDayText.setText("今日:" + entry.getData().getForum_day_count());
        desText.setText(entry.getData().getForum_memo());
        List<BBSTopEntry.DataBean.ForumTagBean> forum_tag = entry.getData().getForum_tag();
        headerBbsTypeView.setData(forum_tag);
        game_icon = entry.getData().getGame_icon();
        if (!TextUtils.isEmpty(game_icon)) {
            PicUtil.displayImg(BBSActivity.this, headerGameIcon, game_icon, R.drawable.ch_default_apk_icon);
        } else {
            headerGameIcon.setImageResource(R.drawable.ch_default_apk_icon);
        }
        headerNameText.setText(forum_name);
        List<BBSTopEntry.DataBean.TopArticleBean> top_article = entry.getData().getTop_article();
        headerTopArticle.removeAllViews();
        if (top_article != null) {
            for (int i = 0; i < top_article.size(); i++) {
                BBSNewsItemView itemView = new BBSNewsItemView(BBSActivity.this);
                final String title = top_article.get(i).getTitle();
                final String article_id = top_article.get(i).getArticle_id();
                itemView.setData(i, top_article.get(i).getTag_name(), title);
                if (i == top_article.size() - 1) {
                    itemView.setLineViewVisibility(View.GONE);
                } else {
                    itemView.setLineViewVisibility(View.VISIBLE);
                }
                final String detail_url = top_article.get(i).getDetail_url();
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(detail_url)) {
                            ForumShareEntry forumShareEntry = new ForumShareEntry();
                            forumShareEntry.setTitle(title);
                            forumShareEntry.setGameIcon(game_icon);
                            forumShareEntry.setGameName(forum_name);
                            WebActivity.startForForumPage(BBSActivity.this, detail_url, article_id, forumShareEntry, -1);
                        }
                    }
                });
                headerTopArticle.addView(itemView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }
    }

    private void topInitData(BBSTopEntry entryResult) {
        topInitData(entryResult, false);
    }

    /**
     * @param tag
     * @param init 初始化
     */
    private void contentListLogic(String tag, boolean init, int pageStart, final boolean load) {
        contentListLogic(is_sort, tag, init, pageStart, load);
    }

    private void contentListLogic(int iss, String tag, boolean init, int pageStart, final boolean load) {
        if (init) {  //初始化的时候才进行加载缓存
            BBSContentEntry bbsContentEntry = (BBSContentEntry) CacheManager.readObject(getApplicationContext(), "BBSContentEntry");
            if (bbsContentEntry != null) {
                List<BBSContentEntry.DataBean> data = bbsContentEntry.getData();
                if (data != null && adapter != null) {
                    adapter.addAll(data);
                }
            }
        }
        final LoadingDialog dialog = new LoadingDialog(this, "");
        if (!load && !isFinishing()) {
            dialog.show();
        }
        BBSContentParam param = new BBSContentParam();
        param.setForum_id(forumId + "");
        switch (tag) {
            case BBSSelectorTypeView.TYPE_QUAN_BU:
                doParam(param, "0", "0", "0");
                break;
            case BBSSelectorTypeView.TYPE_ZHI_DING:
                doParam(param, "1", "0", "0");
                break;
            case BBSSelectorTypeView.TYPE_JING_HUA:
                doParam(param, "0", "1", "0");
                break;
            default:
                doParam(param, "0", "0", tag);
                break;
        }

        param.setPage_start(pageStart);
        BBSContentLogic contentLogic = new BBSContentLogic(iss, BBSActivity.this, param);
        contentLogic.getContent(new GameCenterLogic.AppGameCenterListener() {
            @Override
            public void failed(String errorMsg) {
                dialog.dismiss();
                downloadProgress.setVisibility(View.GONE);
                refreshLayout.finishRefresh();
                CHToast.show(BBSActivity.this, errorMsg);
                if (!AppContext.getAppContext().isNetworkConnected()) {
                    errorLayout.setVisibility(View.VISIBLE);
                    showErrorOrNoNetwork(false);
                    return;
                }
            }

            @Override
            public void success(Object entryResult, int currentPage) {
                dialog.dismiss();
                refreshLayout.finishRefresh();
                if (entryResult instanceof BBSContentEntry) {
                    BBSContentEntry contentEntry = (BBSContentEntry) entryResult;
                    List<BBSContentEntry.DataBean> data = contentEntry.getData();
                    if (currentPage == 0 && adapter.getCount() > 0) {
                        adapter.clear();
                    }
//                    if (sameTag && !load) {
//                        adapter.clear();
//                    }
                    adapter.addAll(data);
                    if (adapter.getCount() == 0) {
//                        listErrorLayout.setVisibility(View.VISIBLE);
                        errorLayout.setVisibility(View.VISIBLE);
                        showErrorOrNoNetwork(true);
                    } else {
                        errorLayout.setVisibility(View.GONE);
//                        listErrorLayout.setVisibility(View.GONE);
                        if (data.size() == 0) {
                            refreshLayout.finishLoadmore();
                            refreshLayout.setLoadmoreFinished(true);
                        } else {
                            refreshLayout.finishLoadmore();
                        }
                    }
                }
                downloadProgress.setVisibility(View.GONE);
            }
        });
    }

    private void showErrorOrNoNetwork(boolean success) {
        ImageView img = (ImageView) errorLayout.findViewById(R.id.ch_bbs_empty_img);
        TextView text = (TextView) errorLayout.findViewById(R.id.ch_bbs_empty_text);
        if (success) {
            text.setText("快来占领沙发吧！");
            img.setImageResource(R.drawable.ch_no_data);
        } else {
            text.setText("网络丢了，点击刷新重试");
            img.setImageResource(R.drawable.ch_no_network_icon);
        }
        errorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentListLogic(mTag, true, 0, false);
            }
        });
    }


    private void doParam(BBSContentParam param, String is_top, String is_good, String tag_id) {
        param.setIs_top(is_top);
        param.setIs_good(is_good);
        param.setTag_id(tag_id);
    }

    private void initView() {
        loadListView = getView(R.id.ch_activity_bbs_load_list_view);
        View view = LayoutInflater.from(getApplication()).inflate(R.layout.ch_bbs_list_view_empty_footer, null);
        LinearLayout layout = new LinearLayout(getApplication());
        layout.setGravity(Gravity.CENTER);
        layout.addView(view);
        errorLayout = layout.findViewById(R.id.ch_list_item_bbs_list_error);
        errorLayout.setVisibility(View.GONE);
        loadListView.addFooterView(layout);
        refreshLayout = getView(R.id.ch_activity_bbs_refresh_layout);
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (TextUtils.isEmpty(mTag)) {
                    mTag = "0";
                }
                contentListLogic(mTag, false, adapter.getCount(), true);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (TextUtils.isEmpty(mTag)) {
                    mTag = "0";
                }
                contentListLogic(mTag, true, 0, true);
            }
        });

        bbsTitleBg = getView(R.id.ch_activity_bbs_title_bg);
        bbsTitleLayout = getView(R.id.ch_activity_bbs_title_layout);
        final View inflate = initTitleView();
        loadListView.addHeaderView(inflate);
        adapter = new ListAdapter(new ArrayList<BBSContentEntry.DataBean>());

        loadListView.setAdapter(adapter);

        backImage = getView(R.id.ch_activity_bbs_back_image);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWelcomeActivity(v.getContext(), 0);
                finish();
            }
        });
        editImage = getView(R.id.ch_bbs_edit);
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendEditActivity.launchForBBS(BBSActivity.this, forumId + "", true);
                AnalyticsHome.umOnEvent(AnalyticsHome.FORUM_EDIT_ARTICLE, "进入编辑界面");
            }
        });
        bbsSkin = getView(R.id.ch_activity_bbs_skin);
        bbsSkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyMsgActivity.start(v.getContext());
                AnalyticsHome.umOnEvent(AnalyticsHome.FORUM_MESSAGE, "论坛消息按钮点击");
            }
        });

        bbsEmptyView = getView(R.id.ch_activity_bbs_empty_view);
        noNetworkView = getView(R.id.ch_activity_bbs_no_network);
        downloadProgress = getView(R.id.ch_bbs_download_progress_layout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SendEditActivity.REQUSET_CODE_FOR_BBS && resultCode == SendEditActivity.RESULT_CODE_FOR_BBS) {
            if (TextUtils.isEmpty(mTag)) {
                mTag = "0";
            }
            if (refreshLayout != null) {
                contentListLogic(mTag, false, 0, false);
            }
//            contentListLogic(mTag, false, 0, false);
        }
    }

    private View initTitleView() {
        View inflate = View.inflate(this, R.layout.ch_bbs_header_layout, null);
        headerGameIcon = ((ImageView) inflate.findViewById(R.id.ch_bbs_header_game_icon));
        headerNameText = ((TextView) inflate.findViewById(R.id.ch_bbs_header_name));
        int width = getWindowManager().getDefaultDisplay().getWidth();
        headerBbsTypeView = ((BBSSelectorTypeView) inflate.findViewById(R.id.ch_header_type_view));
        headerBbsTypeView.setOnTagIdBackListener(new BBSSelectorTypeView.OnTagIdBackListener() {
            @Override
            public void onTagIdBackListener(String tagId) {
                if (!TextUtils.isEmpty(tagId) && tagId.equals(mTag)) {
                    sameTag = true;
                } else {
                    sameTag = false;
                    errorLayout.setVisibility(View.GONE);
                }
                mTag = tagId;
                if (!sameTag) {
                    AnalyticsHome.umOnEvent(AnalyticsHome.FORUM_TAB, "论坛导航栏点击tag = " + tagId);
//                    contentListLogic(tagId, false, 0, false);
                    if (refreshLayout != null) {
                        contentListLogic(mTag, true, 0, false);
                    }
                }
            }
        });
        headerTopArticle = (LinearLayout) inflate.findViewById(R.id.top_article);
        tieziText = (TextView) inflate.findViewById(R.id.ch_bbs_header_game_tiezi);
        toDayText = (TextView) inflate.findViewById(R.id.ch_bbs_header_game_today);
        desText = (TextView) inflate.findViewById(R.id.ch_bbs_header_game_des);
        changeText = (TextView) inflate.findViewById(R.id.ch_header_list_title_change);
        changeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePopEvent();
            }
        });
        return inflate;
    }

    private void changePopEvent() {
        if (popupWindow == null) {
            View view = LayoutInflater.from(getApplication()).inflate(R.layout.ch_bbs_pop_change_text, null);
            popupWindow = new PopupWindow(view,
                    ViewUtil.dp2px(getApplication(), 80), ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.ch_bbs_pop_bg));
            final TextView huiTie = (TextView) view.findViewById(R.id.ch_bbs_change_hui_tie);
            final TextView faTie = (TextView) view.findViewById(R.id.ch_bbs_change_fa_tie);
            popClick(huiTie, "回复优先", 0);
            popClick(faTie, "发帖优先", 1);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    setBackgroundAlpha(1.0f, BBSActivity.this);
                }
            });
        }
        setBackgroundAlpha(0.5f, BBSActivity.this);
        int[] location = new int[2];
        changeText.getLocationOnScreen(location);
        int height = changeText.getHeight();
        popupWindow.showAtLocation(changeText, Gravity.NO_GRAVITY, location[0] - ViewUtil.dp2px(this, 5), location[1] + height);
    }

    private void setBackgroundAlpha(float bgAlpha, Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        activity.getWindow().setAttributes(lp);
    }

    private void popClick(TextView textView, final String value, final int is_sort) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                changeText.setText(value);
                if (TextUtils.isEmpty(mTag)) {
                    mTag = "0";
                }
                BBSActivity.this.is_sort = is_sort;
                contentListLogic(is_sort, mTag, false, 0, false);
            }
        });
    }

    private void offsetTitle(int firstVisibleItem, View inflate) {
        int offset = 0;
        if (headerHeight == 0) {
            headerHeight = ViewUtil.dp2px(getApplication(), 165) - bbsTitleLayout.getMeasuredHeight();
        }
        if (firstVisibleItem == 1 || firstVisibleItem == 0) {
            View firstItem = loadListView.getChildAt(0);
            if (firstItem != null) {
                offset = 0 - firstItem.getTop();
            }
        } else {
            offset = headerHeight;
        }
        float percent = 0;
        if (headerHeight != 0) {
            percent = (offset * 1f) / (headerHeight * 1f);
        }
        if (percent >= 0 && percent <= 1) {
            bbsTitleLayout.setAlpha(percent);
        } else if (percent > 1) {
            bbsTitleLayout.setAlpha(1);
        }
    }

    private class ListAdapter extends BaseAdapter {

        private List<BBSContentEntry.DataBean> dataBeenList;

        public ListAdapter(List<BBSContentEntry.DataBean> dataBeenList) {
            this.dataBeenList = dataBeenList;
        }

        @Override
        public int getCount() {
            return dataBeenList == null ? 0 : dataBeenList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataBeenList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addAll(Collection collection) {
            if (dataBeenList != null) {
                dataBeenList.addAll(collection);
                notifyDataSetChanged();
            }
        }

        public void clear() {
            if (dataBeenList != null) {
                dataBeenList.clear();
                notifyDataSetChanged();
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ch_bbs_content_list_item, null);
                viewHolder.userAccountHeadView = (AccountHeadView) convertView.findViewById(R.id.ch_bbs_content_item_user_photo);
                viewHolder.nickNameText = (TextView) convertView.findViewById(R.id.ch_bbs_content_item_nick_name);
                viewHolder.timeText = (TextView) convertView.findViewById(R.id.ch_bbs_content_item_time);
                viewHolder.contentText = ((TextView) convertView.findViewById(R.id.ch_bbs_content_item_content));
                viewHolder.upvoteTotalText = (TextView) convertView.findViewById(R.id.ch_bbs_content_item_upvote_total);
                viewHolder.commentTotalText = (TextView) convertView.findViewById(R.id.ch_bbs_content_item_comment_total);
                viewHolder.readTotalText = ((TextView) convertView.findViewById(R.id.ch_bbs_content_item_read));
                viewHolder.imageContainerLayout = (LinearLayout) convertView.findViewById(R.id.ch_bbs_content_item_image_container);
                viewHolder.itemContentImage1 = (ImageView) convertView.findViewById(R.id.ch_bbs_content_item_image_1);
                viewHolder.image3Layout = (RelativeLayout) convertView.findViewById(R.id.ch_bbs_content_item_rl_3);
                viewHolder.imageLastText = (TextView) convertView.findViewById(R.id.ch_bbs_content_item_image_text);
                viewHolder.itemContentImage2 = (ImageView) convertView.findViewById(R.id.ch_bbs_content_item_image_2);
                viewHolder.itemContentImage3 = (ImageView) convertView.findViewById(R.id.ch_bbs_content_item_image_3);
                viewHolder.adminNameText = ((TextView) convertView.findViewById(R.id.ch_bbs_content_item_admin_name));
                viewHolder.itemContentStatusImage = (ImageView) convertView.findViewById(R.id.ch_bbs_content_item_status_image);
                viewHolder.itemContentLevelText = (TextView) convertView.findViewById(R.id.ch_bbs_content_item_level_value);
                viewHolder.vipLayout = convertView.findViewById(R.id.ch_bbs_content_item_vip_layout);
                viewHolder.vipValue = (TextView) convertView.findViewById(R.id.ch_bbs_content_item_vip_value);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final BBSContentEntry.DataBean dataBean = dataBeenList.get(position);
            String user_photo = dataBean.getUser_photo();
            viewHolder.userAccountHeadView.setAccountImage(user_photo, false);
            viewHolder.userAccountHeadView.setAccountWidthHeadBg(dataBean.getImg_mask(), 10);

            clickEventUserMsg(viewHolder.userAccountHeadView.getAccountImage(), dataBean.getUserid(), dataBean.getNickname());
            clickEventUserMsg(viewHolder.nickNameText, dataBean.getUserid(), dataBean.getNickname());

            viewHolder.nickNameText.setText(dataBean.getNickname());
            viewHolder.timeText.setText(dataBean.getComment_time());
            viewHolder.contentText.setText(dataBean.getTitle());
            viewHolder.upvoteTotalText.setText(dataBean.getUpvote_total());
            viewHolder.commentTotalText.setText(dataBean.getComment_total());
            List<String> images = dataBean.getImages();
            int size = images.size();
            viewHolder.imageContainerLayout.setVisibility(View.VISIBLE);
            if (size == 1) {
                viewHolder.itemContentImage1.setVisibility(View.VISIBLE);
                viewHolder.itemContentImage2.setVisibility(View.GONE);
                viewHolder.image3Layout.setVisibility(View.GONE);
                viewHolder.imageLastText.setVisibility(View.GONE);
            } else if (size == 2) {
                viewHolder.itemContentImage1.setVisibility(View.VISIBLE);
                viewHolder.itemContentImage2.setVisibility(View.VISIBLE);
                viewHolder.image3Layout.setVisibility(View.GONE);
                viewHolder.imageLastText.setVisibility(View.GONE);
            } else if (size >= 3) {
                viewHolder.itemContentImage1.setVisibility(View.VISIBLE);
                viewHolder.itemContentImage2.setVisibility(View.VISIBLE);
                viewHolder.image3Layout.setVisibility(View.VISIBLE);
                viewHolder.imageLastText.setVisibility(View.GONE);
                if (size > 3) {
                    viewHolder.imageLastText.setVisibility(View.VISIBLE);
                    viewHolder.imageLastText.setText("共" + size + "张图");
                }
            } else {
                viewHolder.itemContentImage1.setVisibility(View.GONE);
                viewHolder.itemContentImage2.setVisibility(View.GONE);
                viewHolder.image3Layout.setVisibility(View.GONE);
                viewHolder.imageLastText.setVisibility(View.GONE);
                viewHolder.imageContainerLayout.setVisibility(View.GONE);
            }

            String vipLevel = dataBean.getVip_level();
            if (TextUtils.isEmpty(vipLevel)) {
                viewHolder.vipLayout.setVisibility(View.GONE);
            } else {
                viewHolder.vipLayout.setVisibility(View.VISIBLE);
                viewHolder.vipValue.setText(vipLevel);
            }

            String admin_name = dataBean.getAdmin_name();
            if (!TextUtils.isEmpty(admin_name)) {
                viewHolder.nickNameText.setTextColor(getResources().getColor(R.color.ch_bbs_purple));
                viewHolder.adminNameText.setVisibility(View.VISIBLE);
                viewHolder.adminNameText.setText(admin_name);
            } else {
                if (!TextUtils.isEmpty(vipLevel)) {
                    viewHolder.nickNameText.setTextColor(getResources().getColor(R.color.ch_red_text));
                } else {
                    viewHolder.nickNameText.setTextColor(getResources().getColor(R.color.ch_black));
                }
                viewHolder.adminNameText.setVisibility(View.GONE);
            }
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    String url = images.get(i);
                    if (i == 0) {
                        doImageIcon(viewHolder.itemContentImage1, url, R.drawable.ch_default_apk_icon);
                    } else if (i == 1) {
                        doImageIcon(viewHolder.itemContentImage2, url, R.drawable.ch_default_apk_icon);
                    } else if (i == 2) {
                        doImageIcon(viewHolder.itemContentImage3, url, R.drawable.ch_default_apk_icon);
                    }
                }
            }

            boolean is_good = dataBean.isIs_good();
            boolean is_lock = dataBean.isIs_lock();
            boolean is_top = dataBean.isIs_top();
            viewHolder.itemContentStatusImage.setVisibility(View.GONE);
            if (is_lock) {
                viewHolder.itemContentStatusImage.setVisibility(View.VISIBLE);
                viewHolder.itemContentStatusImage.setImageResource(R.drawable.ch_bbs_suoding_icon);
            }
            if (is_good) {
                viewHolder.itemContentStatusImage.setVisibility(View.VISIBLE);
                viewHolder.itemContentStatusImage.setImageResource(R.drawable.ch_bbs_jinghua_icon);
            }
            if (is_top) {
                viewHolder.itemContentStatusImage.setVisibility(View.VISIBLE);
                viewHolder.itemContentStatusImage.setImageResource(R.drawable.ch_bbs_zhiding_icon);
            }
            String read_total = dataBean.getRead_total();
            if (TextUtils.isEmpty(read_total)) {
                viewHolder.readTotalText.setText(0 + "人阅读");
            } else {
                viewHolder.readTotalText.setText(read_total + "人阅读");
            }

            String grow_name = dataBean.getGrow_name();
            if (!TextUtils.isEmpty(grow_name)) {
                viewHolder.itemContentLevelText.setText(dataBean.getShow_level());
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnalyticsHome.umOnEvent(AnalyticsHome.FORUM_CONTENT_ITEM, "论坛内容点击");
                    String detail_url = dataBean.getDetail_url();
                    if (!TextUtils.isEmpty(detail_url)) {
                        ForumShareEntry forumShareEntry = new ForumShareEntry();
                        forumShareEntry.setTitle(dataBean.getTitle());
                        forumShareEntry.setGameIcon(game_icon);
                        forumShareEntry.setGameName(forum_name);
                        WebActivity.startForForumPage(BBSActivity.this, detail_url, dataBean.getArticle_id(), forumShareEntry, position);
                    }
                }
            });
            return convertView;
        }

        private void clickEventUserMsg(View view, final String userId, final String userName) {
            if (view != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AccountHomePageActivity.start(BBSActivity.this, userId, userName);
                    }
                });
            }
        }

        public void doImageIcon(ImageView imageView, String url, int defaultPic) {
            imageView.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(url)) {
                PicUtil.displayImg(BBSActivity.this, imageView, url, defaultPic);
            } else {
                imageView.setImageResource(defaultPic);
            }
        }

        class ViewHolder {
            AccountHeadView userAccountHeadView;
            TextView nickNameText;
            TextView timeText;
            TextView contentText;
            TextView upvoteTotalText;
            TextView commentTotalText;
            LinearLayout imageContainerLayout;
            ImageView itemContentImage1;
            ImageView itemContentImage2;
            ImageView itemContentImage3;
            TextView adminNameText;
            RelativeLayout image3Layout;
            TextView imageLastText;
            TextView readTotalText;
            ImageView itemContentStatusImage;
            TextView itemContentLevelText;
            View vipLayout;
            TextView vipValue;
        }
    }
}
