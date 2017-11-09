package com.caohua.games.ui.widget.banner;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.caohua.games.R;
import com.caohua.games.biz.home.BannerEntry;
import com.caohua.games.ui.widget.banner.indicator.CirclePagerIndicator;
import com.chsdk.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class BannerView extends RelativeLayout implements ViewPager.OnPageChangeListener, Runnable {
    private ViewPager vp_news;
    private List<BannerItemView> banners = new ArrayList<>();
    private NewsPagerAdapter adapter;
    private CirclePagerIndicator indicator;
    private TextView tv_news_title;
    private int mCurrentItem = 0;
    private Handler mHandler;
    private boolean isMoving = false;
    private boolean isScroll = false;

    private void setLayoutParams() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.height = ViewUtil.getRealHeight(getContext(), 310, 130, 10, false);
        setLayoutParams(params);
    }

    private SwipeRefreshLayout refreshLayout;

    public BannerView(Context context) {
        super(context);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setRefreshLayout(SwipeRefreshLayout refreshLayout) {
        this.refreshLayout = refreshLayout;
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.ch_view_home_banner, this, true);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewGroup.LayoutParams params = getLayoutParams();
                if (params != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                    setLayoutParams();
                }
            }
        });

        vp_news = (ViewPager) findViewById(R.id.vp_news);
        indicator = (CirclePagerIndicator) findViewById(R.id.indicator);
        tv_news_title = (TextView) findViewById(R.id.tv_news_title);
        adapter = new NewsPagerAdapter();
        vp_news.setAdapter(adapter);
        indicator.bindViewPager(vp_news);
        new SmoothScroller(getContext()).bingViewPager(vp_news);
//        vp_news.setOnPageChangeListener(this);
        indicator.setOnPageChangeListener(this);
        vp_news.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        isMoving = false;
                        if (refreshLayout != null) {
                            refreshLayout.setEnabled(true);
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        isMoving = false;
                        if (refreshLayout != null) {
                            refreshLayout.setEnabled(true);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (refreshLayout != null) {
                            refreshLayout.setEnabled(true);
                        }
                        isMoving = true;
                        break;
                }
                return false;
            }
        });
        setVisibility(GONE);
    }

    public synchronized void initData(List<BannerEntry> banners) {
        boolean update = update(banners);
        if (!update)
            return;

        if (this.banners.size() > 0) {
            this.banners.clear();
            vp_news.removeAllViews();
        } else {
            if (banners != null && banners.size() > 0) {
                tv_news_title.setText(banners.get(0).getBanner_title());
            }
        }

        for (BannerEntry banner : banners) {
            BannerItemView newsBanner = new BannerItemView(getContext());
            newsBanner.initData(banner);
            this.banners.add(newsBanner);
        }

        if (this.banners.size() > 0) {
            setVisibility(VISIBLE);
        }

        adapter.notifyDataSetChanged();
        indicator.notifyDataSetChanged();
        requestNext();
    }

    private boolean update(List<BannerEntry> banners) {
        if (this.banners == null || this.banners.size() == 0)
            return true;

        if (banners == null || banners.size() == 0)
            return true;

        if (this.banners.size() != banners.size())
            return true;

        for (int i = 0; i < banners.size(); i++) {
            if (!banners.get(i).getWap_img().equals(this.banners.get(i).getImageUrl())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        isMoving = mCurrentItem != position;
    }

    @Override
    public void onPageSelected(int position) {
        isMoving = false;
        mCurrentItem = position;
        if (refreshLayout != null) {
            refreshLayout.setEnabled(true);
        }
        isScroll = false;
        tv_news_title.setText(banners.get(position).getTitle());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        isMoving = state != ViewPager.SCROLL_STATE_IDLE;
        isScroll = state != ViewPager.SCROLL_STATE_IDLE;
        if (refreshLayout != null) {
            refreshLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHandler = new Handler();
        // show first
        run();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    private void requestNext() {
        Handler handler = this.mHandler;
        if (handler != null && banners.size() > 1) {
            // do next
            removeRunnable();
            startRunnable();
        }
    }
    //开启轮播任务
    public void startRunnable() {
        if (mHandler != null) {
            mHandler.postDelayed(this, 4000);
        }
    }
    //关闭轮播任务
    public void removeRunnable() {
        if (mHandler != null) {
            mHandler.removeCallbacks(this);
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            if (banners.size() == 0)
                return;
            if (!isMoving && !isScroll) {
                mCurrentItem = (mCurrentItem + 1) % banners.size();
                vp_news.setCurrentItem(mCurrentItem);
            }
            requestNext();
        }
    }

    private class NewsPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return banners.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(getBanner(position));
        }

        public BannerItemView getBanner(int position) {
            synchronized (BannerView.this) {
                if (banners.size() -1 < position) {
                    return null;
                }
                return banners.get(position);
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final BannerItemView banner = getBanner(position);
            container.addView(banner);
            return banner;
        }
    }
}
