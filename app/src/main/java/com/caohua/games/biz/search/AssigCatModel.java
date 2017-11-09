package com.caohua.games.biz.search;

import com.chsdk.model.BaseModel;

/**
 * Created by CXK on 2016/11/9.
 */

public class AssigCatModel extends BaseModel {
    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    private String gameName;


    public AssigCatModel() {
        this.gameName = getGameName();
    }

    @Override
    public void putDataInMap() {
        put(PARAMS_GAME_ASSIGN, gameName);
    }
}
