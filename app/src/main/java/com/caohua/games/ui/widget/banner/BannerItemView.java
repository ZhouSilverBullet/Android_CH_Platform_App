package com.caohua.games.ui.widget.banner;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.caohua.games.R;
import com.caohua.games.biz.home.BannerEntry;
import com.caohua.games.ui.StoreSecondActivity;
import com.caohua.games.ui.topic.TopicDetailActivity;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.RiffEffectImageButton;
import com.chsdk.utils.PicUtil;

public class BannerItemView extends RelativeLayout implements View.OnClickListener {
    private BannerEntry banner;
    private RiffEffectImageButton iv_banner;

    public BannerItemView(Context context) {
        super(context, null);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.ch_view_home_banner_item, this, true);
        iv_banner = (RiffEffectImageButton) findViewById(R.id.iv_banner);
        iv_banner.setOnClickListener(this);
        setOnClickListener(this);
    }

    public void initData(BannerEntry banner) {
        if (banner == null)
            return;

        if (this.banner == null || !this.banner.sameIcon(banner)) {
            PicUtil.displayImg(getContext(), iv_banner, banner.getWap_img(), R.drawable.ch_default_pic);
        }
        this.banner = banner;
    }

    @Override
    public void onClick(View v) {
        if (banner != null) {
            String url = banner.getApp_url();
            String type = banner.getBanner_type();
            Context context = v.getContext();
            if (!TextUtils.isEmpty(url) && url.contains(StoreSecondActivity.SHOP_URL)) {
                Intent webIntent = new Intent(context, StoreSecondActivity.class);
                webIntent.putExtra("url", url);
                webIntent.putExtra("type", 1);
                context.startActivity(webIntent);
            } else if ("1".equals(type) && !TextUtils.isEmpty(url)) {
                WebActivity.startWebPage(context, url);
                AnalyticsHome.umOnEvent(AnalyticsHome.HOME_BANNER_CLICK_ANALYTICS, "进入网页");
            } else if ("2".equals(type)) {
                AnalyticsHome.umOnEvent(AnalyticsHome.HOME_BANNER_CLICK_ANALYTICS, "专题详情二级界面");
                TopicDetailActivity.start(context, banner.getApp_url());
            }
        }
    }

    public String getTitle() {
        return banner.getBanner_title();
    }

    public String getImageUrl() {
        return banner.getWap_img();
    }
}
