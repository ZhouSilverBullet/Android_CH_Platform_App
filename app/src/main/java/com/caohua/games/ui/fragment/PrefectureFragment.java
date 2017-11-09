package com.caohua.games.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.TransmitDataInterface;
import com.caohua.games.biz.prefecture.GameCenterLogic;
import com.caohua.games.biz.prefecture.PrefectureEntry;
import com.caohua.games.biz.prefecture.PrefectureLogic;
import com.caohua.games.biz.prefecture.RoleLogic;
import com.caohua.games.ui.HomePagerActivity;
import com.caohua.games.ui.adapter.PrefecturePagerAdapter;
import com.caohua.games.ui.adapter.PrefectureRecyclerAdapter;
import com.caohua.games.ui.mymsg.MyMsgActivity;
import com.caohua.games.ui.prefecture.GameCenterActivity;
import com.caohua.games.ui.widget.CHTwoBallView;
import com.caohua.games.ui.widget.EmptyView;
import com.chsdk.api.CacheManager;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.configure.DataStorage;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.NetworkUtils;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;
import com.culiu.mhvp.core.MagicHeaderUtils;
import com.culiu.mhvp.core.MagicHeaderViewPager;
import com.culiu.mhvp.core.tabs.com.astuetz.PagerSlidingTabStrip;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhou on 2017/5/8.
 */

public class PrefectureFragment extends NormalFragment {
    private MagicHeaderViewPager mHeaderViewPager;
    private PrefecturePagerAdapter mPagerAdapter;
    private ViewGroup tabsArea;
    private ImageView editImage, msgImage;
    private View view;
    private ImageView coverImage;
    private TextView topUserName;
    private TextView topUserRegion;
    private ImageView topCenterGameIcon;
    private TextView topCenterGameName;
    private View nameRegionLayout;
    private EmptyView emptyView;
    private CHTwoBallView downloadProgress;
    private Context context;
    private RecyclerView recyclerView;
    private PrefectureRecyclerAdapter prefectureRecyclerAdapter;

    @Override
    protected void initChildView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setViewPagerAndIndicator();
        setImageEdit();
        setStickyColor();
        setPrefectureGridView();
        downloadRole();  //角色获取
        downloadData(0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Subscribe
    public void notifyRole(LoginUserInfo info) {
        downloadRole();
    }

    private void downloadRole() {
        if (!AppContext.getAppContext().isLogin()) {
//            nameRegionLayout.setVisibility(View.INVISIBLE);
            return;
        }
        RoleLogic roleLogic = new RoleLogic(getSubjectId() + "");
        roleLogic.role(new GameCenterLogic.AppGameCenterListener() {
            @Override
            public void failed(String errorMsg) {
//                nameRegionLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void success(Object entryResult, int currentPage) {
//                if (entryResult instanceof RoleEntry) {
//                    RoleEntry entry = (RoleEntry) entryResult;
//                    RoleEntry.DataBean.UserRoleBean user_role = entry.getData().getUser_role();
//                    String role_name = null;
//                    String server_name = null;
//                    if (user_role != null) {
//                        role_name = user_role.getRole_name();
//                        server_name = user_role.getServer_name();
//                    }
//                    if (!TextUtils.isEmpty(role_name) || !TextUtils.isEmpty(server_name)) {
//                        nameRegionLayout.setVisibility(View.GONE);
//                        topUserName.setText(role_name);
//                        topUserRegion.setText(server_name);
//                    } else {
//                        nameRegionLayout.setVisibility(View.INVISIBLE);
//                    }
//                } else {
//                    nameRegionLayout.setVisibility(View.INVISIBLE);
//                }
            }
        });
    }

    private void setImageEdit() {
        msgImage = findView(R.id.ch_prefecture_msg);
        editImage = findView(R.id.ch_prefecture_edit);
        editImage.setVisibility(View.GONE);
//        editImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (doLoginAndNotify())
//                    return;
//                SendEditActivity.lauch(context, "" + );
//            }
//        });
        msgImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (doLoginAndNotify())
//                    return;
                MyMsgActivity.start(context);
                AnalyticsHome.umOnEvent(AnalyticsHome.SUBJECT_MESSAGE, "专区中的专区消息二级界面");
            }
        });
        view = findView(R.id.ch_prefecture_change_game);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GameCenterActivity.class);
                startActivity(intent);
                AnalyticsHome.umOnEvent(AnalyticsHome.SUBJECT_CHANGE_GAME, "游戏中心二级界面");
            }
        });

        coverImage = findView(R.id.ch_prefecture_top_cover_image);
