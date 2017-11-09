package com.caohua.games.biz.pay;

import com.chsdk.model.BaseEntry;

/**
 * Created by ZengLei on 2016/11/16.
 */

public class Rebate extends BaseEntry {
    private int min;
    private int max;
    private int value;
    private int benefit_type;
    private String show;

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean hit(int value) {
        return value >= min && value <= max;
    }

    public int getValue() {
        return value;
    }

    public int getBenefit_type() {
        return benefit_type;
    }

    public void setBenefit_type(int benefit_type) {
        this.benefit_type = benefit_type;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }
}
