package com.caohua.games.ui.vip.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.vip.VipEntry;
import com.chsdk.biz.BaseLogic;
import com.chsdk.ui.WebActivity;

/**
 * Created by admin on 2017/8/24.
 */

public class VipCardView extends RelativeLayout {
    private Context context;
    private TextView type;
    private TextView username;
    private TextView level;
    private ProgressBar progress;
    private TextView exp;

    public VipCardView(Context context) {
        this(context, null);
    }

    public VipCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VipCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.ch_vip_card_view_layout, this, true);
        setVisibility(GONE);
        type = (TextView) findViewById(R.id.ch_vip_card_view_type);
        username = (TextView) findViewById(R.id.ch_vip_card_view_username);
        level = (TextView) findViewById(R.id.ch_vip_card_view_level);
        progress = (ProgressBar) findViewById(R.id.ch_vip_card_view_progress);
        exp = (TextView) findViewById(R.id.ch_vip_card_view_exp);
    }

    public void setData(VipEntry.DataBean.UserBean user) {
        if (user == null) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        type.setText(user.getVip_name());
        username.setText(user.getNickname());
        level.setText("VIP" + user.getVip_level());
        exp.setText("VIP值:" + user.getVip_exp() + "/" + user.getNext_exp());
        progress.setProgress(user.getExp_rate());
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.startAppLink(context, BaseLogic.HOST_APP + "vip/expLogView");
            }
        });
    }
}
