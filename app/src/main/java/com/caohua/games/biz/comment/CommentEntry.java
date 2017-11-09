package com.caohua.games.biz.comment;

import com.chsdk.model.BaseEntry;

/**
 * Created by admin on 2017/1/13.
 */

public class CommentEntry extends BaseEntry{
    private String commentID;
    private String commentType;
    private String commentGameType;
    private String commentTypeId;
    private String commentIsVerify;
    private String commentContent;

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getCommentType() {
        return commentType;
    }

    public void setCommentType(String commentType) {
        this.commentType = commentType;
    }

    public String getCommentGameType() {
        return commentGameType;
    }

    public void setCommentGameType(String commentGameType) {
        this.commentGameType = commentGameType;
    }

    public String getCommentTypeId() {
        return commentTypeId;
    }

    public void setCommentTypeId(String commentTypeId) {
        this.commentTypeId = commentTypeId;
    }

    public String getCommentIsVerify() {
        return commentIsVerify;
    }

    public void setCommentIsVerify(String commentIsVerify) {
        this.commentIsVerify = commentIsVerify;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
