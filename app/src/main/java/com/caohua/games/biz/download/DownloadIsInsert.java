package com.caohua.games.biz.download;

import com.chsdk.model.BaseEntry;

/**
 * Created by admin on 2016/12/2.
 */

public class DownloadIsInsert extends BaseEntry{
    private boolean insert;
    private String pkg;

    public DownloadIsInsert(boolean insert) {
        this.insert = insert;
    }

    public DownloadIsInsert(boolean insert, String pkg) {
        this.insert = insert;
        this.pkg = pkg;
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public boolean isInsert() {
        return insert;
    }

    public void setInsert(boolean insert) {
        this.insert = insert;
    }
}
