package com.caohua.games.ui.find;

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

public class FindItemView extends RiffEffectLinearLayout {
    private ImageView left, right;
    private TextView string, redIcon;
    private Drawable iconId;
    private String des;
    private String item_describe;
    private TextView itemDescribe;

    public FindItemView(Context context) {
        super(context);
        loadXml(null);
    }

    public FindItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        loadXml(attrs);
    }

    public FindItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml(attrs);
    }

    private void loadXml(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_view_find_item, this, true);
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.AccountItemView);
            iconId = array.getDrawable(R.styleable.AccountItemView_account_item_icon);
            des = array.getString(R.styleable.AccountItemView_account_item_des);
            item_describe = array.getString(R.styleable.AccountItemView_find_item_describe);
            array.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        left = (ImageView) findViewById(R.id.ch_find_item_icon_left);
        right = (ImageView) findViewById(R.id.ch_find_item_icon_right);
        string = (TextView) findViewById(R.id.ch_find_item_string);
        itemDescribe = (TextView) findViewById(R.id.ch_find_item_describe);
        redIcon = (TextView) findViewById(R.id.ch_find_img_new_message);
        left.setImageDrawable(iconId);
        string.setText(des);
        itemDescribe.setText(item_describe);
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
