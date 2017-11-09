package com.caohua.games.biz.topic;

import android.text.TextUtils;

import com.chsdk.model.BaseEntry;

/**
 * Created by CXK on 2016/10/22.
 */

public class TopicEntry extends BaseEntry {

    public String getSubject_img() {
        return subject_img;
    }

    public void setSubject_img(String subject_img) {
        this.subject_img = subject_img;
    }

    public String getSubject_title() {
        return subject_title;
    }

    public void setSubject_title(String subject_title) {
        this.subject_title = subject_title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String subject_img;
    private String subject_title;
    private String id;

    public boolean sameIcon(TopicEntry entry) {
        return entry != null && !TextUtils.isEmpty(subject_img) && subject_img.equals(entry.subject_img);
    }
}
