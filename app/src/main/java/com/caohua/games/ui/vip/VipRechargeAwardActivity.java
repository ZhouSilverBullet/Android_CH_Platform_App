package com.caohua.games.ui.vip;

import android.content.Context;
import android.content.Intent;

import com.caohua.games.R;
import com.caohua.games.biz.vip.VipRechargeAwardEntry;
import com.caohua.games.biz.vip.VipRechargeAwardLogic;
import com.caohua.games.ui.vip.more.VipCouponListView;
import com.caohua.games.ui.vip.more.VipRebateGiftListView;
import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.ui.widget.CHToast;

import java.util.List;

/**
 * Created by admin on 2017/8/25.
 */

public class VipRechargeAwardActivity extends CommonActivity {
    private VipRebateGiftListView recharge;
    private VipCouponListView coupon;
    private boolean isAuth;
    private boolean isVip;

    @Override
    protected String subTitle() {
        return "充值优惠";
    }

    @Override
    protected int childViewId() {
        return R.layout.ch_activity_vip_recharge_award;
    }

    protected void loadData() {
        showLoadProgress(true);
        VipRechargeAwardLogic logic = new VipRechargeAwardLogic();
        logic.getRechargeAward(new BaseLogic.DataLogicListner<VipRechargeAwardEntry>() {
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
            public void success(VipRechargeAwardEntry entry) {
                if (isFinishing()) {
                    return;
                }
                showNoNetworkView(false);
                showLoadProgress(false);
                showEmptyView(false);
                if (entry != null && entry.getData() != null) {
                    List<VipRechargeAwardEntry.DataBean.CouponBean> couponList = entry.getData().getCoupon();
                    List<VipRechargeAwardEntry.DataBean.RebateBean> rebateList = entry.getData().getRebate();
//                    if (data.size() == 0) {
//                        showEmptyView(true);
//                    }
                    coupon.setData(couponList, isAuth, isVip);
                    recharge.setData(rebateList, isAuth, isVip);
                }
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
    }

    protected void initView() {
        recharge = getView(R.id.ch_vip_recharge_gift_list_view);
        coupon = getView(R.id.ch_vip_coupon_list_view);
    }

    public static void start(Context context, boolean isAuth, boolean isVip) {
        Intent intent = new Intent(context, VipRechargeAwardActivity.class);
        intent.putExtra(IS_AUTH, isAuth);
        intent.putExtra(IS_VIP, isVip);
        context.startActivity(intent);
    }
}
