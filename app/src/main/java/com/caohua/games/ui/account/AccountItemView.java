package com.caohua.games.ui.account;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.chsdk.ui.widget.RiffEffectLinearLayout;

/**
 * Created by CXK on 2016/10/22.
 */

public class AccountItemView extends RiffEffectLinearLayout {
    private ImageView left, right;
    private TextView string, redIcon;
    private Drawable iconId;
    private String des;

    public AccountItemView(Context context) {
        super(context);
        loadXml(null);
    }

    public AccountItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        loadXml(attrs);
    }

    public AccountItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml(attrs);
    }

    private void loadXml(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_view_account_item, this, true);
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.AccountItemView);
            iconId = array.getDrawable(R.styleable.AccountItemView_account_item_icon);
            des = array.getString(R.styleable.AccountItemView_account_item_des);
            array.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        left = (ImageView) findViewById(R.id.ch_account_item_icon_left);
        right = (ImageView) findViewById(R.id.ch_account_item_icon_right);
        string = (TextView) findViewById(R.id.ch_account_item_string);
        redIcon = (TextView) findViewById(R.id.ch_account_img_new_message);
        left.setImageDrawable(iconId);
        string.setText(des);
        setClickable(true);
    }

    public void setText(int tip) {
        if (tip == 0) {
            redIcon.setVisibility(View.INVISIBLE);
        } else if (tip == 1) {
            redIcon.setVisibility(View.VISIBLE);
        }
    }

    public boolean getRedIconVisible() {
        return redIcon.getVisibility() == VISIBLE;
    }
}
