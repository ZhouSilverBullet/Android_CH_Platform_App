package com.caohua.games.ui.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;

/**
 * Created by CXK on 2016/11/11.
 */

public class HistoryItemView extends LinearLayout {
    private TextView string;

    public HistoryItemView(Context context) {
        super(context);
        loadXml();
    }

    public HistoryItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml();
    }

    public HistoryItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadXml();
    }

    private void loadXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_fragment_history_grid_item, this, true);
        setOrientation(VERTICAL);
        setBackgroundResource(R.color.ch_white);
        string = (TextView) findViewById(R.id.ch_fragment_history_string);
    }

    public void setData(String str) {
        if (str != null) {
            string.setText(str);
        }
    }
}
