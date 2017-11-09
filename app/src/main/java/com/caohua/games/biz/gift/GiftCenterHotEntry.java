package com.caohua.games.biz.gift;

import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by admin on 2017/10/27.
 */

public class GiftCenterHotEntry extends BaseEntry {

    private List<ListBean> list;
    private List<MyListBean> my_list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public List<MyListBean> getMy_list() {
        return my_list;
    }

    public void setMy_list(List<MyListBean> my_list) {
        this.my_list = my_list;
    }

    public static class ListBean extends BaseEntry {

        private String gift_name;
        private String gift_desc;
        private String game_icon;
        private String game_name;
        private int rest;
        private String gift_id;
        private int is_get;

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

        public int getRest() {
            return rest;
        }

        public void setRest(int rest) {
            this.rest = rest;
        }

        public String getGift_id() {
            return gift_id;
        }

        public void setGift_id(String gift_id) {
            this.gift_id = gift_id;
        }

        public int getIs_get() {
            return is_get;
        }

        public void setIs_get(int is_get) {
            this.is_get = is_get;
        }
    }

    public static class MyListBean extends BaseEntry {

        private String game_icon;
        private String game_name;
        private String gift_num;
        private String game_id;

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

        public String getGift_num() {
            return gift_num;
        }

        public void setGift_num(String gift_num) {
            this.gift_num = gift_num;
        }

        public String getGame_id() {
            return game_id;
        }

        public void setGame_id(String game_id) {
            this.game_id = game_id;
        }
    }
}
