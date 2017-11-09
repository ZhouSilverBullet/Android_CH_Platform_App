package com.caohua.games.biz.bbs;

import com.chsdk.model.BaseEntry;

/**
 * Created by zhouzhou on 2017/5/26.
 */

public class BBSContentParam extends BaseEntry {
    private String userId;
    private String forum_id;
    private String is_top;
    private String is_good;
    private String tag_id;
    private String page_count;
    private int page_start;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getForum_id() {
        return forum_id;
    }

    public void setForum_id(String forum_id) {
        this.forum_id = forum_id;
    }

    public String getIs_top() {
        return is_top;
    }

    public void setIs_top(String is_top) {
        this.is_top = is_top;
    }

    public String getIs_good() {
        return is_good;
    }

    public void setIs_good(String is_good) {
        this.is_good = is_good;
    }

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public String getPage_count() {
        return page_count;
    }

    public void setPage_count(String page_count) {
        this.page_count = page_count;
    }

    public int getPage_start() {
        return page_start;
    }

    public void setPage_start(int page_start) {
        this.page_start = page_start;
    }
}
