package com.caohua.games.ui.task;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.ui.adapter.TaskRecyclerAdapter;
import com.caohua.games.ui.find.FindContentActivity;
import com.chsdk.utils.ViewUtil;

/**
 * Created by zhouzhou on 2017/3/14.
 */

public class RewardLinearLayout extends LinearLayout implements View.OnLongClickListener, View.OnClickListener {
    public static final int CAO_HUA_DOU = 1;
    public static final int CAO_HUA_BI = 2;
    public static final int CAO_HUA_BANG_YIN = 3;
    public static final int CAO_HUA_EXP = 4;

    public static final String CAO_HUA_DOU_DEC = "草花官方平台积分，可在APP内兑换实物奖励，兑换草花币";
    public static final String CAO_HUA_BI_DEC = "草花手游平台通用货币，可在游戏内购买游戏币";
    public static final String CAO_HUA_BANG_YIN_DEC = "草花手游平台通用货币，可在指定游戏内购买游戏币";
    public static final String CAO_HUA_EXP_DEC = "草花平台用户等级积分，等级越高获得的特权越多";

    private ImageView rewardIcon;
    private TextView rewardText;
    private int describeTextColor;
    private Drawable icon;
    private PopupWindow popupWindow;
    private String popTextValue;
    private int popIconValue;
    private String popDecValue;
    private int popTextColor;
    private int position;

    public RewardLinearLayout(Context context) {
        this(context, null);
    }

    public RewardLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RewardLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RewardLinearLayout, defStyleAttr, 0);
        describeTextColor = a.getColor(R.styleable.RewardLinearLayout_describeTextColor, getResources().getColor(R.color.ch_black));
        icon = a.getDrawable(R.styleable.RewardLinearLayout_icon);
        a.recycle();
        init(context);
    }

    private void init(Context context) {
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
        setClickable(true);
        LayoutInflater.from(context).inflate(R.layout.ch_reward_layout, this, true);
        rewardIcon = (ImageView) findViewById(R.id.ch_reward_icon);
        rewardText = (TextView) findViewById(R.id.ch_reward_text);
        setOnClickListener(this);
        setOnLongClickListener(this);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void resetStatus(String describe, @ColorRes int describeTextColor,
                            @DrawableRes int icon, int status) {
        if (!TextUtils.isEmpty(describe)) {
            rewardText.setText(describe);
        }
        rewardIcon.setImageDrawable(getResources().getDrawable(icon));
        rewardText.setTextColor(getResources().getColor(describeTextColor));
        popTextValue = describe;
        popIconValue = icon;
        switch (status) {
            case CAO_HUA_DOU:
                popDecValue = CAO_HUA_DOU_DEC;
                popTextColor = getResources().getColor(R.color.ch_color_download_normal_title);
                break;
            case CAO_HUA_BI:
                popDecValue = CAO_HUA_BI_DEC;
                popTextColor = getResources().getColor(R.color.ch_color_download_pause);
                break;
            case CAO_HUA_BANG_YIN:
                popDecValue = CAO_HUA_BANG_YIN_DEC;
                popTextColor = getResources().getColor(R.color.ch_red_text);
                break;
            case CAO_HUA_EXP:
                popDecValue = CAO_HUA_EXP_DEC;
                popTextColor = getResources().getColor(R.color.ch_task_purple);
                break;
        }
    }

    private void showPopUp(View v) {
        final FindContentActivity activity = (FindContentActivity) v.getContext();
        LinearLayout layout = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.ch_reward_pop_layout, null);
        TextView popText = (TextView) layout.findViewById(R.id.ch_reward_pop_text);
        ImageView popIcon = (ImageView) layout.findViewById(R.id.ch_reward_pop_icon);
        TextView popDec = (TextView) layout.findViewById(R.id.ch_reward_pop_dec);
        popText.setText(popTextValue);
        popIcon.setImageResource(popIconValue);
        popDec.setText(popDecValue);
        popText.setTextColor(popTextColor);

        popupWindow = new PopupWindow(layout, ViewUtil.dp2px(activity, 180), ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int height = layout.getMeasuredHeight();
        LinearLayoutManager layoutManager = TaskRecyclerAdapter.getScrollYDistance();
        if (layoutManager != null) {
            if (layoutManager.findLastVisibleItemPosition() - 2 <= position) {
                popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + ViewUtil.dp2px(getContext(), 12),
                        location[1] - height - ViewUtil.dp2px(getContext(), 20));
            } else {
                popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + ViewUtil.dp2px(getContext(), 12),
                        location[1] + ViewUtil.dp2px(getContext(), 5));
            }
        } else {
            popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + ViewUtil.dp2px(getContext(), 12),
                    location[1] + ViewUtil.dp2px(getContext(), 5));
        }
        setBackgroundAlpha(0.5f, activity);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f, activity);
            }
        });
    }

    public void setBackgroundAlpha(float bgAlpha, Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        activity.getWindow().setAttributes(lp);
    }

    @Override
    public boolean onLongClick(View v) {
        showPopUp(v);
        return true;
    }

    @Override
    public void onClick(View v) {
        showPopUp(v);
    }
}
