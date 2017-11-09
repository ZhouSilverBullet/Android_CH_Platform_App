package com.caohua.games.ui.vip.widget;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.caohua.games.R;
import com.caohua.games.ui.vip.VipCertificationActivity;
import com.chsdk.biz.BaseLogic;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.utils.ViewUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by admin on 2017/8/22.
 */

public class VipViewPagerLayout extends RelativeLayout {
    private Context context;
    private ViewPager vipViewPager;
    private LinearLayout pointContainer;
    private List<Integer> list;
    private List<View> pointView;
    private ImageView pagerImage;
    private View rl;
    private boolean isVip;

    public VipViewPagerLayout(Context context) {
        this(context, null);
    }

    public VipViewPagerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VipViewPagerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
        initData();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.ch_vip_pager_layout, this, true);
        vipViewPager = (ViewPager) findViewById(R.id.ch_vip_pager_pager);
        vipViewPager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
        pointContainer = (LinearLayout) findViewById(R.id.ch_vip_pager_point_container);
        rl = findViewById(R.id.ch_vip_pager_image_rl);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getVipViewPagerWidth());
        params.topMargin = ViewUtil.dp2px(context, 56);
        vipViewPager.setLayoutParams(params);
        findViewById(R.id.ch_vip_pager_submit).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isVipOrAuth()) {
                    return;
                }
                VipCertificationActivity.start(context);
            }
        });
        pagerImage = (ImageView) findViewById(R.id.ch_vip_pager_image);
        findViewById(R.id.ch_vip_pager_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isVipOrAuth()) {
                    return;
                }
                VipCertificationActivity.start(context);
            }
        });
    }

    private boolean isVipOrAuth() {
        if (!isVip) {
            final CHAlertDialog vipDialog = new CHAlertDialog((Activity) context, true, true);
            vipDialog.show();
            vipDialog.setContent("还未达到VIP条件，去看看怎么成为草花VIP吧");
            vipDialog.setCancelButton("取消", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    vipDialog.dismiss();
                }
            });
            vipDialog.setOkButton("去查看", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebActivity.startAppLink(context, BaseLogic.HOST_APP + "vip/expLogView");
                    vipDialog.dismiss();
                }
            });
            return false;
        }
        return true;
    }

    public void setVipAndAuth(boolean isVip) {
        this.isVip = isVip;
    }

    public void initData() {
        list = new ArrayList<>();
        list.add(R.drawable.ch_vip_view_pager_bg_1);
        list.add(R.drawable.ch_vip_view_pager_bg_2);
        list.add(R.drawable.ch_vip_view_pager_bg_3);
        vipViewPager.setAdapter(new VipPagerAdapter(list));
        pointView = new ArrayList<>();
        View view = null;
        for (int i = 0; i < 3; i++) {
            view = new View(context);
            if (i == 0) {
                view.setBackgroundResource(R.drawable.ch_vip_view_pager_point_p);
            } else {
                view.setBackgroundResource(R.drawable.ch_vip_view_pager_point_n);
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewUtil.dp2px(context, 8), ViewUtil.dp2px(context, 8));
            lp.bottomMargin = ViewUtil.dp2px(context, 5);
            lp.topMargin = ViewUtil.dp2px(context, 5);
            lp.leftMargin = ViewUtil.dp2px(context, 5);
            lp.rightMargin = ViewUtil.dp2px(context, 5);
            view.setLayoutParams(lp);
            pointContainer.addView(view);
            pointView.add(view);
        }

        vipViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < pointView.size(); i++) {
                    pointView.get(i).setBackgroundResource(R.drawable.ch_vip_view_pager_point_n);
                }
                pointView.get(position).setBackgroundResource(R.drawable.ch_vip_view_pager_point_p);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    /**
     * 暂定屏幕宽度的一半
     *
     * @return
     */
    public int getVipViewPagerWidth() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int widthPixels = displayMetrics.widthPixels;
        return (int) (widthPixels / 2f + 0.5f);
    }

    private class VipPagerAdapter extends PagerAdapter {
        private List<Integer> list;

        public VipPagerAdapter(List<Integer> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        public void addAll(Collection<Integer> collection) {
            if (list != null) {
                list.addAll(collection);
                notifyDataSetChanged();
            }
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.ch_vip_view_pager_item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.ch_vip_view_pager_item_image);
            imageView.setImageResource(list.get(position));
            container.addView(view);
            return view;
        }
    }
}
