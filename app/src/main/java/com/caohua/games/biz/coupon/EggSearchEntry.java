package com.caohua.games.biz.coupon;

import com.chsdk.model.BaseEntry;

/**
 * Created by admin on 2017/8/22.
 */

public class EggSearchEntry extends BaseEntry {

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
        private String id;
        private String act_img;
        private String success_img;
        private String url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAct_img() {
            return act_img;
        }

        public void setAct_img(String act_img) {
            this.act_img = act_img;
        }

        public String getSuccess_img() {
            return success_img;
        }

        public void setSuccess_img(String success_img) {
            this.success_img = success_img;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
