package com.caohua.games.ui.ranking;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.chsdk.biz.app.AnalyticsHome;
import com.caohua.games.biz.download.ViewDownloadMgr;
import com.caohua.games.biz.ranking.RankingEntry;
import com.caohua.games.ui.download.DownloadButton;
import com.chsdk.biz.download.DownloadEntry;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.RiffEffectLinearLayout;
import com.chsdk.utils.PicUtil;

/**
 * Created by CXK on 2016/11/2.
 */

public class RankingRecycleItemView extends RiffEffectLinearLayout {
    private ViewDownloadMgr downloadMgr;
    private RankingEntry entry;
    private ImageView icon;
    private TextView title, type, des;

    public RankingRecycleItemView(Context context) {
        super(context);
        loadXml();
    }

    public RankingRecycleItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml();
    }

    public RankingRecycleItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadXml();
    }

    private void loadXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_view_rank_recycle_item, this, true);
        setOrientation(HORIZONTAL);
        icon = (ImageView) findViewById(R.id.ch_view_rank_recycle_item_icon);
        title = (TextView) findViewById(R.id.ch_view_rank_recycle_item_title);
        type = (TextView) findViewById(R.id.ch_view_rank_recycle_item_type);
        des = (TextView) findViewById(R.id.ch_view_rank_recycle_item_des);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalyticsHome.umOnEvent(AnalyticsHome.FRAGMENT_RANKING_RECYCLER_VIEW_ITEM,"游戏专区二级界面");
                DownloadEntry downloadEntry = new DownloadEntry();
                downloadEntry.downloadUrl = entry.getGame_url();
                downloadEntry.pkg = entry.getPackage_name();
                downloadEntry.iconUrl = entry.getGame_icon();
                downloadEntry.title = entry.getGame_name();
                downloadEntry.detail_url = entry.getDetail_url();
                WebActivity.startGameDetail(v.getContext(),downloadEntry);
            }
        });
        downloadMgr = new ViewDownloadMgr((DownloadButton) findViewById(R.id.ch_view_rank_recycle_item_btn));
    }

    public void setData(RankingEntry entry) {
        if (entry == null) {
            return;
        }
        if (this.entry == null || !entry.sameIcon(this.entry)) {
            PicUtil.displayImg(getContext(), icon, entry.getGame_icon(), R.drawable.ch_default_apk_icon);
        }

        downloadMgr.setData(entry.getDownloadEntry());
        this.entry = entry;
        title.setText(entry.getGame_name());
        type.setText(/*entry.getClassify_name() + "|" + */entry.getGame_size()+"MB");
        des.setText(entry.getGame_introduct());
    }

    public void onDestroy() {
        if (downloadMgr != null) {
            downloadMgr.release();
        }
    }
}
