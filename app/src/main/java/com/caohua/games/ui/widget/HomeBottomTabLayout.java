package com.caohua.games.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;

/**
 * Created by zhouzhou on 2017/2/20.
 */

public class HomeBottomTabLayout extends LinearLayout {
    private ImageView tabImage;
    private TextView tabText;
    private String tabTextString;
    private TextView textDot;

    public HomeBottomTabLayout(Context context) {
        this(context, null);
    }

    public HomeBottomTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeBottomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.HomeBottomTabLayout, defStyleAttr, 0);
        tabTextString = a.getString(R.styleable.HomeBottomTabLayout_tabText);
        a.recycle();
        init(context);
    }

    private void init(Context context) {
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        setClickable(true);
        LayoutInflater.from(context).inflate(R.layout.ch_home_bottom_tab_layout, this, true);
        tabImage = ((ImageView) findViewById(R.id.ch_home_bottom_image));
        tabText = ((TextView) findViewById(R.id.ch_home_bottom_text));
        textDot = ((TextView) findViewById(R.id.ch_home_bottom_dot));
        tabText.setText(tabTextString);
    }

    public void setTextDotVisibility(int visibility) {
        textDot.setVisibility(visibility);
    }

    /**
     * 设置状态
     *
     * @param textColor text颜色
     * @param resId 选择的图片
     */
    public void setTabStatus(@ColorRes int textColor, @DrawableRes int resId){
        tabText.setTextColor(getResources().getColor(textColor));
        tabImage.setImageResource(resId);
    }


}
