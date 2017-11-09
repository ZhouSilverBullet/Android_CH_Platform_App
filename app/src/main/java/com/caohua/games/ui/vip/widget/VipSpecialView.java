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

public class VipSpecialView extends LinearLayout {
    private Context context;
    private VipTitleView title;

    public VipSpecialView(Context context) {
        this(context, null);
    }

    public VipSpecialView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VipSpecialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.ch_vip_view_special_view, this, true);
        title = (VipTitleView) findViewById(R.id.ch_vip_view_special_title_view);
        title.setMoreTextGone();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViewById(R.id.ch_vip_view_special_image).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.startAppLink(context, BaseLogic.HOST_APP + "vip/kefuView");
            }
        });
    }
}
