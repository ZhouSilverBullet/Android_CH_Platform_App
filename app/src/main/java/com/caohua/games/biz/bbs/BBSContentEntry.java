package com.caohua.games.biz.bbs;

import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by zhouzhou on 2017/5/26.
 */

public class BBSContentEntry extends BaseEntry {
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
        private String article_id;
        private String img_mask;
        private String userid;
        private String nickname;
        private String user_photo;
        private String tag_id;
        private String tag_name;
        private String title;
        private boolean is_titlebold;
        private boolean is_titlext;
        private boolean is_underline;
        private boolean is_good;
        private boolean is_top;
        private boolean is_lock;
        private String title_color;
        private String comment_total;
        private String upvote_total;
        private String read_total;
        private String comment_time;
        private String detail_url;
        private String admin_name;
        private String show_level;
        private String grow_name;
        private String vip_level;
        private List<String> images;

        public String getArticle_id() {
            return article_id;
        }

        public void setArticle_id(String article_id) {
            this.article_id = article_id;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getUser_photo() {
            return user_photo;
        }

        public void setUser_photo(String user_photo) {
            this.user_photo = user_photo;
        }

        public String getTag_id() {
            return tag_id;
        }

        public void setTag_id(String tag_id) {
            this.tag_id = tag_id;
        }

        public String getTag_name() {
            return tag_name;
        }

        public void setTag_name(String tag_name) {
            this.tag_name = tag_name;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isIs_titlebold() {
            return is_titlebold;
        }

        public void setIs_titlebold(boolean is_titlebold) {
            this.is_titlebold = is_titlebold;
        }

        public boolean isIs_titlext() {
            return is_titlext;
        }

        public void setIs_titlext(boolean is_titlext) {
            this.is_titlext = is_titlext;
        }

        public boolean isIs_underline() {
            return is_underline;
        }

        public void setIs_underline(boolean is_underline) {
            this.is_underline = is_underline;
        }

        public String getTitle_color() {
            return title_color;
        }

        public void setTitle_color(String title_color) {
            this.title_color = title_color;
        }

        public String getComment_total() {
            return comment_total;
        }

        public void setComment_total(String comment_total) {
            this.comment_total = comment_total;
        }

        public String getUpvote_total() {
            return upvote_total;
        }

        public void setUpvote_total(String upvote_total) {
            this.upvote_total = upvote_total;
        }

        public String getComment_time() {
            return comment_time;
        }

        public void setComment_time(String comment_time) {
            this.comment_time = comment_time;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public String getDetail_url() {
            return detail_url;
        }

        public void setDetail_url(String detail_url) {
            this.detail_url = detail_url;
        }

        public String getAdmin_name() {
            return admin_name;
        }

        public void setAdmin_name(String admin_name) {
            this.admin_name = admin_name;
        }

        public String getImg_mask() {
            return img_mask;
        }

        public void setImg_mask(String img_mask) {
            this.img_mask = img_mask;
        }

        public String getRead_total() {
            return read_total;
        }

        public void setRead_total(String read_total) {
            this.read_total = read_total;
        }

        public boolean isIs_good() {
            return is_good;
        }

        public void setIs_good(boolean is_good) {
            this.is_good = is_good;
        }

        public boolean isIs_top() {
            return is_top;
        }

        public void setIs_top(boolean is_top) {
            this.is_top = is_top;
        }

        public boolean isIs_lock() {
            return is_lock;
        }

        public void setIs_lock(boolean is_lock) {
            this.is_lock = is_lock;
        }

        public String getShow_level() {
            return show_level;
        }

        public void setShow_level(String show_level) {
            this.show_level = show_level;
        }

        public String getGrow_name() {
            return grow_name;
        }

        public void setGrow_name(String grow_name) {
            this.grow_name = grow_name;
        }

        public String getVip_level() {
            return vip_level;
        }

        public void setVip_level(String vip_level) {
            this.vip_level = vip_level;
        }
    }
}
