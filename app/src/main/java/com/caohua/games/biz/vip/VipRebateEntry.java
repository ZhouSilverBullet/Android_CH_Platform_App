package com.caohua.games.biz.vip;

import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by admin on 2017/9/25.
 */

public class VipRebateEntry extends BaseEntry {

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

        private String rebate_title;
        private String rebate_desc;
        private String effect_time;
        private String expire_time;
        private String rebate_intro;
        private String game_icon;
        private String package_name;
        private String game_url;
        private String game_name;
        private int get_status;  // 1是可领取 2是已经领取 0不可领取
        private List<RebateBean> rebate;

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

        public String getEffect_time() {
            return effect_time;
        }

        public void setEffect_time(String effect_time) {
            this.effect_time = effect_time;
        }

        public String getExpire_time() {
            return expire_time;
        }

        public void setExpire_time(String expire_time) {
            this.expire_time = expire_time;
        }

        public String getRebate_intro() {
            return rebate_intro;
        }

        public void setRebate_intro(String rebate_intro) {
            this.rebate_intro = rebate_intro;
        }

        public String getGame_icon() {
            return game_icon;
        }

        public void setGame_icon(String game_icon) {
            this.game_icon = game_icon;
        }

        public int getGet_status() {
            return get_status;
        }

        public void setGet_status(int get_status) {
            this.get_status = get_status;
        }

        public List<RebateBean> getRebate() {
            return rebate;
        }

        public void setRebate(List<RebateBean> rebate) {
            this.rebate = rebate;
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

        public String getGame_name() {
            return game_name;
        }

        public void setGame_name(String game_name) {
            this.game_name = game_name;
        }

        public static class RebateBean extends BaseEntry {

            private String vip_level;
            private String reward_desc;

            public String getVip_level() {
                return vip_level;
            }

            public void setVip_level(String vip_level) {
                this.vip_level = vip_level;
            }

            public String getReward_desc() {
                return reward_desc;
            }

            public void setReward_desc(String reward_desc) {
                this.reward_desc = reward_desc;
            }
        }
    }
}
