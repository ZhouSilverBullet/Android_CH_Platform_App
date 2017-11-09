package com.caohua.games.ui.account;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.chsdk.ui.widget.RiffEffectLinearLayout;

/**
 * Created by CXK on 2016/10/28.
 */

public class AccountSettingTextView extends RiffEffectLinearLayout {
    private Drawable iconId;
    private String des;
    private String titleOne;
    private TextView title, text;
    private ImageView icon;
    private onTextClickListener listener;

    public AccountSettingTextView(Context context) {
        super(context);
        loadXml(null);
    }

    public AccountSettingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml(attrs);
    }

    public AccountSettingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadXml(attrs);
    }

    private void loadXml(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_activity_account_item, this, true);
        init();
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.AccountSettingTextView);
            iconId = array.getDrawable(R.styleable.AccountSettingTextView_Ac_item_icon);
            des = array.getString(R.styleable.AccountSettingTextView_Ac_item_des);
            titleOne = array.getString(R.styleable.AccountSettingTextView_Ac_item_title);
            array.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        text = (TextView) findViewById(R.id.ch_activity_account_string_tv);
        title = (TextView) findViewById(R.id.ch_activity_account_tv);
        icon = (ImageView) findViewById(R.id.ch_activity_account_icon);
        icon.setImageDrawable(iconId);
        title.setText(titleOne);
        text.setText(des);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.textClick();
                }
            }
        });
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && listener != null) {
//                    listener.textClick();
                }
            }
        });
    }

    public static interface onTextClickListener {
        void textClick();
    }

    public void setOnTextClick(onTextClickListener listener) {
        this.listener = listener;
    }

    public void setTextValue(String value) {
        if (TextUtils.isEmpty(value)) {
            text.setText("未设置");
        } else {
            text.setText(value);
        }
    }

    public String getText() {
        return text.getText().toString();
    }

    public void setTextColor(int textColor) {
        text.setTextColor(textColor);
    }

}
