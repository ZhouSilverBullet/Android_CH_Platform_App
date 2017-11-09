package com.caohua.games.biz.comment;

import com.chsdk.model.BaseEntry;

/**
 * Created by admin on 2017/1/13.
 */

public class UpdateCommentEntry extends BaseEntry {
    private String commentID;
    private String commentTarget;

    public String getCommentTarget() {
        return commentTarget;
    }

    public void setCommentTarget(String commentTarget) {
        this.commentTarget = commentTarget;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }
}
