package com.chsdk.model.app;

import com.chsdk.model.BaseModel;

/**
 * Created by ZengLei on 2016/11/11.
 */

public class LinkModel extends BaseModel {
    @Override
    public void putDataInMap() {
        String userId = session.getUserId();
        String token = session.getToken();
        put(PARAMS_TOKEN, token);
        put(PARAMS_USER_ID, userId);
    }
}
