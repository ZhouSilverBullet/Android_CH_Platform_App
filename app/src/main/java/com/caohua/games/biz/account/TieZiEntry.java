package com.caohua.games.biz.account;

import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by zhouzhou on 2017/5/28.
 */

public class TieZiEntry extends BaseEntry {
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
        private String game_id;
        private String forum_id;
        private String userid;
        private String title;
        private String add_time;
        private String upvote_total;
        private String comment_total;
        private String read_total;
        private String game_name;
        private String game_icon;
        private String user_photo;
        private String nickname;
        private String admin_name;
        private boolean is_good;
        private boolean is_top;
        private boolean is_lock;
        private String show_level;
        private String grow_name;
        private List<String> images;

        public String getDetail_url() {
            return detail_url;
        }

        public void setDetail_url(String detail_url) {
            this.detail_url = detail_url;
        }

        private String detail_url;

        public String getArticle_id() {
            return article_id;
        }

        public void setArticle_id(String article_id) {
            this.article_id = article_id;
        }

        public String getGame_id() {
            return game_id;
        }

        public void setGame_id(String game_id) {
            this.game_id = game_id;
        }

        public String getForum_id() {
            return forum_id;
        }

        public void setForum_id(String forum_id) {
            this.forum_id = forum_id;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAdd_time() {
            return add_time;
        }

        public void setAdd_time(String add_time) {
            this.add_time = add_time;
        }

        public String getUpvote_total() {
            return upvote_total;
        }

        public void setUpvote_total(String upvote_total) {
            this.upvote_total = upvote_total;
        }

        public String getComment_total() {
            return comment_total;
        }

        public void setComment_total(String comment_total) {
            this.comment_total = comment_total;
        }

        public String getGame_name() {
            return game_name;
        }

        public void setGame_name(String game_name) {
            this.game_name = game_name;
        }

        public String getGame_icon() {
            return game_icon;
        }

        public void setGame_icon(String game_icon) {
            this.game_icon = game_icon;
        }

        public String getUser_photo() {
            return user_photo;
        }

        public void setUser_photo(String user_photo) {
            this.user_photo = user_photo;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
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
    }
}
