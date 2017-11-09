package com.chsdk.biz.download;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZengLei on 2016/10/18.
 */

public class DownloadEntry implements Serializable{
    @SerializedName("game_icon")
    public String iconUrl;
    @SerializedName("game_url")
    public String downloadUrl;
    @SerializedName("package_name")
    public String pkg;
    @SerializedName("game_name")
    public String title;

    public boolean update;

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public String getDetail_url() {
        return detail_url;
    }

    public void setDetail_url(String detail_url) {
        this.detail_url = detail_url;
    }

    public String detail_url;

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
