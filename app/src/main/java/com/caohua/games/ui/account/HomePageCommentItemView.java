package com.caohua.games.ui.account;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.DataInterface;
import com.caohua.games.biz.account.HomePageCommentEntry;
import com.caohua.games.biz.account.HomePagePublishEntry;
import com.caohua.games.biz.bbs.ForumShareEntry;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.RiffEffectLinearLayout;
import com.chsdk.ui.widget.RiffEffectRelativeLayout;
import com.chsdk.utils.PicUtil;

/**
 * Created by Zenglei on 2017/05/23.
 */

public class HomePageCommentItemView extends RiffEffectRelativeLayout implements DataInterface {
    private HomePageCommentEntry entry;
    private TextView chViewAccountHomePageCommentContent;
    private RelativeLayout chViewAccountHomePageCommentReplyLayout;
    private TextView chViewAccountHomePageCommentReplyNick;
    private TextView chViewAccountHomePageCommentReplyContent;
    private ImageView chViewAccountHomePageCommentIcon;
    private TextView chViewAccountHomePageCommentTitle;
    private TextView chViewAccountHomePageCommentTime;

    private String currentId;

    public HomePageCommentItemView(Context context) {
        super(context);
        lodXml();
    }

    public HomePageCommentItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        lodXml();
    }

    public HomePageCommentItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        lodXml();
    }

    private void lodXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_view_account_home_page_comment, this, true);
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
        chViewAccountHomePageCommentContent = (TextView) findViewById(R.id.ch_view_account_home_page_comment_content);
        chViewAccountHomePageCommentReplyLayout = (RelativeLayout) findViewById(R.id.ch_view_account_home_page_comment_reply_layout);
        chViewAccountHomePageCommentReplyNick = (TextView) findViewById(R.id.ch_view_account_home_page_comment_reply_nick);
        chViewAccountHomePageCommentReplyContent = (TextView) findViewById(R.id.ch_view_account_home_page_comment_reply_content);
        chViewAccountHomePageCommentIcon = (ImageView) findViewById(R.id.ch_view_account_home_page_comment_icon);
        chViewAccountHomePageCommentTitle = (TextView) findViewById(R.id.ch_view_account_home_page_comment_title);
        chViewAccountHomePageCommentTime = (TextView) findViewById(R.id.ch_view_account_home_page_comment_time);
    }

    public void setUserId(String id) {
        this.currentId = id;
    }

    public void setData(Object o) {
        if (o == null)
            return;

        if (o instanceof HomePageCommentEntry) {
            final HomePageCommentEntry entry = (HomePageCommentEntry) o;
            chViewAccountHomePageCommentContent.setText(entry.content);
            chViewAccountHomePageCommentTime.setText(entry.add_time);
            chViewAccountHomePageCommentTitle.setText(entry.article_title);
            PicUtil.displayImg(getContext(), chViewAccountHomePageCommentIcon, entry.game_icon, R.drawable.ch_default_apk_icon);
            if (TextUtils.isEmpty(entry.reply_userid)) {
                chViewAccountHomePageCommentReplyLayout.setVisibility(View.GONE);
            } else {
                chViewAccountHomePageCommentReplyLayout.setVisibility(View.VISIBLE);
                chViewAccountHomePageCommentReplyNick.setText(entry.reply_nickname + ":");
                chViewAccountHomePageCommentReplyNick.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentId.equals(entry.reply_userid)) {
                            CHToast.show(v.getContext(), "当前主页已打开");
                            return;
                        }
                        AccountHomePageActivity.start(v.getContext(), entry.reply_userid, entry.reply_nickname);
                    }
                });
                chViewAccountHomePageCommentReplyContent.setText(entry.reply_content);
            }
            this.entry = entry;
        }
    }
}
