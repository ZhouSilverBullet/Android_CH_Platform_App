package com.caohua.games.biz.minegame;

import com.chsdk.model.BaseEntry;

/**
 * Created by zhouzhou on 2017/3/2.
 */

public class TipsEntry extends BaseEntry {
    private int comment;
    private int get_gift;
    private int consume;
    private int recharge;
    private int msg;

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public int getGet_gift() {
        return get_gift;
    }

    public void setGet_gift(int get_gift) {
        this.get_gift = get_gift;
    }

    public int getConsume() {
        return consume;
    }

    public void setConsume(int consume) {
        this.consume = consume;
    }

    public int getRecharge() {
        return recharge;
    }

    public void setRecharge(int recharge) {
        this.recharge = recharge;
    }

    public int getMsg() {
        return msg;
    }

    public void setMsg(int msg) {
        this.msg = msg;
    }
}
