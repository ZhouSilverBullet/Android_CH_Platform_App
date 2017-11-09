package com.caohua.games.ui.widget.refresh;

/**
 * Created by admin on 2017/11/4.
 */

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.chsdk.utils.ViewUtil;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class CHRadarHeader extends FrameLayout implements RefreshHeader {

    private CHRoundDotView mDotView;
    private TextView textView;
    private CHAnimalRoundDotView animalRoundDotView;
    private ImageView endIcon;

    public CHRadarHeader(Context context) {
        this(context, null);
    }

    public CHRadarHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CHRadarHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setMinimumHeight(DensityUtil.dp2px(60));
        /**
         * 初始化headView
         */

        mDotView = new CHRoundDotView(getContext());
        animalRoundDotView = new CHAnimalRoundDotView(getContext());
        this.addView(mDotView, MATCH_PARENT, MATCH_PARENT);
        this.addView(animalRoundDotView, MATCH_PARENT, MATCH_PARENT);

        handleEndIcon();
        handleTextView();

    }

    private void handleTextView() {
        textView = new TextView(getContext());
        textView.setTextColor(0xff666666);
        int pixels = getResources().getDisplayMetrics().widthPixels;
        if (pixels >= 1080) {
            textView.setTextSize(getResources().getDimension(R.dimen.ch_refresh_header_text_size));
        } else {
            textView.setTextSize(getResources().getDimension(R.dimen.ch_refresh_header_text_size_1));
        }
        LayoutParams params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = ViewUtil.dp2px(getContext(), 10);
        this.addView(textView, params);
    }

    private void handleEndIcon() {
        endIcon = new ImageView(getContext());
        LayoutParams params = new LayoutParams(ViewUtil.dp2px(getContext(), 20), ViewUtil.dp2px(getContext(), 20));
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.topMargin = ViewUtil.dp2px(getContext(), 10);
        endIcon.setImageResource(R.drawable.ch_refresh_end_icon);
        endIcon.setVisibility(GONE);
        this.addView(endIcon, params);
    }

    @Override
    @Deprecated
    public void setPrimaryColors(@ColorInt int... colors) {
    }

    @NonNull
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Scale;
    }

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
    }

    @Override
    public void onPullingDown(float percent, int offset, int headHeight, int extendHeight) {
        mDotView.setFraction(percent);
    }

    @Override
    public void onReleasing(float percent, int offset, int headHeight, int extendHeight) {
        onPullingDown(percent, offset, headHeight, extendHeight);
    }

    @Override
    public void onStartAnimator(final RefreshLayout layout, int headHeight, int extendHeight) {
        animalRoundDotView.setVisibility(VISIBLE);
        animalRoundDotView.start();
        mDotView.setVisibility(INVISIBLE);
    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        animalRoundDotView.stop();
        animalRoundDotView.setVisibility(GONE);
        if (success) {
            endIcon.setVisibility(VISIBLE);
            endIcon.setImageResource(R.drawable.ch_refresh_end_icon);
            startAnimal(endIcon);
            textView.setText("加载完成");
        } else {
            endIcon.setVisibility(VISIBLE);
            endIcon.setImageResource(R.drawable.ch_refresh_error_icon);
            startAnimal(endIcon);
            textView.setText("加载失败");
        }
        return 1000;
    }

    private void startAnimal(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1.2f, 1f);
        AnimatorSet animator = new AnimatorSet();
        animator.setDuration(1000);
        animator.playTogether(scaleX, scaleY);
        animator.start();
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case None:
                endIcon.setVisibility(GONE);
                animalRoundDotView.setVisibility(GONE);
                mDotView.setAlpha(1);
                mDotView.setVisibility(VISIBLE);
                break;
            case PullDownToRefresh:
                endIcon.setVisibility(GONE);
                animalRoundDotView.setVisibility(GONE);
                textView.setText("下拉加载");
                break;
            case PullToUpLoad:
                break;
            case Refreshing:
                textView.setText("正在刷新");
                break;
            case ReleaseToRefresh:
                textView.setText("放手啦");
                break;
            case Loading:
                textView.setText("正在加载...");
                break;
        }
    }
}

