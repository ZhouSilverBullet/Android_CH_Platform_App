package com.caohua.games.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by zhouzhou on 2017/6/13.
 */

public class KnowHeightScrollView extends ScrollView {
    public interface ScrollViewListener {
        void onScrollChanged(KnowHeightScrollView scrollView, int x, int y,
                             int oldx, int oldy);
    }

    private ScrollViewListener scrollViewListener = null;

    public KnowHeightScrollView(Context context) {
        super(context);
    }

    public KnowHeightScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public KnowHeightScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }
}
