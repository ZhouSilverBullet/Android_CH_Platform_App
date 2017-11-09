package com.caohua.games.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.caohua.games.R;

/**
 * Created by admin on 2017/11/6.
 */

public class NoNetworkView extends RelativeLayout {

    private Context context;
    private boolean isBgWhite;

    public NoNetworkView(Context context) {
        this(context, null);
    }

    public NoNetworkView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NoNetworkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NoNetworkView, defStyleAttr, 0);
        isBgWhite = a.getBoolean(R.styleable.NoNetworkView_bg, false);
        a.recycle();
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.ch_no_network_view, this, true);
        if (isBgWhite) {
            setBackgroundColor(getResources().getColor(R.color.ch_white));
        } else {
            setBackgroundColor(getResources().getColor(R.color.ch_gray));
        }
        setVisibility(GONE);
    }
}