//        nameRegionLayout = findView(R.id.ch_prefecture_top_name_region);
//        topUserName = findView(R.id.ch_prefecture_top_user_name);
//        topUserRegion = findView(R.id.ch_prefecture_top_user_region);
        topCenterGameIcon = findView(R.id.ch_prefecture_top_center_game_icon);
        topCenterGameName = findView(R.id.ch_prefecture_top_center_game_name);
        int width = ((HomePagerActivity) context).getWindowManager().getDefaultDisplay().getWidth();
        coverImage.setLayoutParams(new RelativeLayout.LayoutParams(width, (int) (width * 0.575)));

        emptyView = findView(R.id.ch_prefecture_empty_view);
        downloadProgress = findView(R.id.ch_prefecture_download_progress_layout);
    }

    private boolean doLoginAndNotify() {
        if (!AppContext.getAppContext().isLogin()) {
            AppContext.getAppContext().login((HomePagerActivity) context, new TransmitDataInterface() {
                @Override
                public void transmit(Object o) {
                    if (o != null) {
                        downloadRole();
                    }
                }
            });
            return true;
        }
        return false;
    }

    private void setStickyColor() {

    }

    private int getSubjectId() {
        return DataStorage.getSubjectGameId(context);
    }

    @Override
    protected void loadData() {
        downloadProgress.setVisibility(View.VISIBLE);
        downloadData(0);
    }

    private void downloadData(int page) {
        if (page == 0) {
            PrefectureEntry prefectureEntry = (PrefectureEntry) CacheManager.readObject(context, "PrefectureEntry");
            if (prefectureEntry != null) {
                doFitDataInPager(prefectureEntry);
            }
        }
        PrefectureLogic prefectureLogic = new PrefectureLogic(context, getSubjectId());
        prefectureLogic.getPrefectureData(new GameCenterLogic.AppGameCenterListener() {
            @Override
            public void failed(String errorMsg) {
                CHToast.show(context, errorMsg);
                downloadProgress.setVisibility(View.GONE);
                if (!AppContext.getAppContext().isNetworkConnected() && mPagerAdapter == null) {
                    showNoNetworkView(true);
                    return;
                }
                if (emptyView != null && mPagerAdapter == null) {
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            emptyView.setVisibility(View.GONE);
                            downloadProgress.setVisibility(View.VISIBLE);
                            downloadData(0);
                        }
                    });
                }
            }

            @Override
            public void success(Object entryResult, int page) {
                showNoNetworkView(false);
                downloadProgress.setVisibility(View.GONE);
                if (entryResult instanceof PrefectureEntry) {
                    doFitDataInPager((PrefectureEntry) entryResult);
                }
            }
        });
    }

    private void doFitDataInPager(PrefectureEntry entryResult) {
        PrefectureEntry topEntry = entryResult;
        PrefectureEntry.DataBean data = topEntry.getData();
        List<PrefectureEntry.DataBean.TabBannerBean> tab_banner = data.getTab_banner();
        List<PrefectureEntry.DataBean.MainBannerBean> main_banner = data.getMain_banner();
//        setGridViewColumns(main_banner.size());
        prefectureRecyclerAdapter.addAll(main_banner);
//        prefectureGridAdapter.addAll(main_banner);

        PrefectureEntry.DataBean.ModuleBean module = data.getModule();
        if (!TextUtils.isEmpty(module.getCover())) {
            PicUtil.displayImg(context, coverImage, module.getCover(), R.drawable.ch_prefecture_top_img);
        } else {
            coverImage.setImageResource(R.drawable.ch_prefecture_top_img);
        }
        if (!TextUtils.isEmpty(module.getGame_icon())) {
            PicUtil.displayImg(context, topCenterGameIcon, module.getGame_icon(), R.drawable.ch_default_apk_icon);
        }
        topCenterGameName.setText(module.getGame_name());
        if (tab_banner.size() > 0) {
            mPagerAdapter = new PrefecturePagerAdapter(getChildFragmentManager(), data, mHeaderViewPager.getViewPager());
            mHeaderViewPager.setPagerAdapter(mPagerAdapter);
        }
        LogUtil.errorLog("mHeaderViewPager :" + mHeaderViewPager.getViewPager());
    }

    private void setViewPagerAndIndicator() {
        if (isVersionMoreKitkat()) {
            mRoot.setPadding(0, ViewUtil.getStatusHeight(AppContext.getAppContext()) + ViewUtil.dp2px(AppContext.getAppContext(), 50), 0, 0);
        } else {
            mRoot.setPadding(0, ViewUtil.dp2px(AppContext.getAppContext(), 50), 0, 0);
        }
        mHeaderViewPager = new MagicHeaderViewPager(context) {
            @Override
            protected void initTabsArea(LinearLayout container) {
                //You can customize your tabStrip or stable area here
                tabsArea = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.ch_layout_tabs1, null);
                // TODO: Set height of stable area manually, then it can be calculated.
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        MagicHeaderUtils.dp2px(context, 45));
                container.addView(tabsArea, lp);
                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.ch_gray));
                view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewUtil.dp2px(context, 1)));
                container.addView(view);
                // some style
                PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) tabsArea.findViewById(R.id.tabs);
                pagerSlidingTabStrip.setTabPaddingLeftRight(ViewUtil.dp2px(context, 16));
                pagerSlidingTabStrip.setTextColor(Color.BLACK);
                pagerSlidingTabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
                pagerSlidingTabStrip.setTypeface(null, Typeface.NORMAL);
                pagerSlidingTabStrip.setIndicatorHeight(ViewUtil.dp2px(context, 2));
                pagerSlidingTabStrip.setBackgroundColor(Color.WHITE);
                // TODO: These two methods must be called to let magicHeaderViewPager know who is stable area and tabs.
                setTabsArea(tabsArea);
                setPagerSlidingTabStrip(pagerSlidingTabStrip);
            }
        };

        LinearLayout mhvpParent = (LinearLayout) findView(R.id.ch_prefecture_ll);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        mhvpParent.addView(mHeaderViewPager, lp);
        final View inflate = LayoutInflater.from(context).inflate(R.layout.ch_prefecture_top_layout, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mHeaderViewPager.addHeaderView(inflate, layoutParams);
        mHeaderViewPager.setOnHeaderScrollListener(new MagicHeaderViewPager.OnHeaderScrollListener() {
            @Override
            public void onHeaderScroll(int headerTransitionY) {

            }
        });
    }

    private void setPrefectureGridView() {
        recyclerView = findView(R.id.ch_prefecture_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        prefectureRecyclerAdapter = new PrefectureRecyclerAdapter(context, new ArrayList<PrefectureEntry.DataBean.MainBannerBean>());
        recyclerView.setAdapter(prefectureRecyclerAdapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ch_fragment_prefecture;
    }


    public void doChangeData(int subjectId) {
        FactoryFragment.map.clear();
        if (NetworkUtils.isNetworkConnected(context)) {
            DataStorage.saveSubjectGameId(context, subjectId);
        }
        downloadData(0);
    }
}
