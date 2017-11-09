package com.caohua.games.biz.task;

/**
 * Created by zhouzhou on 2017/6/19.
 */

public class RewardItemBean {
    public int number;
    public String textValue;
    public int resDrawable;
    public int resColor;

    public RewardItemBean(int number, String textValue, int resDrawable, int resColor) {
        this.number = number;
        this.textValue = textValue;
        this.resDrawable = resDrawable;
        this.resColor = resColor;
    }
}
