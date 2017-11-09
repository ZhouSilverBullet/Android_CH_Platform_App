package com.caohua.games.ui.search;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.home.DataMgr;
import com.caohua.games.biz.search.HotGameEntry;
import com.caohua.games.biz.search.SearchCountEntry;
import com.caohua.games.biz.search.SearchGameEntry;
import com.caohua.games.biz.search.SearchGameLogic;
import com.caohua.games.ui.BaseFragment;
import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.DataStorage;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.ViewUtil;
import com.culiu.mhvp.core.tabs.com.astuetz.PagerSlidingTabStrip;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by CXK on 2016/11/8.
 */

public class SearchFragment extends BaseFragment {
    private List<HotGameEntry> hotGameData;
    private LinearLayout hotGameLayout, historyLayout;
    private int type = DataMgr.DATA_TYPE_SEARCH_GAME;
    private List<String> lists = new ArrayList<>();

    private TextView clearHistory;
    private TextView changeText;
    private List<SearchGameEntry> resultData;
    private List<SearchListItemView> mSearchListItemViews;
    private LinearLayout tabPagerLayout;
    private PagerSlidingTabStrip searchPagerTab;
    private String[] searchTitleTab = {"文章", "帖子", "游戏"};
    private ViewPager searchViewPager;
    private SearchArticleFragment articleFragment;
    private MyPagerAdapter adapter;
    private String gameName;
    private int current = 0;
    private FlexboxLayout historyFlexLayout;
    private FlexboxLayout hotFlex;

    public SearchFragment() {

    }

    @Override
    protected void initLoad() {
        List<HotGameEntry> data = getIntentListData();
        handleData(null, data);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ch_fragment_search;
    }

    @Override
    protected List<LoadParams> getDataType() {
        List<LoadParams> types = new ArrayList<>();
        LoadParams params = new LoadParams();
        params.requestType = type;
        types.add(params);
        return types;
    }

