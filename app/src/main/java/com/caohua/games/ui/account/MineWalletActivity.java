package com.caohua.games.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.ui.BaseActivity;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.biz.pay.WalletEntry;
import com.chsdk.biz.pay.WalletInfoLogic;
import com.chsdk.configure.DataStorage;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.RiffEffectImageButton;

/**
 * Created by zhouzhou on 2017/7/20.
 */

public class MineWalletActivity extends BaseActivity {
    private RiffEffectImageButton walletFaqButton;
    private WalletEntry walletEntry;
    private TextView payShowChb;
    private TextView payShowChd;
    private TextView payShowSilver;
    private TextView payShowCoupon;
    private View couponLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_mine_wallet);
        initVariables();
        initView();
        initData();
    }

    private void initData() {
        if (walletEntry != null) {
            payShowChb.setText(walletEntry.chb);
            payShowChd.setText(walletEntry.chd);
            payShowSilver.setText(walletEntry.silver);
            payShowCoupon.setText(walletEntry.coupon);
        } else {
            loadData(true);
        }
    }

    private void loadData(boolean isRefresh) {
        WalletInfoLogic logic = new WalletInfoLogic(WalletInfoLogic.TYPE_ALL, isRefresh, new BaseLogic.AppLogicListner() {
            @Override
            public void success(Object entryResult) {
                if (entryResult instanceof WalletEntry) {
                    walletEntry = (WalletEntry) entryResult;
                    payShowChb.setText(walletEntry.chb);
                    payShowChd.setText(walletEntry.chd);
                    payShowSilver.setText(walletEntry.silver);
                    payShowCoupon.setText(walletEntry.coupon);
                }
            }

            @Override
            public void failed(String errorMsg) {
            }
        });
        logic.getBalance();
    }

    private void initVariables() {
        Intent intent = getIntent();
        if (intent != null) {
            walletEntry = (WalletEntry) intent.getSerializableExtra("walletEntry");
        }
    }

    private void initView() {
        walletFaqButton = getView(R.id.ch_activity_mine_wallet_faq);
        walletFaqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipWalletFaq();
            }
        });
        payShowChb = getView(R.id.ch_activity_mine_wallet_text_ch_cb);
        payShowChd = getView(R.id.ch_activity_mine_wallet_text_ch_cd);
        payShowSilver = getView(R.id.ch_activity_mine_wallet_text_ch_by);
        payShowCoupon = getView(R.id.ch_activity_mine_wallet_text_ch_daijinjuan);

        getView(R.id.ch_activity_mine_wallet_by_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.startAppLink(MineWalletActivity.this, "https://app-sdk.caohua.com/ucenter/mysilver");
                AnalyticsHome.umOnEvent(AnalyticsHome.ACCOUNT_USER_CENTER_SILVER, "账户绑银二级界面");
            }
        });

        couponLayout = getView(R.id.ch_activity_mine_wallet_daijinjuan_layout);
        couponLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //代金卷入口
                WebActivity.startAppLink(MineWalletActivity.this, "https://app-sdk.caohua.com/coupon/myCoupon");
                AnalyticsHome.umOnEvent(AnalyticsHome.MINE_WALLET_COUPON, "点击我的优惠券次数");
            }
        });

        String couponActive = DataStorage.getCouponActive(this);
        if (TextUtils.isEmpty(couponActive) || "0".equals(couponActive)) {
            couponLayout.setVisibility(View.GONE);
        } else {
            couponLayout.setVisibility(View.VISIBLE);
        }
        getView(R.id.ch_mine_wallet_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void skipWalletFaq() {
        WebActivity.startAppLink(this, "https://app-sdk.caohua.com/ucenter/walletHelp");
    }
}
