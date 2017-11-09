package com.caohua.games.ui.account;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.ViewGroup;

import com.caohua.games.R;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.fragment.CommentFragment;
import com.caohua.games.ui.fragment.TieZiFragment;
import com.chsdk.utils.ViewUtil;
import com.culiu.mhvp.core.tabs.com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhou on 2017/5/28.
 */

public class MineSendContentActivity extends BaseActivity {
    private PagerSlidingTabStrip tabStrip;
    private ViewPager pager;
    private String[] titleTab = {"我的帖子", "我的评论"};
    private TieZiFragment tieZiFragment;
    private CommentFragment commentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_mine_send_content);
        initView();
    }

    private void initView() {
        if (tieZiFragment == null) {
            tieZiFragment = new TieZiFragment();
        }
        if (commentFragment == null) {
            commentFragment = new CommentFragment();
        }
        List<Fragment> list = new ArrayList<>();
        list.add(tieZiFragment);
        list.add(commentFragment);
        tabStrip = ((PagerSlidingTabStrip) findViewById(R.id.ch_activity_mine_content_tab));
        pager = ((ViewPager) findViewById(R.id.ch_activity_mine_content_pager));
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), list));
        tabStrip.setViewPager(pager);
        setTabStrip(tabStrip);
    }

    private PagerSlidingTabStrip setTabStrip(PagerSlidingTabStrip pagerSlidingTabStrip) {
        pagerSlidingTabStrip.setTabPaddingLeftRight(ViewUtil.dp2px(MineSendContentActivity.this, 50));
        pagerSlidingTabStrip.setTextColor(Color.BLACK);
        pagerSlidingTabStrip.setShouldExpand(false);
        pagerSlidingTabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics()));
        pagerSlidingTabStrip.setTypeface(null, Typeface.NORMAL);
        pagerSlidingTabStrip.setIndicatorHeight(ViewUtil.dp2px(MineSendContentActivity.this, 2));
        pagerSlidingTabStrip.setIndicatorColor(getResources().getColor(com.culiu.mhvp.core.R.color.ch_green_1));
        pagerSlidingTabStrip.setBackgroundColor(Color.WHITE);
        return pagerSlidingTabStrip;
    }

    class MyPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> list;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return titleTab.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleTab[position];
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MineSendContentActivity.class);
        context.startActivity(intent);
    }
}
