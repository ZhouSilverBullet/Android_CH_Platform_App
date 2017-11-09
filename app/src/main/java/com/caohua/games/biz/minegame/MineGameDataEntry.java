package com.caohua.games.biz.minegame;

import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by zhouzhou on 2017/2/22.
 */

public class MineGameDataEntry extends BaseEntry{
    private List<MineGameEntry> data;

    public List<MineGameEntry> getData() {
        return data;
    }

    public void setData(List<MineGameEntry> data) {
        this.data = data;
    }
}
