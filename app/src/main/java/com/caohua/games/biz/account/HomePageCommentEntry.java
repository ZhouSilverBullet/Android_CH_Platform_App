package com.caohua.games.biz.account;

import com.chsdk.model.BaseEntry;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ZengLei on 2017/5/23.
 */

public class HomePageCommentEntry extends BaseEntry {
    @SerializedName("comment_id")
    public String comment_id;
    @SerializedName("content")
    public String content;
    @SerializedName("add_time")
    public String add_time;
    @SerializedName("comment_type")
    public String comment_type;
    @SerializedName("reply_userid")
    public String reply_userid;
    @SerializedName("reply_nickname")
    public String reply_nickname;
    @SerializedName("article_id")
    public String article_id;
    @SerializedName("article_title")
    public String article_title;
    @SerializedName("game_id")
    public String game_id;
    @SerializedName("game_name")
    public String game_name;
    @SerializedName("game_icon")
    public String game_icon;
    @SerializedName("reply_content")
    public String reply_content;
    public String detail_url;
}