    @Override
    protected void initChildView() {
        setRefreshEnable(false);
        hasLoadData = true;
        changeText = findView(R.id.ch_fragment_change_text);
        hotGameLayout = findView(R.id.ch_fragment_search_hot_game_layout);
        hotFlex = findView(R.id.ch_fragment_search_hot_flex);
        historyLayout = findView(R.id.ch_fragment_search_history_layout);
        historyFlexLayout = findView(R.id.ch_fragment_history_flex);
        clearHistory = findView(R.id.ch_fragment_clear_history);

        lists = readHistoryRecord();
        if (lists == null || lists.size() == 0) {
            historyLayout.setVisibility(View.GONE);
        } else {
            historyFlexLayout.removeAllViews();
            handleFlexLayout(historyFlexLayout, lists);
        }


        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataStorage.saveGameName(getActivity(), "");
                historyFlexLayout.removeAllViews();
                historyLayout.setVisibility(View.GONE);
            }
        });

        changeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(false);
            }
        });

        initPager();
    }

    private void handleFlexLayout(final FlexboxLayout layout, List<String> lists) {
        for (final String list : lists) {
            TextView textView = new TextView(getContext());
            textView.setBackgroundResource(R.drawable.ch_shape_search_lines);
            textView.setText(list);
            int[] colors = new int[]{R.color.ch_text_hint, R.color.ch_black, R.color.ch_black};
            int[][] states = new int[3][];
            states[0] = new int[]{-android.R.attr.state_pressed};
            states[1] = new int[]{android.R.attr.state_pressed};
            states[2] = new int[]{android.R.attr.state_focused};
            ColorStateList stateList = new ColorStateList(states, colors);
            textView.setTextColor(stateList);
            textView.setIncludeFontPadding(false);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            textView.setPadding(ViewUtil.dp2px(getContext(), 9), ViewUtil.dp2px(getContext(), 3),
                    ViewUtil.dp2px(getContext(), 9), ViewUtil.dp2px(getContext(), 3));
            FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = ViewUtil.dp2px(getContext(), 5);
            layoutParams.bottomMargin = ViewUtil.dp2px(getContext(), 5);
            layoutParams.rightMargin = ViewUtil.dp2px(getContext(), 5);
            layoutParams.leftMargin = ViewUtil.dp2px(getContext(), 5);
            layout.addView(textView, layoutParams);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layout == historyFlexLayout) {
                        ((SearchActivity) getActivity()).setHistoryRecord(list);
                    } else {
                        if (getContext() instanceof SearchActivity) {
                            ((SearchActivity) getContext()).setEdit(list);
                        }
                        searchLogic(list);
                    }
                }
            });
        }
    }

    public void hideHotAndHistoryLayout() {
        hotGameLayout.setVisibility(View.GONE);
        historyLayout.setVisibility(View.GONE);
    }

    public void showHistoryGridView() {
        historyLayout.setVisibility(View.VISIBLE);
    }

    public void hideHistoryGridView() {
        historyLayout.setVisibility(View.GONE);
    }

    public void refreshHotAndHistoryLayout() {
        if (hotGameData != null && hotGameData.size() > 0) {
            hotGameLayout.setVisibility(View.VISIBLE);
        } else {
            hotGameLayout.setVisibility(View.GONE);
        }

        lists = readHistoryRecord();
        if (lists != null && lists.size() > 0) {
            historyFlexLayout.removeAllViews();
            handleFlexLayout(historyFlexLayout, lists);
            historyLayout.setVisibility(View.VISIBLE);
        } else {
            historyLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void handleData(LoadParams param, Object data) {
        if (type == DataMgr.DATA_TYPE_SEARCH_GAME) {  //热门搜索
            List<HotGameEntry> list = (List<HotGameEntry>) data;
            if (list == null || list.size() == 0) {
                return;
            }
            hotGameData = list;
            List<String> stringList = new ArrayList<>();
            for (HotGameEntry hotGameEntry : list) {
                String game_name = hotGameEntry.getGame_name();
                stringList.add(game_name);
            }
            handleFlexLayout(hotFlex, stringList);
        }
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void searchLogic(final String entryGameName) {
        final LoadingDialog dialog = new LoadingDialog(getContext(), "");
        dialog.show();
        new SearchGameLogic(getActivity()).getAssignGame(entryGameName, new BaseLogic.DataLogicListner<SearchCountEntry>() {
            @Override
            public void failed(String errorMsg, int code) {
                dialog.dismiss();
            }

            @Override
            public void success(SearchCountEntry entryResult) {
                dialog.dismiss();
                hideHotAndHistoryLayout();
                setGameName(entryGameName);
                setData(entryResult);
            }
        });
    }

    public void setData(SearchCountEntry entry) {
        if (entry == null) {
            tabPagerLayout.setVisibility(View.GONE);
            return;
        }
        boolean isShow = false;
        if (tabPagerLayout.getVisibility() == View.VISIBLE) {
            isShow = true;
        }
        tabPagerLayout.setVisibility(View.VISIBLE);
        if (articleFragment == null) {
            articleFragment = new SearchArticleFragment();
        }

        int setNes = 0;
        int setForum = 0;
        int setGame = 0;

        if (TextUtils.isEmpty(entry.news)) {
            entry.news = "0";
        } else {
            setNes = getIntValue(entry.news);
        }
        if (TextUtils.isEmpty(entry.forum)) {
            entry.forum = "0";
        } else {
            setForum = getIntValue(entry.forum);
        }
        if (TextUtils.isEmpty(entry.game)) {
            entry.game = "0";
        } else {
            setGame = getIntValue(entry.game);
        }

        searchTitleTab = new String[]{"文章(" + entry.news + ")", "帖子(" + entry.forum + ")", "游戏(" + entry.game + ")"};
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new SearchArticleFragment());
        fragmentList.add(new SearchPostFragment());
        ((SearchActivity) getActivity()).setGameName(gameName);
        fragmentList.add(new SearchGameFragment());
        adapter = new MyPagerAdapter(this.getChildFragmentManager(), fragmentList);
        searchViewPager.setAdapter(adapter);
        searchPagerTab.setViewPager(searchViewPager);
        setTabStrip(searchPagerTab);
        searchPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (!isShow) { //没有出现
            if (setNes != 0 || setNes == -100) {
                //不滑动
            } else { //tab1内容为0
                if (setForum != 0 || setForum == -100) {
                    searchViewPager.setCurrentItem(1);
                } else { //tab2的内容为0同时tab1也为0
                    if (setGame != 0 || setGame == -100) {
                        searchViewPager.setCurrentItem(2);
                    }
                }
            }
        } else {
            searchViewPager.setCurrentItem(current);
        }
    }


    private int getIntValue(String value) {
        int i = 0;
        try {
            i = Integer.parseInt(value);
        } catch (Exception e) {
            i = -100;
        }
        return i;
    }

    public void writeHistoryRecord(String gameName) {
        List<String> gameList = readHistoryRecord();
        if (gameList == null || gameList.size() == 0) {
            DataStorage.saveGameName(getActivity(), gameName);
            return;
        } else {
            List<String> dataList = new ArrayList<>();
            dataList.addAll(gameList);

            if (dataList.contains(gameName)) {
                dataList.remove(gameName);
            } else if (gameList.size() == 4) {
                String lastVaule = dataList.get(dataList.size() - 1);
                dataList.remove(lastVaule);
            }
            dataList.add(0, gameName);

            String str = new String();
            for (String name : dataList) {
                if (!TextUtils.isEmpty(str)) {
                    str = str + "я" + name;
                } else {
                    str = name;
                }
            }
            DataStorage.saveGameName(getActivity(), str);
        }
    }

    public List<String> readHistoryRecord() {
        String gameName = DataStorage.getGameName(getActivity());
        if (TextUtils.isEmpty(gameName)) {
            return null;
        }

        return Arrays.asList(gameName.split("я"));
    }

    protected LayoutAnimationController getAnimationController() {
        LayoutAnimationController controller;
        Animation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);//从0.5倍放大到1倍
        anim.setDuration(500);
        controller = new LayoutAnimationController(anim, 0.1f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        return controller;
    }

    @Override
    public void onDestroy() {
        if (mSearchListItemViews != null) {
            for (SearchListItemView searchListItemView : mSearchListItemViews) {
                if (searchListItemView != null) {
                    searchListItemView.onDestroy();
                }
            }
        }
        super.onDestroy();
    }

    private void initPager() {
        tabPagerLayout = findView(R.id.ch_fragment_search_result_tab_layout);
        searchPagerTab = findView(R.id.ch_fragment_search_result_tab);
        searchViewPager = findView(R.id.ch_fragment_search_result_pager);
        searchViewPager.setOffscreenPageLimit(3);
        adapter = new MyPagerAdapter(this.getChildFragmentManager(), new ArrayList<Fragment>());
        searchViewPager.setAdapter(adapter);
        searchPagerTab.setViewPager(searchViewPager);
        setTabStrip(searchPagerTab);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        private FragmentManager fm;
        private List<Fragment> list;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.fm = fm;
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return searchTitleTab.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            if (list.get(position) instanceof SearchGameFragment) {
//                Bundle args = new Bundle();
//                args.putString(SearchGameFragment.GAME_NAME, gameName);
//                ((SearchGameFragment) list.get(position)).setArguments(args);
//            }
            return super.instantiateItem(container, position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            LogUtil.errorLog("position :" + searchTitleTab[position]);
            return searchTitleTab[position];
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            LogUtil.errorLog("searchFragment setPrimaryItem:");
        }
    }

    private PagerSlidingTabStrip setTabStrip(PagerSlidingTabStrip pagerSlidingTabStrip) {
        pagerSlidingTabStrip.setTabPaddingLeftRight(ViewUtil.dp2px(getActivity(), 15));
        pagerSlidingTabStrip.setTextColor(Color.BLACK);
        pagerSlidingTabStrip.setMatchExpand(true);
        pagerSlidingTabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
        pagerSlidingTabStrip.setTypeface(null, Typeface.NORMAL);
        pagerSlidingTabStrip.setIndicatorHeight(ViewUtil.dp2px(getActivity(), 2));
        pagerSlidingTabStrip.setIndicatorColor(getResources().getColor(com.culiu.mhvp.core.R.color.ch_green_1));
        pagerSlidingTabStrip.setBackgroundColor(Color.WHITE);
        return pagerSlidingTabStrip;
    }

}
