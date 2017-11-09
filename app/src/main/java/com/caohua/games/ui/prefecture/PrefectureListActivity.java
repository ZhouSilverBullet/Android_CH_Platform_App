package com.caohua.games.ui.prefecture;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.prefecture.GameCenterLogic;
import com.caohua.games.biz.prefecture.PrefectureListEntry;
import com.caohua.games.biz.prefecture.PrefectureListLogic;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.widget.BottomLoadListView;
import com.caohua.games.ui.widget.EmptyView;
import com.caohua.games.ui.widget.NoNetworkView;
import com.caohua.games.ui.widget.SubActivityTitleView;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.PicUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zhouzhou on 2017/6/2.
 */

public class PrefectureListActivity extends BaseActivity {
    private SubActivityTitleView titleView;
    private BottomLoadListView bottomListView;
    private int listId;
    private String titleName;
    private ListAdapter adapter;
    private EmptyView empty;
    private PrefectureListTabView tabView;
    private PrefectureListActivity activity;
    private String classifyId = "0";
    private NoNetworkView noNetworkView;
    private View twoBallView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_prefecture_list);
        if (getIntent() != null) {
            listId = getIntent().getIntExtra("listId", -1);
            titleName = getIntent().getStringExtra("titleName");
        } else {
            finish();
        }
        activity = this;
        initView();
        initData();
    }

    private void initData() {
        logic(0, classifyId, false);
    }

    private void showTwoBallView(boolean isShow) {
        twoBallView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void logic(int number, String classifyId, boolean isTab) {
        if (number == 0 && !isTab) {
            showTwoBallView(true);
        }
        final PrefectureListLogic logic = new PrefectureListLogic(number, listId);
        logic.prefectureList(classifyId, new GameCenterLogic.AppGameCenterListener() {

            @Override
            public void failed(String errorMsg) {
                showTwoBallView(false);
                CHToast.show(PrefectureListActivity.this, errorMsg);
                if (adapter != null && adapter.getCount() == 0) {
                    if (!AppContext.getAppContext().isNetworkConnected()) {
                        showNoNetworkView(true);
                    }
                    return;
                }
                showEmpty();
            }

            @Override
            public void success(Object entryResult, int currentPage) {
                showNoNetworkView(false);
                showTwoBallView(false);
                if (empty != null) {
                    empty.setVisibility(View.GONE);
                }
                if (entryResult instanceof PrefectureListEntry) {
                    PrefectureListEntry entry = (PrefectureListEntry) entryResult;
                    List<PrefectureListEntry.ContentBean> beanList = entry.getContent();
                    final List<PrefectureListEntry.ClassifyBean> classify = entry.getClassify();
                    if (!tabView.hasData() && classify != null && classify.size() > 0) {
                        tabView.setData(classify, new PrefectureListTabView.PrefectureTabListener() {
                            @Override
                            public void tabListener(String classifyId) {
                                if (!TextUtils.isEmpty(classifyId) && !classifyId.equals(PrefectureListActivity.this.classifyId)) {
                                    PrefectureListActivity.this.classifyId = classifyId;
                                    logic(0, classifyId, true);
                                }
                            }
                        });
                        tabView.setOneColor();
                    }
                    if (currentPage == 0) {
                        adapter.clear();
                    }
                    adapter.addAll(beanList);
                    boolean enable = beanList.size() == 0;
                    if (enable) {
                        bottomListView.setLoadMoreUnable(true);
                        bottomListView.loadComplete(true);
                    } else {
                        bottomListView.setLoadMoreUnable(false);
                        bottomListView.loadComplete(false);
                    }
                } else if (entryResult == null) {
                    bottomListView.setLoadMoreUnable(true);
                    bottomListView.loadComplete(true);
                }
                showEmpty();
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
                    logic(0, classifyId, false);
                }
            });
        } else {
            noNetworkView.setVisibility(View.GONE);
        }
    }

    private void showEmpty() {
        if (adapter == null || adapter.getCount() == 0) {
            if (empty == null) {
                empty = ((EmptyView) findViewById(R.id.ch_activity_prefecture_list_empty_view));
            }
            empty.setVisibility(View.VISIBLE);
            empty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logic(0, classifyId, false);
                }
            });
        }
    }

    private void initView() {
        titleView = getView(R.id.ch_activity_prefecture_list_title_view);
        bottomListView = getView(R.id.ch_activity_prefecture_list_list);
        titleView.setTitle(titleName);
        adapter = new ListAdapter(new ArrayList<PrefectureListEntry.ContentBean>());
        bottomListView.setAdapter(adapter);
        bottomListView.setOnLoadListener(new BottomLoadListView.OnLoadListener() {
            @Override
            public void onLoad() {
                logic(adapter.getCount(), classifyId, false);
            }
        });
        tabView = (PrefectureListTabView) findViewById(R.id.ch_activity_prefecture_list_tab_view);
        noNetworkView = getView(R.id.ch_activity_prefecture_list_no_network);
        twoBallView = getView(R.id.ch_activity_prefecture_list_two_ball);
    }

    class ListAdapter extends BaseAdapter {
        private List<PrefectureListEntry.ContentBean> dataBeanList;

        public ListAdapter(List<PrefectureListEntry.ContentBean> dataBeanList) {
            this.dataBeanList = dataBeanList;
        }

        @Override
        public int getCount() {
            return dataBeanList == null ? 0 : dataBeanList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataBeanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(parent.getContext(), R.layout.ch_prefecture_item_strategy, null);
                viewHolder.strategyImage = ((ImageView) convertView.findViewById(R.id.ch_prefecture_strategy_image));
                viewHolder.strategyText = ((TextView) convertView.findViewById(R.id.ch_prefecture_strategy_title));
                viewHolder.strategyType = (TextView) convertView.findViewById(R.id.ch_prefecture_strategy_type);
                viewHolder.strategyDate = ((TextView) convertView.findViewById(R.id.ch_prefecture_strategy_date));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final PrefectureListEntry.ContentBean contentBean = dataBeanList.get(position);
            viewHolder.strategyText.setText(contentBean.getTitle());
            if (!TextUtils.isEmpty(contentBean.getImage())) {
                viewHolder.strategyImage.setVisibility(View.VISIBLE);
                PicUtil.displayImg(parent.getContext(), viewHolder.strategyImage, contentBean.getImage(), R.drawable.ch_default_pic);
            } else {
                viewHolder.strategyImage.setVisibility(View.GONE);
            }
            viewHolder.strategyText.setText(contentBean.getTitle());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebActivity.startWebPage(PrefectureListActivity.this, contentBean.getUrl());
                }
            });
            viewHolder.strategyDate.setText(contentBean.getTime());
            viewHolder.strategyType.setText(contentBean.getClassify_name());
            return convertView;
        }

        class ViewHolder {
            ImageView strategyImage;
            TextView strategyText;
            TextView strategyType;
            TextView strategyDate;
        }

        public void addAll(Collection c) {
            if (dataBeanList != null) {
                dataBeanList.addAll(c);
                notifyDataSetChanged();
            }
        }

        public void clear() {
            if (dataBeanList != null) {
                dataBeanList.clear();
            }
        }
    }

}
