package com.caohua.games.ui.search;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.caohua.games.R;
import com.caohua.games.biz.search.HotGameEntry;
import com.caohua.games.ui.download.DownloadListActivity;
import com.caohua.games.ui.widget.AutoTextView;
import com.chsdk.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by admin on 2017/8/18.
 */

public class HomeSearchTitle extends LinearLayout {

    private Context context;
    private AutoTextView autoTextView;
    private Runnable runnable;
    private List<HotGameEntry> data;
    private FrameLayout skipDownload;

    public HomeSearchTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.ch_home_search_title, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        autoTextView = ((AutoTextView) findViewById(R.id.ch_home_search));
        skipDownload = ((FrameLayout) findViewById(R.id.ch_home_btn_down_mgr));
        findViewById(R.id.ch_title_ll).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data != null) {
                    SearchActivity.start(context, data);
                }
            }
        });
        skipDownload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, DownloadListActivity.class));
            }
        });

    }

    public void setData(List<HotGameEntry> data) {
        if (data != null) {
            this.data = data;
        } else {
            this.data = new ArrayList<>();
        }
    }

    /**
     * 开始轮播
     */
    public void setRcmdGame() {
        LogUtil.errorLog("HomeSearchTitle onResume");
        if (autoTextView == null) {
            return;
        }
        String rcmdName = getRcmdGameName();
        if (TextUtils.isEmpty(rcmdName))
            return;
        autoTextView.setText(rcmdName);
        isStart = true;
        runnable = new Runnable() {
            @Override
            public void run() {
                if (context != null) {
                    setRcmdGame();
                    autoTextView.next();
                }
            }
        };
        autoTextView.postDelayed(runnable, 4000);
    }

    public boolean isStart;
    private int mLocation; //记录下次的滚动角标

    private String getRcmdGameName() {
        if (data != null && data.size() > 0) {
            int location = getRandomNumber(data.size());
            HotGameEntry entry = data.get(location);
            if (entry != null && !TextUtils.isEmpty(entry.getGame_name())) {
                return entry.getGame_name();
            }
        }
        return "";
    }

    private int getRandomNumber(int size) {
        if (size == 1) {
            return 0;
        } else {
            int i = new Random().nextInt(size);
            if (mLocation == i) {
                if (i == size - 1) {
                    i = 0;
                } else if (mLocation == 0) {
                    i = size - 1;
                } else {
                    i = i + 1;
                }
            }
            mLocation = i;
            return i;
        }
    }

    /**
     * 暂停轮播
     */
    public void removeAutoTextRunnable() {
        if (autoTextView != null) {
            LogUtil.errorLog("HomeSearchTitle removeAutoTextRunnable");
            autoTextView.removeCallbacks(runnable);
            runnable = null;
        }
    }
}
