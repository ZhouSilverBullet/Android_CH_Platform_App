package com.caohua.games.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.caohua.games.R;
import com.chsdk.configure.DataStorage;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by ZengLei on 2016/10/14.
 */
public class GuideActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private ViewPager guidePager;
    private ArrayList<ImageView> cacheView;
    private List<ImageView> dotList;
    private ImageView dot1;
    private ImageView dot2;
    private ImageView dot3;
    private ImageView dot4;
    private Button guideButton;
    private LinearLayout dotLayout;
    private int[] imgs = {R.drawable.ch_guide_one,
            R.drawable.ch_guide_two,
            R.drawable.ch_guide_three,
            R.drawable.ch_guide_four
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
        setContentView(R.layout.ch_activity_guide);
        DataStorage.setGuideShow(this, true);
        DataStorage.setGuideShowCode(this, getVersionCode());
        initView();
        initEvent();
        String[] perm = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        if (!EasyPermissions.hasPermissions(this, perm)) {
            EasyPermissions.requestPermissions(this, "请允许开启手机定位权限", PERMISSION_FOR_LOCATION, perm);
        }
    }

    private void initEvent() {
        guidePager.setOnPageChangeListener(this);
        guideButton.setOnClickListener(this);
    }

    private void initView() {
        guidePager = getView(R.id.ch_guide_view_pager);
        guideButton = getView(R.id.ch_guide_button);
        dotLayout = getView(R.id.ch_guide_ll_layout);

        dotList = new ArrayList<>();
        dot1 = getView(R.id.ch_img_dotI);
        dot2 = getView(R.id.ch_img_dotII);
        dot3 = getView(R.id.ch_img_dotIII);
        dot4 = getView(R.id.ch_img_dotIIII);
        dotList.add(dot1);
        dotList.add(dot2);
        dotList.add(dot3);
        dotList.add(dot4);
        dot1.setImageResource(R.drawable.ch_dialog_splash_green_dot);

        guidePager.setOffscreenPageLimit(1);
        guidePager.setAdapter(new GuidePagerAdapter());
    }

    public void alphaButton(Button button) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(button, "alpha", 0f, 0.5f, 1f);
        objectAnimator.setDuration(1200);
        objectAnimator.start();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 3) {
            dotLayout.setVisibility(View.INVISIBLE);
            guideButton.postDelayed(new Runnable() {
                @Override
                public void run() {
                    guideButton.setVisibility(View.VISIBLE);
                    alphaButton(guideButton);
                }
            }, 500);
            return;
        }

        dotLayout.setVisibility(View.VISIBLE);
        guideButton.setVisibility(View.INVISIBLE);

        for (int i = 0; i < dotList.size(); i++) {
            if (i == position) {
                dotList.get(i).setImageResource(R.drawable.ch_dialog_splash_green_dot);
            } else {
                dotList.get(i).setImageResource(R.drawable.ch_dialog_splash_black_dot);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ch_guide_button:
                startActivity(new Intent(GuideActivity.this, HomePagerActivity.class));
                finish();
                break;
        }
    }

    class GuidePagerAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView image;
            if (cacheView != null && cacheView.size() > 0) {
                image = cacheView.get(0);
                cacheView.remove(0);
            } else {
                image = new ImageView(getBaseContext());
                image.setScaleType(ImageView.ScaleType.FIT_XY);
            }
            image.setImageResource(imgs[position]);
            container.addView(image, 0);
            return image;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (cacheView == null) {
                cacheView = new ArrayList<>();
            }
            cacheView.add((ImageView) object);
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return imgs.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cacheView != null) {
            cacheView.clear();
        }
    }

    public int getVersionCode() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            return 20100;
        }
    }
}
