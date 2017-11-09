package com.caohua.games.ui.widget;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

/**
 * Created by admin on 2017/10/30.
 */

public class CHRefreshHeaderView extends RelativeLayout implements RefreshHeader {
    private Context context;
    private TextView text;

    public CHRefreshHeaderView(Context context) {
        this(context, null);
    }

    public CHRefreshHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CHRefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.ch_refresh_header_view, this, true);
        text = (TextView) findViewById(R.id.ch_refresh_header_text);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            setPadding(getPaddingLeft(), 0, getPaddingRight(), 0);
        } else {
            setPadding(getPaddingLeft(), 20, getPaddingRight(), 20);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case None:
            case PullDownToRefresh: //下拉可以刷新
                text.setText("下拉可以刷新");
                break;
            case Refreshing:
                text.setText("正在刷新...");
                break;
            case ReleaseToRefresh:
                text.setText("释放立即刷新");
                break;
            case Loading:
                text.setText("正在加载...");
                break;
        }
    }

    @Override
    public void onPullingDown(float percent, int offset, int headerHeight, int extendHeight) {
    }

    @Override
    public void onReleasing(float percent, int offset, int headerHeight, int extendHeight) {
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
    public void setPrimaryColors(@ColorInt int... colors) {

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
        return 500;
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }
}
