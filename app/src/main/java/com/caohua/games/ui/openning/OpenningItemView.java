package com.caohua.games.ui.openning;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.chsdk.biz.app.AnalyticsHome;
import com.caohua.games.biz.download.ViewDownloadMgr;
import com.caohua.games.biz.openning.OpenningEntry;
import com.caohua.games.ui.download.DownloadButton;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.RiffEffectLinearLayout;
import com.chsdk.utils.PicUtil;

/**
 * Created by CXK on 2016/10/24.
 */

public class OpenningItemView extends RiffEffectLinearLayout {
    private ViewDownloadMgr downloadMgr;
    private DownloadButton button;
    private TextView tvTime, title, des;
    private ImageView img;
    private OpenningEntry entry;

    public OpenningItemView(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        loadXml();
    }

    public OpenningItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml();
    }

    private void loadXml(){
        LayoutInflater.from(getContext()).inflate(R.layout.ch_view_open_item, this, true);
        initView();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void initView () {
        button = (DownloadButton) findViewById(R.id.ch_open_item_download_btn);
        downloadMgr = new ViewDownloadMgr(button);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalyticsHome.umOnEvent(AnalyticsHome.OPENING_FRAGMENT_ITEM_VIEW_CLICK_ANALYTICS,"进入游戏专区二级界面");
                WebActivity.startGameDetail(v.getContext(), entry.getDownloadEntry());
            }
        });

        tvTime = (TextView) findViewById(R.id.ch_open_item_download_time);
        img = (ImageView) findViewById(R.id.ch_open_item_download_icon);


        title = (TextView) findViewById(R.id.ch_open_item_download_title);
        des = (TextView) findViewById(R.id.ch_open_item_download_des);
    }

    public void setData(OpenningEntry entry) {
        if (entry == null)
            return;

        if (this.entry == null || !this.entry.sameIcon(entry)) {
            PicUtil.displayImg(getContext(), img, entry.getGame_icon(), R.drawable.ch_default_apk_icon);
        }

        this.entry = entry;
        downloadMgr.setData(entry.getDownloadEntry());
        tvTime.setText(entry.getHour());
        title.setText(entry.getGame_name());
        des.setText(entry.getService_name());
    }

    public void onDestroy() {
        if (downloadMgr != null) {
            downloadMgr.release();
        }
    }
}
