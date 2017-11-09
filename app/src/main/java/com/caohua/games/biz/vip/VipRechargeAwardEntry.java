package com.caohua.games.biz.vip;

import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by admin on 2017/8/29.
 */

public class VipRechargeAwardEntry extends BaseEntry {

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

        private List<RebateBean> rebate;
        private List<CouponBean> coupon;

        public List<RebateBean> getRebate() {
            return rebate;
        }

        public void setRebate(List<RebateBean> rebate) {
            this.rebate = rebate;
        }

        public List<CouponBean> getCoupon() {
            return coupon;
        }

        public void setCoupon(List<CouponBean> coupon) {
            this.coupon = coupon;
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

        public static class CouponBean extends BaseEntry {
            private String name;
            private String use_type;
            private String use_amt;
            private String min_amt;
            private int use_rate;
            private String vip_level;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getUse_type() {
                return use_type;
            }

            public void setUse_type(String use_type) {
                this.use_type = use_type;
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

            public String getVip_level() {
                return vip_level;
            }

            public void setVip_level(String vip_level) {
                this.vip_level = vip_level;
            }
        }

    }
}
