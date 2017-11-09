package com.caohua.games.biz.comment;

import com.chsdk.model.BaseEntry;

/**
 * Created by admin on 2017/1/13.
 */

public class TimesPraiseEntry extends BaseEntry {
    private String comment_times;
    private String praise_times;
    private String is_praise;
    private String article_icon;
    private String article_title;
    private String article_url;
    private String content;
    private String is_collect;
    private String article_id;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getComment_times() {
        return comment_times;
    }

    public void setComment_times(String comment_times) {
        this.comment_times = comment_times;
    }

    public String getPraise_times() {
        return praise_times;
    }

    public void setPraise_times(String praise_times) {
        this.praise_times = praise_times;
    }

    public String getIs_praise() {
        return is_praise;
    }

    public void setIs_praise(String is_praise) {
        this.is_praise = is_praise;
    }

    public String getArticle_icon() {
        return article_icon;
    }

    public void setArticle_icon(String article_icon) {
        this.article_icon = article_icon;
    }

    public String getArticle_title() {
        return article_title;
    }

    public void setArticle_title(String article_title) {
        this.article_title = article_title;
    }

    public String getArticle_url() {
        return article_url;
    }

    public void setArticle_url(String article_url) {
        this.article_url = article_url;
    }

    public String getIs_collect() {
        return is_collect;
    }

    public void setIs_collect(String is_collect) {
        this.is_collect = is_collect;
    }

    public String getArticle_id() {
        return article_id;
    }

    public void setArticle_id(String article_id) {
        this.article_id = article_id;
    }
}
