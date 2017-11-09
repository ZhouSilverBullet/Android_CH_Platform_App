package com.caohua.games.biz.comment;

import com.chsdk.model.BaseEntry;

/**
 * Created by admin on 2017/1/14.
 */

public class CommentScrollEntry extends BaseEntry {
    private String commentTarget;

    public String getCommentTarget() {
        return commentTarget;
    }

    public void setCommentTarget(String commentTarget) {
        this.commentTarget = commentTarget;
    }
}
