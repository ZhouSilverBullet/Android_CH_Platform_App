package com.caohua.games.ui.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.TransmitDataInterface;
import com.caohua.games.biz.home.DataMgr;
import com.caohua.games.biz.hot.HotEntry;
import com.caohua.games.biz.task.TaskLogic;
import com.caohua.games.biz.task.TaskNotifyDotEntry;
import com.caohua.games.ui.HomePagerActivity;
import com.caohua.games.ui.bbs.ForumListActivity;
import com.caohua.games.ui.coupon.CouponCenterActivity;
import com.caohua.games.ui.dataopen.DataOpenActivity;
import com.caohua.games.ui.dataopen.GameLabActivity;
import com.caohua.games.ui.find.FindContentActivity;
import com.caohua.games.ui.find.FindItemView;
import com.caohua.games.ui.giftcenter.GiftCenterActivity;
import com.caohua.games.ui.hot.HotActivity;
import com.caohua.games.ui.ranking.RankingDetailActivity;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.configure.DataStorage;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.app.LinkModel;
import com.chsdk.ui.WebActivity;
import com.chsdk.utils.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * Created by zhouzhou on 2017/5/12.
 */

public class FindFragment extends NormalFragment implements View.OnClickListener {
    private FindItemView shop;
    private FindItemView task;
    private Intent intent;
    private FindItemView hotAct;
    private FindItemView gift;
    private FindItemView rank;
    private View forum;
    private FindItemView coupon;
    private FindItemView gameDataOpen;
    private View act;

    @Override
    protected void initChildView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        hotAct = findView(R.id.ch_find_item_hot_act);
        shop = findView(R.id.ch_find_item_shop);
        task = findView(R.id.ch_find_item_task);
        gift = findView(R.id.ch_find_item_gift);
        rank = findView(R.id.ch_find_item_game_rank);
        forum = findView(R.id.ch_find_item_forum);
        coupon = findView(R.id.ch_find_item_coupon);
        gameDataOpen = findView(R.id.ch_find_item_game_data_open);
        act = findView(R.id.ch_find_item_game_act);
        act.setOnClickListener(this);
        shop.setOnClickListener(this);
        task.setOnClickListener(this);
        hotAct.setOnClickListener(this);
        gift.setOnClickListener(this);
        rank.setOnClickListener(this);
        forum.setOnClickListener(this);
        coupon.setOnClickListener(this);
        gameDataOpen.setOnClickListener(this);
        String couponActive = DataStorage.getCouponActive(getActivity());
        if (TextUtils.isEmpty(couponActive) || "0".equals(couponActive)) {
            coupon.setVisibility(View.GONE);
        } else {
            coupon.setVisibility(View.VISIBLE);
        }
        if (isVersionMoreKitkat()) {
            mRoot.setPadding(0, ViewUtil.getStatusHeight(AppContext.getAppContext()) + ViewUtil.dp2px(AppContext.getAppContext(), 50), 0, 0);
        } else {
            mRoot.setPadding(0, ViewUtil.dp2px(AppContext.getAppContext(), 50), 0, 0);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ch_fragment_find;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ch_find_item_hot_act:  //热门活动
                HotActivity.start(getActivity(), new ArrayList<HotEntry>());
                umOnEvent(AnalyticsHome.FIND_HOT, "热门活动");
                break;
            case R.id.ch_find_item_gift:
                GiftCenterActivity.start(getActivity(), GiftCenterActivity.class);
                umOnEvent(AnalyticsHome.FIND_GIFT, "热门礼包");
                break;
            case R.id.ch_find_item_shop:  //商城
                intent = new Intent(getActivity(), FindContentActivity.class);
                intent.putExtra(FindContentActivity.KEY, FindContentActivity.KEY_SHOP);
                startActivity(intent);
                umOnEvent(AnalyticsHome.FIND_SHOP, "商城");
                break;
            case R.id.ch_find_item_task: //任务
                intent = new Intent(getActivity(), FindContentActivity.class);
                intent.putExtra(FindContentActivity.KEY, FindContentActivity.KEY_TASK);
                startActivity(intent);
                umOnEvent(AnalyticsHome.FIND_TASK, "任务");
                break;
            case R.id.ch_find_item_game_rank:
                RankingDetailActivity.start(getActivity(), DataMgr.DATA_TYPE_RANKING_SYNTH);
                umOnEvent(AnalyticsHome.FIND_RANKING, "游戏排行");
                break;
            case R.id.ch_find_item_forum:  //论坛首页
                ForumListActivity.start(getActivity());
                break;
            case R.id.ch_find_item_coupon: //跳转领卷中心
                intent = new Intent(getActivity(), CouponCenterActivity.class);
                startActivity(intent);
                umOnEvent(AnalyticsHome.FIND_COUPON_CENTER, "发现点击领取那中心");
                break;
            case R.id.ch_find_item_game_data_open:
                GameLabActivity.start(getActivity(), GameLabActivity.class);
                break;
            case R.id.ch_find_item_game_act:
                String url = BaseLogic.HOST_APP + "activity/daily";
                WebActivity.startWebPageGetParam(getActivity(), url, null);
                break;
        }
    }

    private void umOnEvent(String id, String content) {
        AnalyticsHome.umOnEvent(id, content);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && AppContext.getAppContext().isLogin()) {
            taskLogic(true);
        } else if (isVisibleToUser) {
            if (task != null) {
                task.setText(0);
            }
            TaskNotifyDotEntry event = new TaskNotifyDotEntry();
            event.setStatus(View.INVISIBLE);
            EventBus.getDefault().post(event);
        }
    }

    private void taskLogic(boolean refresh) {
        if (!getUserVisibleHint()) {
            return;
        }

        new TaskLogic(refresh).getTaskEntry(new BaseLogic.AppLogicListner() {
            @Override
            public void failed(String errorMsg) {

            }

            @Override
            public void success(Object entryResult) {

            }
        });
    }

    @Subscribe
    public void visibilityDotFind(TaskNotifyDotEntry entry) {
        if (entry != null) {
            int i = entry.getStatus() == View.VISIBLE ? 1 : 0;
            if (task != null) {
                task.setText(i);
            }
        }
    }
}
