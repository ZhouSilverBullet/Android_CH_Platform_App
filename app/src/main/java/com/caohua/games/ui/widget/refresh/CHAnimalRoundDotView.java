package com.caohua.games.ui.widget.refresh;

/**
 * Created by admin on 2017/11/4.
 */

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.caohua.games.R;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import java.util.ArrayList;

/**
 * Created by cjj on 2015/8/27.
 */
public class CHAnimalRoundDotView extends LinearLayout {

    private final Context context;
    private ArrayList<View> viewList;
    private ArrayList<ObjectAnimator> animatorList;
    private int mRadius;

    public CHAnimalRoundDotView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        mRadius = DensityUtil.dp2px(7);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        viewList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            View view = new View(context);
            view.setBackgroundResource(R.drawable.ch_animal_round_bg);
            if (i == 1 || i == 3) {
                view.setAlpha(0.8f);
            } else if (i == 0 || i == 4) {
                view.setAlpha(0.4f);
            }
            viewList.add(view);
        }
        for (View view : viewList) {
            LayoutParams params = new LayoutParams(mRadius, mRadius);
            params.rightMargin = DensityUtil.dp2px(5);
            params.leftMargin = DensityUtil.dp2px(5);
            this.addView(view, params);
        }
    }

    public void start() {
        if (animatorList == null || animatorList.size() == 0) {
            animatorList = new ArrayList<>();
            for (int i = 0; i < viewList.size(); i++) {
                View view = viewList.get(i);
                if (i == 0 || i == 4 || i == 2) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", view.getY(), view.getY() + 5, view.getY() - 5);
                    animator.setDuration(500);
                    animator.setRepeatCount(ValueAnimator.INFINITE);//无限循环
                    animator.setRepeatMode(ValueAnimator.REVERSE);//
                    animator.start();
                    animatorList.add(animator);
                } else if (i == 1 || i == 3) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", view.getY(), view.getY() - 5, view.getY() + 5);
                    animator.setDuration(500);
                    animator.setRepeatCount(ValueAnimator.INFINITE);//无限循环
                    animator.setRepeatMode(ValueAnimator.RESTART);
                    animator.start();
                    animatorList.add(animator);
                }
            }
        } else {
            for (ObjectAnimator objectAnimator : animatorList) {
                objectAnimator.start();
            }
        }

    }

    public void stop() {
        if (animatorList != null) {
            for (ObjectAnimator objectAnimator : animatorList) {
                objectAnimator.cancel();
            }
        }
    }
}

