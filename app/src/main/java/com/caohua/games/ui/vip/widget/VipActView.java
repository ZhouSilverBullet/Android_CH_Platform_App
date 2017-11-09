package com.caohua.games.ui.vip.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.caohua.games.R;
import com.caohua.games.biz.vip.VipEntry;
import com.caohua.games.ui.vip.VipActSecondActivity;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.RiffEffectImageButton;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;

import java.util.List;

/**
 * Created by admin on 2017/8/24.
 */

public class VipActView extends LinearLayout implements View.OnClickListener {
    private VipTitleView title;
    private List<VipEntry.DataBean.ActBean> data;
    private RiffEffectImageButton imgLeft, imgRight, imgBottom;

    public VipActView(Context context) {
        super(context);
        loadXml();
    }

    public VipActView(Context context, AttributeSet attrs) {
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
    }

    private void loadXml() {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.ch_vip_view_act, this, true);
        setVisibility(GONE);
        init();
    }

    private void init() {
        imgLeft = (RiffEffectImageButton) findViewById(R.id.ch_view_vip_icon_left);
        imgRight = (RiffEffectImageButton) findViewById(R.id.ch_view_vip_icon_right);
        imgBottom = (RiffEffectImageButton) findViewById(R.id.ch_view_vip_icon_bottom);
        setLayoutParams(imgLeft, 25, true);
        setLayoutParams(imgRight, 25, true);
        setLayoutParams(imgBottom, 20, false);
        title = (VipTitleView) findViewById(R.id.ch_view_vip_title);
        title.getMoreText().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                AnalyticsHome.umOnEvent(AnalyticsHome.HOME_HOT_ANALYTICS,"专题推荐二级界面");
//                TopicActivity.start(getContext(), data);
                VipActSecondActivity.start(v.getContext());
            }
        });

        imgLeft.setOnClickListener(this);
        imgRight.setOnClickListener(this);
        imgBottom.setOnClickListener(this);
    }

    public synchronized void initData(List<VipEntry.DataBean.ActBean> list) {
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
            setImg(imgBottom, list.get(0).getImage());
        } else if (count == 2) {
            imgLeft.setVisibility(VISIBLE);
            imgRight.setVisibility(VISIBLE);
            imgBottom.setVisibility(GONE);
            setImg(imgRight, list.get(1).getImage());
            setImg(imgLeft, list.get(0).getImage());
        } else {
            imgLeft.setVisibility(VISIBLE);
            imgRight.setVisibility(VISIBLE);
            imgBottom.setVisibility(VISIBLE);
            setImg(imgRight, list.get(1).getImage());
            setImg(imgBottom, list.get(2).getImage());
            setImg(imgLeft, list.get(0).getImage());
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
            WebActivity.startWebPage(getContext(), data.get(0).getUrl());
        } else if (v == imgRight) {
            WebActivity.startWebPage(getContext(), data.get(1).getUrl());
        } else if (v == imgBottom) {
            int index = data.size() == 1 ? 0 : 2;
            WebActivity.startWebPage(getContext(), data.get(index).getUrl());
        }
    }
}
