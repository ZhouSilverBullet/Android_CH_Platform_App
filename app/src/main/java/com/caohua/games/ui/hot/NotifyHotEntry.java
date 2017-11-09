package com.caohua.games.ui.hot;

import com.chsdk.model.BaseEntry;

/**
 * Created by admin on 2016/12/2.
 */

public class NotifyHotEntry extends BaseEntry{
    boolean notify;

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }
}
