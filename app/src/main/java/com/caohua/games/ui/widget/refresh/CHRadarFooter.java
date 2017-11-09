package com.caohua.games.ui.widget.refresh;

/**
 * Created by admin on 2017/11/7.
 */

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.caohua.games.R;
import com.chsdk.utils.ViewUtil;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.util.DensityUtil;

/**
 * Created by admin on 2017/11/6.
 */

public class CHRadarFooter extends FrameLayout implements RefreshFooter {

    private Context context;
    private TextView textView;
    private boolean mLoadmoreFinished;
    private ProgressBar progressBar;
    private ImageView imageView;

    public CHRadarFooter(Context context) {
        this(context, null);
    }

    public CHRadarFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CHRadarFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        setMinimumHeight(DensityUtil.dp2px(60));

        handleText();
        handleProgress();
        handleImage();
    }

    private void handleImage() {
        imageView = new ImageView(context);
        imageView.setVisibility(GONE);
        LayoutParams layoutParams = new LayoutParams(DensityUtil.dp2px(20), DensityUtil.dp2px(20));
        layoutParams.gravity = Gravity.CENTER | Gravity.TOP;
        layoutParams.topMargin = DensityUtil.dp2px(10);
        addView(imageView, layoutParams);
    }

    private void handleProgress() {
        progressBar = new ProgressBar(context);
        progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.ch_progress_footer_circle));
        LayoutParams layoutParams = new LayoutParams(DensityUtil.dp2px(20), DensityUtil.dp2px(20));
        layoutParams.gravity = Gravity.CENTER | Gravity.TOP;
        layoutParams.topMargin = DensityUtil.dp2px(10);
        addView(progressBar, layoutParams);
    }

    private void handleText() {
        textView = new TextView(context);
        textView.setTextColor(0xff666666);
        int pixels = getResources().getDisplayMetrics().widthPixels;
        if (pixels >= 1080) {
            textView.setTextSize(getResources().getDimension(R.dimen.ch_refresh_header_text_size));
        } else {
            textView.setTextSize(getResources().getDimension(R.dimen.ch_refresh_header_text_size_1));
        }
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
        layoutParams.bottomMargin = DensityUtil.dp2px(10);
        addView(textView, layoutParams);
    }

    @Override
    public void onPullingUp(float percent, int offset, int footerHeight, int extendHeight) {

    }

    @Override
    public void onPullReleasing(float percent, int offset, int footerHeight, int extendHeight) {

    }

    @Override
    public boolean setLoadmoreFinished(boolean finished) {
        if (mLoadmoreFinished != finished) {
            mLoadmoreFinished = finished;
            if (finished) {
                progressBar.setVisibility(GONE);
                imageView.setVisibility(VISIBLE);
                imageView.setImageResource(R.drawable.ch_refresh_end_icon);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView, "scaleX", 0f, 1.2f, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView, "scaleY", 0f, 1.2f, 1f);
                AnimatorSet animator = new AnimatorSet();
                animator.setDuration(1000);
                animator.playTogether(scaleX, scaleY);
                animator.start();
                textView.setText("全部加载完成");
            } else {
                textView.setText("上拉加载更多");
            }
        }
        return true;
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {

    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int height, int extendHeight) {

    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        if (success) {
            textView.setText("加载完成");
        } else {
            textView.setText("加载失败");
            imageView.setVisibility(VISIBLE);
            imageView.setImageResource(R.drawable.ch_load_refresh_error);
        }
        return 500;
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        if (!mLoadmoreFinished) {
            switch (newState) {
                case None:
//                    restoreRefreshLayoutBackground();
//                    mArrowView.setVisibility(VISIBLE);
                    imageView.setVisibility(GONE);
                case PullToUpLoad:
                    textView.setText("上拉加载更多");
//                    mArrowView.animate().rotation(180);
                    break;
                case Loading:
//                    mArrowView.setVisibility(GONE);
                    textView.setText("正在加载");
                    break;
                case ReleaseToLoad:
                    textView.setText("释放立即加载");
//                    mArrowView.animate().rotation(0);
//                    replaceRefreshLayoutBackground(refreshLayout);
                    break;
                case Refreshing:
                    textView.setText("正在刷新");
//                    mProgressView.setVisibility(GONE);
//                    mArrowView.setVisibility(GONE);
                    break;
            }
        }
    }
}