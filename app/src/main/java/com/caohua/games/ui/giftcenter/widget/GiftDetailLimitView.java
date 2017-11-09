package com.caohua.games.ui.giftcenter.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;

/**
 * Created by admin on 2017/10/31.
 */

public class GiftDetailLimitView extends RelativeLayout {

    private Context context;
    private TextView limitText;
    private TextView limitValue;
    private String name;

    public GiftDetailLimitView(Context context) {
        this(context, null);
    }

    public GiftDetailLimitView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GiftDetailLimitView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GiftDetailLimitView, defStyleAttr, 0);
        name = a.getString(R.styleable.GiftDetailLimitView_detailText);
        a.recycle();
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.ch_gift_detail_limit_view, this, true);
        setVisibility(GONE);
        limitText = (TextView) findViewById(R.id.ch_gift_detail_limit_text);
        if (!TextUtils.isEmpty(name)) {
            limitText.setText(name);
        }
        limitValue = (TextView) findViewById(R.id.ch_gift_detail_limit_value);
    }

    public void setTextValue(String value) {
        if (!TextUtils.isEmpty(value)) {
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
        limitValue.setText(value);
    }

    public void setLimitText(String value) {
        limitText.setText(value);
    }


}
