package com.caohua.games.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.TransmitDataInterface;
import com.caohua.games.biz.minegame.MineDotEntry;
import com.caohua.games.biz.search.HotGameEntry;
import com.caohua.games.biz.task.SkipNavigationEntry;
import com.caohua.games.biz.task.TaskForHomeNotify;
import com.caohua.games.biz.task.TaskNotifyDotEntry;
import com.caohua.games.ui.adapter.FragmentAdapter;
import com.caohua.games.ui.fragment.FindFragment;
import com.caohua.games.ui.fragment.HomeFragment;
import com.caohua.games.ui.fragment.MineFragment;
import com.caohua.games.ui.fragment.PrefectureFragment;
import com.caohua.games.ui.search.HomeSearchTitle;
import com.caohua.games.ui.widget.HomeBottomTabLayout;
import com.caohua.games.ui.widget.NoScrollViewPager;
import com.caohua.games.views.main.WelcomeActivity;
import com.chsdk.api.AppOperator;
import com.chsdk.api.CHSdk;
import com.chsdk.api.ExitCallBack;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.biz.game.PushLogic;
import com.chsdk.biz.game.TokenRefreshHelper;
import com.chsdk.configure.DataStorage;
import com.chsdk.configure.SdkSession;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.DeviceUtil;
import com.chsdk.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by zhouzhou on 2017/2/18.
 */

public class HomePagerActivity extends BaseActivity implements View.OnClickListener {

