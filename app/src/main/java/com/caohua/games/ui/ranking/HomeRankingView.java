package com.caohua.games.ui.ranking;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.chsdk.biz.app.AnalyticsHome;
import com.caohua.games.biz.home.DataMgr;
import com.caohua.games.biz.ranking.RankingEntry;
import com.caohua.games.ui.BaseHomeListView;

/**
 * Created by CXK on 2016/10/18.
 */

public class HomeRankingView extends BaseHomeListView<RankingEntry, HomeRankingItemView> {

    public HomeRankingView(Context context) {
        super(context);
    }

    @Override
    protected int getMaxCount() {
        return 4;
    }

    @Override
    protected View getItemView() {
        return new HomeRankingItemView(getContext());
    }

    public HomeRankingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected String getTitle() {
        return "游戏综合排行榜";
    }

    protected void onMoreClick() {
        AnalyticsHome.umOnEvent(AnalyticsHome.HOME_GAME_RANKING_ANALYTICS,"综合排行更多被点击");
        RankingDetailActivity.start(getContext(), DataMgr.DATA_TYPE_RANKING_SYNTH);
    }

    public void onDestroy() {
        SparseArray<HomeRankingItemView> cacheView = getCacheViews();
        if (cacheView == null)
            return;

        for (int i = 0; i < cacheView.size(); i++) {
            cacheView.get(i).onDestory();
        }
    }
}
