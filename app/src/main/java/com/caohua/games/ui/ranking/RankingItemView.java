package com.caohua.games.ui.ranking;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.chsdk.biz.app.AnalyticsHome;
import com.caohua.games.biz.home.DataMgr;
import com.caohua.games.biz.ranking.RankingTotalSubEntry;
import com.chsdk.ui.widget.RiffEffectRelativeLayout;
import com.chsdk.utils.ViewUtil;

import java.util.List;

/**
 * Created by CXK on 2016/10/22.
 */

public class RankingItemView extends RiffEffectRelativeLayout {
    private ImageView rankIcon;
    private TextView itemOne, itemTwo, itemThree;
    private LinearLayout layoutOne, layoutTwo, layoutThree;
    private int index;

    public RankingItemView(Context context) {
        super(context);
        loadXml();
    }

    public RankingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml();
    }

    private void loadXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_view_ranking_item, this, true);
        int padding = ViewUtil.dp2px(getContext(), 10);
        setPadding(padding, padding, padding, padding);
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void init() {
        rankIcon = (ImageView) findViewById(R.id.ch_view_rank_icon);
        layoutOne = (LinearLayout) findViewById(R.id.ch_view_rank_layout_item_one);
        layoutTwo = (LinearLayout) findViewById(R.id.ch_view_rank_layout_item_two);
        layoutThree = (LinearLayout) findViewById(R.id.ch_view_rank_layout_item_three);
        itemOne = (TextView) findViewById(R.id.ch_view_rank_item_one);
        itemTwo = (TextView) findViewById(R.id.ch_view_rank_item_two);
        itemThree = (TextView) findViewById(R.id.ch_view_rank_item_three);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalyticsHome.umOnEvent(AnalyticsHome.DETAIL_FRAGMENT_RANKING_CLICK_ANALYTICS,"综合排行榜二级界面");
                RankingDetailActivity.start(getContext(), index);
            }
        });
    }

    private int getType(int index) {
        if (index == 0) {
            return DataMgr.DATA_TYPE_RANKING_SYNTH;
        } else if (index == 1) {
            return DataMgr.DATA_TYPE_RANKING_NEW_GAME;
        } else if (index == 2) {
            return DataMgr.DATA_TYPE_RANKING_RECOMM;
        } else if (index == 3) {
            return DataMgr.DATA_TYPE_RANKING_NEW;
        }
        return DataMgr.DATA_TYPE_RANKING_SYNTH;
    }

    public synchronized void initData(List<RankingTotalSubEntry> list, int index) {
        this.index = getType(index);
        if (list == null || list.size() == 0) {
            return;
        }

        RankingTotalSubEntry entry = list.get(0);
        layoutOne.setVisibility(VISIBLE);
        itemOne.setText(entry.getGame_name());
        if (list.size() > 1) {
            entry = list.get(1);
            layoutTwo.setVisibility(VISIBLE);
            itemTwo.setText(entry.getGame_name());
            if (list.size() > 2) {
                entry = list.get(2);
                layoutThree.setVisibility(VISIBLE);
                itemThree.setText(entry.getGame_name());
            } else {
                layoutThree.setVisibility(GONE);
            }
        } else {
            layoutTwo.setVisibility(GONE);
            layoutThree.setVisibility(GONE);
        }
        
        int resId = 0;
        if (index == 0) {
            resId = R.drawable.ch_rank_mutiple;
        } else if (index == 1) {
            resId = R.drawable.ch_rank_newly;
        } else if (index == 2) {
            resId = R.drawable.ch_rank_rcmd;
        } else if (index == 3) {
            resId = R.drawable.ch_rank_online;
        }
        rankIcon.setBackgroundResource(resId);
    }
}
