package com.caohua.games.biz.account;

import com.chsdk.biz.download.DownloadEntry;
import com.chsdk.model.BaseEntry;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ZengLei on 2017/5/23.
 */

public class HomePageGameEntry extends BaseEntry {
    @SerializedName("game_icon")
    public String icon;
    @SerializedName("game_name")
    public String title;
    @SerializedName("game_introduct")
    public String des;
    @SerializedName("game_id")
    public String id;
    @SerializedName("package_name")
    public String pkg;
    @SerializedName("detail_url")
    public String detailUrl;
    @SerializedName("game_url")
    public String gameUrl;

    public DownloadEntry getDownloadEntry() {
        DownloadEntry entry = new DownloadEntry();
        entry.downloadUrl = gameUrl;
        entry.iconUrl = icon;
        entry.pkg = pkg;
        entry.title = title;
        entry.detail_url = detailUrl;
        return entry;
    }
}
