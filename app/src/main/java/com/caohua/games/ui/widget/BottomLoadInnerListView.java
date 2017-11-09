package com.caohua.games.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.caohua.games.R;
import com.culiu.mhvp.core.InnerListView;

public class BottomLoadInnerListView extends InnerListView implements OnScrollListener {
    private ProgressBar progressBar;
    private TextView tvDes;
    private View footerView;
    int totalItemCount = 0;
    int lastVisibleItem = 0;
    boolean isLoading = false;
    boolean loadMoreUnable;

    public BottomLoadInnerListView(Context context) {
        super(context);
        initView(context);
    }

    public BottomLoadInnerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public BottomLoadInnerListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        footerView =  LayoutInflater.from(context).inflate(R.layout.ch_bottom_load_list_footer, null);
        progressBar = (ProgressBar) footerView.findViewById(R.id.ch_bottom_load_list_progress);
        tvDes = (TextView) footerView.findViewById(R.id.ch_bottom_load_list_des);
        footerView.setVisibility(View.GONE);
        this.setOnScrollListener(this);
        this.addFooterView(footerView);
    }

    public void setLoadMoreUnable(boolean enable) {
        loadMoreUnable = enable;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        super.onScrollStateChanged(view, scrollState);
        if (!loadMoreUnable && lastVisibleItem == totalItemCount && scrollState == SCROLL_STATE_IDLE) {
            if (!isLoading) {
                isLoading = true;
                footerView.setVisibility(View.VISIBLE);
                if (onLoadListener != null)
                    onLoadListener.onLoad();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        this.lastVisibleItem = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;
    }

    private OnLoadListener onLoadListener;

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    public interface OnLoadListener {
        void onLoad();
    }

    public void loadComplete(boolean noMore) {
        if (noMore) {
            progressBar.setVisibility(GONE);
            tvDes.setText("没有更多的数据");
        } else {
            footerView.setVisibility(View.GONE);
        }
        isLoading = false;
        this.invalidate();
    }
}
