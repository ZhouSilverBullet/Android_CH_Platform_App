package com.caohua.games.biz.bbs;

import com.chsdk.model.BaseEntry;

/**
 * Created by zhouzhou on 2017/5/27.
 */

public class ForumShareEntry extends BaseEntry {
    private String gameIcon;
    private String gameName;
    private String title;
    private String shareUrl;

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getGameIcon() {
        return gameIcon;
    }

    public void setGameIcon(String gameIcon) {
        this.gameIcon = gameIcon;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
