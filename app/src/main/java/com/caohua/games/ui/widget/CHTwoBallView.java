package com.caohua.games.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.caohua.games.R;
import com.chsdk.utils.ViewUtil;

/**
 * Created by admin on 2017/11/9.
 */

public class CHTwoBallView extends LinearLayout {

    private Context context;
    private TwoBallRotationProgressBar progressBar;

    public CHTwoBallView(Context context) {
        this(context, null);
    }

    public CHTwoBallView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CHTwoBallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        progressBar = new TwoBallRotationProgressBar(context);
        setBackgroundColor(getResources().getColor(R.color.ch_gray));
        LayoutParams params = new LayoutParams(ViewUtil.dp2px(context, 50), ViewUtil.dp2px(context, 50));
        addView(progressBar, params);
        setGravity(Gravity.CENTER);
        setVisibility(GONE);
    }
}
