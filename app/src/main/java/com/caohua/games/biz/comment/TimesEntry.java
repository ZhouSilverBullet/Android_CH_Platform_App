package com.caohua.games.biz.comment;

import com.chsdk.model.BaseEntry;

/**
 * Created by admin on 2017/1/13.
 */

public class TimesEntry extends BaseEntry {
    private String type;
    private String id;  //
    private String commentType;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommentType() {
        return commentType;
    }

    public void setCommentType(String commentType) {
        this.commentType = commentType;
    }
}
