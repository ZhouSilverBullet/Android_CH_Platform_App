package com.caohua.games.ui.vip;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.caohua.games.R;
import com.caohua.games.biz.vip.CHVipNotify;
import com.caohua.games.biz.vip.VipEntry;
import com.caohua.games.biz.vip.VipLogic;
import com.caohua.games.ui.vip.widget.VipActView;
import com.caohua.games.ui.vip.widget.VipCardView;
import com.caohua.games.ui.vip.widget.VipMySpecialView;
import com.caohua.games.ui.vip.widget.VipPlatformPowerView;
import com.caohua.games.ui.vip.widget.VipRechargeGiftView;
import com.caohua.games.ui.vip.widget.VipRewardView;
import com.caohua.games.ui.vip.widget.VipSpecialView;
import com.caohua.games.ui.vip.widget.VipViewPagerLayout;
import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.ui.widget.CHToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by admin on 2017/8/22.
 */

public class CHVipActivity extends CommonActivity {
    private VipRewardView rewardView;
    private VipActView actView;
    private VipRechargeGiftView rechargeGiftView;
    private VipMySpecialView mySpecialView;
    private VipCardView cardView;

    public static final String IS_AUTH = "1"; // 1表示已经认证 0 未认证
    private VipViewPagerLayout pagerLayout;
    private VipSpecialView specialView;
    private VipPlatformPowerView platformView;

    @Override
    protected String subTitle() {
        return "草花VIP";
    }

    @Override
    protected void initVariables() {
        super.initVariables();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected boolean hasLogin() {
        return true;
    }

    @Override
    protected int childViewId() {
        return R.layout.ch_activity_ch_vip;
    }

    @Override
    protected void loadData() {
        showLoadProgress(true);
        new VipLogic().getVipEntry(new BaseLogic.DataLogicListner<VipEntry>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                showLoadProgress(false);
                if (isFinishing()) {
                    return;
                }
                CHToast.show(activity, errorMsg);
                if (HttpConsts.ERROR_NO_NETWORK.equals(errorMsg)) {
                    showNoNetworkView(true);
                    return;
                }
                showEmptyView(true);
            }

            @Override
            public void success(VipEntry entry) {
                showLoadProgress(false);
                showEmptyView(false);
                showNoNetworkView(false);
                if (isFinishing()) {
                    return;
                }
                if (entry != null) {
                    VipEntry.DataBean data = entry.getData();
                    if (data != null) {
                        List<VipEntry.DataBean.ActBean> active = data.getActive();
                        List<VipEntry.DataBean.RechargeBean> coupon = data.getCoupon();
                        List<VipEntry.DataBean.RewardBean> gift = data.getGift();
                        VipEntry.DataBean.UserBean user = data.getUser();
                        List<VipEntry.DataBean.RebateBean> rebate = data.getRebate();
                        boolean isVip = false;
                        boolean isAuth = false;
                        if (user != null) {
                            String is_auth = user.getIs_auth();
                            isVip = handlerIsVip(user.getVip_level());
                            if (IS_AUTH.equals(is_auth)) {
                                cardView.setData(user);
                                pagerLayout.setVisibility(View.GONE);
                                mySpecialView.setVisibility(View.VISIBLE);
                                mySpecialView.setData(user.getPriv_flag());
                                isAuth = true;
                                pagerLayout.setVipAndAuth(isVip);
                            } else {
                                pagerLayout.setVisibility(View.VISIBLE);
                                pagerLayout.setVipAndAuth(isVip);
                            }
                        }
                        rewardView.setData(gift, isAuth, isVip);
                        actView.initData(active);
                        rechargeGiftView.setData(coupon, rebate, isAuth, isVip);
                        specialView.setVisibility(View.VISIBLE);
                        platformView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private boolean handlerIsVip(String value) {
        int i = 0;
        try {
            i = Integer.parseInt(value);
        } catch (Exception e) {
        }
        return i > 0;
    }

    @Subscribe
    public void notifyCHVip(CHVipNotify entry) {
        new VipLogic().getVipEntry(new BaseLogic.DataLogicListner<VipEntry>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                showLoadProgress(false);
                if (isFinishing()) {
                    return;
                }
                CHToast.show(activity, errorMsg);
                if (HttpConsts.ERROR_NO_NETWORK.equals(errorMsg)) {
                    showNoNetworkView(true);
                    return;
                }
                showEmptyView(true);
            }

            @Override
            public void success(VipEntry entry) {
                showLoadProgress(false);
                showEmptyView(false);
                showNoNetworkView(false);
                if (isFinishing()) {
                    return;
                }
                if (entry != null) {
                    VipEntry.DataBean data = entry.getData();
                    if (data != null) {
                        List<VipEntry.DataBean.RewardBean> gift = data.getGift();
                        VipEntry.DataBean.UserBean user = data.getUser();
                        boolean isVip = false;
                        boolean isAuth = false;
                        if (user != null) {
                            String is_auth = user.getIs_auth();
                            isVip = handlerIsVip(user.getVip_level());
                            if (IS_AUTH.equals(is_auth)) {
                                cardView.setData(user);
                                pagerLayout.setVisibility(View.GONE);
                                mySpecialView.setVisibility(View.VISIBLE);
                                mySpecialView.setData(user.getPriv_flag());
                                isAuth = true;
                                pagerLayout.setVipAndAuth(isVip);
                            } else {
                                pagerLayout.setVisibility(View.VISIBLE);
                                pagerLayout.setVipAndAuth(isVip);
                            }
                        }
                        rewardView.setData(gift, isAuth, isVip);
                        specialView.setVisibility(View.VISIBLE);
                        platformView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int forDialog = intent.getIntExtra("for_dialog", -1);
        if (forDialog == 1) {
            loadData();
        }
    }

    protected void initView() {
        pagerLayout = getView(R.id.ch_activity_vip_pager_layout);
        rewardView = getView(R.id.ch_activity_ch_vip_reward_view);
        actView = getView(R.id.ch_activity_ch_vip_act_view);
        rechargeGiftView = getView(R.id.ch_activity_ch_vip_recharge_gift_view);
        mySpecialView = getView(R.id.ch_vip_my_special_view);
        cardView = getView(R.id.ch_activity_vip_card_view);
        specialView = getView(R.id.ch_activity_ch_vip_special_view);
        platformView = getView(R.id.ch_activity_ch_vip_platform_view);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, CHVipActivity.class);
        context.startActivity(intent);
    }

    public static void startForDialog(Context context) {
        Intent intent = new Intent(context, CHVipActivity.class);
        intent.putExtra("for_dialog", 1);
        context.startActivity(intent);
    }
}
