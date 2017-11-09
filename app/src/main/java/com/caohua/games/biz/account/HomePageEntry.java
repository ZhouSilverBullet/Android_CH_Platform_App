package com.caohua.games.biz.account;

import com.chsdk.model.BaseEntry;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ZengLei on 2017/5/24.
 */

public class HomePageEntry extends BaseEntry {
    @SerializedName("game_list")
    public List<HomePageGameEntry> gameList;
    @SerializedName("nickname")
    public String nickName;
    @SerializedName("user_photo")
    public String photo;
    @SerializedName("type_name")
    public String typeName;
    @SerializedName("be_upvote")
    public String upvoteCount;
    public String show_level;
    public String img_mask;
    public String grow_name;
    public String vip_level;
    public String vip_name;
}
