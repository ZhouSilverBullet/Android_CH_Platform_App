package com.caohua.games.ui.ranking;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.download.ViewDownloadMgr;
import com.caohua.games.biz.ranking.RankingEntry;
import com.caohua.games.ui.download.DownloadButton;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.RiffEffectRelativeLayout;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;

/**
 * Created by CXK on 2016/11/2.
 */

public class RankingTopItemView extends RiffEffectRelativeLayout {
    private RankingEntry entry;
    private ImageView icon;
    private TextView title, type, des;
    private ViewDownloadMgr downloadMgr;
    private ImageView numberImage;
    private TextView numberText;

    public RankingTopItemView(Context context) {
        super(context);
        loadXml();
    }

    public RankingTopItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml();
    }

    public RankingTopItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadXml();
    }

    private void loadXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_view_ranking_detail_top, this, true);
        int padding = ViewUtil.dp2px(getContext(), 10);
        setPadding(0, padding, padding, padding);
        icon = (ImageView) findViewById(R.id.ch_view_ranking_top_img);
        title = (TextView) findViewById(R.id.ch_view_ranking_top_title);
        type = (TextView) findViewById(R.id.ch_view_ranking_top_type);
        des = (TextView) findViewById(R.id.ch_view_ranking_top_des);
        numberImage = (ImageView) findViewById(R.id.ch_view_ranking_top_number_image);
        numberText = (TextView) findViewById(R.id.ch_view_ranking_top_number_text);
        downloadMgr = new ViewDownloadMgr((DownloadButton) findViewById(R.id.ch_view_ranking_top_btn));

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.startGameDetail(v.getContext(), entry.getDownloadEntry());
            }
        });
    }

    public void setData(RankingEntry entry, int position) {
        if (entry == null) {
            return;
        }
        if (this.entry == null || !entry.sameIcon(this.entry)) {
            PicUtil.displayImg(getContext(), icon, entry.getGame_icon(), R.drawable.ch_default_apk_icon);
        }
        this.entry = entry;
        downloadMgr.setData(entry.getDownloadEntry());
        title.setText(entry.getGame_name());
        type.setText(entry.getClassify_name() + " | " + entry.getGame_size() + "MB");
        des.setText(entry.getGame_introduct());
        switch (position) {
            case 0:
                numberImage.setVisibility(VISIBLE);
                numberText.setVisibility(INVISIBLE);
                numberImage.setImageResource(R.drawable.ch_ranking_number_1);
                break;
            case 1:
                numberImage.setVisibility(VISIBLE);
                numberText.setVisibility(INVISIBLE);
                numberImage.setImageResource(R.drawable.ch_ranking_number_2);
                break;
            case 2:
                numberImage.setVisibility(VISIBLE);
                numberText.setVisibility(INVISIBLE);
                numberImage.setImageResource(R.drawable.ch_ranking_number_3);
                break;
            default:
                numberImage.setVisibility(INVISIBLE);
                numberText.setVisibility(VISIBLE);
                numberText.setText("" + (position + 1));
                break;
        }
    }

    public void onDestroy() {
        if (downloadMgr != null) {
            downloadMgr.release();
        }
    }
}
