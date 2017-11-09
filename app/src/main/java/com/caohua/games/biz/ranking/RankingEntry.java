package com.caohua.games.biz.ranking;

import android.text.TextUtils;

import com.chsdk.biz.download.DownloadEntry;
import com.chsdk.model.BaseEntry;

/**
 * Created by CXK on 2016/10/22.
 */

public class RankingEntry extends BaseEntry {
    private String game_icon;
    private String game_name;
    private String game_introduct;
    private String game_size;
    private String package_name;
    private String game_url;
    private String game_type;
    private String game_sort;
    private String classify_name;
    private String detail_url;

    public String getDetail_url() {
        return detail_url;
    }

    public void setDetail_url(String detail_url) {
        this.detail_url = detail_url;
    }

    public String getClassify_name() {
        return classify_name;
    }

    public void setClassify_name(String classify_name) {
        this.classify_name = classify_name;
    }

    public String getGame_icon() {
        return game_icon;
    }

    public void setGame_icon(String game_icon) {
        this.game_icon = game_icon;
    }

    public String getGame_introduct() {
        return game_introduct;
    }

    public void setGame_introduct(String game_introduct) {
        this.game_introduct = game_introduct;
    }

    public String getGame_name() {
        return game_name;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public String getGame_size() {
        return game_size;
    }

    public void setGame_size(String game_size) {
        this.game_size = game_size;
    }

    public String getGame_sort() {
        return game_sort;
    }

    public void setGame_sort(String game_sort) {
        this.game_sort = game_sort;
    }

    public String getGame_type() {
        return game_type;
    }

    public void setGame_type(String game_type) {
        this.game_type = game_type;
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

    public DownloadEntry getDownloadEntry() {
        DownloadEntry entry = new DownloadEntry();
        entry.downloadUrl = game_url;
        entry.iconUrl = game_icon;
        entry.pkg = package_name;
        entry.title = game_name;
        entry.detail_url = detail_url;
        return entry;
    }

    public boolean sameIcon(RankingEntry entry) {
        return entry != null && !TextUtils.isEmpty(game_icon) && game_icon.equals(entry.game_icon);
    }
}
