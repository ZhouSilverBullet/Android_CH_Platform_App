package com.caohua.games.biz.gift;

import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by admin on 2017/10/31.
 */

public class GameGiftEntry extends BaseEntry {

    private GameBean game;
    private List<ListBean> list;

    public GameBean getGame() {
        return game;
    }

    public void setGame(GameBean game) {
        this.game = game;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class GameBean extends BaseEntry {

        private String classify_id;
        private String game_icon;
        private String game_url;
        private String game_id;
        private String game_size;
        private String short_desc;
        private String inner_gid;
        private String inner_gname;
        private String game_name;

        public String getClassify_id() {
            return classify_id;
        }

        public void setClassify_id(String classify_id) {
            this.classify_id = classify_id;
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

        public String getShort_desc() {
            return short_desc;
        }

        public void setShort_desc(String short_desc) {
            this.short_desc = short_desc;
        }

        public String getInner_gid() {
            return inner_gid;
        }

        public void setInner_gid(String inner_gid) {
            this.inner_gid = inner_gid;
        }

        public String getInner_gname() {
            return inner_gname;
        }

        public void setInner_gname(String inner_gname) {
            this.inner_gname = inner_gname;
        }

        public String getGame_name() {
            return game_name;
        }

        public void setGame_name(String game_name) {
            this.game_name = game_name;
        }
    }

    public static class ListBean extends BaseEntry {

        private String gift_id;
        private String game_id;
        private String gift_name;
        private String gift_desc;
        private String valid_num;
        private String app_take;
        private int app_rest;
        private int is_get;

        public String getGift_id() {
            return gift_id;
        }

        public void setGift_id(String gift_id) {
            this.gift_id = gift_id;
        }

        public String getGame_id() {
            return game_id;
        }

        public void setGame_id(String game_id) {
            this.game_id = game_id;
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

        public String getValid_num() {
            return valid_num;
        }

        public void setValid_num(String valid_num) {
            this.valid_num = valid_num;
        }

        public String getApp_take() {
            return app_take;
        }

        public void setApp_take(String app_take) {
            this.app_take = app_take;
        }

        public int getApp_rest() {
            return app_rest;
        }

        public void setApp_rest(int app_rest) {
            this.app_rest = app_rest;
        }

        public int getIs_get() {
            return is_get;
        }

        public void setIs_get(int is_get) {
            this.is_get = is_get;
        }
    }
}
