package com.caohua.games.biz.gift;

import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by admin on 2016/11/12.
 */

public class GetGiftDetailsEntry extends BaseEntry {

    private String gift_id;
    private String gift_name;
    private String gift_time;
    private String gift_desc;
    private String game_name;
    private String game_icon;
    private String game_url;
    private String package_name;
    private int is_get;
    private String take_limit;
    private String use_state;
    private int item;

    public String getUse_state() {
        return use_state;
    }

    public void setUse_state(String use_state) {
        this.use_state = use_state;
    }


    public int getIs_get() {
        return is_get;
    }

    public void setIs_get(int is_get) {
        this.is_get = is_get;
    }

    public String getGift_id() {
        return gift_id;
    }

    public void setGift_id(String gift_id) {
        this.gift_id = gift_id;
    }

    public String getGift_name() {
        return gift_name;
    }

    public void setGift_name(String gift_name) {
        this.gift_name = gift_name;
    }

    public String getGift_desc() {
        return gift_desc;
    }

    public void setGift_desc(String gift_desc) {
        this.gift_desc = gift_desc;
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

    public String getGift_time() {
        return gift_time;
    }

    public void setGift_time(String gift_time) {
        this.gift_time = gift_time;
    }

    public String getTake_limit() {
        return take_limit;
    }

    public void setTake_limit(String take_limit) {
        this.take_limit = take_limit;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }
}
