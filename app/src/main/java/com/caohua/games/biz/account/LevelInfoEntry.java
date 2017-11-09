package com.caohua.games.biz.account;

import com.chsdk.model.BaseModel;

/**
 * Created by zhouzhou on 2017/6/13.
 */

public class LevelInfoEntry extends BaseModel {
    private String grow_level;
    private String img_mask;
    private String grow_name;

    public String getGrow_level() {
        return grow_level;
    }

    public void setGrow_level(String grow_level) {
        this.grow_level = grow_level;
    }

    public String getImg_mask() {
        return img_mask;
    }

    public void setImg_mask(String img_mask) {
        this.img_mask = img_mask;
    }

    public String getGrow_name() {
        return grow_name;
    }

    public void setGrow_name(String grow_name) {
        this.grow_name = grow_name;
    }
}
