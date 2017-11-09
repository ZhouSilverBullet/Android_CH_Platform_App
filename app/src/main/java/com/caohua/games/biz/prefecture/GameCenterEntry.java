package com.caohua.games.biz.prefecture;

import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by zhouzhou on 2017/5/23.
 */

public class GameCenterEntry extends BaseEntry {

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
        private List<MultipleBean> multiple;
        private List<MygameBean> mygame;
        private List<AllgameBean> allgame;

        public List<MultipleBean> getMultiple() {
            return multiple;
        }

        public void setMultiple(List<MultipleBean> multiple) {
            this.multiple = multiple;
        }

        public List<MygameBean> getMygame() {
            return mygame;
        }

        public void setMygame(List<MygameBean> mygame) {
            this.mygame = mygame;
        }

        public List<AllgameBean> getAllgame() {
            return allgame;
        }

        public void setAllgame(List<AllgameBean> allgame) {
            this.allgame = allgame;
        }

        public static class MultipleBean extends BaseEntry {

            private String game_id;
            private String game_sort;
            private String game_icon;
            private String game_name;
            private String game_url;
            private String package_name;
            private String short_desc;
            private String detail_url;

            public String getGame_id() {
                return game_id;
            }

            public void setGame_id(String game_id) {
                this.game_id = game_id;
            }

            public String getGame_sort() {
                return game_sort;
            }

            public void setGame_sort(String game_sort) {
                this.game_sort = game_sort;
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

            public String getGame_url() {
                return game_url;
            }

            public void setGame_url(String game_url) {
                this.game_url = game_url;
            }

            public String getPackage_name() {
                return package_name;
            }

            public void setPackage_name(String package_name) {
                this.package_name = package_name;
            }

            public String getShort_desc() {
                return short_desc;
            }

            public void setShort_desc(String short_desc) {
                this.short_desc = short_desc;
            }

            public String getDetail_url() {
                return detail_url;
            }

            public void setDetail_url(String detail_url) {
                this.detail_url = detail_url;
            }
        }

        public static class MygameBean extends BaseEntry {
            private String game_icon;
            private String game_name;
            private String game_introduct;
            private String game_size;
            private String game_url;
            private String package_name;
            private String game_id;
            private String game_version;
            private String detail_url;

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

            public String getGame_introduct() {
                return game_introduct;
            }

            public void setGame_introduct(String game_introduct) {
                this.game_introduct = game_introduct;
            }

            public String getGame_size() {
                return game_size;
            }

            public void setGame_size(String game_size) {
                this.game_size = game_size;
            }

            public String getGame_url() {
                return game_url;
            }

            public void setGame_url(String game_url) {
                this.game_url = game_url;
            }

            public String getPackage_name() {
                return package_name;
            }

            public void setPackage_name(String package_name) {
                this.package_name = package_name;
            }

            public String getGame_id() {
                return game_id;
            }

            public void setGame_id(String game_id) {
                this.game_id = game_id;
            }

            public String getGame_version() {
                return game_version;
            }

            public void setGame_version(String game_version) {
                this.game_version = game_version;
            }

            public String getDetail_url() {
                return detail_url;
            }

            public void setDetail_url(String detail_url) {
                this.detail_url = detail_url;
            }
        }

        public static class AllgameBean extends BaseEntry {

            private String game_name;
            private String short_desc;
            private int subject_id;
            private String game_icon;
            private String detail_url;
            private String package_name;
            private String game_url;
            private String game_id;

            public String getGame_name() {
                return game_name;
            }

            public void setGame_name(String game_name) {
                this.game_name = game_name;
            }

            public String getShort_desc() {
                return short_desc;
            }

            public void setShort_desc(String short_desc) {
                this.short_desc = short_desc;
            }

            public int getSubject_id() {
                return subject_id;
            }

            public void setSubject_id(int subject_id) {
                this.subject_id = subject_id;
            }

            public String getGame_icon() {
                return game_icon;
            }

            public void setGame_icon(String game_icon) {
                this.game_icon = game_icon;
            }

            public String getDetail_url() {
                return detail_url;
            }

            public void setDetail_url(String detail_url) {
                this.detail_url = detail_url;
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

            public String getGame_id() {
                return game_id;
            }

            public void setGame_id(String game_id) {
                this.game_id = game_id;
            }
        }
    }
}
