package com.caohua.games.biz.search;

import com.caohua.games.app.AppContext;
import com.chsdk.model.BaseModel;
import com.chsdk.model.login.LoginUserInfo;

import java.util.Map;

/**
 * Created by CXK on 2016/11/9.
 */

public class SearchGameModel extends BaseModel {
    private String gameName;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }


    public SearchGameModel() {
        gameName = getGameName();
    }


    @Override
    public void putDataInMap() {
        AppContext appContext = AppContext.getAppContext();
        LoginUserInfo user = appContext.getUser();
        if (appContext.isLogin() && user != null) {
            put(PARAMS_USER_ID, user.userId);
        } else {
            put(PARAMS_USER_ID, "0");
        }
        put(PARAMS_GAME_ASSIGN, gameName);
    }

    @Override
    public Map<String, String> getDataMap() {
        return super.getDataMap();
    }
}
