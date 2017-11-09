package com.caohua.games.biz.ranking;

import com.chsdk.model.BaseEntry;

/**
 * Created by CXK on 2016/10/22.
 */

public class RankingTotalSubEntry extends BaseEntry {
    public String game_name;
    public String game_icon;

    public String getGame_url() {
        return game_url;
    }

    public void setGame_url(String game_url) {
        this.game_url = game_url;
    }

    public String getGame_icon() {
        return game_icon;
    }

    public void setGame_icon(String game_icon) {
        this.game_icon = game_icon;
    }

    public String getGame_name() {
        return game_name;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public String game_url;
}
