package com.caohua.games.ui.hot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.DataInterface;
import com.caohua.games.biz.hot.HotEntry;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.RiffEffectImageButton;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ZengLei on 2016/10/31.
 */

public class HotItemView extends RelativeLayout implements DataInterface{

    private RiffEffectImageButton imgIcon;
    private TextView tvDate, endCover;
    private HotEntry entry;

    public HotItemView(Context context) {
        super(context);
        loadXml();
    }

    public HotItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml();
    }

    private void loadXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_view_hot_item, this, true);
        setClickable(true);
//        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
//                ViewUtil.dp2px(getContext(), 160));
//        setLayoutParams(params);

        imgIcon = (RiffEffectImageButton) findViewById(R.id.ch_view_hot_item_img);
        imgIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entry != null) {
                    AnalyticsHome.umOnEvent(AnalyticsHome.HOT_ITEM_ACTIVITY_SECOND, "hot_item_activity_second");
                    WebActivity.startWebPage(getContext(), entry.getActivity_url());
                }
            }
        });

        int height = setLayoutParams(imgIcon);
        setDecHeight(height);

        tvDate = (TextView) findViewById(R.id.ch_view_hot_item_date);
        endCover = (TextView) findViewById(R.id.ch_view_hot_item_cover);
    }

    private void setDecHeight(int height) {
        LinearLayout layoutDot = (LinearLayout) findViewById(R.id.ch_view_hot_item_dec);
        RelativeLayout.LayoutParams params = (LayoutParams) layoutDot.getLayoutParams();
        params.height = height + ViewUtil.dp2px(getContext(), 20);
        layoutDot.setLayoutParams(params);

    }

    public void setData(Object o) {
        if (o == null)
            return;

        HotEntry entry = (HotEntry) o;
        if (this.entry == null || !entry.sameIcon(this.entry)) {
            PicUtil.displayImg(getContext(), imgIcon, entry.getActivity_img(), R.drawable.ch_default_pic);
        }

        this.entry = entry;
        tvDate.setText(getUnixTime(entry.getStart_time()) + " - " + getUnixTime(entry.getEnd_time()));
        if ("1".equals(entry.getIs_close())) {
            endCover.setVisibility(VISIBLE);
        } else {
            endCover.setVisibility(GONE);
        }
    }

    private String getUnixTime(String time) {
        if (time == null) {
            return "";
        }
        String formats = "yyyy-MM-dd HH:mm";
        Long timestamp = Long.parseLong(time) * 1000;
        String date = new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));
        return date;
    }

    private int setLayoutParams(View view) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.height = ViewUtil.getRealHeight(getContext(), 310, 130, 27, false);
        view.setLayoutParams(params);
        return params.height;
    }
}
