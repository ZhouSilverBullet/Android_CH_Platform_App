package com.caohua.games.biz.search;

import android.text.TextUtils;

import com.chsdk.biz.download.DownloadEntry;
import com.chsdk.model.BaseEntry;

import java.io.Serializable;

/**
 * Created by CXK on 2016/11/9.
 */

public class SearchGameEntry extends BaseEntry {
    public String getGame_name() {
        return game_name;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public String getGame_icon() {
        return game_icon;
    }

    public void setGame_icon(String game_icon) {
        this.game_icon = game_icon;
    }

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public String getGame_size() {
        return game_size;
    }

    public void setGame_size(String game_size) {
        this.game_size = game_size;
    }

    public String getGame_url() {
        return game_url;
    }

    public void setGame_url(String game_url) {
        this.game_url = game_url;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    private String game_name;
    private String game_id;
    private String game_icon;
    private String game_size;
    private String package_name;
    private String game_url;
    private String short_desc;

    public String getShort_desc() {
        return short_desc;
    }

    public void setShort_desc(String short_desc) {
        this.short_desc = short_desc;
    }

    public String getClassify_name() {
        return classify_name;
    }

    public void setClassify_name(String classify_name) {
        this.classify_name = classify_name;
    }

    public String getDetail_url() {
        return detail_url;
    }

    public void setDetail_url(String detail_url) {
        this.detail_url = detail_url;
    }

    public String getGame_introduct() {
        return game_introduct;
    }

    public void setGame_introduct(String game_introduct) {
        this.game_introduct = game_introduct;
    }

    private String game_introduct;
    private String detail_url;
    private String classify_name;   //分类名

    public boolean sameIcon(SearchGameEntry entry) {
        return entry != null && !TextUtils.isEmpty(game_icon)
                && game_icon.equals(entry.getGame_icon());
    }

    public DownloadEntry getDownloadEntry() {
        DownloadEntry entry = new DownloadEntry();
        entry.downloadUrl = game_url;
        entry.iconUrl = game_icon;
        entry.pkg = package_name;
        entry.title = game_name;
        entry.detail_url = detail_url;
        return entry;
    }
}
