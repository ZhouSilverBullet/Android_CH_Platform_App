package com.caohua.games.ui.account;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;

/**
 * Created by CXK on 2016/10/27.
 */

public class SafeItemView extends LinearLayout {
    private Drawable iconId;
    private String des;
    private String titleOne;
    private TextView string, title;
    private ImageView icon;

    public SafeItemView(Context context) {
        super(context);
        loadXml(null);
    }

    public SafeItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml(attrs);
    }

    public SafeItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        loadXml(attrs);
    }

    private void loadXml(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_safe_setting_item, this, true);
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.SafeItemView);
            iconId = array.getDrawable(R.styleable.SafeItemView_safe_item_icon);
            des = array.getString(R.styleable.SafeItemView_safe_item_des);
            titleOne = array.getString(R.styleable.SafeItemView_safe_item_title);
            array.recycle();
        }

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        title = (TextView) findViewById(R.id.ch_dialog_safe_tv);
        icon = (ImageView) findViewById(R.id.ch_dialog_safe_icon);
        string = (TextView) findViewById(R.id.ch_dialog_safe_phonebound);
        string.setText(des);
        icon.setImageDrawable(iconId);
        title.setText(titleOne);
    }

}