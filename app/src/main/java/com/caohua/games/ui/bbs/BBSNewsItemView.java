package com.caohua.games.ui.bbs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.caohua.games.R;
import com.chsdk.ui.widget.RiffEffectLinearLayout;

/**
 * Created by zhouzhou on 2017/5/31.
 */

public class BBSNewsItemView extends RiffEffectLinearLayout {
    private TextView newsTitle;
    private TextView newsContent;
    private View lineView;

    public BBSNewsItemView(Context context) {
        this(context, null);
    }

    public BBSNewsItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BBSNewsItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.ch_bbs_news_item_view, this, true);
        newsTitle = ((TextView) findViewById(R.id.ch_bbs_news_item_text_title));
        newsContent = ((TextView) findViewById(R.id.ch_bbs_news_item_text_content));
        lineView = findViewById(R.id.ch_bbs_news_item_text_line);
    }

    public void setLineViewVisibility(int visibility) {
        lineView.setVisibility(visibility);
    }

    public void setData(int index, String title, String content) {
        switch (index) {
            case 0:
                newsTitle.setTextColor(getColor(R.color.ch_sub_activity_pop));
                newsTitle.setBackgroundDrawable(getResources().getDrawable(R.drawable.ch_bbs_news_item_view_act_bg));
                newsContent.setTextColor(getColor(R.color.ch_sub_activity_pop));
                break;
            case 1:
                newsTitle.setTextColor(getColor(R.color.ch_color_download_pause));
                newsTitle.setBackgroundDrawable(getResources().getDrawable(R.drawable.ch_bbs_news_item_view_note_bg));
                newsContent.setTextColor(getColor(R.color.ch_color_download_pause));
                break;
            case 2:
                newsTitle.setTextColor(getColor(R.color.ch_usercenter_official_bg_color));
                newsTitle.setBackgroundDrawable(getResources().getDrawable(R.drawable.ch_bbs_news_item_view_news_bg));
                newsContent.setTextColor(getColor(R.color.ch_usercenter_official_bg_color));
                break;
        }
        newsTitle.setText(title);
        newsContent.setText(content);
    }

    private int getColor(int color) {
        return getResources().getColor(color);
    }
}
