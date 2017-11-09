package com.caohua.games.ui.topic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.DataInterface;
import com.caohua.games.biz.download.ViewDownloadMgr;
import com.caohua.games.biz.topic.TopicDetailEntry;
import com.caohua.games.ui.download.DownloadButton;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.RiffEffectRelativeLayout;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;

/**
 * Created by CXK on 2016/11/2.
 */

public class TopicDetailItemView extends RiffEffectRelativeLayout implements DataInterface{
    private TopicDetailEntry entry;
    private ImageView icon;
    private TextView title, type, des;
    private ViewDownloadMgr downloadMgr;

    public TopicDetailItemView(Context context) {
        super(context);
        loadXml();
    }

    public TopicDetailItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml();
    }

    public TopicDetailItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadXml();
    }

    private void loadXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_view_topic_detail_item, this, true);
        int padding = ViewUtil.dp2px(getContext(), 10);
        setPadding(padding, padding, padding, padding);
        icon = (ImageView) findViewById(R.id.ch_view_topic_detail_img);
        title = (TextView) findViewById(R.id.ch_view_topic_detail_title);
        type = (TextView) findViewById(R.id.ch_view_topic_detail_type);
        des = (TextView) findViewById(R.id.ch_view_topic_detail_des);
        downloadMgr = new ViewDownloadMgr((DownloadButton) findViewById(R.id.ch_view_topic_detail_btn));

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.startGameDetail(v.getContext(), entry.getDownloadEntry());
            }
        });
    }

    public void setData(Object o) {
        if (o == null) {
            return;
        }

        TopicDetailEntry entry = (TopicDetailEntry) o;
        if (this.entry == null || !entry.sameIcon(this.entry)) {
            PicUtil.displayImg(getContext(), icon, entry.getGame_icon(), R.drawable.ch_default_apk_icon);
        }
        this.entry = entry;
        downloadMgr.setData(entry.getDownloadEntry());
        title.setText(entry.getGame_name());
        type.setText(entry.getClassify_name() + " | " + entry.getGame_size() + "MB");
        des.setText(entry.getGame_introduct());
    }

    public void release() {
        if (downloadMgr != null) {
            downloadMgr.release();
        }
    }
}
