package com.caohua.games.ui.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.download.ViewDownloadMgr;
import com.caohua.games.biz.search.SearchGameEntry;
import com.caohua.games.ui.download.DownloadButton;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.RiffEffectLinearLayout;
import com.chsdk.utils.PicUtil;

/**
 * Created by CXK on 2016/11/12.
 */

public class SearchListItemView extends RiffEffectLinearLayout {
    private ImageView icon;
    private TextView title;
    private TextView des;
    private TextView type;
    private SearchGameEntry entry;
    private RelativeLayout layout;
    private ViewDownloadMgr downloadMgr;

    public SearchListItemView(Context context) {
        super(context);
        loadXml();
    }

    public SearchListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml();
    }

    public SearchListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadXml();
    }

    private void loadXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_fragment_search_list, this, true);
        icon = (ImageView) findViewById(R.id.ch_view_fragment_item_icon);
        title = (TextView) findViewById(R.id.ch_view_fragment_item_title);
        type = (TextView) findViewById(R.id.ch_view_fragment_item_type_and_size);
        des = (TextView) findViewById(R.id.ch_view_fragment_item_des);
        downloadMgr = new ViewDownloadMgr((DownloadButton) findViewById(R.id.ch_view_fragment_search_item_btn));
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.startGameDetail(v.getContext(), entry.getDownloadEntry());
            }
        });

    }


    public void setData(Object o) {
        if (o == null) {
            return;
        }

        SearchGameEntry entry = (SearchGameEntry) o;

        if (this.entry == null || !this.entry.sameIcon(entry)) {
            PicUtil.displayImg(getContext(), icon, entry.getGame_icon(), R.drawable.ch_default_apk_icon);
        }
        this.entry = entry;
        downloadMgr.setData(entry.getDownloadEntry());
        title.setText(entry.getGame_name());
        type.setText(entry.getClassify_name() + " | " + entry.getGame_size() + "MB");
        des.setText(entry.getGame_introduct());
    }

    public void onDestroy() {
        if (downloadMgr != null) {
            downloadMgr.release();
        }
    }
}
