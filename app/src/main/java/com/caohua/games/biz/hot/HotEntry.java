package com.caohua.games.biz.hot;

import android.text.TextUtils;

import com.chsdk.model.BaseEntry;

/**
 * Created by ZengLei on 2016/10/28.
 */

public class HotEntry extends BaseEntry {
    public static final int IS_SUB_HOT = 2;

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getIs_close() {
        return is_close;
    }

    public void setIs_close(String is_close) {
        this.is_close = is_close;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getActivity_url() {
        return activity_url;
    }

    public void setActivity_url(String activity_url) {
        this.activity_url = activity_url;
    }

    public String getActivity_img() {
        return activity_img;
    }

    public void setActivity_img(String activity_img) {
        this.activity_img = activity_img;
    }

    public int getType() {
        return type;
    }

    public boolean isSubHot() {
        if (type == IS_SUB_HOT) {
            return true;
        }
        return false;
    }
    public void setType(int type) {
        this.type = type;
    }

    private String activity_img;
    private String activity_url;
    private String activity_name;
    private String start_time;
    private String end_time;
    private String is_close;
    private int type;

    public boolean sameIcon(HotEntry entry) {
        return entry != null && !TextUtils.isEmpty(activity_img) && activity_img.equals(entry.getActivity_img());
    }
}
