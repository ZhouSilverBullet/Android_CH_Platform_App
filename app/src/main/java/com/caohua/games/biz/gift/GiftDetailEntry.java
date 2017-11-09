package com.caohua.games.biz.gift;

import com.chsdk.model.BaseEntry;

/**
 * Created by admin on 2017/10/31.
 */

public class GiftDetailEntry extends BaseEntry {

    /**
     * gift_id : 4281
     * gift_name : 元宵节礼包
     * gift_desc : 50元宝、10000军功、召唤部队1分身*2、召唤战神部队
     * take_limit : 20:50
     * gift_time : 2017-12-30 00:00:00
     * use_state : 登录游戏-点击福利-点击礼品卡-输入礼包码
     * game_icon : https://cdn-sdk.caohua.com/game_img/20161229112135.png
     * game_name : 风云天下重燃
     * package_name : com.tencent.tmgp.fytxcr.caohua
     * game_url : http://dl4.caohua.com/APK/fytxcr_caohua/639/576/fytxcr_caohua.apk
     * item : 1
     * take_hb : 草花币50个
     * take_certify : 1
     * take_grow : 5
     * take_level : 4
     * take_vip : 3
     * is_get : 0
     */

    public String gift_id;
    public String gift_name;
    public String gift_desc;
    public String take_limit;
    public String gift_time;
    public String use_state;
    public String game_icon;
    public String game_name;
    public String package_name;
    public String game_url;
    public int item;
    public String take_hb;
    public int take_certify;
    public String take_grow;
    public String take_level;
    public String take_vip;
    public int is_get;
    public String game_introduct;

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

    public String getTake_limit() {
        return take_limit;
    }

    public void setTake_limit(String take_limit) {
        this.take_limit = take_limit;
    }

    public String getGift_time() {
        return gift_time;
    }

    public void setGift_time(String gift_time) {
        this.gift_time = gift_time;
    }

    public String getUse_state() {
        return use_state;
    }

    public void setUse_state(String use_state) {
        this.use_state = use_state;
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

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getGame_url() {
        return game_url;
    }

    public void setGame_url(String game_url) {
        this.game_url = game_url;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public String getTake_hb() {
        return take_hb;
    }

    public void setTake_hb(String take_hb) {
        this.take_hb = take_hb;
    }

    public int getTake_certify() {
        return take_certify;
    }

    public void setTake_certify(int take_certify) {
        this.take_certify = take_certify;
    }

    public String getTake_grow() {
        return take_grow;
    }

    public void setTake_grow(String take_grow) {
        this.take_grow = take_grow;
    }

    public String getTake_level() {
        return take_level;
    }

    public void setTake_level(String take_level) {
        this.take_level = take_level;
    }

    public String getTake_vip() {
        return take_vip;
    }

    public void setTake_vip(String take_vip) {
        this.take_vip = take_vip;
    }

    public int getIs_get() {
        return is_get;
    }

    public void setIs_get(int is_get) {
        this.is_get = is_get;
    }

    public String getGame_introduct() {
        return game_introduct;
    }

    public void setGame_introduct(String game_introduct) {
        this.game_introduct = game_introduct;
    }
}
