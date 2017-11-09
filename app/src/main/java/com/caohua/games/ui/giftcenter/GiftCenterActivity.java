package com.caohua.games.ui.giftcenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;

import com.caohua.games.R;
import com.caohua.games.ui.vip.CommonActivity;
import com.chsdk.configure.DataStorage;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.ui.widget.CHSingleBtnAlertDialog;
import com.chsdk.utils.ViewUtil;
import com.culiu.mhvp.core.tabs.com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/10/26.
 */

public class GiftCenterActivity extends CommonActivity {

    private PagerSlidingTabStrip tab;
    private ViewPager viewPager;
    private String[] tabTitle = {"热门礼包", "礼包分类", "存号箱"};
    public static final String GIFT_TAB = "gift_tab";
    private int tabValue;

    @Override
    protected void initVariables() {
        Intent intent = getIntent();
        if (intent != null) {
            tabValue = intent.getIntExtra(GIFT_TAB, -1);
        }
    }

    public int getTabValue() {
        return tabValue;
    }

    @Override
    protected String subTitle() {
        return "礼包中心";
    }

    @Override
    protected int childViewId() {
        return R.layout.ch_activity_gift_center;
    }

    @Override
    protected void initView() {
        tab = getView(R.id.ch_activity_gift_center_tab);
        viewPager = getView(R.id.ch_activity_gift_center_pager);
        List<Fragment> list = new ArrayList<>();
        list.add(new GiftCenterHotFragment());
        list.add(new GiftCenterClassifyFragment());
        list.add(new GiftCenterSaveFragment());
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), list));
        tab.setViewPager(viewPager);
        setTabStrip(tab);
        viewPager.setOffscreenPageLimit(3);
        tab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                boolean giftSaveDialog = DataStorage.getGiftSaveDialog(activity);
                if (position == 2 && !giftSaveDialog) {
                    DataStorage.saveGiftSaveDialog(activity, true);
                    showSaveHintDialog();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (tabValue > -1 && tabValue < 3) {
            viewPager.setCurrentItem(tabValue);
        }

    }

    private void showSaveHintDialog() {
        final CHSingleBtnAlertDialog singleDialog = new CHSingleBtnAlertDialog(activity, true, true);
        singleDialog.show();
        singleDialog.setTitle("提示");
        singleDialog.setContent("已领取的礼包请尽快使用，长时间未使用会进入到淘号并可能会被其他玩家使用");
        singleDialog.setCancelButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singleDialog.dismiss();
            }
        });
    }

    private PagerSlidingTabStrip setTabStrip(PagerSlidingTabStrip pagerSlidingTabStrip) {
        pagerSlidingTabStrip.setTabPaddingLeftRight(ViewUtil.dp2px(activity, 16));
        pagerSlidingTabStrip.setTextColor(Color.BLACK);
        pagerSlidingTabStrip.setMatchExpand(true);
        pagerSlidingTabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
        pagerSlidingTabStrip.setTypeface(null, Typeface.NORMAL);
        pagerSlidingTabStrip.setIndicatorHeight(ViewUtil.dp2px(activity, 2));
        pagerSlidingTabStrip.setIndicatorColor(getResources().getColor(com.culiu.mhvp.core.R.color.ch_green_1));
        pagerSlidingTabStrip.setBackgroundColor(Color.WHITE);
        return pagerSlidingTabStrip;
    }

    @Override
    protected void loadData() {

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
            return tabTitle.length;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitle[position];
        }
    }

    public static void start(Context context, int tabValue) {
        Intent intent = new Intent(context, GiftCenterActivity.class);
        intent.putExtra(GIFT_TAB, tabValue);
        context.startActivity(intent);
    }
}
