package com.caohua.games.ui.bbs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.bbs.BBSListLogic;
import com.caohua.games.biz.bbs.ForumListEntry;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.widget.NoNetworkView;
import com.caohua.games.ui.widget.SubActivityTitleView;
import com.chsdk.biz.BaseLogic;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.RiffEffectRelativeLayout;
import com.chsdk.utils.PicUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zhouzhou on 2017/6/20.
 */

public class ForumListActivity extends BaseActivity {
    private SmartRefreshLayout refreshLayout;
    private ListView bottomListView;
    private View emptyView;
    private View progressLayout;
    private ListAdapter adapter;
    private NoNetworkView noNetworkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_forum_list);
        initVariables();
        initView();
        initData();
        logic(0, false);
    }

    private void initVariables() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openWelcomeActivity(this, 2);
    }

    private void initView() {
        refreshLayout = getView(R.id.ch_activity_forum_refresh_layout);
        bottomListView = getView(R.id.ch_activity_forum_list_view);
        emptyView = getView(R.id.ch_activity_forum_empty);
        noNetworkView = getView(R.id.ch_activity_forum_no_network);
        progressLayout = getView(R.id.ch_forum_list_download_progress_layout);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                logic(0, true);
            }
        });

        ((SubActivityTitleView) getView(R.id.ch_activity_forum_sub_title)).getBackImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWelcomeActivity(v.getContext(), 2);
                finish();
            }
        });
    }

    private void initData() {
        adapter = new ListAdapter(new ArrayList<ForumListEntry.DataBean>());
        bottomListView.setAdapter(adapter);
    }

    private void logic(int page, boolean load) {
        if (!load) {
            progressLayout.setVisibility(View.VISIBLE);
        }
        BBSListLogic bbsListLogic = new BBSListLogic();
        bbsListLogic.getBBSListLogic(page + "", new BaseLogic.AppLogicListner() {
            @Override
            public void failed(String errorMsg) {
                progressLayout.setVisibility(View.GONE);
                refreshLayout.finishRefresh(false);
                CHToast.show(ForumListActivity.this, errorMsg);
                if (adapter.getCount() == 0 && !AppContext.getAppContext().isNetworkConnected()) {
                    showNoNetworkView(true);
                    return;
                }
                showNoNetworkView(false);
                if (adapter.getCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            emptyView.setVisibility(View.GONE);
                            refreshLayout.autoRefresh();
                        }
                    });
                }
            }

            @Override
            public void success(Object entryResult) {
                showNoNetworkView(false);
                progressLayout.setVisibility(View.GONE);
                refreshLayout.finishRefresh();
                if (entryResult instanceof ForumListEntry) {
                    ForumListEntry entry = (ForumListEntry) entryResult;
                    List<ForumListEntry.DataBean> data = entry.getData();
                    if (adapter.getCount() != 0) {
                        adapter.clear();
                    }
                    adapter.addAll(data);
                }
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
                    refreshLayout.autoRefresh();
                }
            });
        } else {
            noNetworkView.setVisibility(View.GONE);
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, ForumListActivity.class);
        context.startActivity(intent);
    }

    class ListAdapter extends BaseAdapter {
        List<ForumListEntry.DataBean> beanList;

        public ListAdapter(List<ForumListEntry.DataBean> beanList) {
            this.beanList = beanList;
        }

        @Override
        public int getCount() {
            return beanList == null ? 0 : beanList.size();
        }

        @Override
        public Object getItem(int position) {
            return beanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return beanList.size();
        }

        public void addAll(Collection collection) {
            if (beanList != null) {
                beanList.addAll(collection);
                notifyDataSetChanged();
            }
        }

        public void clear() {
            if (beanList != null) {
                beanList.clear();
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ch_forum_list_item_view, null);
                viewHolder.itemImage = (ImageView) convertView.findViewById(R.id.ch_forum_list_item_image);
                viewHolder.itemName = (TextView) convertView.findViewById(R.id.ch_forum_list_item_name);
                viewHolder.itemDescribe = (TextView) convertView.findViewById(R.id.ch_forum_list_item_describe);
                viewHolder.itemLayout = (RiffEffectRelativeLayout) convertView.findViewById(R.id.ch_forum_list_item_layout);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ForumListEntry.DataBean bean = beanList.get(position);
            viewHolder.itemName.setText(bean.getForum_name());
            PicUtil.displayImg(parent.getContext(), viewHolder.itemImage, bean.getGame_icon(), R.drawable.ch_default_apk_icon);
            viewHolder.itemDescribe.setText(bean.getForum_memo());
            final String forum_id = bean.getForum_id();
            viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(forum_id)) {
                        int forumId = Integer.parseInt(forum_id);
                        Intent intent = new Intent(ForumListActivity.this, BBSActivity.class);
                        intent.putExtra("forumId", forumId);
                        ForumListActivity.this.startActivity(intent);
                    }
                }
            });
            return convertView;
        }

        class ViewHolder {
            ImageView itemImage;
            TextView itemName;
            TextView itemDescribe;
            RiffEffectRelativeLayout itemLayout;
        }
    }
}
