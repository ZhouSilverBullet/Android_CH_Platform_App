package com.caohua.games.biz.coupon;

import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by zhouzhou on 2017/8/2.
 */

public class CouponEntry extends BaseEntry {

    private int code;
    private String msg;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean extends BaseEntry {

        private String center_id;
        private String coupon_code;
        private int has_recive;
        private String use_type;
        private String take_type;
        private String name;
        private String tag;
        private String game_icon;
        private int game_type;
        private String use_amt;
        private String min_amt;
        private int use_rate;
        private String description;
        private int jump_type;
        private String jump_url;
        private String jump_game_id;
        private String jump_game_icon;
        private String jump_game_name;
        private String jump_pack_name;
        private String jump_game_url;
        private List<String> game_names;

        public String getCenter_id() {
            return center_id;
        }

        public void setCenter_id(String center_id) {
            this.center_id = center_id;
        }

        public String getCoupon_code() {
            return coupon_code;
        }

        public void setCoupon_code(String coupon_code) {
            this.coupon_code = coupon_code;
        }

        public int getHas_recive() {
            return has_recive;
        }

        public void setHas_recive(int has_recive) {
            this.has_recive = has_recive;
        }

        public String getUse_type() {
            return use_type;
        }

        public void setUse_type(String use_type) {
            this.use_type = use_type;
        }

        public String getTake_type() {
            return take_type;
        }

        public void setTake_type(String take_type) {
            this.take_type = take_type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getGame_icon() {
            return game_icon;
        }

        public void setGame_icon(String game_icon) {
            this.game_icon = game_icon;
        }

        public int getGame_type() {
            return game_type;
        }

        public void setGame_type(int game_type) {
            this.game_type = game_type;
        }

        public String getUse_amt() {
            return use_amt;
        }

        public void setUse_amt(String use_amt) {
            this.use_amt = use_amt;
        }

        public String getMin_amt() {
            return min_amt;
        }

        public void setMin_amt(String min_amt) {
            this.min_amt = min_amt;
        }

        public int getUse_rate() {
            return use_rate;
        }

        public void setUse_rate(int use_rate) {
            this.use_rate = use_rate;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getJump_type() {
            return jump_type;
        }

        public void setJump_type(int jump_type) {
            this.jump_type = jump_type;
        }

        public String getJump_url() {
            return jump_url;
        }

        public void setJump_url(String jump_url) {
            this.jump_url = jump_url;
        }

        public String getJump_game_id() {
            return jump_game_id;
        }

        public void setJump_game_id(String jump_game_id) {
            this.jump_game_id = jump_game_id;
        }

        public String getJump_game_icon() {
            return jump_game_icon;
        }

        public void setJump_game_icon(String jump_game_icon) {
            this.jump_game_icon = jump_game_icon;
        }

        public String getJump_game_name() {
            return jump_game_name;
        }

        public void setJump_game_name(String jump_game_name) {
            this.jump_game_name = jump_game_name;
        }

        public String getJump_pack_name() {
            return jump_pack_name;
        }

        public void setJump_pack_name(String jump_pack_name) {
            this.jump_pack_name = jump_pack_name;
        }

        public String getJump_game_url() {
            return jump_game_url;
        }

        public void setJump_game_url(String jump_game_url) {
            this.jump_game_url = jump_game_url;
        }

        public List<String> getGame_names() {
            return game_names;
        }

        public void setGame_names(List<String> game_names) {
            this.game_names = game_names;
        }
    }
}
