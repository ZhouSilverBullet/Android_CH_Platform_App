package com.caohua.games.ui.vip.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.caohua.games.R;
import com.chsdk.biz.BaseLogic;
import com.chsdk.ui.WebActivity;

/**
 * Created by admin on 2017/8/24.
 */

public class VipPlatformPowerView extends LinearLayout {
    private Context context;
    private VipTitleView title;

    public VipPlatformPowerView(Context context) {
        this(context, null);
    }

    public VipPlatformPowerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VipPlatformPowerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.ch_vip_platform_power_view, this, true);
        title = (VipTitleView) findViewById(R.id.ch_view_vip_platform_title);
        title.setMoreTextGone();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViewById(R.id.ch_vip_platform_item_rl1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.startAppLink(context, BaseLogic.HOST_APP + "vip/growShow");
            }
        });

        findViewById(R.id.ch_vip_platform_item_rl2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.startAppLink(context, BaseLogic.HOST_APP + "vip/growShow");
            }
        });
    }
}
