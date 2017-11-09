package com.caohua.games.ui.account;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.DataInterface;
import com.caohua.games.biz.account.HomePageGameEntry;
import com.caohua.games.biz.account.HomePagePublishEntry;
import com.caohua.games.biz.bbs.ForumShareEntry;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.RiffEffectLinearLayout;
import com.chsdk.ui.widget.RiffEffectRelativeLayout;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;

/**
 * Created by Zenglei on 2017/05/23.
 */

public class HomePagePublishItemView extends RiffEffectLinearLayout implements DataInterface {
    private HomePagePublishEntry entry;
    private ImageView icon;
    private TextView title;
    private TextView time;

    public HomePagePublishItemView(Context context) {
        super(context);
        lodXml();
    }

    public HomePagePublishItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        lodXml();
    }

    public HomePagePublishItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        lodXml();
    }

    private void lodXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_view_account_home_page_publish, this, true);
        setOrientation(HORIZONTAL);
        setPadding(0, 0, 0, 0);
        setClickable(true);
        init();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entry != null) {
                    String detail_url = entry.detail_url;
                    if (!TextUtils.isEmpty(detail_url)) {
                        try {
                            String[] split = detail_url.split("id=");
                            if (split.length >= 2) {
                                String articleId = split[1];
                                ForumShareEntry forumShareEntry = new ForumShareEntry();
                                forumShareEntry.setTitle(entry.article_title);
                                forumShareEntry.setGameIcon(entry.game_icon);
                                forumShareEntry.setGameName(entry.game_name);
                                WebActivity.startForForumPage(getContext(), detail_url, articleId, forumShareEntry, -1);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
        });
    }

    private void init() {
        icon = (ImageView) findViewById(R.id.ch_view_account_home_page_publish_icon);
        title = (TextView) findViewById(R.id.ch_view_account_home_page_publish_title);
        time = (TextView) findViewById(R.id.ch_view_account_home_page_publish_time);
    }

    public void setData(Object o) {
        if (o == null)
            return;

        if (o instanceof HomePagePublishEntry) {
            HomePagePublishEntry entry = (HomePagePublishEntry) o;
            title.setText(entry.title);
            time.setText(entry.add_time);
            PicUtil.displayImg(getContext(), icon, entry.game_icon, R.drawable.ch_default_apk_icon);
            this.entry = entry;
        }
    }
}
