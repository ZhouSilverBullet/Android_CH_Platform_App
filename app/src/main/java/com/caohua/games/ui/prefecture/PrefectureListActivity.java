package com.caohua.games.ui.prefecture;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
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
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;
import com.culiu.mhvp.core.tabs.com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zhouzhou on 2017/6/2.
 */

public class PrefectureListActivity extends BaseActivity {
    private SubActivityTitleView titleView;
    private int listId;
    private String titleName;
    private ListAdapter adapter;
    private EmptyView empty;
    //    private PrefectureListTabView tabView;
    private PrefectureListActivity activity;
    private String classifyId = "0";
    private NoNetworkView noNetworkView;
    private View twoBallView;
    private PagerSlidingTabStrip tab;
    private String[] searchTitleTab = {"文章", "帖子", "游戏"};
    private ViewPager pager;
    private List<PrefectureListEntry.ClassifyBean> classify;

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
        logic(0, classifyId);
    }

    private void showTwoBallView(boolean isShow) {
        twoBallView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void logic(int number, final String classifyId) {
        if (number == 0) {
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
                    if (classify == null) {
                        classify = entry.getClassify();
                        PrefectureListEntry.ClassifyBean bean = new PrefectureListEntry.ClassifyBean();
                        bean.setClassify_id("0");
                        bean.setClassify_name("全部");
                        classify.add(0, bean);
                        List<String> list = new ArrayList<String>();
                        for (PrefectureListEntry.ClassifyBean classifyBean : classify) {
                            list.add(classifyBean.getClassify_name());
                        }
                        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), classify, list));
                        tab.setViewPager(pager);
                        pager.setOffscreenPageLimit(classify.size());
                        setTabStrip(tab);
                    }
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
                    logic(0, classifyId);
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
                    logic(0, classifyId);
                }
            });
        }
    }

    private void initView() {
        titleView = getView(R.id.ch_activity_prefecture_list_title_view);
        titleView.setTitle(titleName);
        adapter = new ListAdapter(new ArrayList<PrefectureListEntry.ContentBean>());
        noNetworkView = getView(R.id.ch_activity_prefecture_list_no_network);
        twoBallView = getView(R.id.ch_activity_prefecture_list_two_ball);
        tab = getView(R.id.ch_activity_prefecture_list_tab);
        pager = getView(R.id.ch_activity_prefecture_list_pager);
        setTabStrip(tab);
    }

    private PagerSlidingTabStrip setTabStrip(PagerSlidingTabStrip pagerSlidingTabStrip) {
        pagerSlidingTabStrip.setTabPaddingLeftRight(ViewUtil.dp2px(this, 15));
        pagerSlidingTabStrip.setTextColor(Color.BLACK);
        pagerSlidingTabStrip.setMatchExpand(true);
        pagerSlidingTabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
        pagerSlidingTabStrip.setTypeface(null, Typeface.NORMAL);
        pagerSlidingTabStrip.setIndicatorHeight(ViewUtil.dp2px(this, 2));
        pagerSlidingTabStrip.setIndicatorColor(getResources().getColor(com.culiu.mhvp.core.R.color.ch_green_1));
        pagerSlidingTabStrip.setBackgroundColor(Color.WHITE);
        return pagerSlidingTabStrip;
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        private List<PrefectureListEntry.ClassifyBean> list;
        private List<String> titleTab;
        private FragmentManager fm;

        public MyPagerAdapter(FragmentManager fm, List<PrefectureListEntry.ClassifyBean> list, List<String> titleTab) {
            super(fm);
            this.fm = fm;
            this.list = list;
            this.titleTab = titleTab;
        }

        @Override
        public Fragment getItem(int position) {
            return PrefectureListFragment.newInstance(list.get(position).getClassify_id(), listId);
        }

        @Override
        public int getCount() {
            return titleTab == null ? 0 : titleTab.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            LogUtil.errorLog("position :" + titleTab.get(position));
            return titleTab.get(position);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            LogUtil.errorLog("searchFragment setPrimaryItem:");
        }
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
