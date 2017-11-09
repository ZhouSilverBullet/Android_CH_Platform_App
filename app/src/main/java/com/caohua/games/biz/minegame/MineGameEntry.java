package com.caohua.games.biz.minegame;

import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by zhouzhou on 2017/2/22.
 */

public class MineGameEntry extends BaseEntry{

    String packageName;
    String versionName;
    String appName;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
