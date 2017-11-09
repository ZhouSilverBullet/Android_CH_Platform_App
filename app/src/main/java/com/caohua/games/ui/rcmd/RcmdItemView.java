package com.caohua.games.ui.rcmd;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.rcmd.DailyRcmdEntry;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.ui.WebActivity;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by CXK on 2016/10/18.
 */

public class RcmdItemView extends LinearLayout implements View.OnClickListener {
    private TextView tvTitle, tvDes, tvDate;
    private ImageView imgBg, imgIcon;
    private DailyRcmdEntry entry;
    private Button btn;

    public RcmdItemView(Context context) {
        super(context);
        loadXml();
    }

    public RcmdItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml();
    }

    private void loadXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_view_rcmd_item, this, true);
        int padding = ViewUtil.dp2px(getContext(), 30);
        setPadding(padding, ViewUtil.dp2px(getContext(), 40), padding, ViewUtil.dp2px(getContext(), 50));
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void init() {
        btn = (Button) findViewById(R.id.ch_view_rcmd_item_btn);
        btn.setOnClickListener(this);
        tvDes = (TextView) findViewById(R.id.ch_view_rcmd_item_des);
        tvDes.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvTitle = (TextView) findViewById(R.id.ch_view_rcmd_item_title);
        imgBg = (ImageView) findViewById(R.id.ch_view_rcmd_item_bg);
        imgBg.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (imgBg.getWidth() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        imgBg.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        imgBg.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                    setLayoutParams(imgBg);
                }
            }
        });
        imgIcon = (ImageView) findViewById(R.id.ch_view_rcmd_item_icon);
        tvDate = (TextView) findViewById(R.id.ch_view_rcmd_item_date);
    }

    private void setLayoutParams(View view) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.height = (int) (((float) view.getWidth() ) * 444 / 580);
        view.setLayoutParams(params);
    }

    public synchronized void initData(DailyRcmdEntry entry) {
        if (entry == null)
            return;

        if (this.entry == null || !this.entry.getGame_icon().equals(entry.getGame_icon())) {
            PicUtil.displayImg(getContext(), imgIcon, entry.getGame_icon(), R.drawable.ch_default_apk_icon);
        }

        if (this.entry == null || !this.entry.getGame_bg().equals(entry.getGame_bg())) {
            PicUtil.displayImg(getContext(), imgBg, entry.getGame_bg(), R.drawable.ch_default_pic);
        }

        this.entry = entry;
        tvDes.setText(entry.getGame_introduct());
        tvTitle.setText(entry.getGame_name());

        if (TextUtils.isEmpty(entry.getShow_time())) {
            //没请求到
        } else {
            String data = showTime(entry.getShow_time());
            if (!TextUtils.isEmpty(data)) {
                tvDate.setText(data);
            }
        }
    }

    @Override
    public void onClick(View v) {
        AnalyticsHome.umOnEvent(AnalyticsHome.HOME_DAY_RECOMMEND_ANALYTICS,"每日推荐跳游戏专区");
        WebActivity.startGameDetail(getContext(),entry.getDownloadEntry());
    }

    public String showTime(String time) {
        String date = "";
        try {
            String formats = "yyyy-MM-dd HH:mm:ss";
            Long timestamp = Long.parseLong(time) * 1000;
            date = new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));
        } catch (Exception e) {

        }
        return date;
    }
}
