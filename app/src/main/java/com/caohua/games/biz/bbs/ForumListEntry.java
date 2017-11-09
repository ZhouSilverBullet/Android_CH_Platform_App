package com.caohua.games.biz.bbs;

import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by zhouzhou on 2017/6/20.
 */

public class ForumListEntry extends BaseEntry {

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

        private String forum_id;
        private String forum_name;
        private String game_icon;
        private String forum_memo;

        public String getForum_id() {
            return forum_id;
        }

        public void setForum_id(String forum_id) {
            this.forum_id = forum_id;
        }

        public String getForum_name() {
            return forum_name;
        }

        public void setForum_name(String forum_name) {
            this.forum_name = forum_name;
        }

        public String getGame_icon() {
            return game_icon;
        }

        public void setGame_icon(String game_icon) {
            this.game_icon = game_icon;
        }

        public String getForum_memo() {
            return forum_memo;
        }

        public void setForum_memo(String forum_memo) {
            this.forum_memo = forum_memo;
        }
    }
}
