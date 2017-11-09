package com.caohua.games.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by zhouzhou on 2017/2/20.
 */

public class NoScrollViewPager extends ViewPager {
    private boolean isCanScroll;
    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCanScroll(boolean isCanScroll) {
    /*对外公开的方法，设置是否可以滑动*/
        this.isCanScroll = isCanScroll;
    }
    public boolean isCanScroll() {
    /*对外公开的方法，获取当前是否可以滑动*/
        return isCanScroll;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
    /*重写此方法，判断当前是否可以滑动，如果可以，正常调用父类的方法，该干嘛干嘛！如果不可以滑动，直接返回false，不做任何触摸事件的处理*/
        if (isCanScroll()) {
            return super.onTouchEvent(ev);
        }
        return false;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    /*这里意思和上面的重写方法差不多，不多解释*/
        if (isCanScroll()) {
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }
}
