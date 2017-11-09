package com.caohua.games.biz.account;

import com.chsdk.model.BaseEntry;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ZengLei on 2017/5/23.
 */

public class HomePagePublishEntry extends BaseEntry {
    @SerializedName("article_id")
    public String article_id;
    @SerializedName("game_id")
    public String game_id;
    @SerializedName("forum_id")
    public String forum_id;
    @SerializedName("userid")
    public String userid;
    @SerializedName("title")
    public String title;
    @SerializedName("add_time")
    public String add_time;
    @SerializedName("game_name")
    public String game_name;
    @SerializedName("game_icon")
    public String game_icon;
    public String detail_url;
    public String article_title;
}
