package com.caohua.games.biz.minegame;

import com.chsdk.biz.download.DownloadEntry;
import com.chsdk.model.BaseEntry;

/**
 * Created by zhouzhou on 2017/2/22.
 */

public class MineGameListEntry extends BaseEntry{
    private String game_name;
    private String game_icon;
    private String game_introduct;
    private String game_url;
    private String game_size;
    private String package_name;
    private String classify_name;
    private String detail_url;
    private String game_version;
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

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

    public String getGame_introduct() {
        return game_introduct;
    }

    public void setGame_introduct(String game_introduct) {
        this.game_introduct = game_introduct;
    }

    public String getGame_url() {
        return game_url;
    }

    public void setGame_url(String game_url) {
        this.game_url = game_url;
    }

    public String getGame_size() {
        return game_size;
    }

    public void setGame_size(String game_size) {
        this.game_size = game_size;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
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

    public String getGame_version() {
        return game_version;
    }

    public void setGame_version(String game_version) {
        this.game_version = game_version;
    }

    public DownloadEntry getDownloadEntry() {
        DownloadEntry entry = new DownloadEntry();
        entry.downloadUrl = game_url;
        entry.iconUrl = game_icon;
        entry.pkg = package_name;
        entry.title = game_name;
        entry.detail_url = detail_url;
        entry.setUpdate(getStatus() == 3);
        return entry;
    }
}
