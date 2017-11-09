package com.caohua.games.ui.topic;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.caohua.games.R;
import com.chsdk.biz.app.AnalyticsHome;
import com.caohua.games.biz.topic.TopicEntry;
import com.caohua.games.ui.widget.HomeSubTitleView;
import com.chsdk.ui.widget.RiffEffectImageButton;
import com.chsdk.ui.widget.RiffEffectLinearLayout;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;

import java.util.List;

/**
 * Created by CXK on 2016/10/18.
 */

public class HomeTopicView extends RiffEffectLinearLayout implements View.OnClickListener{
    private HomeSubTitleView title;
    private List<TopicEntry> data;
    private RiffEffectImageButton imgLeft, imgRight, imgBottom;

    public HomeTopicView(Context context) {
        super(context);
        loadXml();
    }

    public HomeTopicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml();
    }

    private void setLayoutParams(View view, int padding, boolean flag) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.height = ViewUtil.getRealHeight(getContext(), 310, 130, padding, flag);
        view.setLayoutParams(params);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void loadXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_view_home_topic, this, true);
        setVisibility(GONE);
    }

    private void init() {
        imgLeft = (RiffEffectImageButton) findViewById(R.id.ch_view_home_topic_icon_left);
        imgRight = (RiffEffectImageButton) findViewById(R.id.ch_view_home_topic_icon_right);
        imgBottom = (RiffEffectImageButton) findViewById(R.id.ch_view_home_topic_icon_bottom);
        setLayoutParams(imgLeft, 25, true);
        setLayoutParams(imgRight, 25, true);
        setLayoutParams(imgBottom, 20, false);
        title = (HomeSubTitleView) findViewById(R.id.ch_view_home_topic_title);
        title.setTopTitle("专题");
        title.setOnMoreClickListener(new HomeSubTitleView.OnMoreClickListener() {
            @Override
            public void moreClick() {
                AnalyticsHome.umOnEvent(AnalyticsHome.HOME_HOT_TOPIC_ANALYTICS,"专题推荐二级界面");
                TopicActivity.start(getContext(), data);
            }
        });

        imgLeft.setOnClickListener(this);
        imgRight.setOnClickListener(this);
        imgBottom.setOnClickListener(this);
    }

    public synchronized void initData(List<TopicEntry> list) {
        if (list == null || list.size() == 0) {
            setVisibility(GONE);
            return;
        }

        setVisibility(VISIBLE);
        int count = list.size();
        if (count == 1) {
            imgRight.setVisibility(GONE);
            imgLeft.setVisibility(GONE);
            imgBottom.setVisibility(VISIBLE);
            setImg(imgBottom, list.get(0).getSubject_img());
        } else if (count == 2) {
            imgLeft.setVisibility(VISIBLE);
            imgRight.setVisibility(VISIBLE);
            imgBottom.setVisibility(GONE);
            setImg(imgRight, list.get(1).getSubject_img());
            setImg(imgLeft, list.get(0).getSubject_img());
        } else {
            imgLeft.setVisibility(VISIBLE);
            imgRight.setVisibility(VISIBLE);
            imgBottom.setVisibility(VISIBLE);
            setImg(imgRight, list.get(1).getSubject_img());
            setImg(imgBottom, list.get(2).getSubject_img());
            setImg(imgLeft, list.get(0).getSubject_img());
        }
        data = list;
    }

    private void setImg(View img, String imgUlr) {
        if (TextUtils.isEmpty(imgUlr))
            return;

        img.setVisibility(VISIBLE);
        String url = (String) img.getTag();
        if (TextUtils.isEmpty(url) || !url.equals(imgUlr)) {
            PicUtil.displayImg(getContext(), img, imgUlr, R.drawable.ch_default_pic);
            img.setTag(imgUlr);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == imgLeft) {
            TopicDetailActivity.start(v.getContext(), data.get(0).getId());
        } else if (v == imgRight) {
            TopicDetailActivity.start(v.getContext(), data.get(1).getId());
        } else if (v == imgBottom) {
            int index = data.size() == 1 ? 0 : 2;
            TopicDetailActivity.start(v.getContext(), data.get(index).getId());
        }
    }
}
