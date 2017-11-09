package com.caohua.games.biz.prefecture;

import com.chsdk.model.BaseEntry;

/**
 * Created by zhouzhou on 2017/5/27.
 */

public class NotifyPrefecturePosition extends BaseEntry {
    private int subjectId;
    private boolean selector;

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public boolean isSelector() {
        return selector;
    }

    public void setSelector(boolean selector) {
        this.selector = selector;
    }
}
