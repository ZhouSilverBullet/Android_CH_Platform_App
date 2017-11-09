package com.caohua.games.ui.mymsg;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.DataInterface;
import com.caohua.games.biz.bbs.ForumShareEntry;
import com.caohua.games.biz.mymsg.MyMsgEntry;
import com.caohua.games.ui.account.AccountHeadView;
import com.caohua.games.ui.account.AccountHomePageActivity;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.RiffEffectLinearLayout;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;

/**
 * Created by Zenglei on 2017/05/23.
 */

public class MyMsgItemView extends RiffEffectLinearLayout implements DataInterface {
    private MyMsgEntry entry;
    private AccountHeadView chViewMyMsgBodyIcon;
    private TextView chViewMyMsgBodyName;
    private TextView chViewMyMsgBodyDes;
    private TextView chViewMyMsgTime;
    private TextView chViewMyMsgContent;
    private ImageView chViewMyMsgSubIcon;
    private TextView chViewMyMsgSubTitle;

    public MyMsgItemView(Context context) {
        super(context);
        lodXml();
    }

    public MyMsgItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        lodXml();
    }

    public MyMsgItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        lodXml();
    }

    private void lodXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_view_my_msg_item, this, true);
        int padding = ViewUtil.dp2px(getContext(), 10);
        setPadding(padding, padding, padding, padding);
        setClickable(true);
        setOrientation(VERTICAL);
        init();
    }

    private void init() {
        chViewMyMsgBodyIcon = (AccountHeadView) findViewById(R.id.ch_view_my_msg_body_icon);
        chViewMyMsgBodyName = (TextView) findViewById(R.id.ch_view_my_msg_body_name);
        chViewMyMsgBodyDes = (TextView) findViewById(R.id.ch_view_my_msg_body_des);
        chViewMyMsgTime = (TextView) findViewById(R.id.ch_view_my_msg_time);
        chViewMyMsgContent = (TextView) findViewById(R.id.ch_view_my_msg_content);
        chViewMyMsgSubIcon = (ImageView) findViewById(R.id.ch_view_my_msg_sub_icon);
        chViewMyMsgSubTitle = (TextView) findViewById(R.id.ch_view_my_msg_sub_title);
    }

    public void setData(Object o) {
        if (o == null)
            return;

        if (o instanceof MyMsgEntry) {
            final MyMsgEntry entry = (MyMsgEntry) o;
            chViewMyMsgBodyName.setText(entry.nickname);
            chViewMyMsgSubTitle.setText(entry.from_content);
            chViewMyMsgContent.setText(entry.content);
            chViewMyMsgBodyDes.setText(entry.tip);
            chViewMyMsgTime.setText(entry.add_time);
            chViewMyMsgBodyIcon.getAccountImage().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AccountHomePageActivity.start(getContext(), entry.userid, entry.nickname);
                }
            });
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String detail_url = entry.detail_url;
                    if (!TextUtils.isEmpty(detail_url)) {
                        try {
                            String[] split = detail_url.split("id=");
                            if (split.length >= 2) {
                                String articleId = split[1];
                                ForumShareEntry forumShareEntry = new ForumShareEntry();
                                forumShareEntry.setTitle(entry.content);
                                forumShareEntry.setGameIcon(entry.game_icon);
                                forumShareEntry.setGameName(entry.forum_name);
                                WebActivity.startForForumPage(getContext(), detail_url, articleId, forumShareEntry, -1);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            });
            String user_photo = entry.user_photo;
            chViewMyMsgBodyIcon.setAccountWidthHeadBg(entry.img_mask, 12);
            chViewMyMsgBodyIcon.setAccountImage(user_photo, false, R.drawable.ch_my_msg_default);
            String game_icon = entry.game_icon;
            doImage(game_icon, chViewMyMsgSubIcon, R.drawable.ch_default_apk_icon);
            this.entry = entry;
        }
    }

    private void doImage(String path, ImageView imageView, int defaultIcon) {
        if (!TextUtils.isEmpty(path)) {
            PicUtil.displayImg(getContext(), imageView, path, defaultIcon);
        } else {
            imageView.setImageResource(defaultIcon);
        }
    }
}
