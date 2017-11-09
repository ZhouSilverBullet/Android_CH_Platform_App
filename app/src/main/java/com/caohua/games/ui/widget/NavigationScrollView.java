package com.caohua.games.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.caohua.games.R;

/**
 * Created by ZengLei on 2016/10/15.
 */

public class NavigationScrollView extends LinearLayout implements View.OnClickListener {
    private OnItemSelectedListener listener;
    private Button recommend, ranking, open;
    private View scrollView;
    private int currIndex = 0;
    private int screenWidth;

    public NavigationScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
        inflate(context, R.layout.ch_view_navigation_scroll, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void initView() {
        scrollView = getView(R.id.ch_home_low);
        recommend = getView(R.id.ch_home_recommend);
        ranking = getView(R.id.ch_home_ranking);
        open = getView(R.id.ch_home_open);
        recommend.setOnClickListener(this);
        ranking.setOnClickListener(this);
        open.setOnClickListener(this);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    private <T extends View> T getView(int id) {
        return (T) findViewById(id);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        int index = 0;
        switch (view.getId()) {
            case R.id.ch_home_recommend:
                index = 0;
                break;
            case R.id.ch_home_ranking:
                index = 1;
                break;
            case R.id.ch_home_open:
                index = 2;
                break;
        }

        if (listener != null) {
            listener.itemSelected(index);
        }
    }

    public void setAnim(int index, final Runnable animEndCallback) {
        setButtonColor(index);

        int fromX = currIndex * screenWidth / 3;
        int toX = index * screenWidth / 3;

        Animation animation = new TranslateAnimation(fromX, toX, 0, 0);
        animation.setFillEnabled(true);
        animation.setFillAfter(true);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (animEndCallback != null) {
                    animEndCallback.run();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        scrollView.startAnimation(animation);

        currIndex = index;
    }

    private void setButtonColor(int index) {
        if (index == 0) {
            recommend.setTextColor(Color.parseColor("#34cf4d"));
            open.setTextColor(Color.BLACK);
            ranking.setTextColor(Color.BLACK);
        } else if (index == 1) {
            recommend.setTextColor(Color.BLACK);
            open.setTextColor(Color.BLACK);
            ranking.setTextColor(Color.parseColor("#34cf4d"));
        } else if (index == 2) {
            recommend.setTextColor(Color.BLACK);
            open.setTextColor(Color.parseColor("#34cf4d"));
            ranking.setTextColor(Color.BLACK);
        }
    }

    public static interface OnItemSelectedListener {
        void itemSelected(int index);
    }
}
