package com.caohua.games.biz.vip;

import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by admin on 2017/8/22.
 */

public class VipEntry extends BaseEntry {

    private int code;
    private String msg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean extends BaseEntry {

        private UserBean user;
        private List<RewardBean> gift;
        private List<RechargeBean> coupon;
        private List<ActBean> active;
        private List<RebateBean> rebate;

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public List<RewardBean> getGift() {
            return gift;
        }

        public void setGift(List<RewardBean> gift) {
            this.gift = gift;
        }

        public List<RechargeBean> getCoupon() {
            return coupon;
        }

        public void setCoupon(List<RechargeBean> coupon) {
            this.coupon = coupon;
        }

        public List<ActBean> getActive() {
            return active;
        }

        public void setActive(List<ActBean> active) {
            this.active = active;
        }

        public List<RebateBean> getRebate() {
            return rebate;
        }

        public void setRebate(List<RebateBean> rebate) {
            this.rebate = rebate;
        }

        public static class UserBean extends BaseEntry {

            private String is_auth;
            private String nickname;
            private String vip_level;
            private String vip_name;
            private String vip_exp;
            private String next_exp;
            private int exp_rate;
            private List<Integer> priv_flag;

            public String getIs_auth() {
                return is_auth;
            }

            public void setIs_auth(String is_auth) {
                this.is_auth = is_auth;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getVip_level() {
                return vip_level;
            }

            public void setVip_level(String vip_level) {
                this.vip_level = vip_level;
            }

            public String getVip_name() {
                return vip_name;
            }

            public void setVip_name(String vip_name) {
                this.vip_name = vip_name;
            }

            public String getVip_exp() {
                return vip_exp;
            }

            public void setVip_exp(String vip_exp) {
                this.vip_exp = vip_exp;
            }

            public String getNext_exp() {
                return next_exp;
            }

            public void setNext_exp(String next_exp) {
                this.next_exp = next_exp;
            }

            public int getExp_rate() {
                return exp_rate;
            }

            public void setExp_rate(int exp_rate) {
                this.exp_rate = exp_rate;
            }

            public List<Integer> getPriv_flag() {
                return priv_flag;
            }

            public void setPriv_flag(List<Integer> priv_flag) {
                this.priv_flag = priv_flag;
            }
        }

        public static class RewardBean extends BaseEntry {

            private String name;
            private String reward;
            private String game_icon;
            private String tag;
            private int get_type;
            private int rate;
            private String gift_id;
            private int has_get;
            private int is_active;
            private int rebate_id;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getReward() {
                return reward;
            }

            public void setReward(String reward) {
                this.reward = reward;
            }

            public String getGame_icon() {
                return game_icon;
            }

            public void setGame_icon(String game_icon) {
                this.game_icon = game_icon;
            }

            public String getTag() {
                return tag;
            }

            public void setTag(String tag) {
                this.tag = tag;
            }

            public int getRate() {
                return rate;
            }

            public void setRate(int rate) {
                this.rate = rate;
            }

            public int getGet_type() {
                return get_type;
            }

            public void setGet_type(int get_type) {
                this.get_type = get_type;
            }

            public String getGift_id() {
                return gift_id;
            }

            public void setGift_id(String gift_id) {
                this.gift_id = gift_id;
            }

            public int getHas_get() {
                return has_get;
            }

            public void setHas_get(int has_get) {
                this.has_get = has_get;
            }

            public int getIs_active() {
                return is_active;
            }

            public void setIs_active(int is_active) {
                this.is_active = is_active;
            }

            public int getRebate_id() {
                return rebate_id;
            }

            public void setRebate_id(int rebate_id) {
                this.rebate_id = rebate_id;
            }
        }

        public static class RechargeBean extends BaseEntry {

            private String use_amt;
            private String min_amt;

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
        }

        public static class ActBean extends BaseEntry {

            private String url;
            private String image;
            private String name;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class RebateBean extends BaseEntry {

            private String rebate_id;
            private String rebate_title;
            private String rebate_desc;
            private String game_icon;
            private String low_vip;
            private int is_close;
            private int is_open;

            public String getRebate_id() {
                return rebate_id;
            }

            public void setRebate_id(String rebate_id) {
                this.rebate_id = rebate_id;
            }

            public String getRebate_title() {
                return rebate_title;
            }

            public void setRebate_title(String rebate_title) {
                this.rebate_title = rebate_title;
            }

            public String getRebate_desc() {
                return rebate_desc;
            }

            public void setRebate_desc(String rebate_desc) {
                this.rebate_desc = rebate_desc;
            }

            public String getGame_icon() {
                return game_icon;
            }

            public void setGame_icon(String game_icon) {
                this.game_icon = game_icon;
            }

            public String getLow_vip() {
                return low_vip;
            }

            public void setLow_vip(String low_vip) {
                this.low_vip = low_vip;
            }

            public int getIs_close() {
                return is_close;
            }

            public void setIs_close(int is_close) {
                this.is_close = is_close;
            }

            public int getIs_open() {
                return is_open;
            }

            public void setIs_open(int is_open) {
                this.is_open = is_open;
            }
        }
    }
}
