package com.caohua.games.ui.dataopen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.caohua.games.R;
import com.caohua.games.ui.vip.widget.VipTitleView;

/**
 * Created by admin on 2017/9/20.
 */

public class DataOpenNotifyRuleView extends RelativeLayout {
    private Context context;
    private VipTitleView titleView;

    public DataOpenNotifyRuleView(Context context) {
        this(context, null);
    }

    public DataOpenNotifyRuleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DataOpenNotifyRuleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.ch_view_data_open_notify_rule_view, this, true);
        setVisibility(GONE);
        titleView = (VipTitleView) findViewById(R.id.ch_view_data_open_rule_title_view);
        titleView.setIconAndMoreTextGone();
    }
}
