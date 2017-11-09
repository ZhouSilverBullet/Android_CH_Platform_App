package com.caohua.games.biz.task;

import com.chsdk.model.BaseEntry;

/**
 * Created by zhouzhou on 2017/3/17.
 * 用于刷新任务的
 */

public class TaskForHomeNotify extends BaseEntry {
    private boolean isRefresh;

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }
}
