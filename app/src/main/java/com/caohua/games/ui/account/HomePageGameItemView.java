package com.caohua.games.ui.account;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.DataInterface;
import com.caohua.games.biz.account.HomePageGameEntry;
import com.caohua.games.biz.mymsg.MyMsgEntry;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.RiffEffectLinearLayout;
import com.chsdk.ui.widget.RiffEffectRelativeLayout;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;

/**
 * Created by Zenglei on 2017/05/23.
 */

public class HomePageGameItemView extends RiffEffectRelativeLayout implements DataInterface {
    private HomePageGameEntry entry;
    private ImageView icon;
    private TextView title;
    private TextView des;

    public HomePageGameItemView(Context context) {
        super(context);
        lodXml();
    }

    public HomePageGameItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        lodXml();
    }

    public HomePageGameItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        lodXml();
    }

    private void lodXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_view_account_home_page_game, this, true);
        int padding = ViewUtil.dp2px(getContext(), 10);
        setPadding(padding, padding, padding, padding);
        setClickable(true);
        init();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.startGameDetail(v.getContext(), entry.getDownloadEntry());
            }
        });
    }

    private void init() {
        icon = (ImageView) findViewById(R.id.ch_view_account_home_page_game_icon);
        title = (TextView) findViewById(R.id.ch_view_account_home_page_game_title);
        des = (TextView) findViewById(R.id.ch_view_account_home_page_game_des);
    }

    public void setData(Object o) {
        if (o == null)
            return;

        if (o instanceof HomePageGameEntry) {
            HomePageGameEntry entry = (HomePageGameEntry) o;
            title.setText(entry.title);
            des.setText(entry.des);
            PicUtil.displayImg(getContext(), icon, entry.icon, R.drawable.ch_default_apk_icon);
            this.entry = entry;
        }
    }
}
