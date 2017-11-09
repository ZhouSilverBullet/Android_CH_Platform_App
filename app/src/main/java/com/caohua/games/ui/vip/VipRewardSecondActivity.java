package com.caohua.games.ui.vip;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.caohua.games.R;
import com.caohua.games.biz.vip.VipRewardGameEntry;
import com.caohua.games.biz.vip.VipRewardGameLogic;
import com.caohua.games.biz.vip.VipRewardSecondNotify;
import com.caohua.games.ui.vip.more.VipGameListView;
import com.caohua.games.ui.vip.more.VipRebateRewardListView;
import com.caohua.games.ui.vip.more.VipRewardListView;
import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.ui.widget.CHToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by admin on 2017/8/26.
 */

public class VipRewardSecondActivity extends CommonActivity {
    private VipRewardListView rewardList;
    private VipGameListView gameList;
    private boolean isAuth;
    private boolean isVip;
    private VipRebateRewardListView rebateList;

    @Override
    protected String subTitle() {
        return "VIP福利";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected int childViewId() {
        return R.layout.ch_activity_vip_reward_second;
    }

    protected void loadData() {
        showLoadProgress(true);
        new VipRewardGameLogic().getRewardGame(new BaseLogic.DataLogicListner<VipRewardGameEntry>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                if (isFinishing()) {
                    return;
                }
                showLoadProgress(false);
                CHToast.show(activity, errorMsg);
                if (HttpConsts.ERROR_NO_NETWORK.equals(errorMsg)) {
                    showNoNetworkView(true);
                    return;
                }
                showEmptyView(true);
            }

            @Override
            public void success(VipRewardGameEntry entry) {
                if (isFinishing()) {
                    return;
                }
                showNoNetworkView(false);
                if (entry != null && entry.getData() != null) {
                    List<VipRewardGameEntry.DataBean.PrivBean> priv = entry.getData().getPriv();
                    List<VipRewardGameEntry.DataBean.GameBean> game = entry.getData().getGame();
                    List<VipRewardGameEntry.DataBean.RebateBean> rebate = entry.getData().getRebate();
                    rewardList.setData(priv, isAuth, isVip);
                    gameList.setData(game);
                    rebateList.setData(rebate, isAuth, isVip);
                }
                showLoadProgress(false);
                showEmptyView(false);
            }
        });
    }

    @Subscribe
    public void notifyReward(VipRewardSecondNotify notify) {
        showLoadProgress(true);
        new VipRewardGameLogic().getRewardGame(new BaseLogic.DataLogicListner<VipRewardGameEntry>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                if (isFinishing()) {
                    return;
                }
                showLoadProgress(false);
                showEmptyView(true);
                CHToast.show(activity, errorMsg);
            }

            @Override
            public void success(VipRewardGameEntry entry) {
                if (isFinishing()) {
                    return;
                }
                if (entry != null && entry.getData() != null) {
                    List<VipRewardGameEntry.DataBean.PrivBean> priv = entry.getData().getPriv();
                    rewardList.setData(priv, isAuth, isVip);
                }
                showLoadProgress(false);
                showEmptyView(false);
            }
        });
    }

    @Override
    protected void initVariables() {
        Intent intent = getIntent();
        if (intent != null) {
            isAuth = intent.getBooleanExtra(IS_AUTH, false);
            isVip = intent.getBooleanExtra(IS_VIP, false);
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    protected void initView() {
        rewardList = getView(R.id.ch_activity_ch_vip_reward_reward_list);
        gameList = getView(R.id.ch_activity_ch_vip_reward_game_list);
        rebateList = getView(R.id.ch_activity_ch_vip_reward_rebate_list);
    }

    public static void start(Context context, boolean isAuth, boolean isVip) {
        Intent intent = new Intent(context, VipRewardSecondActivity.class);
        intent.putExtra(IS_AUTH, isAuth);
        intent.putExtra(IS_VIP, isVip);
        context.startActivity(intent);
    }
}
