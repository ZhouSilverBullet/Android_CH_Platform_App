package com.caohua.games.biz.home;

import com.chsdk.model.BaseModel;

/**
 * Created by ZengLei on 2016/11/3.
 */

public class RequestPageModel extends BaseModel {

    private String requestCount;
    private String loadedCount;

    public RequestPageModel(String requestCount, String loadedCount) {
        this.requestCount = requestCount;
        this.loadedCount = loadedCount;
    }

    public void putDataInMap() {
        put(PARAMS_NUMBER, loadedCount);
        put(PARAMS_PAGE_SIZE, requestCount);
    }
}
