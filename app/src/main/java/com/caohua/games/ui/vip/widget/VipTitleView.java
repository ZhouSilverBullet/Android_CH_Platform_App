package com.caohua.games.ui.vip.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.chsdk.utils.ViewUtil;

/**
 * Created by admin on 2017/8/25.
 */

public class VipTitleView extends RelativeLayout {
    private Context context;
    private TextView nameText;
    private TextView moreText;
    private String moreValue;
    private String nameValue;
    private View icon;
    private View img;
    private TextView smallText;
    private boolean leftIconVisible;

    public VipTitleView(Context context) {
        this(context, null);
    }

    public VipTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VipTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VipTitleView, defStyleAttr, 0);
        moreValue = a.getString(R.styleable.VipTitleView_moreTextValue);
        nameValue = a.getString(R.styleable.VipTitleView_nameTextValue);
        leftIconVisible = a.getBoolean(R.styleable.VipTitleView_nameTextLeftIcon, false);
        a.recycle();
        init();
    }

    private void init() {
        setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutInflater.from(context).inflate(R.layout.ch_vip_title_view, this, true);
        setBackgroundColor(getResources().getColor(R.color.ch_white));
        setPadding(ViewUtil.dp2px(context, 10), ViewUtil.dp2px(context, 10), 0, ViewUtil.dp2px(context, 10));
        nameText = (TextView) findViewById(R.id.ch_vip_title_name);
        moreText = (TextView) findViewById(R.id.ch_vip_title_more);
        img = findViewById(R.id.ch_vip_title_more_img);
        smallText = (TextView) findViewById(R.id.ch_vip_title_small_name);
        icon = findViewById(R.id.ch_vip_title_icon);
        nameText.setText(nameValue);
        moreText.setText(moreValue);
        if (leftIconVisible) {
            icon.setVisibility(GONE);
        }
    }

    public TextView getSmallText() {
        return smallText;
    }

    public TextView getMoreText() {
        return moreText;
    }

    public TextView getNameText() {
        return nameText;
    }

    public View getIcon() {
        return icon;
    }

    public void setIconAndMoreTextGone() {
        icon.setVisibility(GONE);
        moreText.setVisibility(GONE);
        img.setVisibility(GONE);
    }

    public void setMoreTextGone() {
        moreText.setVisibility(GONE);
        img.setVisibility(GONE);
    }
}
