package com.caohua.games.biz.article;

import com.chsdk.model.BaseEntry;

/**
 * Created by ZengLei on 2017/5/24.
 */

public class ReadArticleEntry extends BaseEntry {
    public String comment_total; //评论数
    public String upvote_total; //点赞数
    public String is_collect; //是否收藏
    public String is_top; //是否置顶
    public String priv_top; //置顶权限
    public String is_good; //是否精华
    public String priv_good; //加精权限
    public String is_lock; //是否锁定
    public String priv_lock; //锁定权限
    public String is_hide; //是否隐藏
    public String priv_hide; //隐藏权限
    public String is_vote;
}
