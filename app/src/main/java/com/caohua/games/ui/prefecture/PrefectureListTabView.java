package com.caohua.games.ui.prefecture;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.prefecture.PrefectureListEntry;
import com.chsdk.utils.ViewUtil;

import java.util.List;

/**
 * Created by admin on 2017/11/1.
 */

public class PrefectureListTabView extends RelativeLayout {

    private Context context;
    private LinearLayout linearLayout;
    private List<PrefectureListEntry.ClassifyBean> list;

    public interface PrefectureTabListener {
        void tabListener(String classifyId);
    }

    public PrefectureListTabView(Context context) {
        this(context, null);
    }

    public PrefectureListTabView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrefectureListTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.ch_prefecture_list_tab_view, this, true);
        setVisibility(GONE);
        linearLayout = (LinearLayout) findViewById(R.id.ch_prefecture_list_tab_layout);
    }

    public void setData(List<PrefectureListEntry.ClassifyBean> list, final PrefectureTabListener listener) {
        if (list == null || list.size() == 0) {
            setVisibility(GONE);
            return;
        }
        this.list = list;
        PrefectureListEntry.ClassifyBean entry = new PrefectureListEntry.ClassifyBean();
        entry.setClassify_id("0");
        entry.setClassify_name("全部");
        list.add(0, entry);
        for (int i = 0; i < list.size(); i++) {
            final PrefectureListEntry.ClassifyBean classifyBean = list.get(i);
            TextView textView = new TextView(context);
            textView.setText(classifyBean.getClassify_name());
            final int position = i;
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.tabListener(classifyBean.getClassify_id());
                    }
                    for (int j = 0; j < linearLayout.getChildCount(); j++) {
                        if (position == j) {
                            ((TextView) linearLayout.getChildAt(j)).setTextColor(getResources().getColor(R.color.green));
                        } else {
                            ((TextView) linearLayout.getChildAt(j)).setTextColor(getResources().getColor(R.color.black));
                        }
                    }
                }
            });
            textView.setTextColor(getResources().getColor(R.color.ch_black));
            textView.setPadding(getDp(10), getDp(10), getDp(10), getDp(10));
            linearLayout.addView(textView);
        }

        setVisibility(VISIBLE);
    }

    public void setOneColor() {

        ((TextView) linearLayout.getChildAt(0)).setTextColor(getResources().getColor(R.color.green));
    }

    public boolean hasData() {
        if (list == null || list.size() == 0) {
            return false;
        }
        return true;
    }

    public int getDp(int px) {
        return ViewUtil.dp2px(context, px);
    }
}
