package com.caohua.games.biz.gift;

import android.text.TextUtils;
import android.widget.TextView;

import com.chsdk.model.BaseEntry;

/**
 * Created by CXK on 2016/10/22.
 */

public class GiftEntry extends BaseEntry {

    public String getGift_name() {
        return gift_name;
    }

    public void setGift_name(String gift_name) {
        this.gift_name = gift_name;
    }

    public String getGame_icon() {
        return game_icon;
    }

    public void setGame_icon(String game_icon) {
        this.game_icon = game_icon;
    }

    public String getGame_name() {
        return game_name;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public String getGift_desc() {
        return gift_desc;
    }

    public void setGift_desc(String gift_desc) {
        this.gift_desc = gift_desc;
    }

    public String getGift_id() {
        return gift_id;
    }

    public void setGift_id(String gift_id) {
        this.gift_id = gift_id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getIs_get() {
        return is_get;
    }

    public void setIs_get(int is_get) {
        this.is_get = is_get;
    }

    private String gift_id;
    private String gift_name;
    private String gift_desc;
    private String game_name;
    private String game_icon;
    private String item;
    private int is_get;
    private String package_name;

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public boolean sameIcon(GiftEntry entry) {
        return entry != null && !TextUtils.isEmpty(game_icon) && entry.game_icon.equals(game_icon);
    }
}