    private NoScrollViewPager noScrollPager;
    private HomeBottomTabLayout buttonTab1;
    private HomeBottomTabLayout buttonTab2;
    private HomeBottomTabLayout buttonTab3;
    private HomeBottomTabLayout buttonTab4;
    private List<Fragment> fragmentList;
    private FragmentAdapter fragmentAdapter;
    private HomeFragment homeFragment;
    private PrefectureFragment prefectureFragment;
    //    private TaskFragment taskFragment;
    private FindFragment findFragment;
    //    private StoreFragment storeFragment;
    private MineFragment mineFragment;
    private boolean init;
    private Bundle savedInstanceState;
    private int selectorViewPagerItem; //是否是当前页面
    private boolean isLoginRefresh;
    boolean isStoreNotify = true;
    boolean isTaskNotify = true;
    private int skipValue;
    private HomeSearchTitle homeSearchTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_home_pager);
        if (savedInstanceState != null) {
            prefectureFragment = getFragment(savedInstanceState, PrefectureFragment.class);
            homeFragment = getFragment(savedInstanceState, HomeFragment.class);
//            taskFragment = getFragment(savedInstanceState, TaskFragment.class);
            findFragment = getFragment(savedInstanceState, FindFragment.class);
//            storeFragment = getFragment(savedInstanceState, StoreFragment.class);
            mineFragment = getFragment(savedInstanceState, MineFragment.class);
            LogUtil.errorLog("HomePagerActivity reonCreate,recommendPageFragment:"
                    /*+ homeFragment*/);
        }
        init = false;
        AppContext.getAppContext().setRun(true);
        this.savedInstanceState = savedInstanceState;
        initVariables();
        initView();
        initEvent();
        initData();
        String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE};
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(this, "", PERMISSION_FOR_PHONE_STATE, permissions);
        }
    }

    private void initVariables() {
        Intent intent = getIntent();
        if (intent != null) {
            skipValue = intent.getIntExtra(WelcomeActivity.WELCOME_OPEN_HOME, -1);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (!init) {
            init = true;
            handleLoginStatus(savedInstanceState);
        }
        if (homeSearchTitle != null) {
            if (!homeSearchTitle.isStart) {
                homeSearchTitle.setRcmdGame();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (homeSearchTitle != null) {
            homeSearchTitle.removeAutoTextRunnable();
            homeSearchTitle.isStart = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (homeSearchTitle != null) {
            homeSearchTitle.removeAutoTextRunnable();
        }
        AppContext.getAppContext().setRun(false);
        LogUtil.errorLog("HomePagerActivity setRun: " + AppContext.getAppContext().isRun());
    }

    private void handleLoginStatus(Bundle savedInstanceState) {
        if (CHSdk.updateApp(HomePagerActivity.this)) {
            return;
        }
        if (savedInstanceState == null) {
            if (SdkSession.getInstance().showLoginDialog) {
                AppContext.getAppContext().login(this, new TransmitDataInterface() {
                    @Override
                    public void transmit(Object o) {
                        if (o != null) {
//                            setAccountImg();
                        }
                    }
                });
            } else {
                if (CHSdk.accountInvalid) {
                    CHSdk.accountInvalid = false;
                    CHToast.show(this, "验证账号信息失败,请重新登录", Toast.LENGTH_LONG);
                    AppContext.getAppContext().login(this, new TransmitDataInterface() {
                        @Override
                        public void transmit(Object o) {
                            if (o != null) {
//                                setAccountImg();
                                if (!CHSdk.updateApp(HomePagerActivity.this)) {
                                    getPushMsg();
                                }
                            }
                        }
                    });
                } else {
                    if (AppContext.getAppContext().isLogin()) {
                        TokenRefreshHelper.registerAlarm(getApplicationContext());
                    }

                    if (!CHSdk.updateApp(this)) {
                        getPushMsg();
                    }
                }
            }
        }
    }

    private void getPushMsg() {
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                final boolean login = AppContext.getAppContext().isLogin();
                final String pushIds = getPushIds(login);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PushLogic logic = new PushLogic(HomePagerActivity.this);
                        logic.getMsg(pushIds, login);
                    }
                });
            }
        });
    }

    private String getPushIds(boolean login) {
        final StringBuilder sb = new StringBuilder();
        if (login) {
            Map<String, List<String>> list = DataStorage.getPushList(HomePagerActivity.this, DeviceUtil.getUnixTimestamp());
            if (list != null && list.size() >= 0) {
                List<String> data = list.get(SdkSession.getInstance().getUserName());
                if (data != null && data.size() > 0) {
                    for (String id : data) {
                        if (sb.length() > 0) {
                            sb.append(",");
                        }
                        sb.append(id);
                    }
                }
            }
        } else {
            List<String> lists = DataStorage.getNotUserPushList(HomePagerActivity.this, DeviceUtil.getUnixTimestamp());
            if (lists != null && lists.size() > 0) {
                for (String id : lists) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(id);
                }
            }
        }
        return sb.toString();
    }

    @Override
    public void onBackPressed() {
        CHSdk.handleBackAction(this, new ExitCallBack() {
            @Override
            public void exit() {
                finish();
            }
        });
    }

    private void initView() {
        noScrollPager = getView(R.id.home_pager_view_pager);
        noScrollPager.setCurrentItem(0);
        buttonTab1 = getView(R.id.ch_home_pager_tab1);
        buttonTab1.setTabStatus(R.color.ch_green, R.drawable.ch_press_tab1);
        buttonTab2 = getView(R.id.ch_home_pager_tab2);
        buttonTab2.setTabStatus(R.color.ch_black, R.drawable.ch_normal_tab2);
        buttonTab3 = getView(R.id.ch_home_pager_tab3);
        buttonTab3.setTabStatus(R.color.ch_black, R.drawable.ch_normal_tab3);
        buttonTab4 = getView(R.id.ch_home_pager_tab4);
        buttonTab4.setTabStatus(R.color.ch_black, R.drawable.ch_normal_tab4);
        homeSearchTitle = getView(R.id.ch_home_search_title);
    }

    private void initEvent() {
        buttonTab1.setOnClickListener(this);
        buttonTab2.setOnClickListener(this);
        buttonTab3.setOnClickListener(this);
        buttonTab4.setOnClickListener(this);
        noScrollPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                resetTab();
                switch (position) {
                    case 0:
                        buttonTab1.setTabStatus(R.color.ch_green, R.drawable.ch_press_tab1);
                        break;
                    case 1:
                        buttonTab2.setTabStatus(R.color.ch_green, R.drawable.ch_press_tab2);
                        break;
                    case 2:
                        buttonTab3.setTabStatus(R.color.ch_green, R.drawable.ch_press_tab3);
                        break;
                    case 3:
                        buttonTab4.setTabStatus(R.color.ch_green, R.drawable.ch_press_tab4);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData() {
        if (fragmentList == null) {
            fragmentList = new ArrayList<>();
        }
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }

        if (prefectureFragment == null) {
            prefectureFragment = new PrefectureFragment();
        }
//
//        if (taskFragment == null) {
//            taskFragment = new TaskFragment();
//        }

        if (findFragment == null) {
            findFragment = new FindFragment();
        }
//        if (storeFragment == null) {
//            storeFragment = new StoreFragment();
//        }
        if (mineFragment == null) {
            mineFragment = new MineFragment();
        }

        fragmentList.add(prefectureFragment);
        fragmentList.add(homeFragment);
//        fragmentList.add(taskFragment);
        fragmentList.add(findFragment);
//        fragmentList.add(storeFragment);
        fragmentList.add(mineFragment);

        fragmentAdapter = new FragmentAdapter(this, getSupportFragmentManager(), fragmentList);
        noScrollPager.setCanScroll(false);
        noScrollPager.setOffscreenPageLimit(3);
        noScrollPager.setAdapter(fragmentAdapter);
        if (skipValue != -1) {
            noScrollPager.setCurrentItem(skipValue);
        }
    }

    @Override
    public void onClick(View v) {
        resetTab();
        switch (v.getId()) {
            case R.id.ch_home_pager_tab1:  //专区
                taskNotifyMethod();
                AnalyticsHome.umOnEvent(AnalyticsHome.HOME_FRAGMENT, "专区tab");
                break;
            case R.id.ch_home_pager_tab2:  //广场
                AnalyticsHome.umOnEvent(AnalyticsHome.TASK_FRAGMENT, "广场tab");
                homeNotifyMethod();
                break;
            case R.id.ch_home_pager_tab3:  //发现
                AnalyticsHome.umOnEvent(AnalyticsHome.STORE_FRAGMENT, "发现tab");
                storeNotifyMethod();
                break;
            case R.id.ch_home_pager_tab4:  //我的
                AnalyticsHome.umOnEvent(AnalyticsHome.MINE_FRAGMENT, "我的tab");
                mineNotifyMethod();
                break;
        }
    }

    private void mineNotifyMethod() {
        isStoreNotify = true;
        isTaskNotify = true;
        if (mineFragment != null) {
            mineFragment.tipsLogic(false);
        }
        noScrollPager.setCurrentItem(3, false);
        buttonTab4.setTabStatus(R.color.ch_green, R.drawable.ch_press_tab4);
        selectorViewPagerItem = 3;
    }

    private void taskNotifyMethod() {
        isStoreNotify = true;
//        taskFragment.setLazyRefresh(true);
        if (isTaskNotify) {
            EventBus.getDefault().post(new TaskForHomeNotify());
            isTaskNotify = false;
        }
        noScrollPager.setCurrentItem(0, false);
        buttonTab1.setTabStatus(R.color.ch_green, R.drawable.ch_press_tab1);
        selectorViewPagerItem = 1;
    }

    private void homeNotifyMethod() {
        isStoreNotify = true;
        isTaskNotify = true;
        noScrollPager.setCurrentItem(1, false);
//        if (homeFragment != null) {
//            homeFragment.setRcmdGame();
//        }
        buttonTab2.setTabStatus(R.color.ch_green, R.drawable.ch_press_tab2);
        selectorViewPagerItem = 0;
    }

    private void resetTab() {
        buttonTab1.setTabStatus(R.color.ch_black, R.drawable.ch_normal_tab1);
        buttonTab2.setTabStatus(R.color.ch_black, R.drawable.ch_normal_tab2);
        buttonTab3.setTabStatus(R.color.ch_black, R.drawable.ch_normal_tab3);
        buttonTab4.setTabStatus(R.color.ch_black, R.drawable.ch_normal_tab4);
    }

    @Subscribe
    public void skipShop(SkipNavigationEntry entry) {
        if (entry != null && noScrollPager != null) {
            switch (entry.getTabValue()) {
                case 0:
                    homeNotifyMethod();
                    break;
                case 1:
                    taskNotifyMethod();
                    break;
                case 2:
                    storeNotifyMethod();
                    break;
                case 3:
                    mineNotifyMethod();
                    break;
            }
        }
    }

    private void storeNotifyMethod() {
        isTaskNotify = true;
        noScrollPager.setCurrentItem(2, false);
//        if (isStoreNotify && storeFragment != null && !storeFragment.getLazyRefresh()) {
//            storeFragment.setLazyRefresh(true);
//            fragmentAdapter.notifyDataSetChanged();
//            isStoreNotify = false;
//        }
//        if (isStoreNotify && selectorViewPagerItem != 2 && isLoginRefresh) {
//            fragmentAdapter.notifyDataSetChanged();
//            isLoginRefresh = false;
//            isStoreNotify = false;
//        }
//        if (isStoreNotify && AppContext.getAppContext().isLogin()) {
//            fragmentAdapter.notifyDataSetChanged();
//        }
        buttonTab3.setTabStatus(R.color.ch_green, R.drawable.ch_press_tab3);
        selectorViewPagerItem = 2;
    }

    @Subscribe
    public void visibilityMineDot(MineDotEntry entry) {
        if (entry != null && buttonTab4 != null) {
            buttonTab4.setTextDotVisibility(entry.getStatus());
        }
    }

    @Subscribe
    public void visibilityDot(TaskNotifyDotEntry entry) {
        if (entry != null && buttonTab3 != null) {
            buttonTab3.setTextDotVisibility(entry.getStatus());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int subjectId = intent.getIntExtra("subjectId", -1);
        if (subjectId == DataStorage.getSubjectGameId(this)) {
            noScrollPager.setCurrentItem(0, false);
            return;
        }
        if (subjectId != -1) {
            noScrollPager.setCurrentItem(0, false);
            prefectureFragment.doChangeData(subjectId);
        }

        skipValue = intent.getIntExtra(WelcomeActivity.WELCOME_OPEN_HOME, -1);
        if (skipValue != -1) {
            noScrollPager.setCurrentItem(skipValue);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        LogUtil.errorLog("HomePagerActivity onSaveInstanceState");
        saveFragment(outState, prefectureFragment);
        saveFragment(outState, homeFragment);
//        saveFragment(outState, taskFragment);
        saveFragment(outState, findFragment);
//        saveFragment(outState, storeFragment);
        saveFragment(outState, mineFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void refreshChildLogout() {
        doLogin();
    }

    public static void start(Context context, int subject_id) {
        Intent intent = new Intent(context, HomePagerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("subjectId", subject_id);
        context.startActivity(intent);
    }


    private void doLogin() {
        AppContext.getAppContext().login(this, new TransmitDataInterface() {
            @Override
            public void transmit(Object o) {
                if (o instanceof LoginUserInfo) {
                }
            }
        });
    }

    public void setRcmdGame(List<HotGameEntry> entryList) {
        homeSearchTitle.setData(entryList);
        if (!homeSearchTitle.isStart) {
            homeSearchTitle.setRcmdGame();
        }
    }
}
