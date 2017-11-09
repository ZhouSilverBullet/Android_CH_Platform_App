package com.caohua.games.ui.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.search.HotGameEntry;
import com.chsdk.ui.WebActivity;
import com.chsdk.utils.PicUtil;

/**
 * Created by CXK on 2016/11/8.
 */

public class SearchItemView extends LinearLayout {
    private HotGameEntry entry;
    private ImageView icon;
    private TextView string;

    SearchListItemListener searchItemListener;

    public interface SearchListItemListener {
        void clickListener(String entryName);
    }

    public void setSearchItemListener(SearchListItemListener searchItemListener) {
        this.searchItemListener = searchItemListener;
    }

    public SearchItemView(Context context) {
        super(context);
        loadXml();
    }

    public SearchItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml();
    }

    public SearchItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadXml();
    }

    private void loadXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_view_search_item, this, true);
        setOrientation(HORIZONTAL);
        icon = (ImageView) findViewById(R.id.ch_activity_search_grid_item_icon);
        string = (TextView) findViewById(R.id.ch_activity_search_grid_item_text);

    }

    public void setData(Object o) {
        if (o == null) {
            return;
        }

        HotGameEntry entry = (HotGameEntry) o;
        if (this.entry == null || !this.entry.sameIcon(entry)) {
            PicUtil.displayImg(getContext(), icon, entry.getGame_icon(), R.drawable.ch_default_apk_icon);
        }
        this.entry = entry;
        string.setText(entry.getGame_name());
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchItemListener != null) {
                    searchItemListener.clickListener(SearchItemView.this.entry.getGame_name());
                }
            }
        });
    }
}
