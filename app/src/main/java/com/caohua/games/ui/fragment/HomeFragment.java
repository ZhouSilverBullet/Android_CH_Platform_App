package com.caohua.games.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.ui.adapter.FragmentAdapter;
import com.caohua.games.ui.openning.OpenningFragment;
import com.caohua.games.ui.ranking.RankingFragment;
import com.caohua.games.ui.rcmd.RcmdFragment;
import com.caohua.games.ui.widget.AutoTextView;
import com.caohua.games.ui.widget.HomeSubTitleView;
import com.caohua.games.ui.widget.NavigationScrollView;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhou on 2017/2/20.
 */

public class HomeFragment extends NormalFragment {
    private FragmentAdapter adapter;
    private ViewPager viewpager;
    private NavigationScrollView topScrollView;
    private AutoTextView tvRcmdGame;
    private RcmdFragment recommendPageFragment;
    private RankingFragment rankingFragment;
    private OpenningFragment openningFragment;
    private Runnable runnable;
    private boolean init;

    @Override
    protected void initChildView() {
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ch_activity_home;
    }

    private void initView() {
        if (isVersionMoreKitkat()) {
            mRoot.setPadding(0, ViewUtil.getStatusHeight(AppContext.getAppContext()) + ViewUtil.dp2px(AppContext.getAppContext(), 50), 0, 0);
        } else {
            mRoot.setPadding(0, ViewUtil.dp2px(AppContext.getAppContext(), 50), 0, 0);
        }
        tvRcmdGame = findView(R.id.ch_home_search);
        setTopScrollView();
        setViewpager();
    }

    private void setTopScrollView() {
        topScrollView = (NavigationScrollView) findView(R.id.ch_home_top_scrollView);
        topScrollView.setOnItemSelectedListener(new NavigationScrollView.OnItemSelectedListener() {
            @Override
            public void itemSelected(int index) {
                viewpager.setCurrentItem(index);
            }
        });
    }

    private void setViewpager() {
        viewpager = (ViewPager) findView(R.id.ch_home_viewpager);

        List<Fragment> list = new ArrayList<>();
        if (recommendPageFragment == null) {
            recommendPageFragment = new RcmdFragment();
        }
        recommendPageFragment.setOnMoreClickListener(new HomeSubTitleView.OnMoreClickListener() {
            @Override
            public void moreClick() {
                AnalyticsHome.umOnEvent(AnalyticsHome.HOME_OPENING_ANALYTICS, "进入开服列表");
                viewpager.setCurrentItem(2);
            }
        });
        list.add(recommendPageFragment);

        if (rankingFragment == null) {
            rankingFragment = new RankingFragment();
        }
        list.add(rankingFragment);

        if (openningFragment == null) {
            openningFragment = new OpenningFragment();
        }
        list.add(openningFragment);

        adapter = new FragmentAdapter(getChildFragmentManager(), list);
        viewpager.setAdapter(adapter);
        viewpager.setOffscreenPageLimit(2);
        viewpager.setCurrentItem(0);
        viewpager.setOnPageChangeListener(new PageChangeListener());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        LogUtil.errorLog("HomeFragment onSaveInstanceState");
        saveFragment(outState, recommendPageFragment);
        saveFragment(outState, rankingFragment);
        saveFragment(outState, openningFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            if (!init) {
                init = true;
            }
            LogUtil.errorLog("HomeFragment isResume");
//            setRcmdGame();
        } else {
//            removeAutoTextRunnable();
            LogUtil.errorLog("HomeFragment onPause");
        }
    }

//    public void removeAutoTextRunnable() {
//        if (tvRcmdGame != null) {
//            LogUtil.errorLog("HomeFragment removeAutoTextRunnable");
//            tvRcmdGame.removeCallbacks(runnable);
//        }
//    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            recommendPageFragment = getFragment(savedInstanceState, RcmdFragment.class);
            rankingFragment = getFragment(savedInstanceState, RankingFragment.class);
            openningFragment = getFragment(savedInstanceState, OpenningFragment.class);
            LogUtil.errorLog("HomeFragment reonCreate,recommendPageFragment:"
                    + recommendPageFragment);
        }
        init = false;
    }

//    /**
//     * 改成EventBus通知的形式
//     * fragment调用另一个fragment
//     * 中的方法有点麻烦
//     */
//    public void setRcmdGame() {
//        LogUtil.errorLog("HomeFragment onResume");
//        if (tvRcmdGame == null) {
//            return;
//        }
//        String rcmdName = getRcmdGameName();
//        if (TextUtils.isEmpty(rcmdName))
//            return;
//        tvRcmdGame.setText(rcmdName);
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                if (getActivity() != null && !getActivity().isFinishing()) {
//                    setRcmdGame();
//                    tvRcmdGame.next();
//                }
//            }
//        };
//        tvRcmdGame.postDelayed(runnable, 4000);
//    }
//
//    private int mLocation; //记录下次的滚动角标
//
//    private String getRcmdGameName() {
//        List<HotGameEntry> data = recommendPageFragment.getRcmdGames();
//        if (data != null && data.size() > 0) {
//            int location = getRandomNumber(data.size());
//            HotGameEntry entry = data.get(location);
//            if (entry != null && !TextUtils.isEmpty(entry.getGame_name())) {
//                return entry.getGame_name();
//            }
//        }
//        return "";
//    }
//
//    private int getRandomNumber(int size) {
//        if (size == 1) {
//            return 0;
//        } else {
//            int i = new Random().nextInt(size);
//            if (mLocation == i) {
//                if (i == size - 1) {
//                    i = 0;
//                } else if (mLocation == 0) {
//                    i = size - 1;
//                } else {
//                    i = i + 1;
//                }
//            }
//            mLocation = i;
//            return i;
//        }
//    }

    class PageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(final int index) {
            topScrollView.setAnim(index, new Runnable() {
                @Override
                public void run() {
                    if (index == 1) {
                        rankingFragment.onSelected();
                    } else if (index == 2) {
                        openningFragment.onSelected();
                    }
                }
            });
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
