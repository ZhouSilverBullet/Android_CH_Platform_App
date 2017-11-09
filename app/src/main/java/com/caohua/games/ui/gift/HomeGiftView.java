package com.caohua.games.ui.gift;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.caohua.games.biz.vip.VipAuthSubmitEntry;
import com.caohua.games.ui.giftcenter.GiftCenterActivity;
import com.caohua.games.ui.vip.VipCertificationDialog;
import com.chsdk.biz.app.AnalyticsHome;
import com.caohua.games.biz.gift.GiftEntry;
import com.caohua.games.ui.BaseHomeListView;
import com.chsdk.configure.DataStorage;
import com.chsdk.configure.SdkSession;
import com.chsdk.ui.WebActivity;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by CXK on 2016/10/18.
 */

public class HomeGiftView extends BaseHomeListView<GiftEntry, HomeGiftItemView> {

    public HomeGiftView(Context context) {
        super(context);
    }

    @Override
    protected int getMaxCount() {
        return 4;
    }

    @Override
    protected View getItemView() {
        return new HomeGiftItemView(getContext());
    }

    @Override
    protected String getTitle() {
        return "热门礼包";
    }

    public HomeGiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onMoreClick() {
        GiftCenterActivity.start(getContext(), GiftCenterActivity.class);
//        String giftMoreUrl = DataStorage.getGiftMoreUrl(SdkSession.getInstance().getAppContext());
//        String giftInterceptUrl = DataStorage.getGiftInterceptUrl(SdkSession.getInstance().getAppContext());
//        AnalyticsHome.umOnEvent(AnalyticsHome.HOME_HOT_GIFT_ANALYTICS,"礼包中心二级界面");
//        if (!TextUtils.isEmpty(giftMoreUrl) && !TextUtils.isEmpty(giftInterceptUrl)) {
//            WebActivity.startWebPage(getContext(), giftMoreUrl);
//        } else {
//            WebActivity.startWebPage(getContext(), "https://m.caohua.com/gift/index?fr=app");
//            DataStorage.saveGiftInterceptUrl(SdkSession.getInstance().getAppContext(),
//                    "https://m.caohua.com/gift/detail?id=");
//        }
    }

    public void unRegisterEvent() {
        SparseArray<HomeGiftItemView> cacheView = getCacheViews();
        if (cacheView == null)
            return;

        for (int i = 0; i < cacheView.size(); i++) {
            EventBus.getDefault().unregister(cacheView.get(i));
        }
    }
}
