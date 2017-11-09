package com.caohua.games.ui.task;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.caohua.games.R;
import com.chsdk.ui.widget.RippleEffectButton;
import com.chsdk.utils.ViewUtil;

/**
 * Created by zhouzhou on 2017/3/21.
 */

public class SubmitRippleButton extends RippleEffectButton {
    public SubmitRippleButton(Context context) {
        super(context);
    }

    public SubmitRippleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SubmitRippleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setBackgroundComplete() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
        layoutParams.width = ViewUtil.dp2px(getContext(), 60);
        layoutParams.height = ViewUtil.dp2px(getContext(), 33);
        setBackgroundResource(R.drawable.ch_task_complete_button_bg);
        setText("");
    }

    public void setBackgroundText(String status) {
        setBackgroundText(status, 0);
    }

    public void setBackgroundText(String status, int code) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
        layoutParams.width = ViewUtil.dp2px(getContext(), 60);
        layoutParams.height = ViewUtil.dp2px(getContext(), 25);
        switch (status) {
            case "未完成":
                setBackgroundResource(R.drawable.ch_task_submit_btn_bg);
                setTextColor(getResources().getColor(R.color.ch_color_download_pause));
                setText(status);
                break;
            case "去完成":
                setBackgroundResource(R.drawable.ch_ripple_btn_bg);
                setTextColor(getResources().getColor(R.color.ch_color_download_normal_title));
                setText(status);
                break;
            case "领取":
                if (code == 1) {
                    setBackgroundResource(R.drawable.download_task_button_shape_not_gave);
                } else {
                    setBackgroundResource(R.drawable.download_button_shape_normal);
                }
                setTextColor(getResources().getColor(R.color.ch_color_white));
                setText(status);
                break;
            case "领取失败":
                setBackgroundResource(R.drawable.ch_task_submit_btn_bg);
                setTextColor(getResources().getColor(R.color.ch_color_download_pause));
                setText(status);
                break;
        }
    }
}
