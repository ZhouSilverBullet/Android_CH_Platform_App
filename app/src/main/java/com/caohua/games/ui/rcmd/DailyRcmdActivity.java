package com.caohua.games.ui.rcmd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.caohua.games.R;
import com.caohua.games.biz.rcmd.DailyRcmdEntry;
import com.caohua.games.ui.BaseActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CXK on 2016/10/18.
 */

public class DailyRcmdActivity extends BaseActivity {
    private static int TOTAL_COUNT = 3;
    private static String KEY_DATA = "data";

    private ViewPager viewPager;
    private List<DailyRcmdEntry> data;
    private ArrayList<RcmdItemView> cacheView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_rcmd);
        initData();
        initView();
    }

    private void initData() {
        if (getIntent() == null) {
            return;
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.get(KEY_DATA) instanceof List) {
                data = (List<DailyRcmdEntry>) bundle.get(KEY_DATA);
            }
        }
    }

    private void initView() {
        if (data != null && data.size() > 0) {
            setViewPager();
        }
    }

    private void setViewPager() {
        viewPager = (ViewPager) findViewById(R.id.ch_activity_rcmd_viewpage);
        viewPager.setOffscreenPageLimit(TOTAL_COUNT);

        int width = getResources().getDisplayMetrics().widthPixels;
        int pagerWidth = width - (width / 10);
        ViewGroup.LayoutParams lp = viewPager.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(pagerWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            lp.width = pagerWidth;
        }
        viewPager.setLayoutParams(lp);
        viewPager.setPageMargin(-(lp.width / 5));
        viewPager.setPageTransformer(true, new Transformation());
        viewPager.setAdapter(new RcmdAdapter());
    }

    class RcmdAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            RcmdItemView view = (RcmdItemView) object;
            if (cacheView == null) {
                cacheView = new ArrayList<>();
            }
            cacheView.add(view);
            container.removeView(view);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            RcmdItemView view;
            if (cacheView != null && cacheView.size() > 0) {
                view = cacheView.get(0);
                cacheView.remove(0);
            } else {
                view = new RcmdItemView(DailyRcmdActivity.this);
            }
            view.initData(data.get(position));
            container.addView(view, 0);
            return view;
        }
    }

    @Override
    public void onDestroy() {
        if (cacheView != null) {
            cacheView.clear();
        }
        if (data != null) {
            data.clear();
        }
        super.onDestroy();
    }

    public static void start(Context context, List<DailyRcmdEntry> list) {
        Intent intent = new Intent(context, DailyRcmdActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_DATA, (Serializable) list);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    class Transformation implements ViewPager.PageTransformer {
        @Override
        public void transformPage(View page, float position) {
            float scaleFactor = Math.max(0.85f, 1 - Math.abs(position));
            float rotate = 20 * Math.abs(position);
            if (position < 0) {
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                page.setRotationY(rotate);
            } else if (position >= 0 && position < 1) {
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                page.setRotationY(-rotate);
            } else if (position >= 1) {
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                page.setRotationY(-rotate);
            }
        }
    }
}
