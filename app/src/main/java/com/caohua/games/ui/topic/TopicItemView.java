package com.caohua.games.ui.topic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.caohua.games.R;
import com.chsdk.biz.app.AnalyticsHome;
import com.caohua.games.biz.DataInterface;
import com.caohua.games.biz.topic.TopicEntry;
import com.chsdk.ui.widget.RiffEffectLinearLayout;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;

/**
 * Created by CXK on 2016/11/3.
 */

public class TopicItemView extends RiffEffectLinearLayout implements DataInterface {
    private TopicEntry entry;
    private ImageView icon;

    public TopicItemView(Context context) {
        super(context);
        lodXml();
    }

    public TopicItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        lodXml();
    }

    public TopicItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        lodXml();
    }

    private void lodXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_activity_topic_list, this, true);
        setClickable(true);
//        int height = ViewUtil.getRealHeight(getContext(), 310, 130, 15, false);
//        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
//                height/*ViewUtil.dp2px(getContext(), 160)*/);
//        setLayoutParams(params);
        setOrientation(VERTICAL);
        init();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalyticsHome.umOnEvent(AnalyticsHome.HOME_TOPIC_CLICK_ANALYTICS,"专题详情二级界面");
                TopicDetailActivity.start(v.getContext(), entry.getId());
            }
        });
    }

    private void init() {
        icon = (ImageView) findViewById(R.id.ch_activity_topic_item_img);
        setLayoutParams(icon);
    }

    public void setData(Object o) {
        if (o == null)
            return;
        TopicEntry entry = (TopicEntry) o;
        if (this.entry == null || !this.entry.sameIcon(entry)) {
            PicUtil.displayImg(getContext(), icon, entry.getSubject_img(), R.drawable.ch_default_pic);
        }
        this.entry = entry;
    }

    private void setLayoutParams(View view) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.height = ViewUtil.getRealHeight(getContext(), 310, 130, 15, false);
        view.setLayoutParams(params);
    }
}
