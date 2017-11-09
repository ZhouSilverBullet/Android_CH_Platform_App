package com.caohua.games.biz.hot;

import com.chsdk.model.BaseEntry;

/**
 * Created by zhouzhou on 2017/4/12.
 */

public class RewardEntry extends BaseEntry {
    private String reward;
    private String num;

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
