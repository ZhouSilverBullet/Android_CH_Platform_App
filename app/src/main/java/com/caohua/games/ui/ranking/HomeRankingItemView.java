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
import com.caohua.games.biz.DataInterface;
import com.caohua.games.biz.download.ViewDownloadMgr;
import com.caohua.games.biz.ranking.RankingEntry;
import com.caohua.games.ui.download.DownloadButton;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.RiffEffectRelativeLayout;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;

/**
 * Created by CXK on 2016/10/22.
 */

public class HomeRankingItemView extends RiffEffectRelativeLayout implements DataInterface{

    private ViewDownloadMgr downloadMgr;
    private RankingEntry entry;
    private ImageView imgIcon;
    private TextView tvTitle, tvType, tvDes;

    public HomeRankingItemView(Context context) {
        super(context);
        loadXml();
    }

    public HomeRankingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml();
    }

    private void loadXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_view_home_ranking_item, this, true);
        int padding = ViewUtil.dp2px(getContext(), 10);
        setPadding(padding, padding, padding, padding);
        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setData(Object o) {
        if (o == null)
            return;

        RankingEntry entry = (RankingEntry) o;
        if (this.entry == null || !this.entry.sameIcon(entry)) {
            PicUtil.displayImg(getContext(), imgIcon, entry.getGame_icon(), R.drawable.ch_default_apk_icon);
        }
        this.entry = entry;
        downloadMgr.setData(entry.getDownloadEntry());
        tvTitle.setText(entry.getGame_name());
        tvDes.setText(entry.getGame_introduct());
        tvType.setText(entry.getClassify_name() + " | " + entry.getGame_size() + "MB");
    }

    private void init() {
        imgIcon = (ImageView) findViewById(R.id.ch_view_home_openning_item_icon);
        tvTitle = (TextView) findViewById(R.id.ch_view_home_openning_item_title);
        tvType = (TextView) findViewById(R.id.ch_view_home_openning_item_type_and_size);
        tvDes = (TextView) findViewById(R.id.ch_view_home_openning_item_des);
        downloadMgr = new ViewDownloadMgr((DownloadButton) findViewById(R.id.ch_view_home_openning_item_btn));

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalyticsHome.umOnEvent(AnalyticsHome.HOME_GAME_RANKING_CLICK_ANALYTICS,"游戏专区二级界面");
                WebActivity.startGameDetail(v.getContext(), entry.getDownloadEntry());
            }
        });
    }

    public void onDestory() {
        downloadMgr.release();
    }
}
