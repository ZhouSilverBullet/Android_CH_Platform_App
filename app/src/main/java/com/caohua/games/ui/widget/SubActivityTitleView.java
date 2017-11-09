package com.caohua.games.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.ui.account.AccountSettingActivity;
import com.caohua.games.ui.account.SystemSetupActivity;
import com.chsdk.configure.DataStorage;
import com.chsdk.ui.WebActivity;
import com.chsdk.utils.ViewUtil;

/**
 * Created by CXK on 2016/10/19.
 */

public class SubActivityTitleView extends RelativeLayout implements View.OnClickListener {
    private final int bg;
    private final String skipTitleValue;
    private final int skipTitleColor;
    private OnLogoutListener listener;
    private TextView tvTitle;
    private PopupWindow popWindow;
    private TextView accountSetting, appSetting, logout;
    private String des;
    private LinearLayout popMenu;
    private ImageView rightView;
    public static final int SHOW_UER_CENTER = 0;
    public static final int SHOW_PAY_CENTER = 1;
    private int mShow;
    private int subTextColor;
    private ImageView backImage;
    private boolean imgChange;
    private TextView skipTitle;

    public SubActivityTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.SubActivityTitleView);
        subTextColor = array.getColor(R.styleable.SubActivityTitleView_sub_activity_title_text_color, getResources().getColor(R.color.white));
        imgChange = array.getBoolean(R.styleable.SubActivityTitleView_sub_activity_title_img_change, false);
        bg = array.getColor(R.styleable.SubActivityTitleView_sub_activity_title_bg, getResources().getColor(R.color.ch_black_alpha_title));
        des = array.getString(R.styleable.SubActivityTitleView_sub_activity_title_des);
        skipTitleValue = array.getString(R.styleable.SubActivityTitleView_sub_activity_skip_text);
        skipTitleColor = array.getColor(R.styleable.SubActivityTitleView_sub_activity_skip_text_color, getResources().getColor(R.color.ch_black));
        array.recycle();
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.ch_sub_activity_title, this, true);
//        setBackgroundColor(bg);
        setBackgroundResource(R.drawable.ch_home_title_bg);
        setFitsSystemWindows(true);
        setClipToPadding(true);
        tvTitle = (TextView) findViewById(R.id.ch_sub_activity_title);
        rightView = (ImageView) findViewById(R.id.ch_sub_activity_menu);
        skipTitle = (TextView)findViewById(R.id.ch_sub_activity_title_text);
        if (!TextUtils.isEmpty(skipTitleValue)) {
            skipTitle.setVisibility(VISIBLE);
            skipTitle.setText(skipTitleValue);
        }
        tvTitle.setTextColor(subTextColor);
        rightView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShow == SHOW_UER_CENTER) {
                    if (popWindow != null && popWindow.isShowing()) {
                        popWindow.dismiss();
                        return;
                    } else {
                        showPopWindow();
                        popWindow.showAsDropDown(v, 0, 5);
                    }
                } else if (mShow == SHOW_PAY_CENTER) {
                    WebActivity.startWebPage(getContext(),
                            "https://app-sdk.caohua.com/main/chargeJump");
                }
            }
        });

        backImage = (ImageView) findViewById(R.id.ch_sub_activity_goback);
        if (imgChange) {
//            backImage.setImageResource(R.drawable.ch_user_center_left_icon);
            backImage.setVisibility(GONE);
        }
        backImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }
        });

        tvTitle.setText(des);
    }

    public TextView getSkipTitle() {
        return skipTitle;
    }

    public ImageView getBackImage() {
        return backImage;
    }

    private void showPopWindow() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.ch_view_app_menu, null);
        accountSetting = (TextView) view.findViewById(R.id.ch_pop_usercenter_account_setting);
        appSetting = (TextView) view.findViewById(R.id.ch_pop_usercenter_app_setting);
        logout = (TextView) view.findViewById(R.id.ch_pop_usercenter_loginout);
        if (AppContext.getAppContext().isLogin()) {
            logout.setVisibility(VISIBLE);
            accountSetting.setVisibility(VISIBLE);
        }
        popWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popWindow.setOutsideTouchable(true);
        popMenu = (LinearLayout) view.findViewById(R.id.ch_view_pop_menu);
        popMenu.setFocusable(true);
        popMenu.setFocusableInTouchMode(true);
        Drawable drawable = getResources().getDrawable(R.drawable.ch_sub_activity_title_view_pop_bg);
        popWindow.setBackgroundDrawable(drawable);
        int dimensionY = (int) getResources().getDimension(R.dimen.ch_pop_y);
        int dimensionX = (int) getResources().getDimension(R.dimen.ch_pop_x);
        popWindow.showAsDropDown(rightView, dimensionX, dimensionY);
        popMenu.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    popWindow.dismiss();
                }
                return false;
            }
        });
        accountSetting.setOnClickListener(this);
        appSetting.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    public void setOnMoreClickListener(OnLogoutListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ch_pop_usercenter_account_setting:
                getContext().startActivity(new Intent(getContext(), AccountSettingActivity.class));
                popWindow.dismiss();
                break;

            case R.id.ch_pop_usercenter_app_setting:
                getContext().startActivity(new Intent(getContext(), SystemSetupActivity.class));
                popWindow.dismiss();
                break;
            case R.id.ch_pop_usercenter_loginout:
                DataStorage.setAppLogin(getContext(), false);
                popWindow.dismiss();
                if (listener != null) {
                    listener.logout();
                }
                break;
        }
    }

    public void showMenu(int show) {
        mShow = show;
        rightView.setVisibility(VISIBLE);
    }

    public void showPlay(int show) {
        this.mShow = show;
        rightView.setVisibility(VISIBLE);
        rightView.setImageResource(R.drawable.ch_pay_question);
        int padding = ViewUtil.dp2px(getContext(), 5);
        rightView.setPadding(padding, padding, padding, padding);
        ViewGroup.LayoutParams layoutParams = rightView.getLayoutParams();
        layoutParams.width = ViewUtil.dp2px(getContext(), 30);
        layoutParams.height = ViewUtil.dp2px(getContext(), 30);
        rightView.setLayoutParams(layoutParams);
    }

    public static interface OnLogoutListener {
        void logout();
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }
}
