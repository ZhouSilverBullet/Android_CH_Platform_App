package com.caohua.games.biz.vip;

import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by admin on 2017/8/29.
 */

public class VipRewardGameEntry extends BaseEntry {

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
        private List<PrivBean> priv;
        private List<GameBean> game;
        private List<RebateBean> rebate;

        public List<PrivBean> getPriv() {
            return priv;
        }

        public List<RebateBean> getRebate() {
            return rebate;
        }

        public void setRebate(List<RebateBean> rebate) {
            this.rebate = rebate;
        }

        public void setPriv(List<PrivBean> priv) {
            this.priv = priv;
        }

        public List<GameBean> getGame() {
            return game;
        }

        public void setGame(List<GameBean> game) {
            this.game = game;
        }

        public static class PrivBean extends BaseEntry {

            private String name;
            private String reward;
            private int get_type;
            private int has_get;
            private int is_active;

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

            public int getGet_type() {
                return get_type;
            }

            public void setGet_type(int get_type) {
                this.get_type = get_type;
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
        }

        public static class GameBean extends BaseEntry {

            private String game_icon;
            private String name;
            private String tag;
            private float rate;
            private String gift_id;

            public String getGame_icon() {
                return game_icon;
            }

            public void setGame_icon(String game_icon) {
                this.game_icon = game_icon;
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

            public float getRate() {
                return rate;
            }

            public void setRate(float rate) {
                this.rate = rate;
            }

            public String getGift_id() {
                return gift_id;
            }

            public void setGift_id(String gift_id) {
                this.gift_id = gift_id;
            }
        }

        public static class RebateBean extends BaseEntry {

            private String name;
            private String reward;
            private String game_icon;
            private int rebate_id;
            private int has_get;
            private int get_type;
            private int is_active;

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

            public int getGet_type() {
                return get_type;
            }

            public void setGet_type(int get_type) {
                this.get_type = get_type;
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

    }
}
