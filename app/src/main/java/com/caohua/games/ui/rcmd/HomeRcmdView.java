package com.caohua.games.ui.rcmd;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.caohua.games.ui.gift.HomeGiftItemView;
import com.chsdk.biz.app.AnalyticsHome;
import com.caohua.games.biz.rcmd.DailyRcmdEntry;
import com.caohua.games.ui.BaseHomeListView;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by CXK on 2016/10/18.
 */

public class HomeRcmdView extends BaseHomeListView<DailyRcmdEntry, HomeRcmdItemView> {
    public HomeRcmdView(Context context) {
        super(context);
    }

    public HomeRcmdView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getMaxCount() {
        return 1;
    }

    @Override
    protected View getItemView() {
        return new HomeRcmdItemView(getContext());
    }

    @Override
    protected String getTitle() {
        return "每日推荐";
    }

    protected void onMoreClick() {
        AnalyticsHome.umOnEvent(AnalyticsHome.HOME_DAY_RECOMMEND_ANALYTICS,"每日推荐二级界面");
        DailyRcmdActivity.start(getContext(), getDataList());
    }

    public void onDestroy() {
        SparseArray<HomeRcmdItemView> cacheView = getCacheViews();
        if (cacheView == null)
            return;

        for (int i = 0; i < cacheView.size(); i++) {
            cacheView.get(i).onDestory();
        }
    }
}
