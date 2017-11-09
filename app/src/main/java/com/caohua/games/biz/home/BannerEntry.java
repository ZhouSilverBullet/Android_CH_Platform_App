package com.caohua.games.biz.home;

import android.text.TextUtils;

import com.chsdk.model.BaseEntry;

import org.w3c.dom.Text;

/**
 * Created by ZengLei on 2016/10/15.
 */

public class BannerEntry extends BaseEntry {

    public String getApp_url() {
        return app_url;
    }

    public void setApp_url(String app_url) {
        this.app_url = app_url;
    }

    public String getBanner_title() {
        return banner_title;
    }

    public void setBanner_title(String banner_title) {
        this.banner_title = banner_title;
    }

    public String getBanner_type() {
        return banner_type;
    }

    public void setBanner_type(String banner_type) {
        this.banner_type = banner_type;
    }

    public String getWap_img() {
        return wap_img;
    }

    public void setWap_img(String wap_img) {
        this.wap_img = wap_img;
    }

    private String app_url;
    private String banner_title;
    private String wap_img;
    private String banner_type;

    public boolean sameIcon(BannerEntry entry) {
        return entry != null && !TextUtils.isEmpty(wap_img) && wap_img.equals(entry.wap_img);
    }
}
