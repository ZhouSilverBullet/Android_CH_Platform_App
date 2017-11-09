package com.caohua.games.ui.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.account.HomePageEntry;
import com.caohua.games.biz.account.HomePageGameLogic;
import com.caohua.games.ui.BaseActivity;
import com.chsdk.biz.BaseLogic;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.ViewUtil;
import com.culiu.mhvp.core.InnerScrollerContainer;
import com.culiu.mhvp.core.MagicHeaderUtils;
import com.culiu.mhvp.core.MagicHeaderViewPager;
import com.culiu.mhvp.core.OuterPagerAdapter;
import com.culiu.mhvp.core.OuterScroller;
import com.culiu.mhvp.core.tabs.com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by ZengLei on 2017/5/23.
 */

public class AccountHomePageActivity extends BaseActivity {

    private static final String KEY_DATA = "userid";

    private MagicHeaderViewPager mHeaderViewPager;
    private PrefecturePagerAdapter mPagerAdapter;
    private ViewGroup tabsArea;
    private List<Fragment> listFragment;

    private AccountHeadView chViewAccountHomePageTopHeadView;
    private TextView chViewAccountHomePageTopNick;
    private TextView chViewAccountHomePageTopVote;
    private TextView chViewAccountHomePageTopType;

    private String userId;
    //    private SubActivityTitleView subTitleView;
    private String userName;
    private TextView homeLevelValue;
    private TextView homeLevelName;
    private View levelLayout;
    private View topView;
    private int topHeight;
    private View vipLayout;
    private TextView vipValue;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_account_home_page);

        if (getIntent() != null) {
            userId = getIntent().getStringExtra(KEY_DATA);
            userName = getIntent().getStringExtra("userName");
        }
        if (TextUtils.isEmpty(userId)) {
            CHToast.show(this, "您访问的主页不存在,请重试");
            finish();
            return;
        }

        listFragment = new ArrayList<>();
        listFragment.add(new HomePageGameFragment());
        listFragment.add(new HomePagePublishFragment());
        listFragment.add(new HomePageCommentFragment());
        setViewPagerAndIndicator();
        initView();
        getData();
    }

    private void initView() {
        getView(R.id.ch_activity_account_home_page_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private Activity getActivity() {
        return this;
    }

    private void setViewPagerAndIndicator() {
        mHeaderViewPager = new MagicHeaderViewPager(this) {

            @Override
            protected void initTabsArea(LinearLayout container) {
                tabsArea = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.ch_view_account_home_page_viewpage, null);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        MagicHeaderUtils.dp2px(getActivity(), 50));
                container.addView(tabsArea, lp);

                PagerSlidingTabStrip pagerSlidingTabStrip = setTabStrip(tabsArea);
                setTabsArea(tabsArea);
                setPagerSlidingTabStrip(pagerSlidingTabStrip);
            }
        };

        LinearLayout mhvpParent = getView(R.id.ch_activity_account_home_page_container);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        mhvpParent.addView(mHeaderViewPager, lp);

//        mPagerAdapter = new PrefecturePagerAdapter(getSupportFragmentManager(), Arrays.asList(new String[]{"玩游戏", "发表", "评论"}));
//        mHeaderViewPager.setPagerAdapter(mPagerAdapter);

        topView = LayoutInflater.from(getActivity()).inflate(R.layout.ch_view_account_home_page_top, null);
//        subTitleView = ((SubActivityTitleView) topView.findViewById(R.id.ch_account_home_title));
//        subTitleView.setVisibility(View.GONE);
//        subTitleView.setTitle(userName + "");
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mHeaderViewPager.addHeaderView(topView, layoutParams);
        mHeaderViewPager.getViewPager().setOffscreenPageLimit(2);
    }

    private PagerSlidingTabStrip setTabStrip(ViewGroup tabsArea) {
        PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) tabsArea.findViewById(R.id.ch_view_account_home_page_viewpage_strip);
        pagerSlidingTabStrip.setTabPaddingLeftRight(ViewUtil.dp2px(getActivity(), 16));
        pagerSlidingTabStrip.setTextColor(Color.BLACK);
        pagerSlidingTabStrip.setShouldExpand(true);
        pagerSlidingTabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics()));
        pagerSlidingTabStrip.setTypeface(null, Typeface.NORMAL);
        pagerSlidingTabStrip.setIndicatorHeight(ViewUtil.dp2px(getActivity(), 2));
        pagerSlidingTabStrip.setBackgroundColor(Color.WHITE);
        return pagerSlidingTabStrip;
    }

    private void getData() {
        LoadingDialog.start(this);

        HomePageGameLogic logic = new HomePageGameLogic();
        logic.getData(userId, new BaseLogic.DataLogicListner<HomePageEntry>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                LoadingDialog.stop();
                CHToast.show(AccountHomePageActivity.this, errorMsg);
            }

            @Override
            public void success(HomePageEntry entryResult) {
                LoadingDialog.stop();
                if (entryResult != null) {
                    setData(entryResult);
                } else {
                    CHToast.show(AccountHomePageActivity.this, "获取个人信息失败");
                }
            }
        });
    }

    private void setData(HomePageEntry data) {
        chViewAccountHomePageTopHeadView = (AccountHeadView) findViewById(R.id.ch_view_account_home_page_top_icon);
        levelLayout = findViewById(R.id.ch_view_account_home_page_level_layout);
        chViewAccountHomePageTopNick = (TextView) findViewById(R.id.ch_view_account_home_page_top_nick);
        chViewAccountHomePageTopVote = (TextView) findViewById(R.id.ch_view_account_home_page_top_vote);
        chViewAccountHomePageTopType = (TextView) findViewById(R.id.ch_view_account_home_page_top_type);
        homeLevelValue = ((TextView) findViewById(R.id.ch_account_home_level_value));
        homeLevelName = ((TextView) findViewById(R.id.ch_account_home_level_name));
        vipLayout = findViewById(R.id.ch_view_home_page_top_vip_layout);
        vipValue = (TextView) findViewById(R.id.ch_account_home_page_top_vip_value);
        if (TextUtils.isEmpty(data.vip_name)) {
            vipLayout.setVisibility(View.GONE);
        } else {
            vipLayout.setVisibility(View.VISIBLE);
            vipValue.setText(data.vip_name);
        }
        String grow_name = data.grow_name;
        chViewAccountHomePageTopHeadView.setAccountImage(data.photo, false);
        if (!TextUtils.isEmpty(grow_name)) {
            chViewAccountHomePageTopHeadView.setAccountHeadBg(data.img_mask);
            homeLevelName.setText(grow_name);
            String show_level = data.show_level;
            homeLevelValue.setText(show_level);
            levelLayout.setVisibility(View.VISIBLE);
        }

        chViewAccountHomePageTopNick.setText(data.nickName);
        chViewAccountHomePageTopVote.setText("被赞数:" + data.upvoteCount);
        if (TextUtils.isEmpty(data.typeName)) {
            chViewAccountHomePageTopType.setVisibility(View.GONE);
        } else {
            chViewAccountHomePageTopType.setVisibility(View.VISIBLE);
            chViewAccountHomePageTopType.setText(data.typeName);
        }

        HomePageGameFragment homePageGameFragment = (HomePageGameFragment) listFragment.get(0);
        homePageGameFragment.setData(data.gameList);
        mPagerAdapter = new PrefecturePagerAdapter(getSupportFragmentManager(), Arrays.asList(new String[]{"游戏", "帖子", "评论"}));
        mHeaderViewPager.setPagerAdapter(mPagerAdapter);
    }

    public static void start(Context context, String userId, String userName) {
        Intent intent = new Intent(context, AccountHomePageActivity.class);
        intent.putExtra(KEY_DATA, userId);
        intent.putExtra("userName", userName);
        context.startActivity(intent);
    }

    class PrefecturePagerAdapter extends FragmentPagerAdapter implements OuterPagerAdapter {

        private final List<String> tab_title;
        private OuterScroller mOuterScroller;

        public PrefecturePagerAdapter(FragmentManager fm, List<String> tab_title) {
            super(fm);
            this.tab_title = tab_title;
        }

        @Override
        public Fragment getItem(int position) {
            if (tab_title == null) return null;
            return listFragment.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return tab_title == null ? 0 : tab_title.size();
        }

        public void addAll(Collection<String> collection) {
            if (tab_title != null) {
                tab_title.clear();
                tab_title.addAll(collection);
                notifyDataSetChanged();
            }
        }

        @Override
        public void setOuterScroller(OuterScroller outerScroller) {
            mOuterScroller = outerScroller;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            InnerScrollerContainer fragment =
                    (InnerScrollerContainer) super.instantiateItem(container, position);

            if (null != mOuterScroller) {
                fragment.setOuterScroller(mOuterScroller, position);
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tab_title.get(position);
        }
    }
}
