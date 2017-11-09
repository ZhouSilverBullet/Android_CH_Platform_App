package com.caohua.games.biz.gift;

import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by admin on 2017/10/27.
 */

public class GiftCenterClassifyEntry extends BaseEntry {

    private List<GiftBean> gift;
    private List<String> nav;

    public List<GiftBean> getGift() {
        return gift;
    }

    public void setGift(List<GiftBean> gift) {
        this.gift = gift;
    }

    public List<String> getNav() {
        return nav;
    }

    public void setNav(List<String> nav) {
        this.nav = nav;
    }

    public static class GiftBean extends BaseEntry {

        private String game_name;
        private String game_id;
        private String gift_num;
        private String game_icon;
        private String nav;
        private int type;

        public String getGame_name() {
            return game_name;
        }

        public void setGame_name(String game_name) {
            this.game_name = game_name;
        }

        public String getGame_id() {
            return game_id;
        }

        public void setGame_id(String game_id) {
            this.game_id = game_id;
        }

        public String getGift_num() {
            return gift_num;
        }

        public void setGift_num(String gift_num) {
            this.gift_num = gift_num;
        }

        public String getGame_icon() {
            return game_icon;
        }

        public void setGame_icon(String game_icon) {
            this.game_icon = game_icon;
        }

        public String getNav() {
            return nav;
        }

        public void setNav(String nav) {
            this.nav = nav;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
