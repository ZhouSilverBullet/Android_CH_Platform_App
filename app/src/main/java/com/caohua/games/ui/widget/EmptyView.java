package com.caohua.games.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;

/**
 * Created by ZengLei on 2016/10/27.
 */

public class EmptyView extends LinearLayout {
    private TextView tvDes;
    private String des;
    private OnEmptyClickListener listener;

    public EmptyView(Context context) {
        super(context);
        loadXml();

    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.EmptyView);
        des = array.getString(R.styleable.EmptyView_empty_des);
        array.recycle();

        loadXml();
    }

    private void loadXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_view_empty, this, true);

        tvDes = (TextView) findViewById(R.id.ch_no_data_des);
//        tvDes.setText(des);
        tvDes.setText("暂无数据");

        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        setClickable(true);
        setVisibility(GONE);
        setBackgroundColor(getResources().getColor(R.color.ch_gray));
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.emptyClick();
                }
            }
        });
    }

    public void setDes(String des) {
//        tvDes.setText(des);
    }

    public void setText(String text) {
        tvDes.setText(text);
    }

    public void setOnEmptyListener(OnEmptyClickListener listener) {
        this.listener = listener;
    }

    public static interface OnEmptyClickListener {
        void emptyClick();
    }
}
