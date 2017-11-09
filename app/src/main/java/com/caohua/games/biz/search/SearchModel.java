package com.caohua.games.biz.search;

import com.chsdk.model.BaseModel;

/**
 * Created by CXK on 2016/11/8.
 */

public class SearchModel extends BaseModel {
    private String gameId;
    private String time;
    private String appVersion;


    public SearchModel() {
        gameId = "20127";
        time = getTime();
        appVersion = "1.0";
    }

    @Override
    public void putDataInMap() {
        put(PARAMS_GAME_ID, gameId);
        put(PARAMS_TIMESTAMP, time);
        put(PARAMS_SDK_VERSION, appVersion);
    }

    public String getTime() {

        long time = System.currentTimeMillis() / 1000;//获取系统时间的10位的时间戳

        String str = String.valueOf(time);

        return str;

    }
}
