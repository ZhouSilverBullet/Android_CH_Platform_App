package com.caohua.games.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;

/**
 * Created by CXK on 2016/10/19.
 */

public class HomeSubTitleView extends RelativeLayout {
    private OnMoreClickListener listener;
    private TextView tvDes;
    private TextView tvMore;

    public HomeSubTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeSubTitleView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.ch_home_sub_title, this, true);
        tvDes = (TextView) findViewById(R.id.ch_home_common_title_des);
        tvMore = (TextView) findViewById(R.id.ch_home_common_title_more);
        tvMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.moreClick();
                }
            }
        });
    }

    public void setTvMore(String more) {
        tvMore.setText(more);
    }

    public void setTopTitle(String title) {
        tvDes.setText(title);
    }

    public void setOnMoreClickListener(OnMoreClickListener listener) {
        this.listener = listener;
    }

    public static interface OnMoreClickListener {
        void moreClick();
    }
}
